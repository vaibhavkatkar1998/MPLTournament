package com.project.MplTournament.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MatchTimeTable")
public class TimeTableImport {

    @Id
    @GeneratedValue
    private Integer id;

    private String team1;

    private String team2;

    private LocalDate matchDate;

    private LocalTime matchTime;

    private Integer matchStatus;

    private String stadium;

}
