package ru.taste.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import ru.taste.database.entities.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
}
