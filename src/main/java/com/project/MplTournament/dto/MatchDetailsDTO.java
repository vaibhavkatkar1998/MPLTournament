package com.project.MplTournament.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchDetailsDTO {

    private Integer id;

    private LocalDate matchDate;

    private LocalTime matchTime;

    private String team1;

    private String team2;

    private String matchStatus;

    private String stadium;

    private String selectedTeam;

    private Integer betValue;
}
