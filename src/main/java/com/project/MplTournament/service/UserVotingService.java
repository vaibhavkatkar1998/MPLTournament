package com.project.MplTournament.service;
import com.project.MplTournament.ExcpetionHandler.UserNameNotFoundException;
import com.project.MplTournament.ExcpetionHandler.VotingTimeExceedException;
import com.project.MplTournament.dto.MatchDetailsDTO;
import com.project.MplTournament.entity.UserPrincipal;
import com.project.MplTournament.entity.UserVoting;
import com.project.MplTournament.entity.Users;
import com.project.MplTournament.repository.UserVotingRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserVotingService {

    private final UserVotingRepo userVotingRepo;

    private static final Logger log = LoggerFactory.getLogger(UserVotingService.class);

    public String registerUserVote(MatchDetailsDTO matchDetailsDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Users users = userPrincipal.getUser();
            LocalDateTime matchLocalDateAndTime = LocalDateTime.of(matchDetailsDTO.getMatchDate(),matchDetailsDTO.getMatchTime());
            // compare local date and time is before match toss schedule time and also check weather user object is not null
            if (LocalDateTime.now().isBefore(matchLocalDateAndTime) && users.getId() != null) {
                Optional<UserVoting> userVotingOptional = userVotingRepo.findByMatchIdAndUserId(matchDetailsDTO.getId(),users.getId());
                UserVoting userVoting;
                if(userVotingOptional.isPresent()) {
                    log.info("Vote is already present hence updating existing vote {}",users.getUserName());
                    userVoting = userVotingOptional.get();
                    userVoting.setSelectedTeam(matchDetailsDTO.getSelectedTeam());
                    userVoting.setVotedOn(new Date());
                } else {
                    log.info("Vote is not present hence creating new vote for user {}",users.getUserName());
                    userVoting = UserVoting.builder()
                            .selectedTeam(matchDetailsDTO.getSelectedTeam())
                            .votedOn(new Date())
                            .userId(users.getId())
                            .matchId(matchDetailsDTO.getId())
                            .build();
                }
                userVotingRepo.save(userVoting);
                return "Vote saved successfully";
            } else {
                if(users.getId() == null) {
                    log.error("User not present in security context");
                    throw new UserNameNotFoundException("User not present in security context");
                } else {
                    log.error("User is trying to vote after time exceeds {}",users.getUserName());
                    throw new VotingTimeExceedException("Voting Time exceeds");
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
