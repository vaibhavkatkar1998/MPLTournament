package com.project.MplTournament.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "UserVoting")
public class UserVoting {

    @Id
    @GeneratedValue
    private Integer id;

    private String selectedTeam;

    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "match_id", referencedColumnName = "id", nullable = false)
    private MatchDetails matchDetails;

    private Date votedOn;
}
