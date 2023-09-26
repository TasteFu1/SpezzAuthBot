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
import ru.taste.utilities.security.EncryptionUtils;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private UUID id;

    private String email;
    private String password;
    private String username;
    private String discordId;
    private long registrationDate;
    private int appLimit;
    private Role role;
    private UUID subscriptionId;

    @Builder
    private Account(String email, String password, String discordId, Role role) throws Exception {
        this.email = email;
        this.password = EncryptionUtils.encrypt(password);
        this.username = email.split("@")[0];
        this.discordId = discordId;
        this.registrationDate = System.currentTimeMillis();
        this.role = role;
    }

    public Subscription getSubscription() {
        return subscriptionId == null ? null : Instance.get().getSubscriptionRepository().findById(subscriptionId).orElse(null);
    }

    public void incrementAppLimit(int val) {
        appLimit += val;
    }

    public void decrementAppLimit(int val) {
        appLimit -= val;
    }

    @Getter
    @AllArgsConstructor
    public enum Role {
        MEMBER("Member"), //
        ADMIN("Admin"), //
        BANNED("Banned");

        final String funcyName;
    }
}
