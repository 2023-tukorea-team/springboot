package com.example.team2.dao;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import com.example.team2.entity.Usersensor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository {
    boolean checkId(Sensor sensor);
    boolean addId(Sensor sensor);
    boolean updateTime(Sensor sensor);
    boolean AddLog(Sensorlog sensorlog);
    List<Sensor> searchSensorList();
    boolean addUserSensor(Usersensor userSensor);
    boolean checkUserSensor(Usersensor userSensor);
    boolean updateUserSensor(Usersensor userSensor);
    boolean checkCode(Usersensor userSensor);
    boolean updateCheckCode(Usersensor userSensor);
    List<Sensorlog> sensorLog(Sensorlog sensorlog);
    boolean checkCodeTime(Usersensor userSensor);
}
