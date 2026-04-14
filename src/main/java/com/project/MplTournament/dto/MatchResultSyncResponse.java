package com.project.MplTournament.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchResultSyncResponse {

    private final boolean completed;

    private final String statusText;

    private final String winningTeam;

    private final String team1Name;

    private final String team1ShortName;

    private final String team2Name;

    private final String team2ShortName;
}
