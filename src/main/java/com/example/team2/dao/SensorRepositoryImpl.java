package com.example.team2.dao;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import com.example.team2.entity.Usersensor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

@Component
public class SensorRepositoryImpl implements SensorRepository {

    TimeZone seoulTimeZone = TimeZone.getTimeZone("Asia/Seoul");
    ZoneId seoulZoneId = seoulTimeZone.toZoneId();

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
    public boolean updateTime(Sensor sensor) {
        String sql = "UPDATE Sensor SET logtime = current_timestamp WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id",sensor.getId());
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean AddLog(Sensorlog sensorlog) {
        try {
            entityManager.persist(sensorlog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Sensor> searchSensorList() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        String sql = "SELECT s FROM Sensor s WHERE s.logtime >= :fiveMinutesAgo";
        Query query = entityManager.createQuery(sql);
        query.setParameter("fiveMinutesAgo", fiveMinutesAgo);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean addUserSensor(Usersensor userSensor) {
        try {
            entityManager.persist(userSensor);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean checkUserSensor(Usersensor userSensor) {
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM Usersensor u WHERE u.userid = :userid and u.sensorid =:sensorid");
        query.setParameter("userid", userSensor.getUserid());
        query.setParameter("sensorid",userSensor.getSensorid());
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public boolean updateUserSensor(Usersensor userSensor) {
        String sql = "UPDATE Usersensor u SET u.code = :code, u.codetime = current_timestamp WHERE u.userid = :userid and u.sensorid = :sensorid";
        Query query = entityManager.createQuery(sql)
                .setParameter("code",userSensor.getCode())
                .setParameter("userid",userSensor.getUserid())
                .setParameter("sensorid",userSensor.getSensorid());
        return query.executeUpdate() > 0;
    }

    @Override
    public boolean checkCode(Usersensor userSensor) {
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM Usersensor u WHERE u.userid = :userid and u.sensorid =:sensorid and u.code =:code");
        query.setParameter("userid", userSensor.getUserid());
        query.setParameter("sensorid",userSensor.getSensorid());
        query.setParameter("code",userSensor.getCode());
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public boolean updateCheckCode(Usersensor userSensor) {
        String sql = "UPDATE Usersensor u SET u.codecheck = 1, u.direct = 1 WHERE u.userid = :userid and u.sensorid = :sensorid and u.code =:code";
        Query query = entityManager.createQuery(sql)
                .setParameter("code",userSensor.getCode())
                .setParameter("userid",userSensor.getUserid())
                .setParameter("sensorid",userSensor.getSensorid());
        return query.executeUpdate() > 0;
    }

    @Override
    public List<Sensorlog> sensorLog(Sensorlog sensorlog) {
        String sql = "SELECT s FROM Sensorlog s WHERE s.id = :id ORDER BY s.sid DESC" ;
        Query query = entityManager.createQuery(sql)
                .setParameter("id",sensorlog.getId());
        return query.getResultList();
    }

    @Override
    public boolean checkCodeTime(Usersensor userSensor) {
        LocalDateTime threeMinutesAgo = LocalDateTime.now(seoulZoneId).minusMinutes(3);
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM Usersensor u WHERE u.userid = :userid and u.sensorid =:sensorid and u.codetime >= :threeMinutesAgo");
        query.setParameter("userid", userSensor.getUserid());
        query.setParameter("sensorid",userSensor.getSensorid());
        query.setParameter("threeMinutesAgo",threeMinutesAgo);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public List<String> findToken(String sensorid) {
        Query query = entityManager.createQuery( "SELECT u.token FROM Usersensor us JOIN User u ON us.userid = u.id " +
                "WHERE us.direct = 1 AND us.sensorid = :sensorid AND u.token IS NOT NULL");
        query.setParameter("sensorid", sensorid);
        List<String> stringList = query.getResultList();
        return stringList;
    }

    @Override
    public List<Usersensor> searchSensorUserList(String userid) {
        String sql = "SELECT u FROM Usersensor u WHERE u.userid =:userid AND u.direct = 1";
        Query query = entityManager.createQuery(sql)
                .setParameter("userid",userid);
        return query.getResultList();
    }
}
