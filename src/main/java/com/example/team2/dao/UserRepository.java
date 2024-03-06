package com.example.team2.dao;

import com.example.team2.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {
    boolean checkId(String id);
    boolean checkEmail(String email);
    boolean checkPhone(String phone);
    boolean registerUser(User user);
    boolean loginUser(User user);
    List checkLogin(String id);
    User loginUserInfo(String id);
    boolean loginEmailCheckCode(String id, String code);
    String findSendEmail(String id);
    boolean loginEmailCheckKey(String id, String emailKey);
    boolean loginEmailCheckTime(String id);
    boolean loginEmailCheckState(String id);
    boolean loginPhoneCheckCode(String id, String code);
    String findSendPhone(String id);
    boolean loginPhoneCheckKey(String id, String phoneKey);
    boolean loginPhoneCheckTime(String id);
    boolean loginPhoneCheckState(String id);
    boolean equalFindIdEmail(String name, String email);
    String findIdbyEmail(String email);
    boolean equalFindIdPhone(String name, String phone);
    String findIdbyPhone(String phone);
    boolean equalFindPwEmail(String id, String email);
    boolean equalFindPwPhone(String id, String phone);
    boolean saveTempPw(String id, String tempPw);
    List checkUpdateUserProfile(String id);
    boolean updateCheckEmail0(String id);
    boolean updateCheckPhone0(String id);
    boolean updateUserProfile(User user);
}