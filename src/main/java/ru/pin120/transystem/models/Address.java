package ru.pin120.transystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="addresses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"state","city","street","house"}))
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(length = 50, nullable = false)
    @Pattern(regexp = "^[А-Яа-я]*(?:[\s-][А-Яа-я]*)*$", message = "Субъект должен состоять из русских букв")
    private String state;


    @Column(length = 30, nullable = false)
    @Pattern(regexp = "^[А-Яа-я]*(?:[\s-][А-Яа-я]*)*$", message = "Город должен состоять из русских букв")
    private String city;


    @Column(length = 30, nullable = false)
    @Pattern(regexp = "^[А-Яа-я]*(?:[\s-][А-Яа-я]*)*$", message = "Улица должна состоять из русских букв")
    private String street;


    @Column(length = 7, nullable = false)
    @Pattern(regexp = "^\\d+[А-Г/\\d]*$", message = "Некорректный ввод номера дома")
    private String house;
}
