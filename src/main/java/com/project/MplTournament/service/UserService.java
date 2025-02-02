package com.project.MplTournament.service;


import com.project.MplTournament.ExcpetionHandler.UserNameNotFoundException;
import com.project.MplTournament.entity.UserPrincipal;
import com.project.MplTournament.entity.Users;
import com.project.MplTournament.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepo userRepo;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public String registerUser(Users user) {
        user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));
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
}
