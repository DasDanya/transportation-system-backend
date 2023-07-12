package ru.pin120.transystem.models;



import jakarta.persistence.*;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="responsibles",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "phone")
        })
public class Responsible implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(length = 30, nullable = false)
    @Pattern(regexp = "^[А-Яа-я]+$", message = "Фамилия должна состоять из русских букв")
    private String surname;


    @Column(length = 20, nullable = false)
    @Pattern(regexp = "^[А-Яа-я]+$", message = "Имя должно состоять из русских букв")
    private String name;

    @Column(length = 30)
    @Pattern(regexp = "^[А-Яа-я]*$", message = "Отчество должно состоять из русских букв")
    private String patronymic;


    @Column(length = 17, nullable = false)
    @Pattern(regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}", message="Некорректный ввод номера телефона. Пример: +7-111-111-11-11")
    private String phone;


    @Column(nullable = false)
    private byte[] photo;

    //@JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "responsible")
    private Set<Warehouse> warehouses = new HashSet<>();
}
