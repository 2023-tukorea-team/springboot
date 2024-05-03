package com.example.team2.rest;

import com.example.team2.entity.User;
import com.example.team2.service.FcmService;
import com.example.team2.service.UserService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
public class UserRestController {

    private final UserService userService;
    private final FcmService fcmService;

    @Autowired
    public UserRestController(UserService userService, FcmService fcmService) {
        this.userService = userService;
        this.fcmService = fcmService;
    }

    @GetMapping("/")
    public String serverStart() {
        return "서버 작동중입니다.";
    }

    // id -> bool (true: 중복있음, false: 중복없음)
    @PostMapping("/signup/checkid")
    public ResponseEntity<Map<String, Object>> checkId(@RequestBody User user) {
        return ResponseEntity.ok(userService.checkId(user));
    }

    @PostMapping("/signup/checkemail")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestBody User user) {
        return ResponseEntity.ok(userService.checkEmail(user));
    }

    @PostMapping("/signup/checkphone")
    public ResponseEntity<Map<String, Object>> checkPhone(@RequestBody User user) {
        return ResponseEntity.ok(userService.checkPhone(user));
    }

    // id, pw, name, email, phone -> bool (true: 생성완료)
    @PostMapping("/signup/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    // id, pw, token -> 로그인 성공 유무 + 추가적인 인증 필요 유무
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginUser(user));
    }

    // id -> 로그아웃 성공 유무
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.logoutUser(user));
    }

    // id -> User(id, name, email, phone)
    @PostMapping("/login/userinfo")
    public ResponseEntity<User> loginUserInfo(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginUserInfo(user));
    }

    // 로그인 되어 있는 유저의 아이디로 이메일 전달 요청을 받음
    // 유저의 아이디를 토대로 이메일을 찾아서 이메일 전송
    // id -> String
    @PostMapping("/login/emailcheck")
    public ResponseEntity<Map<String, Object>> loginEmailCheck(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginEmailCheck(user));
    }

    // DB안의 코드와 이메일에 전송된 코드가 일치하면 이메일 인증 성공
    // id, emailkey -> bool
    @PostMapping("/login/emailcheck/client")
    public ResponseEntity<Map<String, Object>> loginEmailCheckKey(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginEmailCheckKey(user));
    }

    // 문자 발송
    // id -> String
    @PostMapping("/login/phonecheck")
    public ResponseEntity<Map<String, Object>> loginPhoneCheck(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginPhoneCheck(user));
    }

    // DB안의 코드와 문자에 전송된 코드가 일치하면 문자 인증 성공
    // id, emailkey -> bool
    @PostMapping("/login/phonecheck/client")
    public ResponseEntity<Map<String, Object>> loginPhoneCheckKey(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginPhoneCheckKey(user));
    }

    // 아아디 찾기 (이메일로)
    @PostMapping("/find/id/email")
    public ResponseEntity<Map<String, Object>> findIdEmail(@RequestBody User user) {
        return ResponseEntity.ok(userService.findIdEmail(user));
    }

    // 아이디 찾기 (전화번호로)
    @PostMapping("/find/id/phone")
    public ResponseEntity<Map<String, Object>> findIdPhone(@RequestBody User user) {
        return ResponseEntity.ok(userService.findIdPhone(user));
    }

    // 비밀번호 찾기 (이메일로)
    @PostMapping("/find/pw/email")
    public ResponseEntity<Map<String, Object>> findPwEmail(@RequestBody User user) {
        return ResponseEntity.ok(userService.findPwEmail(user));
    }

    // 비밀번호 찾기 (전화번호로)
    @PostMapping("/find/pw/phone")
    public ResponseEntity<Map<String, Object>> findPwPhone(@RequestBody User user) {
        return ResponseEntity.ok(userService.findPwPhone(user));
    }

    // 개인정보 수정
    // 하기전에 /login을 통해 아이디와 비밀번호가 일치하는지 재확인
    // User 개인정보를 확인하는 것은 클라이언트쪽에서 /login/userinfo를 통해 가능함
    // 일치할 경우에만 개인정보 수정 가능하도록 하기
    // id, pw, name, email, phone 모두 입력할 수 있도록 (단, email, phone 변경시 변경된 항목은 재인증 필요함)
    @PostMapping("/user/profile/update")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUserProfile(user));
    }

    // 회원 탈퇴
    // id
    @PostMapping("/user/withdraw")
    public ResponseEntity<Map<String, Object>> withdrawUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.withdrawUser(user));
    }

    // 비밀번호 변경
    // id, pw (새로 바뀔 비밀번호)
    @PostMapping("/user/repw")
    public ResponseEntity<Map<String, Object>> changePw(@RequestBody User user) {
        return ResponseEntity.ok(userService.changePw(user));
    }

    @PostMapping("/message/fcm/token")
    public ResponseEntity sendMessageToken(@RequestBody User user) throws FirebaseMessagingException{
        fcmService.sendMessageByToken("알림 감지", "알림이 감지되었습니다", user.getToken(), "id");
        return ResponseEntity.ok().build();
    }
}
