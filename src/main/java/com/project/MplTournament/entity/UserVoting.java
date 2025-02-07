package com.project.MplTournament.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    private Integer matchId;

    private Date votedOn;
}
