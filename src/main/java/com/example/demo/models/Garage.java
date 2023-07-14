package com.example.demo.models;

import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name = "garages")
public class Garage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long garageId;
    private String region;
    private String address;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Car> cars;
}
