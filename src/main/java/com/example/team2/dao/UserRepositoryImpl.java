package com.example.team2.dao;

import com.example.team2.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public boolean checkEmail(String email) {
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email");
        query.setParameter("email", email);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public boolean checkPhone(String phone) {
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.phone = :phone");
        query.setParameter("phone", phone);
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
    @Transactional
    public boolean logoutUser(String id) {
        String sql = "UPDATE User u SET u.token = null WHERE u.id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.executeUpdate() > 0;
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
        String sql = "UPDATE User u SET u.emailkey = :code, u.emailtime = current_timestamp WHERE u.id = :id";
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
    public boolean loginEmailCheckTime(String id) {
        LocalDateTime threeMinutesAgo = LocalDateTime.now().minusMinutes(3);
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.id = :id AND u.emailtime >= :threeMinutesAgo");
        query.setParameter("id",id);
        query.setParameter("threeMinutesAgo",threeMinutesAgo);
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
        String sql = "UPDATE User u SET u.phonekey = :code, u.phonetime = current_timestamp WHERE u.id = :id";
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
    public boolean loginPhoneCheckTime(String id) {
        LocalDateTime threeMinutesAgo = LocalDateTime.now().minusMinutes(3);
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.id = :id AND u.phonetime >= :threeMinutesAgo");
        query.setParameter("id",id);
        query.setParameter("threeMinutesAgo",threeMinutesAgo);
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

    @Override
    public boolean equalFindIdEmail(String name, String email) {
        String sql = "SELECT COUNT(*) FROM User u WHERE u.name = :name AND u.email = :email";
        Query query = entityManager.createQuery(sql)
                .setParameter("name",name)
                .setParameter("email",email);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public String findIdbyEmail(String email) {
        String sql = "SELECT u.id FROM User u WHERE u.email = :email";
        Query query = entityManager.createQuery(sql)
                .setParameter("email", email);
        return (String) query.getSingleResult();
    }

    @Override
    public boolean equalFindIdPhone(String name, String phone) {
        String sql = "SELECT COUNT(*) FROM User u WHERE u.name = :name AND u.phone = :phone";
        Query query = entityManager.createQuery(sql)
                .setParameter("name",name)
                .setParameter("phone",phone);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public String findIdbyPhone(String phone) {
        String sql = "SELECT u.id FROM User u WHERE u.phone = :phone";
        Query query = entityManager.createQuery(sql)
                .setParameter("phone", phone);
        return (String) query.getSingleResult();
    }

    @Override
    public boolean equalFindPwEmail(String id, String email) {
        String sql = "SELECT COUNT(*) FROM User u WHERE u.id = :id AND u.email = :email";
        Query query = entityManager.createQuery(sql)
                .setParameter("id",id)
                .setParameter("email",email);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public boolean equalFindPwPhone(String id, String phone) {
        String sql = "SELECT COUNT(*) FROM User u WHERE u.id = :id AND u.phone = :phone";
        Query query = entityManager.createQuery(sql)
                .setParameter("id",id)
                .setParameter("phone",phone);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public boolean saveTempPw(String id, String tempPw) {
        String sql = "UPDATE User SET pw = :tempPw WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id)
                .setParameter("tempPw",tempPw);
        return query.executeUpdate() > 0;
    }

    @Override
    public List<?> checkUpdateUserProfile(String id) {
        String sql = "SELECT u.email, u.phone, u.emailcheck, u.phonecheck FROM User u WHERE u.id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean updateCheckEmail0(String id) {
        String sql = "UPDATE User SET emailcheck = 0 WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateCheckPhone0(String id) {
        String sql = "UPDATE User SET phonecheck = 0 WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE User u SET u.pw =:pw, u.name =:name, u.email =:email, u.phone =:phone WHERE id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", user.getId())
                .setParameter("pw",user.getPw())
                .setParameter("name",user.getName())
                .setParameter("email",user.getEmail())
                .setParameter("phone",user.getPhone());
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateToken(String id, String token) {
        String sql = "UPDATE User u SET u.token =:token WHERE u.id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id)
                .setParameter("token", token);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean deleteLoginInfo(String token) {
        String sql = "UPDATE User u SET u.token = null WHERE u.token = :token";
        Query query = entityManager.createQuery(sql)
                .setParameter("token", token);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean deleteUser(String id) {
        String sql = "DELETE FROM User u WHERE u.id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean changePw(String id, String pw) {
        String sql = "UPDATE User u SET u.pw = :pw WHERE u.id = :id";
        Query query = entityManager.createQuery(sql)
                .setParameter("id", id)
                .setParameter("pw", pw);
        return query.executeUpdate() > 0;
    }
}
