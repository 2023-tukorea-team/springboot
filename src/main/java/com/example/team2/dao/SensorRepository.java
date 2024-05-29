package com.example.team2.dao;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import com.example.team2.entity.Usersensor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    List<Sensorlog> sensorLog1(Sensorlog sensorlog);
    boolean checkCodeTime(Usersensor userSensor);
    List<String> findToken(String id);
    List<Usersensor> searchSensorUserList(String userid);
    boolean updateUserSensorState(String id);
    boolean readUserSensorState(String userid, String sensorid);
    boolean deleteUserSensor(String userid, String sensorid);
    boolean renameUserSensor(String userid, String sensorid, String name);
    List<Sensor> locateSensor(String id);
}
