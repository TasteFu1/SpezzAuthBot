package ru.taste.database.entities;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.taste.utilities.java.TimerUtils;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class FileRequest {
    @Id
    @GeneratedValue
    private UUID id;

    private long creationDate;
    private UUID accountId;

    @Column(columnDefinition = "longtext")
    private String content;

    private String fileName;

    @Builder
    private FileRequest(String fileName, String content, UUID accountId) {
        this.fileName = fileName;
        this.content = content;
        this.creationDate = System.currentTimeMillis();
        this.accountId = accountId;
    }

    public boolean hasExpired() {
        return TimerUtils.delay(creationDate, TimeUnit.DAYS.toMillis(1));
    }
}
