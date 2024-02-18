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
import java.util.List;
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

    public boolean checkId(String id) {
        return userRepository.checkId(id);
    }

    public boolean checkEmail(String email) {
        return userRepository.checkId(email);
    }

    public boolean checkPhone(String phone) {
        return userRepository.checkId(phone);
    }

    public boolean registerUser(User user) {
        return userRepository.registerUser(user);
    }

    public String loginUser(User user) {
        // 아이디와 비밀번호가 일치하여 로그인 완료 (true)
        boolean state = userRepository.loginUser(user);
        if (state == true) {

            // 이메일 휴대폰 인증 되었는지 확인
            List<Object[]> checkLoginList = userRepository.checkLogin(user.getId());
            Object[] row = checkLoginList.get(0);
            Integer emailCheck = (Integer) row[0];
            Integer phoneCheck = (Integer) row[1];

            if ((emailCheck == 1) && (phoneCheck == 1)) {
                return "로그인 성공";
            } else if ((emailCheck == 1) && (phoneCheck == 0)) {
                return "휴대폰 인증이 필요합니다.";
            } else if ((emailCheck == 0) && (phoneCheck == 1)) {
                return "이메일 인증이 필요합니다.";
            } else {
                return "이메일 인증과 휴대폰 인증이 필요합니다.";
            }
        } else {
            return "아이디와 비밀번호가 일치하지 않습니다.";
        }
    }

    public User loginUserInfo(String id) {
        return userRepository.loginUserInfo(id);
    }

    public String loginEmailCheck(String id) {
        // 인증코드 생성
        String code = createCode();

        // 생성한 것 DB에 넣기
        userRepository.loginEmailCheckCode(id, code);

        // 보낼 이메일 주소 찾기
        String sendEmail = userRepository.findSendEmail(id);

        // 생성한 것으로 메일 보내기
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendEmail);
        message.setSubject("이메일 인증을 위한 코드입니다.");
        message.setText(code);
        javaMailSender.send(message);

        return sendEmail + "로 코드를 전송했습니다.";
    }

    public boolean loginEmailCheckKey(String id, String emailKey) {
        boolean result = userRepository.loginEmailCheckKey(id, emailKey);
        // 이메일 인증 성공 시 상태 변환
        if (result == true) {
            return userRepository.loginEmailCheckState(id);
        }
        return false;
    }

    public String loginPhoneCheck(String id) {
        // 인증코드 생성
        String code = createCode();

        // 생성한 것 DB에 넣기
        userRepository.loginPhoneCheckCode(id, code);

        // 보낼 문자 주소 찾기
        String sendPhone = userRepository.findSendPhone(id);

        // 문자 보내기
        Message message = new Message();
        message.setFrom(sendPhoneNum);
        message.setTo(sendPhone);
        message.setText(code);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);
        return sendPhone + "로 코드를 전송했습니다.";
    }

    public boolean loginPhoneCheckKey(String id, String PhoneKey) {
        boolean result = userRepository.loginPhoneCheckKey(id, PhoneKey);
        // 문자 인증 성공 시 상태 변환
        if (result == true) {
            return userRepository.loginPhoneCheckState(id);
        }
        return false;
    }

    public String findIdEmail(String name, String email) {
        // 자격 확인 (이름과 이메일 일치하는지)
        boolean state = userRepository.equalFindIdEmail(name, email);

        if (state == true) {
            // 아이디 조회 결과
            String id = userRepository.findIdbyEmail(email);

            // 이메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("아이디 조회");
            message.setText(id);
            javaMailSender.send(message);

            return email + "로 id를 전송했습니다.";
        }
        return "입력하신 정보가 올바르지 않습니다.";
    }

    public String findIdPhone(String name, String phone) {
        // 자격 확인 (이름과 전화번호 일치하는지)
        boolean state = userRepository.equalFindIdPhone(name, phone);

        if (state == true) {
            // 아이디 조회 결과
            String id = userRepository.findIdbyPhone(phone);

            // 문자 전송
            Message message = new Message();
            message.setFrom(sendPhoneNum);
            message.setTo(phone);
            message.setText(id);
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);

            return phone + "로 id를 전송했습니다.";
        }
        return "입력하신 정보가 올바르지 않습니다.";
    }

    public String findPwEmail(String id, String email) {
        // 자격 확인 (id와 이메일 일치하는지)
        boolean state = userRepository.equalFindPwEmail(id, email);

        if (state == true) {
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

            return email + "로 임시 비밀번호를 전송했습니다.";
        }
        return "입력하신 정보가 올바르지 않습니다.";
    }

    public String findPwPhone(String id, String phone) {
        // 자격 확인 (id와 전화번호 일치하는지)
        boolean state = userRepository.equalFindPwPhone(id, phone);

        if (state == true) {
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

            return phone + "로 임시 비밀번호를 전송했습니다.";
        }
        return "입력하신 정보가 올바르지 않습니다.";
    }

    public String updateUserProfile(User user) {
        // 이메일과 아이디를 가져옴
        List<Object[]> checkUpdageList = userRepository.checkUpdateUserProfile(user.getId());
        Object[] row = checkUpdageList.get(0);
        String preEmail = (String) row[0];
        String prePhone = (String) row[1];
        Integer checkEmail = (Integer) row[2];
        Integer checkPhone = (Integer) row[3];

        String id = user.getId();

        // 이메일 인증 했으나 변경됨 -> 이메일 재인증 (0)
        if ((!preEmail.equals(user.getEmail())) && (checkEmail == 1) && !userRepository.updateCheckEmail0(id)) return "이메일 인증 상태 변경 실패";

        // 문자 인증 했으나 변경됨 -> 문자 재인증 (0)
        if ((!prePhone.equals(user.getPhone())) && (checkPhone == 1) && !userRepository.updateCheckPhone0(id)) return "문자 인증 상태 변경 실패";

        // 회원 정보 수정
        if (userRepository.updateUserProfile(user)) return "회원 정보 수정 성공";
        return "회원 정보 수정 실패";
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