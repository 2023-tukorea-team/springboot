package com.example.team2.dao;

import com.example.team2.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {
    boolean checkId(String id);
    boolean registerUser(User user);
    boolean loginUser(User user);
    List checkLogin(String id);
    User loginUserInfo(String id);
    boolean loginEmailCheckCode(String id, String code);
    String findSendEmail(String id);
    boolean loginEmailCheckKey(String id, String emailKey);
    boolean loginEmailCheckState(String id);
    boolean loginPhoneCheckCode(String id, String code);
    String findSendPhone(String id);
    boolean loginPhoneCheckKey(String id, String phoneKey);
    boolean loginPhoneCheckState(String id);
}