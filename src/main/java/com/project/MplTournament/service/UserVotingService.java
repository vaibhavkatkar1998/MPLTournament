package com.project.MplTournament.service;
import com.project.MplTournament.ExcpetionHandler.MatchNotFoundException;
import com.project.MplTournament.ExcpetionHandler.UserNameNotFoundException;
import com.project.MplTournament.ExcpetionHandler.VotingTimeExceedException;
import com.project.MplTournament.dto.MatchDetailsDTO;
import com.project.MplTournament.dto.UserVotingDTO;
import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.entity.UserPrincipal;
import com.project.MplTournament.entity.UserVoting;
import com.project.MplTournament.entity.Users;
import com.project.MplTournament.repository.MatchRepo;
import com.project.MplTournament.repository.UserRepo;
import com.project.MplTournament.repository.UserVotingRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserVotingService {

    private final UserVotingRepo userVotingRepo;

    private final UserRepo userRepo;

    private final MatchRepo matchRepo;

    private final ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(UserVotingService.class);

    /**
     * This method will use to register user vote
     *
     * @param matchDetailsDTO get it as response from UI
     * @param userId get user as request param
     * @return string as response
     */
    public String registerUserVote(MatchDetailsDTO matchDetailsDTO, Integer userId) {
        try {
            Optional<Users> users = userRepo.findById(userId);
            LocalDateTime matchLocalDateAndTime = LocalDateTime.of(matchDetailsDTO.getMatchDate(),matchDetailsDTO.getMatchTime());
            // compare local date and time is before match toss schedule time and also check weather user object is not null
            if (LocalDateTime.now().isBefore(matchLocalDateAndTime) && users.isPresent()) {
                Optional<UserVoting> userVotingOptional = userVotingRepo.findByMatchDetails_IdAndUserId(matchDetailsDTO.getId(),users.get().getId());
                UserVoting userVoting;
                if(userVotingOptional.isPresent()) {
                    log.info("Vote is already present hence updating existing vote {}",users.get().getUserName());
                    userVoting = userVotingOptional.get();
                    userVoting.setSelectedTeam(matchDetailsDTO.getSelectedTeam());
                    userVoting.setVotedOn(new Date());
                } else {
                    log.info("Vote is not present hence creating new vote for user {}",users.get().getUserName());
                    Optional<MatchDetails> matchDetails = matchRepo.findById(matchDetailsDTO.getId());
                    if(matchDetails.isPresent()) {
                        userVoting = UserVoting.builder()
                                .selectedTeam(matchDetailsDTO.getSelectedTeam())
                                .votedOn(new Date())
                                .userId(users.get().getId())
                                .matchDetails(matchDetails.get())
                                .build();
                    } else {
                        log.error("Match details not found with match id {}", matchDetailsDTO.getId());
                        throw new MatchNotFoundException("Match details not found with match id " + matchDetailsDTO.getId());
                    }
                }
                userVotingRepo.save(userVoting);
                log.info("Voting saved successfully");
                return "Vote saved successfully";
            } else {
                if(users.get().getId() == null) {
                    log.error("User not present in security context");
                    throw new UserNameNotFoundException("User not present in security context");
                } else {
                    log.error("User is trying to vote after time exceeds {}",users.get().getUserName());
                    throw new VotingTimeExceedException("Voting Time exceeds");
                }
            }
        } catch (RuntimeException e) {
            log.error("Something went wrong during adding vote for user with exception {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * This method returns last 10 votes of user
     * @param userId get it as input
     * @return list of user votes
     */
    public List<UserVotingDTO> getLastTenVotes(Integer userId) {
        List<UserVoting> userVotingList = userVotingRepo.findTop10ByUserIdOrderByVotedOnDesc(userId);
        return userVotingList.stream().map(userVoting ->
                modelMapper.map(userVoting,UserVotingDTO.class)).toList();
    }

}
