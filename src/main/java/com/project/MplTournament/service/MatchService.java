package com.project.MplTournament.service;


import com.project.MplTournament.ExcpetionHandler.MatchNotFoundException;
import com.project.MplTournament.ExcpetionHandler.MatchResultUpdateException;
import com.project.MplTournament.dto.MatchDetailsDTO;
import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.repository.MatchRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepo matchRepo;

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    /**
     * This method is used to update match status on the basis of match result can be updated
     * by admin role only
     * @param matchDetailsDTO got match result here as request body
     * @return response as string
     */
    @PreAuthorize("hasRole('Admin')")
    public String updateMatchResult(MatchDetailsDTO matchDetailsDTO) {
        // update here to check role of user it should be admin
        log.info("Finding match by match id {}", matchDetailsDTO.getId());
        Optional<MatchDetails> matchDetailsOptional = matchRepo.findById(matchDetailsDTO.getId());
        if(matchDetailsOptional.isPresent()) {
            MatchDetails matchDetails = matchDetailsOptional.get();
            // updating match status if not updated earlier
            if(matchDetails.getMatchStatus().equals("No result")) {
                matchDetails.setMatchStatus(matchDetailsDTO.getMatchStatus());
                MatchDetails matchDetailsResponse = matchRepo.save(matchDetails);
                log.info("update users who votes for the match {}", matchDetailsDTO.getId());
                // updating user points with bet value
                userService.updateUserPoints(matchDetailsResponse, matchDetailsDTO.getBetValue());
            } else {
                // throw error of this match has been already updated
                log.error("Match result can be update only once");
                throw new MatchResultUpdateException("Match result can be update only once");
            }
        } else {
            log.error("Match not found with matchId {}", matchDetailsDTO.getId());
            throw new MatchNotFoundException("Match not found with matchId " + matchDetailsDTO.getId());
        }
        log.info("Result updated successfully");
        return "Result updated successfully";
    }

    /**
     * Get list of all matches from DB in count of page
     * @param pageable page 0 size 15
     * @return list of page
     */
    public Page<MatchDetails> getAllMatches(Pageable pageable) {
        return matchRepo.findAll(pageable);
    }
}
