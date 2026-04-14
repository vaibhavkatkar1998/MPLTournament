package com.project.MplTournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchResultScheduler {

    private final RapidApiMatchSyncService rapidApiMatchSyncService;

    @Scheduled(cron = "${rapid.api.scheduler.cron:0 */15 * * * *}", zone = "${rapid.api.scheduler.zone:Asia/Kolkata}")
    public void syncMatchResults() {
        rapidApiMatchSyncService.syncPendingMatchResults();
    }
}
