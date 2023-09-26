package ru.taste.database.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.taste.Instance;
import ru.taste.utilities.java.StringUtils;
import ru.taste.utilities.java.TimerUtils;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class License {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String ipAddress;
    private long issueDate;
    private long usageDate;
    private long expirationDate;
    private UUID applicationId;
    private UUID accountId;

    @Builder
    private License(long usageDate, long expirationDate, UUID accountId, UUID applicationId) {
        this.code = StringUtils.randomCode();
        this.issueDate = System.currentTimeMillis();
        this.usageDate = usageDate;
        this.expirationDate = expirationDate;
        this.accountId = accountId;
        this.applicationId = applicationId;
    }

    public Account getAccount() {
        return accountId == null ? null : Instance.get().getAccountRepository().findById(accountId).get();
    }

    public Application getApplication() {
        return applicationId == null ? null : Instance.get().getApplicationRepository().findById(applicationId).get();
    }

    public boolean hasExpired() {
        return TimerUtils.delay(usageDate, expirationDate);
    }
}
