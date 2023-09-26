package ru.taste.database.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
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
public class Subscription {
    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private long issueDate;
    private long usageDate;
    private long expirationDate;
    private Type type;
    private int appLimit;
    private String ipAddress;
    private UUID accountId;

    @Builder
    private Subscription(long expirationDate, Type type) {
        this.code = StringUtils.randomCode();
        this.issueDate = System.currentTimeMillis();
        this.expirationDate = expirationDate;
        this.type = type;
        this.appLimit = type == Type.PROFESSIONAL ? 3 : type == Type.ENTERPRISE ? 5 : 1;
    }

    public Account getAccount() {
        return accountId == null ? null : Instance.get().getAccountRepository().findById(accountId).orElse(null);
    }

    public boolean hasExpired() {
        return TimerUtils.delay(usageDate, expirationDate);
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        STARTER("Starter"), //
        PROFESSIONAL("Professional"), //
        ENTERPRISE("Enterprise");

        private final String funcyName;
    }
}
