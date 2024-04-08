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
public class Usersensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userid;
    private String sensorid;
    private String code;
    private Integer codecheck;
    private Integer direct;
    private LocalDateTime codetime;
    private Integer state;

    public Usersensor() {}

    public Usersensor(String userid, String sensorid, String code) {
        this.userid = userid;
        this.sensorid = sensorid;
        this.code = code;
    }
}
