package com.example.team2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;

    private String id;
    private String pw;
    private String name;
    private String email;
    private String phone;
    private String emailkey;
    private String phonekey;
    private Integer onlinestate;
    private Integer emailcheck;
    private Integer phonecheck;
    private LocalDateTime emailtime;
    private LocalDateTime phonetime;
    private String token;

    public User() {}

    public User(String id, String pw, String name, String email, String phone) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}