package com.project.MplTournament.service;

import com.project.MplTournament.dto.TimeTableImportDTO;
import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.repository.MatchRepo;
import com.project.MplTournament.utility.ExcelParser;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ExcelImportService {

    @Autowired
    private ExcelParser excelParser;

    @Autowired
    private MatchRepo matchRepo;

    private static final Logger log = LoggerFactory.getLogger(ExcelImportService.class);
    public void processExcelFile(MultipartFile excelFile) throws IOException {

        List<TimeTableImportDTO> timeTableImportDTOList = excelParser.parseTimeTable(excelFile);

        for(TimeTableImportDTO timeTableImportDTO : timeTableImportDTOList) {
            MatchDetails matchDetails = new MatchDetails();
            matchDetails.setMatchStatus("No result");
            matchDetails.setStadium(timeTableImportDTO.getStadium());
            matchDetails.setTeam1(timeTableImportDTO.getTeam1());
            matchDetails.setTeam2(timeTableImportDTO.getTeam2());
            matchDetails.setMatchDate(timeTableImportDTO.getDate());
            matchDetails.setMatchTime(timeTableImportDTO.getTime());
            matchRepo.save(matchDetails);
        }
    }
}
