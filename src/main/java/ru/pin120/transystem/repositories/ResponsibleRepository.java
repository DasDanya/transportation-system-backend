package ru.pin120.transystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.transystem.models.Responsible;

@Repository
public interface ResponsibleRepository extends JpaRepository<Responsible,Integer> {
    void deleteResponsibleById(int id);


}
