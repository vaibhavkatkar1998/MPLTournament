package com.project.MplTournament.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MatchDetails")
public class MatchDetails {

    @Id
    @GeneratedValue
    private Integer id;

    private String team1;

    private String team2;

    private LocalDate matchDate;

    private LocalTime matchTime;

    private String matchStatus;

    private String stadium;

}
