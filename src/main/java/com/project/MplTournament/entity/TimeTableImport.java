package com.project.MplTournament.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class TimeTableImport {

    private LocalDateTime matchDateAndTime;

    private String matchName;
}
