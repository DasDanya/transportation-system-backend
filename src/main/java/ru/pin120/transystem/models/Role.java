package ru.pin120.transystem.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;


    public Role(ERole name) {
        this.name = name;
    }

}
