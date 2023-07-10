package ru.pin120.transystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.transystem.models.Responsible;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsibleRepository extends JpaRepository<Responsible,Integer> {
    void deleteResponsibleById(int id);
    Optional<Responsible> findResponsibleById(int id);
    List<Responsible> findResponsibleBySurname(String surname);
    List<Responsible> findResponsibleByName(String name);
    List<Responsible> findResponsibleByPatronymic(String patronymic);
    List<Responsible> findResponsibleByPhone(String phone);
}
