package ru.taste.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import ru.taste.database.entities.AppProfile;

@Repository
public interface AppProfileRepository extends JpaRepository<AppProfile, UUID> {
    List<AppProfile> findAllByAccountId(UUID accountId);

    List<AppProfile> findAllByApplicationId(UUID applicationId);

    List<AppProfile> findAllByApplicationIdAndRole(UUID applicationId, AppProfile.Role role);

    AppProfile findByAccountIdAndApplicationId(UUID accountId, UUID applicationId);

    AppProfile findByLicenseId(UUID licenseId);

    AppProfile findByApplicationIdAndUid(UUID applicationId, int uid);
}
