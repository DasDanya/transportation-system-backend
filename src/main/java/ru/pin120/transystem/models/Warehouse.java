package ru.pin120.transystem.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "actualWarehouse")
    private List<Cargo> cargos;


    //@JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="responsible_id")
    private Responsible responsible;

    @JsonIgnore
    public List<Cargo> getCargos() {
        return cargos;
    }

}
