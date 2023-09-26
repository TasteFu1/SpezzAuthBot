package ru.taste.database.entities;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.taste.utilities.java.StringUtils;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String version;
    private String logoUrl;
    private String websiteUrl;
    private String apiKey;
    private String secretKey;
    private long hardwareIdResetPeriod;
    private long creationDate;
    private boolean enabled;
    private UUID ownerId;

    @Builder
    private Application(String name, String version, String logoUrl, String websiteUrl, UUID ownerId) {
        this.name = name;
        this.version = version;
        this.logoUrl = logoUrl;
        this.websiteUrl = websiteUrl;
        this.apiKey = StringUtils.random(16);
        this.secretKey = StringUtils.random(64);
        this.hardwareIdResetPeriod = -1;
        this.creationDate = System.currentTimeMillis();
        this.enabled = false;
        this.ownerId = ownerId;
    }
}
