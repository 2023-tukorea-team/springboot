package com.example.team2.rest;

import com.example.team2.entity.Sensorlog;
import com.example.team2.entity.User;
import com.example.team2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String serverStart() {
        return "서버 작동중입니다.";
    }

    // id -> bool (true: 중복있음, false: 중복없음)
    @PostMapping("/signup/checkid")
    public ResponseEntity<Boolean> checkId(@RequestBody User user) {
        String id = user.getId();
        return ResponseEntity.ok(userService.checkId(id));
    }

    @PostMapping("/signup/checkemail")
    public ResponseEntity<Boolean> checkEmail(@RequestBody User user) {
        String email = user.getEmail();
        return ResponseEntity.ok(userService.checkEmail(email));
    }

    @PostMapping("/signup/checkphone")
    public ResponseEntity<Boolean> checkPhone(@RequestBody User user) {
        String phone = user.getPhone();
        return ResponseEntity.ok(userService.checkPhone(phone));
    }

    // id, pw, name, email, phone -> bool (true: 생성완료)
    @PostMapping("/signup/register")
    public ResponseEntity<Boolean> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    // id, pw -> 로그인 성공 유무 + 추가적인 인증 필요 유무
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginUser(user));
    }

    // id -> User(id, name, email, phone)
    @PostMapping("/login/userinfo")
    public ResponseEntity<User> loginUserInfo(@RequestBody User user) {
        String id = user.getId();
        return ResponseEntity.ok(userService.loginUserInfo(id));
    }

    // 로그인 되어 있는 유저의 아이디로 이메일 전달 요청을 받음
    // 유저의 아이디를 토대로 이메일을 찾아서 이메일 전송
    // id -> String
    @PostMapping("/login/emailcheck")
    public ResponseEntity<String> loginEmailCheck(@RequestBody User user) {
        String id = user.getId();
        return ResponseEntity.ok(userService.loginEmailCheck(id));
    }

    // DB안의 코드와 이메일에 전송된 코드가 일치하면 이메일 인증 성공
    // id, emailkey -> bool
    @PostMapping("/login/emailcheck/client")
    public ResponseEntity<Boolean> loginEmailCheckKey(@RequestBody User user) {
        String id = user.getId();
        String emailkey = user.getEmailkey();
        return ResponseEntity.ok(userService.loginEmailCheckKey(id, emailkey));
    }

    // 문자 발송
    // id -> String
    @PostMapping("/login/phonecheck")
    public ResponseEntity<String> loginPhoneCheck(@RequestBody User user) {
        String id = user.getId();
        return ResponseEntity.ok(userService.loginPhoneCheck(id));
    }

    // DB안의 코드와 문자에 전송된 코드가 일치하면 문자 인증 성공
    // id, emailkey -> bool
    @PostMapping("/login/phonecheck/client")
    public ResponseEntity<Boolean> loginPhoneCheckKey(@RequestBody User user) {
        String id = user.getId();
        String phonekey = user.getPhonekey();
        return ResponseEntity.ok(userService.loginPhoneCheckKey(id, phonekey));
    }

    // 아아디 찾기 (이메일로)
    @PostMapping("/find/id/email")
    public ResponseEntity<String> findIdEmail(@RequestBody User user) {
        String name = user.getName();
        String email = user.getEmail();
        return ResponseEntity.ok(userService.findIdEmail(name, email));
    }

    // 아이디 찾기 (전화번호로)
    @PostMapping("/find/id/phone")
    public ResponseEntity<String> findIdPhone(@RequestBody User user) {
        String name = user.getName();
        String phone = user.getPhone();
        return ResponseEntity.ok(userService.findIdPhone(name, phone));
    }

    // 비밀번호 찾기 (이메일로)
    @PostMapping("/find/pw/email")
    public ResponseEntity<String> findPwEmail(@RequestBody User user) {
        String id = user.getId();
        String email = user.getEmail();
        return ResponseEntity.ok(userService.findPwEmail(id, email));
    }

    // 비밀번호 찾기 (전화번호로)
    @PostMapping("/find/pw/phone")
    public ResponseEntity<String> findPwPhone(@RequestBody User user) {
        String id = user.getId();
        String phone = user.getPhone();
        return ResponseEntity.ok(userService.findPwPhone(id, phone));
    }

    // 개인정보 수정
    // 하기전에 /login을 통해 아이디와 비밀번호가 일치하는지 재확인
    // User 개인정보를 확인하는 것은 클라이언트쪽에서 /login/userinfo를 통해 가능함
    // 일치할 경우에만 개인정보 수정 가능하도록 하기
    // id, pw, name, email, phone 모두 입력할 수 있도록 (단, email, phone 변경시 변경된 항목은 재인증 필요함)
    @PostMapping("/user/profile/update")
    public ResponseEntity<String> updateUserProfile(@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUserProfile(user));
    }
}
