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
    private Integer start;
    private Integer door;
    private Integer person;
    private Integer speed;
    private LocalDateTime logtime;
    private Integer warning;

    public Sensorlog() {}

    public Sensorlog(String id, Integer start, Integer door, Integer person, Integer speed, Integer warning) {
        this.id = id;
        this.start = start;
        this.door = door;
        this.person = person;
        this.speed = speed;
        this.warning = warning;
    }
}
