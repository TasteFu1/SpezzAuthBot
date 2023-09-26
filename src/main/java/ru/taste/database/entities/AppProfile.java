package ru.taste.database.entities;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.taste.Instance;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class AppProfile {
    @Id
    @GeneratedValue
    private UUID id;

    private int uid;
    private long lastHardwareIdReset;
    private String profileName;

    @Column(columnDefinition = "text")
    private String hardwareId;

    private UUID accountId;
    private UUID licenseId;
    private UUID applicationId;
    private Role role;

    @Builder
    private AppProfile(Role role, UUID accountId, UUID licenseId, UUID applicationId) {
        this.role = role;
        this.accountId = accountId;
        this.licenseId = licenseId;
        this.applicationId = applicationId;
        this.uid = nextUid();
        this.profileName = getAccount().getUsername();
    }

    private int nextUid() {
        List<AppProfile> appProfileList = Instance.get() //
                .getAppProfileRepository() //
                .findAllByApplicationId(applicationId);

        return appProfileList.isEmpty() ? 1 : appProfileList //
                .stream() //
                .max(Comparator.comparingInt(AppProfile::getUid)) //
                .get() //
                .getUid() + 1;
    }

    public Account getAccount() {
        return accountId == null ? null : Instance.get().getAccountRepository().findById(accountId).get();
    }

    public License getLicense() {
        return licenseId == null ? null : Instance.get().getLicenseRepository().findById(licenseId).get();
    }

    public Application getApplication() {
        return applicationId == null ? null : Instance.get().getApplicationRepository().findById(applicationId).get();
    }

    @Getter
    @AllArgsConstructor
    public enum Role {
        OWNER("Owner"), //
        CUSTOMER("Customer"), //
        ADMIN("Administrator"), //
        BANNED("Banned");

        private final String funcyName;
    }
}
