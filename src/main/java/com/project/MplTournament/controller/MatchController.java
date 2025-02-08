package com.project.MplTournament.controller;


import com.project.MplTournament.dto.MatchDetailsDTO;
import com.project.MplTournament.service.ExcelImportService;
import com.project.MplTournament.service.MatchService;
import com.project.MplTournament.service.UserVotingService;
import com.project.MplTournament.utility.Constants;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class MatchController {

    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private MatchService matchService;


    @PostMapping("/importExcel")
    public ResponseEntity<String> importMatchesFromExcel(@RequestParam("excelFile") MultipartFile excelFile) {
        try {
            String fileType = excelFile.getContentType();
            if(!Constants.NON_ALLOWED_EXTENSION.equals(fileType)) {
                log.error("Valid excel file is not provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload valid excel file with .xlsx type");
            };
            excelImportService.processExcelFile(excelFile);
        } catch (Exception e) {
            log.error("Error while parsing excel with exception :- {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
    }

    @PostMapping("/updateMatchResult")
    public ResponseEntity<String> updateMatchResult(@RequestBody MatchDetailsDTO matchDetailsDTO) {
        matchService.updateMatchResult(matchDetailsDTO);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }


}
