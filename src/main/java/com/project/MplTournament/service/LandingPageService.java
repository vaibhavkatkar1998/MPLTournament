package com.project.MplTournament.service;

import com.project.MplTournament.entity.TimeTableImport;
import com.project.MplTournament.repository.TimeTableImportRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class LandingPageService {

    @Autowired
    private TimeTableImportRepo timeTableImportRepo;

    public List<TimeTableImport> getTodayMatches() {
        LocalDate localDate = LocalDate.now();
        return timeTableImportRepo.findAllByMatchDate(localDate);
    }
}
