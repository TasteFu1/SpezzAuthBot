package ru.taste.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import ru.taste.database.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Account findByDiscordId(String discordId);

    Account findByEmail(String email);

    Account findByUsername(String username);
}
