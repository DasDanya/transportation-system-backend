package ru.pin120.transystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.transystem.models.ERole;
import ru.pin120.transystem.models.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(ERole name);

}
