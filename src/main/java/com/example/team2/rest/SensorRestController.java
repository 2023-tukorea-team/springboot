package com.example.team2.rest;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import com.example.team2.entity.Usersensor;
import com.example.team2.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SensorRestController {

    @Autowired
    private final SensorService sensorService;

    public SensorRestController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    // 기기를 DB에 등록 (라즈베리파이 일련 번호로 등록)
    // id -> bool (true는 존재함, false는 존재하지 않음)
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
    public ResponseEntity<Map<String, Object>> checkCode(@RequestBody Usersensor userSensor) {
        return ResponseEntity.ok(sensorService.checkCode(userSensor));
    }

    // 모바일 앱에서 요청 -> 단말에게 로그 전달해 달라고 하기
    // id (센서id)
    @PostMapping("/sensor/logrequest")
    public ResponseEntity<List<Sensorlog>> requestLog(@RequestBody Sensorlog sensorlog) {
        return ResponseEntity.ok(sensorService.requestLog(sensorlog));
    }
}