package com.example.team2.service;

import com.example.team2.dao.SensorRepository;
import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public Map<String, Object> checkId(Sensor sensor) {
        boolean result = sensorRepository.checkId(sensor);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", result);
        responseBody.put("entry", false);

        // 만약 등록되어 있다면
        if (result) {
            responseBody.put("description", "이미 등록된 기기입니다.");
        } else if (sensorRepository.addId(sensor)) {
            responseBody.put("description", "등록을 성공했습니다.");
            responseBody.put("result", true);
        } else {
            responseBody.put("description", "등록을 실패했습니다.");
        }

        return responseBody;
    }

    public Map<String, Object> detectLog(Sensorlog sensorlog) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            sensorlog.setLogtime(currentTime);

            boolean result = sensorRepository.AddDetectLog(sensorlog);

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
}
