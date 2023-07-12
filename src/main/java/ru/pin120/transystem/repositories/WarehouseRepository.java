package ru.pin120.transystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.transystem.models.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
}
