package com.project.MplTournament.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableImportDTO {

    private Integer id;

    private LocalDate date;

    private LocalTime time;

    private String team1;

    private String team2;

    private String matchStatus;

    private String stadium;

}
