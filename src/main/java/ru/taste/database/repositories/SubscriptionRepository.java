package ru.taste.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import ru.taste.database.entities.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Subscription findByCode(String code);
}
