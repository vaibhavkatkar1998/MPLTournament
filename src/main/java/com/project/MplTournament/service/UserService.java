package com.project.MplTournament.service;


import com.project.MplTournament.ExcpetionHandler.UserNameNotFoundException;
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

import java.util.List;
import java.util.Optional;

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

    public String registerUser(Users user) {
        user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));
        user.setRole("User");
        user.setTotalPoints(0);
        Users response = userRepo.save(user);
        if(response.getUserName() != null) {
            return "User register successfully";
        }
        return "Error while adding user";
    }

    public String verifyUser(Users user) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(),user.getUserPassword()));
            if(authentication.isAuthenticated()) {
                // Cast to your custom UserPrincipal class
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                Users users = userPrincipal.getUser();
                return jwtService.generateToken(user.getUserName(), users.getRole(), users.getId());
            }
        } catch (Exception e){
            throw new UserNameNotFoundException("Invalid user");
        }
        return "";
    }

    public void updateUserPoints(MatchDetails matchDetailsResponse, Integer betValue) {
        log.info("Find user voting by match id {}", matchDetailsResponse.getId());
        List<UserVoting> userVotingList = userVotingRepo.findByMatchDetails_Id(matchDetailsResponse.getId());
        if(!userVotingList.isEmpty()) {
            for (UserVoting userVoting : userVotingList) {
                // checking if match status and user voted for status is not same
                if(!userVoting.getSelectedTeam().equals(matchDetailsResponse.getMatchStatus())) {
                    Optional<Users> user = userRepo.findById(userVoting.getUserId());
                    if(user.isPresent()) {
                        Users users = user.get();
                        // updating user point if voting and result is same
                        users.setTotalPoints(users.getTotalPoints() + betValue);
                        userRepo.save(users);
                        log.info("Points updated for user {}", userVoting.getUserId());
                    } else {
                        log.error("User not found by user id {}",userVoting.getUserId());
                        throw new UserNameNotFoundException("User not present");
                    }
                }
            }
        }
    }

    public List<Users> getAllUser() {
        return userRepo.findAll(Sort.by(Sort.Direction.DESC, "totalPoints"));
    }

}
