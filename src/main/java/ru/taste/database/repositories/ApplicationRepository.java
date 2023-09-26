package ru.taste.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import ru.taste.database.entities.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findAllByOwnerId(UUID ownerId);

    Application findByName(String name);
}
