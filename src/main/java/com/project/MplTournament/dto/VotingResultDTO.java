package com.project.MplTournament.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotingResultDTO {
    private String team1Name;
    private String team2Name;
    private int team1;
    private int team2;
    private List<VoteDetailDTO> totalVotes;
}
