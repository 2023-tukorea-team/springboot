package com.example.team2.rest;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import com.example.team2.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    // 감지 시 서버로 전송
    // id, option(문열림감지 1, 인체감지 2)
    @PostMapping("/sensor/detect")
    public ResponseEntity<Map<String, Object>> detectLog(@RequestBody Sensorlog sensorlog) {
        return ResponseEntity.ok(sensorService.detectLog(sensorlog));
    }
}
