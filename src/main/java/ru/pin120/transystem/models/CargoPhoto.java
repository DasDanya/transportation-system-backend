package ru.pin120.transystem.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="cargo_photos")
@NoArgsConstructor
@Getter
@Setter
public class CargoPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private byte[] photo;

    public CargoPhoto(byte[] photo) {
        this.photo = photo;
    }
}
