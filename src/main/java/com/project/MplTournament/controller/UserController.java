package com.project.MplTournament.controller;


import com.project.MplTournament.entity.Users;
import com.project.MplTournament.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Users users){
        String response = userService.registerUser(users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/custom-login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Users users){
        String response = userService.verifyUser(users);
        Map<String, String> token = new HashMap<>();
        token.put("token", response);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<Users>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser());
    }
}
