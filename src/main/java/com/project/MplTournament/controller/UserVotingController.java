package com.project.MplTournament.controller;

import com.project.MplTournament.dto.MatchDetailsDTO;
import com.project.MplTournament.dto.UserVotingDTO;
import com.project.MplTournament.entity.UserVoting;
import com.project.MplTournament.service.UserVotingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserVotingController {

    private final UserVotingService userVotingService;

    private static final Logger log = LoggerFactory.getLogger(UserVotingController.class);

    @PostMapping("/registerVote")
    public ResponseEntity<String> registerVote(@RequestBody MatchDetailsDTO matchDetailsDTO, @RequestParam Integer userId) {
        String response = userVotingService.registerUserVote(matchDetailsDTO, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getLast10Votes")
    public ResponseEntity<List<UserVotingDTO>> getLast1oVotes(@RequestParam Integer userId) {
        List<UserVotingDTO> response = userVotingService.getLastTenVotes(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
