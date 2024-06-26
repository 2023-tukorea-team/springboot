package com.example.team2.rest;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import com.example.team2.entity.Usersensor;
import com.example.team2.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class SensorRestController {

    @Autowired
    private final SensorService sensorService;

    public SensorRestController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    // 기기를 DB에 등록 (라즈베리파이 일련 번호로 등록) - 좌표는 임시로 선택으로 입력하도록 지정 (입력 하지 않을 시 (0,0)으로 가정)
    // id, latitude, longitude -> bool (true는 존재함, false는 존재하지 않음)
    @PostMapping("/sensor/checkid")
    public ResponseEntity<Map<String, Object>> checkId(@RequestBody Sensor sensor) {
        return ResponseEntity.ok(sensorService.checkId(sensor));
    }

    // 서버로 로그 전송
    // id, start(int), door(int), person(int), spped(int)
    @PostMapping("/sensor/log")
    public ResponseEntity<Map<String, Object>> addLog(@RequestBody Sensorlog sensorlog) {
        return ResponseEntity.ok(sensorService.addLog(sensorlog));
    }

    // 사용자가 단말기 검색하기 (서버와 연결한지 5분 이내의 것만 검색하도록)
    // 입력값 없음
    @PostMapping("/sensor/list")
    public ResponseEntity<List<Sensor>> searchSensorList() {
        return ResponseEntity.ok(sensorService.searchSensorList());
    }

    // 사용자가 단말기를 선택
    // userid, sensorid
    @PostMapping("/sensor/select")
    public ResponseEntity<Map<String, Object>> selectSensor(@RequestBody Usersensor userSensor) {
        return ResponseEntity.ok(sensorService.selectSensor(userSensor));
    }

    // 코드 확인
    // userid, sensorid, code
    @PostMapping("/sensor/checkcode")
    public ResponseEntity<Map<String, Object>> checkCode(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.checkCode(usersensor));
    }

    // 모바일 앱에서 요청 -> 단말에게 로그 전달해 달라고 하기 (모든 로그 시간 역순으로 전달)
    // id (센서id)
    @PostMapping("/sensor/logrequest")
    public ResponseEntity<List<Sensorlog>> requestLog(@RequestBody Sensorlog sensorlog) {
        return ResponseEntity.ok(sensorService.requestLog(sensorlog));
    }

    @PostMapping("/sensor/logrequest1")
    public ResponseEntity<List<Sensorlog>> requestLog1(@RequestBody Sensorlog sensorlog) {
        return ResponseEntity.ok(sensorService.requestLog1(sensorlog));
    }

    // 사용자와 연결된 기기 확인
    // id ->
    @PostMapping("/usersensor/list")
    public ResponseEntity<List<Usersensor>> searchSensorUserList(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.searchSensorUserList(usersensor));
    }

    // 사용자가 알림을 확인했다면 느낌표 사라지게 하기
    // userid, sensorid
    @PostMapping("/usersensor/readstate")
    public ResponseEntity<Map<String, Object>> readState(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.readState(usersensor));
    }

    // 사용자가 차량 연결을 삭제한 경우
    // userid, sensorid
    @PostMapping("/usersensor/delete")
    public ResponseEntity<Map<String, Object>> deleteUserSensor(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.deleteUserSensor(usersensor));
    }

    // 사용자가 단말기의 이름을 변경하는 경우
    // userid, sensorid, name
    @PostMapping("/usersensor/rename")
    public ResponseEntity<Map<String, Object>> renameUserSensor(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.renameUserSensor(usersensor));
    }

    // DB에서 저장된 좌표를 가져옴
    // id -> latitude, longitude
    @PostMapping("/sensor/locate")
    public ResponseEntity<List<Sensor>> locateSensor(@RequestBody Sensor sensor) {
        return ResponseEntity.ok(sensorService.locateSensor(sensor));
    }

    // DB에 64개의 데이터 값 저장하기
    // id, leftdata, rightdata
    @PostMapping("/sensor/chart/add")
    public ResponseEntity<Map<String, Object>> addChartSensor(@RequestBody Sensor sensor) {
        return ResponseEntity.ok(sensorService.addChartSensor(sensor));
    }

    // DB에서 64개의 데이터 값 가져오기 (정확히는 Sensor의 모든 값 가져오기)
    // id, leftdata, rightdata
    @PostMapping("/sensor/chart/get")
    public ResponseEntity<Sensor> getChartSensor(@RequestBody Sensor sensor) {
        return ResponseEntity.ok(sensorService.getChartSensor(sensor));
    }

    // 이전에 연결했던 기기 가져오기
    // userid
    @PostMapping("/usersensor/recheck/get")
    public ResponseEntity<List<Usersensor>> getRecheckSensor(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.getRecheckSensor(usersensor));
    }

    // 이전에 연결했던 기기 인증번호 없이 연결하기
    // userid, sensorid
    @PostMapping("/usersensor/recheck/add")
    public ResponseEntity<Map<String, Object>> addRecheckSensor(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.addRecheckSensor(usersensor));
    }

    // 창문 열기
    // userid, sensorid
    @PostMapping("/usersensor/window/open")
    public ResponseEntity<Map<String, Object>> openWindow(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.openWindow(usersensor));
    }

    // 창문 닫기
    // userid, sensorid
    @PostMapping("/usersensor/window/close")
    public ResponseEntity<Map<String, Object>> closeWindow(@RequestBody Usersensor usersensor) {
        return ResponseEntity.ok(sensorService.closeWindow(usersensor));
    }
}