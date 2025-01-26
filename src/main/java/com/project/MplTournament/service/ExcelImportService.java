package com.project.MplTournament.service;

import com.project.MplTournament.dto.TimeTableImportDTO;
import com.project.MplTournament.entity.TimeTableImport;
import com.project.MplTournament.repository.ExcelImportRepo;
import com.project.MplTournament.utility.ExcelParser;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ExcelImportService {

    @Autowired
    private ExcelParser excelParser;

    @Autowired
    private ExcelImportRepo excelImportRepo;

    private static final Logger log = LoggerFactory.getLogger(ExcelImportService.class);
    public void processExcelFile(MultipartFile excelFile) throws IOException {

        List<TimeTableImportDTO> timeTableImportDTOList = excelParser.parseTimeTable(excelFile);

        for(TimeTableImportDTO timeTableImportDTO : timeTableImportDTOList) {
            TimeTableImport timeTableImport = new TimeTableImport();
            timeTableImport.setMatchStatus(2);
            timeTableImport.setStadium(timeTableImportDTO.getStadium());
            timeTableImport.setMatchName(timeTableImportDTO.getMatchName());
            timeTableImport.setMatchDateAndTime(LocalDateTime.of(timeTableImportDTO.getDate(),timeTableImportDTO.getTime()));
            excelImportRepo.save(timeTableImport);
        }
        log.info("Started processing excel import");
    }
}
