package com.example.team2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Sensor {
    @Id
    private String id;
    private String userid;

    public Sensor() {}

    public Sensor(String id) {
        this.id = id;
    }

    public Sensor(String id, String userid) {
        this.id = id;
        this.userid = userid;
    }
}
