package com.example.team2.rest;

import com.example.team2.entity.User;
import com.example.team2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // id -> bool (true: 중복있음, false: 중복없음)
    @PostMapping("/signup/checkid")
    public ResponseEntity<Boolean> checkID(@RequestParam String id) {
        return ResponseEntity.ok(userService.checkId(id));
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
    public ResponseEntity<User> loginUserInfo(@RequestParam String id) {
        return ResponseEntity.ok(userService.loginUserInfo(id));
    }

    // 로그인 되어 있는 유저의 아이디로 이메일 전달 요청을 받음
    // 유저의 아이디를 토대로 이메일을 찾아서 이메일 전송
    // id -> String
    @PostMapping("/login/emailcheck")
    public ResponseEntity<String> loginEmailCheck(@RequestParam String id) {
        return ResponseEntity.ok(userService.loginEmailCheck(id));
    }

    // DB안의 코드와 이메일에 전송된 코드가 일치하면 이메일 인증 성공
    // id, emailkey -> bool
    @PostMapping("/login/emailcheck/client")
    public ResponseEntity<Boolean> loginEmailCheckKey(@RequestParam String id, String emailKey) {
        return ResponseEntity.ok(userService.loginEmailCheckKey(id, emailKey));
    }

    // 문자 발송
    // id -> String
    @PostMapping("/login/phonecheck")
    public ResponseEntity<String> loginPhoneCheck(@RequestParam String id) {
        return ResponseEntity.ok(userService.loginPhoneCheck(id));
    }

    // DB안의 코드와 문자에 전송된 코드가 일치하면 문자 인증 성공
    // id, emailkey -> bool
    @PostMapping("/login/phonecheck/client")
    public ResponseEntity<Boolean> loginPhoneCheckKey(@RequestParam String id, String phoneKey) {
        return ResponseEntity.ok(userService.loginPhoneCheckKey(id, phoneKey));
    }
}
