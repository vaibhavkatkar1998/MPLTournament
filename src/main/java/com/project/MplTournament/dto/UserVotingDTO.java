package com.project.MplTournament.dto;

import com.project.MplTournament.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVotingDTO {

    private Integer id;

    private String selectedTeam;

    private Users user;

    private Integer matchId;

    private Date votedOn;

    private MatchDetailsDTO matchDetails;
}
