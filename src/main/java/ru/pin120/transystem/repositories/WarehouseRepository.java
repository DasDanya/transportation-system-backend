package ru.pin120.transystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.transystem.models.Warehouse;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
    Optional<Warehouse> findWarehouseById(int id);

    void deleteWarehouseById(int id);
}
