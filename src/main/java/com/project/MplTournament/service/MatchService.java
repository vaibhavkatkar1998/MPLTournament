package com.project.MplTournament.service;


import com.project.MplTournament.ExcpetionHandler.MatchNotFoundException;
import com.project.MplTournament.dto.MatchDetailsDTO;
import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.repository.MatchRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepo matchRepo;

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    public void updateMatchResult(MatchDetailsDTO matchDetailsDTO) {
        log.info("Finding match by match id {}", matchDetailsDTO.getId());
        Optional<MatchDetails> matchDetailsOptional = matchRepo.findById(matchDetailsDTO.getId());
        if(matchDetailsOptional.isPresent()) {
            MatchDetails matchDetails = matchDetailsOptional.get();
            // updating match status
            matchDetails.setMatchStatus(matchDetailsDTO.getMatchStatus());
            MatchDetails matchDetailsResponse = matchRepo.save(matchDetails);
            log.info("update users who votes for the match {}", matchDetailsDTO.getId());
            userService.updateUserPoints(matchDetailsResponse, matchDetailsDTO.getBetValue());
        } else {
            throw new MatchNotFoundException("Match not found with matchId " + matchDetailsDTO.getId());
        }
    }
}
