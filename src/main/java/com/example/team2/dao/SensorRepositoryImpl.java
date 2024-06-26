package com.example.team2.dao;

import com.example.team2.entity.Sensor;
import com.example.team2.entity.Sensorlog;
import com.example.team2.entity.User;
import com.example.team2.entity.Usersensor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    public boolean updateTime(Sensor sensor) {
        String sql = "UPDATE Sensor s SET s.logtime = current_timestamp, s.latitude =: latitude, s.longitude =: longitude WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id",sensor.getId())
                .setParameter("latitude",sensor.getLatitude())
                .setParameter("longitude",sensor.getLongitude());
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
        String sql = "UPDATE Usersensor u SET u.code = :code, u.codetime = :codetime WHERE u.userid = :userid and u.sensorid = :sensorid";
        Query query = entityManager.createQuery(sql)
                .setParameter("code",userSensor.getCode())
                .setParameter("userid",userSensor.getUserid())
                .setParameter("sensorid",userSensor.getSensorid())
                .setParameter("codetime",userSensor.getCodetime());
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
    public List<Sensorlog> sensorLog1(Sensorlog sensorlog) {
        String sql = "SELECT s FROM Sensorlog s WHERE s.id = :id ORDER BY s.sid DESC limit 1" ;
        Query query = entityManager.createQuery(sql)
                .setParameter("id",sensorlog.getId());
        return query.getResultList();
    }

    @Override
    public boolean checkCodeTime(Usersensor userSensor) {
        LocalDateTime threeMinutesAgo = LocalDateTime.now().minusMinutes(3);
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

    @Override
    @Transactional
    public boolean updateUserSensorState(String id) {
        String sql = "UPDATE Usersensor u SET u.state = 1 WHERE u.sensorid =:id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean readUserSensorState(String userid, String sensorid) {
        String sql = "UPDATE Usersensor u SET u.state = 0 WHERE u.sensorid =:sensorid AND u.userid =:userid";
        Query query = entityManager.createQuery(sql)
                .setParameter("sensorid", sensorid)
                .setParameter("userid", userid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean deleteUserSensor(String userid, String sensorid) {
        String sql = "UPDATE Usersensor u SET u.direct = 0 WHERE u.sensorid =:sensorid AND u.userid =:userid";
        Query query = entityManager.createQuery(sql)
                .setParameter("sensorid", sensorid)
                .setParameter("userid", userid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean renameUserSensor(String userid, String sensorid, String name) {
        String sql = "UPDATE Usersensor u SET u.name =: name WHERE u.sensorid =:sensorid AND u.userid =:userid";
        Query query = entityManager.createQuery(sql)
                .setParameter("sensorid", sensorid)
                .setParameter("userid", userid)
                .setParameter("name", name);
        return query.executeUpdate() > 0;
    }

    @Override
    public List<Sensor> locateSensor(String id) {
        String sql = "SELECT s FROM Sensor s WHERE s.id =:id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id",id);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean addChartSensor(String id, String left, String right) {
        String sql = "UPDATE Sensor s SET s.leftdata =: left, s.rightdata =: right WHERE s.id =: id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id)
                .setParameter("left", left)
                .setParameter("right", right);
        return query.executeUpdate() > 0;
    }

    @Override
    public Sensor getChartSensor(String id) {
        String sql = "SELECT s FROM Sensor s WHERE s.id =: id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return (Sensor) query.getSingleResult();
    }

    @Override
    public List<Usersensor> getRecheckSensor(String userid) {
        String sql = "SELECT u FROM Usersensor u WHERE u.userid =:userid AND u.codecheck = 1 AND u.direct = 0";
        Query query = entityManager.createQuery(sql)
                .setParameter("userid",userid);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean addRecheckSensor(String userid, String sensorid) {
        String sql = "UPDATE Usersensor u SET u.direct = 1 WHERE u.userid =: userid AND u.sensorid =: sensorid";
        Query query = entityManager.createQuery(sql)
                .setParameter("userid", userid)
                .setParameter("sensorid", sensorid);
        return query.executeUpdate() > 0;
    }
}
