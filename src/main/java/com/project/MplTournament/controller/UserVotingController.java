package com.project.MplTournament.controller;

import com.project.MplTournament.dto.MatchDetailsDTO;
import com.project.MplTournament.entity.UserVoting;
import com.project.MplTournament.service.UserVotingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserVotingController {

    private final UserVotingService userVotingService;

    private static final Logger log = LoggerFactory.getLogger(UserVotingController.class);

    @PostMapping("/registerVote")
    public ResponseEntity<String> registerVote(@RequestBody MatchDetailsDTO matchDetailsDTO) {
        String response = userVotingService.registerUserVote(matchDetailsDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
