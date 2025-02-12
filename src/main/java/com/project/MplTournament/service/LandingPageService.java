package com.project.MplTournament.service;

import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.repository.MatchRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class LandingPageService {

    @Autowired
    private MatchRepo matchRepo;

    public List<MatchDetails> getTodayMatches(Boolean fromAdmin) {
        LocalDate todayDate = LocalDate.now();
        LocalDate yesterdayDate = LocalDate.now().minusDays(1);
        if(fromAdmin) {
            return matchRepo.findAllByMatchDateBetween(yesterdayDate, todayDate);
        } else {
            return matchRepo.findAllByMatchDate(todayDate);
        }
    }
}
