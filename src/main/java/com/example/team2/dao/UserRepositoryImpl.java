package com.example.team2.dao;

import com.example.team2.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserRepositoryImpl implements UserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean checkId(String id) {
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.id = :id");
        query.setParameter("id", id);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public boolean registerUser(User user) {
        try {
            entityManager.persist(user);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean loginUser(User user) {
        String sql = "SELECT COUNT(*) FROM User u WHERE u.id = :id AND u.pw = :pw";
        Query query = entityManager.createQuery(sql)
                .setParameter("id",user.getId())
                .setParameter("pw",user.getPw());
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public List<Integer> checkLogin(String id) {
        String sql = "SELECT u.emailcheck, u.phonecheck FROM User u WHERE u.id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public User loginUserInfo(String id) {
        String sql = "SELECT u.id, u.name, u.email, u.phone FROM User u WHERE u.id = :id";
        Query query = entityManager.createQuery(sql, User.class)
                .setParameter("id", id);
        return (User) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean loginEmailCheckCode(String id, String code) {
        String sql = "UPDATE User SET emailkey = :code WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id)
                .setParameter("code", code);
        return query.executeUpdate() > 0;
    }

    @Override
    public String findSendEmail(String id) {
        String sql = "SELECT u.email FROM User u WHERE u.id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return (String) query.getSingleResult();
    }

    @Override
    public boolean loginEmailCheckKey(String id, String emailKey) {
        String sql = "SELECT COUNT(*) FROM User u WHERE u.id = :id AND u.emailkey = :emailKey";
        Query query = entityManager.createQuery(sql)
                .setParameter("id",id)
                .setParameter("emailKey",emailKey);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public boolean loginEmailCheckState(String id) {
        String sql = "UPDATE User SET emailcheck = 1 WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean loginPhoneCheckCode(String id, String code) {
        String sql = "UPDATE User SET phonekey = :code WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id)
                .setParameter("code", code);
        return query.executeUpdate() > 0;
    }

    @Override
    public String findSendPhone(String id) {
        String sql = "SELECT u.phone FROM User u WHERE u.id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return (String) query.getSingleResult();
    }

    @Override
    public boolean loginPhoneCheckKey(String id, String phoneKey) {
        String sql = "SELECT COUNT(*) FROM User u WHERE u.id = :id AND u.phonekey = :phoneKey";
        Query query = entityManager.createQuery(sql)
                .setParameter("id",id)
                .setParameter("phoneKey",phoneKey);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public boolean loginPhoneCheckState(String id) {
        String sql = "UPDATE User SET phonecheck = 1 WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.executeUpdate() > 0;
    }
}
