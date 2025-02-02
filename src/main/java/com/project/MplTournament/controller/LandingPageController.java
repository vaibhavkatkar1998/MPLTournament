package com.project.MplTournament.controller;

import com.project.MplTournament.entity.TimeTableImport;
import com.project.MplTournament.service.LandingPageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class LandingPageController {

    private static final Logger log = LoggerFactory.getLogger(LandingPageController.class);

    @Autowired
    private LandingPageService landingPageService;

    @GetMapping("/todayMatches")
    public ResponseEntity<List<TimeTableImport>> getTodayMatches(){
        List<TimeTableImport> timeTableImport = landingPageService.getTodayMatches();
        return ResponseEntity.status(HttpStatus.OK).body(timeTableImport);
    }

}
