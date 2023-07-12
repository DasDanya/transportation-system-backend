package ru.pin120.transystem.models;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 30, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="start_address_id", nullable = false)
    private Address startAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="end_address_id", nullable = false)
    private Address endAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="warehouse_id")
    private Warehouse warehouse;

}
