package com.project.MplTournament.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer id;

    private String userName;

    private String role;

    private int totalPoints;

    private int winingPercentage;

    private int totalMatchVotedFor;

}
