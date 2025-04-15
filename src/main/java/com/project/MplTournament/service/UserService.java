package com.project.MplTournament.service;


import com.project.MplTournament.ExcpetionHandler.UserNameNotFoundException;
import com.project.MplTournament.dto.PasswordResetDTO;
import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.entity.UserPrincipal;
import com.project.MplTournament.entity.UserVoting;
import com.project.MplTournament.entity.Users;
import com.project.MplTournament.repository.UserRepo;
import com.project.MplTournament.repository.UserVotingRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepo userRepo;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserVotingRepo userVotingRepo;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * This method is used to register user by setting some default values.
     * @param user (gets user object from request body)
     * @return (return string response)
     */
    public String registerUser(Users user) {
        user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));
        user.setRole("User");
        user.setTotalPoints(0);
        Users response = userRepo.save(user);
        if(response.getUserName() != null) {
            log.info("User registered successfully");
            return "User register successfully";
        }
        log.error("Error while registering the user");
        return "Error while adding user";
    }

    /**
     * This method is used to verify the user and generate JWT token
     * @param user (gets user object from request body)
     * @return (return string response)
     */
    public String verifyUser(Users user) {
        try {
            log.info("Started Login {}", Instant.now());
            // Fetch user from the database with case-sensitive check
            Users foundUser = userRepo.findByUserName(user.getUserName());
            if(foundUser == null) {
                throw new UserNameNotFoundException("User not found");
            }
            // Ensure case-sensitive match
            if (!foundUser.getUserName().equals(user.getUserName())) {
                throw new UserNameNotFoundException("Case Mismatch for user name");
            }
            // Token creation and user validation
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(),user.getUserPassword()));
            if(authentication.isAuthenticated()) {
                // Cast to your custom UserPrincipal class
                log.info("User is authenticated");
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                Users users = userPrincipal.getUser();
                log.info("Ended Login {}", Instant.now());
                return jwtService.generateToken(user.getUserName(), users.getRole(), users.getId());
            }
        } catch (Exception e){
            log.error("Error while authentication of user with exception {}",e.getMessage());
            throw new UserNameNotFoundException("Invalid user");
        }
        log.error("Token returned as null due to some error");
        return "";
    }

    /**
     * Update user points on the basis of match result
     * @param matchDetailsResponse getting match details from match service
     * @param betValue getting custom bet value
     */
    public void updateUserPoints(MatchDetails matchDetailsResponse, Integer betValue) {
        log.info("Finding user votes for match id {}", matchDetailsResponse.getId());

        List<Users> allUsers = userRepo.findAll();
        List<UserVoting> userVotingList = userVotingRepo.findByMatchDetails_Id(matchDetailsResponse.getId());

        Map<Integer, Users> updatedUsers = new HashMap<>();

        if (!userVotingList.isEmpty()) {
            for (UserVoting userVoting : userVotingList) {
                userRepo.findById(userVoting.getUser().getId()).ifPresent(user -> {
                    // Increase points for incorrect vote
                    if (!userVoting.getSelectedTeam().equals(matchDetailsResponse.getMatchStatus())) {
                        user.setTotalPoints(user.getTotalPoints() + betValue);
                    }
                    // Store user for update
                    updatedUsers.put(user.getId(), user);
                    log.debug("Updated points for voting user {}", userVoting.getUser().getUserName());
                });
            }
        } else {
            log.error("User voting list not found with matchId {}", matchDetailsResponse.getId());
        }

        // Add 25 points to users who didn't vote (not in updatedUsers)
        for (Users user : allUsers) {
            if (!updatedUsers.containsKey(user.getId())) {
                user.setTotalPoints(user.getTotalPoints() + betValue);
                updatedUsers.put(user.getId(), user);
                log.debug("Added 25 points for non-voting user {}", user.getId());
            }
        }

        // Save all updated users in a single batch
        userRepo.saveAll(updatedUsers.values());
        log.info("User points updated successfully.");
    }


    /**
     * Get list of all user to showcase leader board
     * @return list of users
     */
    public List<Users> getAllUser() {
        List<Users> users = userRepo.findAll(Sort.by(Sort.Direction.ASC, "totalPoints"));
        for (Users users1 : users) {
            List<UserVoting> userVotingList = userVotingRepo.findByUser_Id(users1.getId());
            int totalUserVote = userVotingList.size();
            AtomicInteger winningCount = new AtomicInteger();
            userVotingList.forEach(userVoting -> {
                if (Objects.equals(userVoting.getSelectedTeam(), userVoting.getMatchDetails().getMatchStatus())) {
                    winningCount.getAndIncrement();
                }
            });
            int winningPercentage = 0;
            if (totalUserVote > 0) {
                winningPercentage = (winningCount.get() * 100) / totalUserVote;
            }
            users1.setWiningPercentage(winningPercentage);
        }
        return users;
    }


    /**
     * Reset user password internally(only for admin) if user forgot there password
     * @param passwordResetDTO accept userName and newPassword
     * @return response message
     */
    public String resetUserPassword(PasswordResetDTO passwordResetDTO) {
        Users users = userRepo.findByUserName(passwordResetDTO.getUserName());
        if(users == null) {
            throw new UserNameNotFoundException("No user found with name " + passwordResetDTO.getUserName());
        }
        if(passwordResetDTO.getNewPassword().length() > 6) {
            users.setUserPassword(bCryptPasswordEncoder.encode(passwordResetDTO.getNewPassword()));
        } else {
            throw new RuntimeException("Length of password should be greater that 6");
        }
        userRepo.save(users);
        return "Password reset successfully for user " + passwordResetDTO.getUserName();
    }

}
