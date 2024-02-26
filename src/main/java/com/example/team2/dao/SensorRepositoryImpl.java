package com.example.team2.dao;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class SensorRepositoryImpl implements SensorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean checkId(Sensor sensor) {
        Query query = entityManager.createQuery("SELECT COUNT(s) FROM Sensor s WHERE s.id = :id");
        query.setParameter("id", sensor.getId());
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public boolean addId(Sensor sensor) {
        try {
            entityManager.persist(sensor);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean AddDetectLog(Sensorlog sensorlog) {
        try {
            entityManager.persist(sensorlog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
