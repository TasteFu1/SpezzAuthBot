package ru.taste.database.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(columnDefinition = "text")
    private String stackTrace;

    @Builder
    private Report(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
