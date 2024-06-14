package com.example.team2.service;

import com.example.team2.dao.SensorRepository;
import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import com.example.team2.entity.User;
import com.example.team2.entity.Usersensor;
import jakarta.persistence.Query;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final FcmService fcmService;

    @Value("${broker.address}")
    private String brokerAddress;

    @Value("${broker.username}")
    private String brokerUsername;

    @Value("${broker.password}")
    private String brokerPassword;

    @Value("${broker.clientid}")
    private String brokerClientId;

    @Autowired
    public SensorService(SensorRepository sensorRepository, FcmService fcmService) {
        this.sensorRepository = sensorRepository;
        this.fcmService = fcmService;
    }

    public Map<String, Object> checkId(Sensor sensor) {
        LocalDateTime currentTime = LocalDateTime.now();
        sensor.setLogtime(currentTime);

        boolean result = sensorRepository.checkId(sensor);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", result);
        responseBody.put("entry", false);


        if (sensor.getLatitude() == null)
            sensor.setLatitude(37.340523);
        if (sensor.getLongitude() == null)
            sensor.setLongitude(126.734424);

        // 만약 등록되어 있다면
        if (result) {
            sensorRepository.updateTime(sensor);
            responseBody.put("description", "이미 등록된 기기입니다.");
        } else if (sensorRepository.addId(sensor)) {
            responseBody.put("description", "등록을 성공했습니다.");
            responseBody.put("entry", true);
        } else {
            responseBody.put("description", "등록을 실패했습니다.");
        }

        return responseBody;
    }

    public Map<String, Object> addLog(Sensorlog sensorlog) {
        LocalDateTime currentTime = LocalDateTime.now();
        sensorlog.setLogtime(currentTime);
        String id = sensorlog.getId();

        try {
            Integer warning = sensorlog.getWarning();
            if (warning != 0) {
                // 센서 id를 통해 일치하는 유저의 토큰을 찾아낸다.
                List<String> tokenList = sensorRepository.findToken(id);

                // userSensor의 state를 모두 1로 변환 (알림 확인하지 않은 상태로 변환)
                boolean stateCheck =  sensorRepository.updateUserSensorState(id);

                if (warning == 1) {
                    for (String token : tokenList) {
                        fcmService.sendMessageByToken("사람 감지", "사람이 감지되었습니다", token, id);
                    }
                } else {
                    for (String token : tokenList) {
                        fcmService.sendMessageByToken("알림 감지", "알림이 감지되었습니다", token, id);
                    }
                }
            }

            boolean result = sensorRepository.AddLog(sensorlog);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("result", result);
            if (result) {
                responseBody.put("description", "로그 등록을 성공했습니다.");
            } else {
                responseBody.put("description", "로그 등록을 실패했습니다.");
            }
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("result", false);
            responseBody.put("description", "오류가 발생했습니다.");
            return responseBody;
        }
    }

    public List<Sensor> searchSensorList() {
        return sensorRepository.searchSensorList();
    }

    public Map<String, Object> selectSensor(Usersensor userSensor) {
        LocalDateTime currentTime = LocalDateTime.now();
        userSensor.setCodetime(currentTime);

        Map<String, Object> responseBody = new HashMap<>();
        // 기존에 이미 존재하는 지 확인하기
        boolean checkUserSensor = sensorRepository.checkUserSensor(userSensor);

        // 인증번호 생성
        String code = createCode();
        userSensor.setCode(code);

        // 만약 존재한다면 인증번호만 업데이트만 하기
        if (checkUserSensor) {
            responseBody.put("result", false);
            if (sensorRepository.updateUserSensor(userSensor)) {
                responseBody.put("description", "DB에 저장된 값으로 인증번호만 수정");
            } else {
                responseBody.put("description", "DB에 저장된 값이며 인증번호 수정 실패");
            }
        }
        // DB에 저장 (사용자 id + 단말기 번호 + 인증번호 + 이름(초기는 단말기 번호와 일치)
        else {
            userSensor.setState(0);
            userSensor.setName(userSensor.getSensorid());
            boolean result = sensorRepository.addUserSensor(userSensor);
            responseBody.put("result", result);
            responseBody.put("description", "DB에 저장되지 않은 값으로 새로 저장");
        }

        // 라즈베리파이에 전송
        Map<String, Object> jsonCode = new HashMap<>();
        jsonCode.put("id", userSensor.getUserid());
        jsonCode.put("code", code);
        sendMessage("code/" + userSensor.getSensorid(), jsonCode);

        return responseBody;
    }

    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void sendMessage(String topic, Object data) {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient mqttClient = new MqttClient(brokerAddress, brokerClientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(brokerUsername);
            connOpts.setPassword(brokerPassword.toCharArray());
            mqttClient.connect(connOpts);

            // JSON 데이터를 문자열로 변환하여 MQTT 메시지로 설정
            String jsonData = data.toString();
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(jsonData.getBytes());

            mqttClient.publish(topic, mqttMessage);
            mqttClient.disconnect();
        } catch (MqttException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }

    public Map<String, Object> checkCode(Usersensor userSensor) {
        Map<String, Object> responseBody = new HashMap<>();

        boolean codeCheck = sensorRepository.checkCode(userSensor);
        boolean timeCheck = sensorRepository.checkCodeTime(userSensor);
        responseBody.put("code",codeCheck);
        responseBody.put("time",timeCheck);

        if (codeCheck) {
            if (timeCheck) {
                boolean result = sensorRepository.updateCheckCode(userSensor);
                responseBody.put("result", result);
                if (result) {
                    responseBody.put("description", "코드 인증 성공");
                } else {
                    responseBody.put("description", "코드 인증 성공하였으나 오류 발생으로 서버에 반영되지 않음");
                }
            } else {
                responseBody.put("result", false);
                responseBody.put("description", "코드는 맞으나 시간이 만료됨");
            }
        } else {
            if (timeCheck) {
                responseBody.put("result", false);
                responseBody.put("description", "코드 인증 실패");
            } else {
                responseBody.put("result", false);
                responseBody.put("description", "코드 인증 실패에 시간이 만료됨");
            }
        }
        return responseBody;
    }

    public List<Sensorlog> requestLog(Sensorlog sensorlog) {
        // 라즈베리파이에 보내달라고 요청
        Map<String, Object> jsonCode = new HashMap<>();
        jsonCode.put("id", sensorlog.getId());
        sendMessage("log" + sensorlog.getId(), jsonCode);

        // 기다림 (3초)
        // try {
        // Thread.sleep(3000);
        // } catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        //}
        // 로그 가져오기
        return sensorRepository.sensorLog(sensorlog);
    }

    // 가장 마지막에 확인한 로그 가져오기
    public List<Sensorlog> requestLog1(Sensorlog sensorlog) {
        return sensorRepository.sensorLog1(sensorlog);
    }

    public List<Usersensor> searchSensorUserList(Usersensor usersensor) {
        return sensorRepository.searchSensorUserList(usersensor.getUserid());
    }

    public Map<String, Object> readState(Usersensor usersensor) {
        Map<String, Object> responseBody = new HashMap<>();
        boolean result = sensorRepository.readUserSensorState(usersensor.getUserid(), usersensor.getSensorid());
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "수정에 성공했습니다.");
        } else {
            responseBody.put("description", "수정에 실패했습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> deleteUserSensor(Usersensor usersensor) {
        Map<String, Object> responseBody = new HashMap<>();
        boolean result = sensorRepository.deleteUserSensor(usersensor.getUserid(), usersensor.getSensorid());
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "삭제에 성공했습니다.");
        } else {
            responseBody.put("description", "삭제에 실패했습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> renameUserSensor(Usersensor usersensor) {
        Map<String, Object> responseBody = new HashMap<>();
        boolean result = sensorRepository.renameUserSensor(usersensor.getUserid(), usersensor.getSensorid(), usersensor.getName());
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "이름 변경에 성공했습니다.");
        } else {
            responseBody.put("description", "이름 변경에 실패했습니다.");
        }
        return responseBody;
    }

    // DB에 있는 위치 정보를 가져와야 됨
    public List<Sensor> locateSensor(Sensor sensor) {
        return sensorRepository.locateSensor(sensor.getId());
    }

    public Map<String, Object> addChartSensor(Sensor sensor) {
        Map<String, Object> responseBody = new HashMap<>();
        boolean result = sensorRepository.addChartSensor(sensor.getId(), sensor.getLeftdata(), sensor.getRightdata());
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "데이터 추가에 성공했습니다.");
        } else {
            responseBody.put("description", "데이터 추가에 실패했습니다.");
        }
        return responseBody;
    }

    public Sensor getChartSensor(Sensor sensor) {
        return sensorRepository.getChartSensor(sensor.getId());
    }

    public List<Usersensor> getRecheckSensor(Usersensor usersensor) {
        return sensorRepository.getRecheckSensor(usersensor.getUserid());
    }

    public Map<String, Object> addRecheckSensor(Usersensor usersensor) {
        // 센서 아이디와 유저 아이디를 넣으면 연결시켜줘야 됨
        Map<String, Object> responseBody = new HashMap<>();
        boolean result = sensorRepository.addRecheckSensor(usersensor.getUserid(), usersensor.getSensorid());
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "연결에 성공했습니다.");
        } else {
            responseBody.put("description", "연결에 실패했습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> openWindow(Usersensor usersensor) {
        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> jsonCode = new HashMap<>();
        jsonCode.put("id", usersensor.getUserid());
        jsonCode.put("request", "open");
        sendMessage("window/" + usersensor.getSensorid(), jsonCode);
        responseBody.put("result", true);
        return responseBody;
    }

    public Map<String, Object> closeWindow(Usersensor usersensor) {
        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> jsonCode = new HashMap<>();
        jsonCode.put("id", usersensor.getUserid());
        jsonCode.put("request", "close");
        sendMessage("window/" + usersensor.getSensorid(), jsonCode);
        responseBody.put("result", true);
        return responseBody;
    }
}

