package ru.pin120.transystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.transystem.models.Cargo;

import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Integer> {

    Optional<Cargo> findCargoById(int id);

    void deleteCargoById(int id);
}
