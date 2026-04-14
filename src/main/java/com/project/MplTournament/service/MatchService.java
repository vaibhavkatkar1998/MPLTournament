package com.project.MplTournament.service;

import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.repository.MatchRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepo matchRepo;

    private final RapidApiMatchSyncService rapidApiMatchSyncService;

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    /**
     * This method triggers a fresh sync from the external RapidAPI feed.
     * @return response as string
     */
    public String updateMatchResult() {
        log.info("Triggering manual recent-match sync");
        return rapidApiMatchSyncService.syncPendingMatchResults();
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
