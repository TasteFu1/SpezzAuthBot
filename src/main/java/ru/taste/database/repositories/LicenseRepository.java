package ru.taste.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import ru.taste.database.entities.License;

@Repository
public interface LicenseRepository extends JpaRepository<License, UUID> {
    License findByCode(String code);

    List<License> findAllByAccountIdAndApplicationId(UUID accountId, UUID applicationId);

    List<License> findAllByApplicationId(UUID applicationId);

    List<License> findAllByAccountId(UUID accountId);

    List<License> findAllByApplicationIdAndAccountIdIsNull(UUID applicationId);

    List<License> findAllByApplicationIdAndAccountIdNotNull(UUID applicationId);
}
