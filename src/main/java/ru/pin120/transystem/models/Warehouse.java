package ru.pin120.transystem.models;


import jakarta.persistence.*;
import jakarta.validation.Valid;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "warehouses",
        uniqueConstraints = @UniqueConstraint(columnNames = "address_id"))
public class Warehouse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="address_id", nullable = false)
    @Valid
    private Address address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse")
    private Set<Product> products = new HashSet<>();

    //@JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="responsible_id")
    private Responsible responsible;
}
