package ru.taste.database.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.taste.utilities.java.TimerUtils;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class DiscordToken {
    @Id
    @GeneratedValue
    private UUID id;

    private String discordId;
    private String accessToken;
    private String refreshToken;
    private long refreshDate;
    private int expriesIn;

    @Builder
    private DiscordToken(String discordId, String accessToken, String refreshToken, long refreshDate, int expriesIn) {
        this.discordId = discordId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshDate = refreshDate;
        this.expriesIn = expriesIn;
    }

    public boolean hasExpired() {
        return TimerUtils.delay(refreshDate, expriesIn * 1000L);
    }
}
