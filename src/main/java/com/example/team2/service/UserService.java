package com.example.team2.service;

import com.example.team2.dao.UserRepository;
import com.example.team2.entity.User;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    @Value("${coolsms.api.key}")
    private String apiKey;
    @Value("${coolsms.api.secret}")
    private String apiSecret;
    @Value("${coolsms.phone}")
    private String sendPhoneNum;

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final DefaultMessageService messageService;

    @Autowired
    public UserService(UserRepository userRepository, JavaMailSender javaMailSender, @Value("${coolsms.api.key}") String apiKey, @Value("${coolsms.api.secret}") String apiSecret) {
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public Map<String, Object> checkId(User user) {
        String id = user.getId();
        boolean result = userRepository.checkId(id);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "중복된 아이디입니다.");
        } else {
            responseBody.put("description", "사용가능한 아이디입니다.");
        }
        return responseBody;
    }

    public Map<String, Object> checkEmail(User user) {
        String email = user.getEmail();
        boolean result = userRepository.checkEmail(email);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "중복된 이메일입니다.");
        } else {
            responseBody.put("description", "사용가능한 이메일입니다.");
        }
        return responseBody;
    }

    public Map<String, Object> checkPhone(User user) {
        String phone = user.getPhone();
        boolean result = userRepository.checkPhone(phone);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "중복된 전화번호입니다.");
        } else {
            responseBody.put("description", "사용가능한 전화번호입니다.");
        }
        return responseBody;
    }

    public Map<String, Object> registerUser(User user) {
        user.setEmailcheck(1);
        user.setPhonecheck(1);
        boolean result = userRepository.registerUser(user);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "유저정보 등록에 성공하였습니다.");
        } else {
            responseBody.put("description", "유저정보 등록에 실패하였습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> loginUser(User user) {
        String id = user.getId();
        String token = user.getToken();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", false);

        // 아이디와 비밀번호가 일치하여 로그인 완료 (true)
        boolean state = userRepository.loginUser(user);
        if (state) {
            // 이메일 휴대폰 인증 되었는지 확인
            List<Object[]> checkLoginList = userRepository.checkLogin(id);
            Object[] row = checkLoginList.get(0);
            Integer emailCheck = (Integer) row[0];
            Integer phoneCheck = (Integer) row[1];

            responseBody.put("check", true);
            responseBody.put("emailcheck", emailCheck);
            responseBody.put("phonecheck", phoneCheck);

            if ((emailCheck == 1) && (phoneCheck == 1)) {
                responseBody.put("emailcheck", true);
                responseBody.put("phonecheck", true);
                if (userRepository.updateToken(id, token)) { // 로그인 성공 -> 토큰 저장
                    responseBody.put("token", true);
                    responseBody.put("description", "로그인에 성공하여 토큰이 저장되었습니다.");
                } else {
                    responseBody.put("description", "로그인에 성공하여 토큰이 저장되지 못했습니다.");
                }
            } else if ((emailCheck == 1) && (phoneCheck == 0)) {
                responseBody.put("emailcheck", true);
                responseBody.put("phonecheck", false);
                responseBody.put("description", "휴대폰 인증이 필요합니다.");
            } else if ((emailCheck == 0) && (phoneCheck == 1)) {
                responseBody.put("emailcheck", false);
                responseBody.put("phonecheck", true);
                responseBody.put("description", "이메일 인증이 필요합니다.");
            } else {
                responseBody.put("emailcheck", false);
                responseBody.put("phonecheck", false);
                responseBody.put("description", "이메일 인증과 휴대폰 인증이 필요합니다.");
            }
        } else {
            responseBody.put("check", false);
            responseBody.put("emailcheck", false);
            responseBody.put("phonecheck", false);
            responseBody.put("description", "아이디와 비밀번호가 일치하지 않습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> logoutUser(User user) {
        Map<String, Object> responseBody = new HashMap<>();
        boolean result = userRepository.logoutUser(user.getId());
        responseBody.put("result", result);
        if (result) {
            responseBody.put("description", "로그아웃에 성공했습니다.");
        } else {
            responseBody.put("description", "로그아웃에 실패했습니다.");
        }
        return responseBody;
    }

    public User loginUserInfo(User user) {
        return userRepository.loginUserInfo(user.getId());
    }

    public Map<String, Object> loginEmailCheck(User user) {
        String id = user.getId();
        Map<String, Object> responseBody = new HashMap<>();

        // 인증코드 생성
        String code = createCode();

        // 생성한 것 DB에 넣기
        responseBody.put("result", userRepository.loginEmailCheckCode(id, code));

        // 보낼 이메일 주소 찾기
        String sendEmail = userRepository.findSendEmail(id);
        responseBody.put("email", sendEmail);

        // 생성한 것으로 메일 보내기
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendEmail);
        message.setSubject("이메일 인증을 위한 코드입니다.");
        message.setText(code);
        javaMailSender.send(message);

        return responseBody;
    }

    public Map<String, Object> loginEmailCheckKey(User user) {
        String id = user.getId();
        String emailKey = user.getEmailkey();
        Map<String, Object> responseBody = new HashMap<>();

        // 이메일 인증 여부
        boolean codeCheck = userRepository.loginEmailCheckKey(id, emailKey);
        responseBody.put("code", codeCheck);

        // 제한 시간 내 인증 여부
        boolean timeCheck = userRepository.loginEmailCheckTime(id);
        responseBody.put("time", timeCheck);

        if (codeCheck) {
            if (timeCheck) {
                boolean result = userRepository.loginEmailCheckState(id);
                responseBody.put("result", result);
                if (result) {
                    responseBody.put("description", "코드 인증 성공");
                } else {
                    responseBody.put("description", "코드 인증 성공하였으나 오류 발생으로 서버에 반영되지 않음");
                }
            } else {
                responseBody.put("result", false);
                responseBody.put("description", "코드는 맞으나 시간이 만료됨");
            }
        } else {
            if (timeCheck) {
                responseBody.put("result", false);
                responseBody.put("description", "코드 인증 실패");
            } else {
                responseBody.put("result", false);
                responseBody.put("description", "코드 인증 실패에 시간이 만료됨");
            }
        }
        return responseBody;
    }

    public Map<String, Object> loginPhoneCheck(User user) {
        String id = user.getId();
        Map<String, Object> responseBody = new HashMap<>();

        // 인증코드 생성
        String code = createCode();

        // 생성한 것 DB에 넣기
        responseBody.put("result", userRepository.loginPhoneCheckCode(id, code));

        // 보낼 문자 주소 찾기
        String sendPhone = userRepository.findSendPhone(id);
        responseBody.put("phone", sendPhone);

        // 문자 보내기
        Message message = new Message();
        message.setFrom(sendPhoneNum);
        message.setTo(sendPhone);
        message.setText(code);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);
        return responseBody;
    }

    public Map<String, Object> loginPhoneCheckKey(User user) {
        String id = user.getId();
        String phoneKey = user.getPhonekey();
        Map<String, Object> responseBody = new HashMap<>();

        // 이메일 인증 여부
        boolean codeCheck = userRepository.loginPhoneCheckKey(id, phoneKey);
        responseBody.put("code", codeCheck);

        // 제한 시간 내 인증 여부
        boolean timeCheck = userRepository.loginPhoneCheckTime(id);
        responseBody.put("time", timeCheck);

        if (codeCheck) {
            if (timeCheck) {
                boolean result = userRepository.loginPhoneCheckState(id);
                responseBody.put("result", result);
                if (result) {
                    responseBody.put("description", "코드 인증 성공");
                } else {
                    responseBody.put("description", "코드 인증 성공하였으나 오류 발생으로 서버에 반영되지 않음");
                }
            } else {
                responseBody.put("result", false);
                responseBody.put("description", "코드는 맞으나 시간이 만료됨");
            }
        } else {
            if (timeCheck) {
                responseBody.put("result", false);
                responseBody.put("description", "코드 인증 실패");
            } else {
                responseBody.put("result", false);
                responseBody.put("description", "코드 인증 실패에 시간이 만료됨");
            }
        }
        return responseBody;
    }

    public Map<String, Object> findIdEmail(User user) {
        String name = user.getName();
        String email = user.getEmail();
        Map<String, Object> responseBody = new HashMap<>();

        // 자격 확인 (이름과 이메일 일치하는지)
        boolean result = userRepository.equalFindIdEmail(name, email);
        responseBody.put("result", result);

        if (result) {
            // 아이디 조회 결과
            String id = userRepository.findIdbyEmail(email);

            // 이메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("아이디 조회");
            message.setText(id);
            javaMailSender.send(message);
            responseBody.put("id", id);
        } else {
            responseBody.put("description", "입력하신 정보가 올바르지 않습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> findIdPhone(User user) {
        String name = user.getName();
        String phone = user.getPhone();
        Map<String, Object> responseBody = new HashMap<>();

        // 자격 확인 (이름과 전화번호 일치하는지)
        boolean result = userRepository.equalFindIdPhone(name, phone);
        responseBody.put("result", result);

        if (result) {
            // 아이디 조회 결과
            String id = userRepository.findIdbyPhone(phone);

            // 문자 전송
            Message message = new Message();
            message.setFrom(sendPhoneNum);
            message.setTo(phone);
            message.setText(id);
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
            responseBody.put("id", id);
        } else {
            responseBody.put("description", "입력하신 정보가 올바르지 않습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> findPwEmail(User user) {
        String id = user.getId();
        String email = user.getEmail();
        Map<String, Object> responseBody = new HashMap<>();

        // 자격 확인 (id와 이메일 일치하는지)
        boolean result = userRepository.equalFindPwEmail(id, email);
        responseBody.put("result", result);

        if (result) {
            // 임시 비밀번호 생성
            String tempPw = createCode();

            // DB에 임시 비밀번호 저장
            userRepository.saveTempPw(id, tempPw);

            // 이메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("임시 비밀번호");
            message.setText(tempPw);
            javaMailSender.send(message);
            responseBody.put("description", "임시 비밀번호를 전송했습니다.");
        } else {
            responseBody.put("description", "입력하신 정보가 올바르지 않습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> findPwPhone(User user) {
        String id = user.getId();
        String phone = user.getPhone();
        Map<String, Object> responseBody = new HashMap<>();

        // 자격 확인 (id와 전화번호 일치하는지)
        boolean result = userRepository.equalFindPwPhone(id, phone);
        responseBody.put("result", result);

        if (result) {
            // 임시 비밀번호 생성
            String tempPw = createCode();

            // DB에 임시 비밀번호 저장
            userRepository.saveTempPw(id, tempPw);

            // 문자 전송
            Message message = new Message();
            message.setFrom(sendPhoneNum);
            message.setTo(phone);
            message.setText(tempPw);
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
            responseBody.put("description", "임시 비밀번호를 전송했습니다.");
        } else {
            responseBody.put("description", "입력하신 정보가 올바르지 않습니다.");
        }
        return responseBody;
    }

    public Map<String, Object> updateUserProfile(User user) {
        Map<String, Object> responseBody = new HashMap<>();

        // 이메일과 아이디를 가져옴
        List<Object[]> checkUpdageList = userRepository.checkUpdateUserProfile(user.getId());
        Object[] row = checkUpdageList.get(0);
        String preEmail = (String) row[0];
        String prePhone = (String) row[1];
        Integer checkEmail = (Integer) row[2];
        Integer checkPhone = (Integer) row[3];

        String id = user.getId();

        // 이메일 인증 했으나 변경됨 -> 이메일 재인증 (0)
        if ((!preEmail.equals(user.getEmail())) && (checkEmail == 1) && userRepository.updateCheckEmail0(id)) {
            responseBody.put("email", true);
        } else {
            // 이메일 인증 상태 변경 실패
            responseBody.put("email", false);
        }

        // 문자 인증 했으나 변경됨 -> 문자 재인증 (0)
        if ((!prePhone.equals(user.getPhone())) && (checkPhone == 1) && userRepository.updateCheckPhone0(id)) {
            responseBody.put("phone", true);
        } else {
            // 문자 인증 상태 변경 실패
            responseBody.put("phone", false);
        }

        boolean result = userRepository.updateUserProfile(user);
        responseBody.put("result", result);

        // 회원 정보 수정
        if (result) {
            responseBody.put("description", "회원 정보 수정 성공");
        } else {
            responseBody.put("description", "회원 정보 수정 실패");
        }
        return responseBody;
    }

    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}