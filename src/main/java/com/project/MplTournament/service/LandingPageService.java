package com.project.MplTournament.service;

import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.repository.MatchRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@AllArgsConstructor
public class LandingPageService {

    @Autowired
    private MatchRepo matchRepo;

    /**
     * This method return today's and yesterday's matches on the basis of flag
     * @param fromAdmin flag to return today's and yesterday's matches
     * @return list of matches
     */
    public List<MatchDetails> getTodayMatches(Boolean fromAdmin) {
        LocalDate todayDate = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate yesterdayDate = LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(1);
        // if request coming from admin then return today's and yesterday's matches
        if(fromAdmin) {
            return matchRepo.findAllByMatchDateBetween(yesterdayDate, todayDate);
        } else {
            return matchRepo.findAllByMatchDate(todayDate);
        }
    }
}
