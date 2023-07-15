package ru.pin120.transystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "cargos")
@NoArgsConstructor
@Getter
@Setter
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 50,nullable = false)
    @Pattern(regexp = "^[А-Яа-я]+$", message = "Категория должна состоять из русских букв")
    private String category;

    @Column(nullable = false)
    @DecimalMin(value="1", message = "Минимальная стоимость единицы груза = 1₽")
    @DecimalMax(value = "9999999.99", message = "Максимальная стоимость единицы груза = 9999999.99₽")
    @Digits(integer = 7,fraction = 2, message = "Доступно 2 цифры после запятой. Длина целой части стоимости должна быть не более чем из 7 цифр")
    private BigDecimal cost;

    @Column(nullable = false)
    @Min(value = 1,message = "Минимальное количество единиц груза = 1")
    @Max(value = 100000, message = "Максимальное количество единиц груза = 100000")
    private int count;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="start_address_id", nullable = false)
//    private Address startAddress;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="end_address_id", nullable = false)
//    private Address endAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="start_warehouse_id", nullable = false)
    private Warehouse startWarehouse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="actual_warehouse_id")
    private Warehouse actualWarehouse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="end_warehouse_id", nullable = false)
    private Warehouse endWarehouse;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="cargo_id",nullable = false)
    List<CargoPhoto> photos;

}
