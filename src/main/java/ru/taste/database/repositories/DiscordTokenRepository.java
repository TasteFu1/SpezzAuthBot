package ru.taste.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import ru.taste.database.entities.DiscordToken;

@Repository
public interface DiscordTokenRepository extends JpaRepository<DiscordToken, UUID> {
    DiscordToken findByDiscordId(String discordId);
}
