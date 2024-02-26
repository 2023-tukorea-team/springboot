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
public class Sensorlog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;

    private String id;
    private String logoption;
    private LocalDateTime logtime;

    public Sensorlog() {}

    public Sensorlog(String id, String logoption) {
        this.id = id;
        this.logoption = logoption;
    }
}
