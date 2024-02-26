package com.example.team2.dao;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository {
    boolean checkId(Sensor sensor);
    boolean addId(Sensor sensor);
    boolean AddDetectLog(Sensorlog sensorlog);
}
