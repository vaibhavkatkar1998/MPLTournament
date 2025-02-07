package com.project.MplTournament.utility;

import com.project.MplTournament.dto.TimeTableImportDTO;
import com.project.MplTournament.service.ExcelImportService;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configuration
public class ExcelParser {

    private static final Logger log = LoggerFactory.getLogger(ExcelParser.class);

    public List<TimeTableImportDTO> parseTimeTable(MultipartFile matchScheduleExcelFile) throws IOException {
        List<TimeTableImportDTO> timeTableImports = new ArrayList<>();
        log.info("Started processing excel import");
        try(InputStream inputStream = matchScheduleExcelFile.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            // getting data from first sheet
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            // Skip header row
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if(rowIterator.hasNext()) {
                    // Extract data from each row
                    String date = getCellValue(row.getCell(0),0);
                    String time = getCellValue(row.getCell(1),1);
                    String team1 = getCellValue(row.getCell(2),2);
                    String team2 = getCellValue(row.getCell(3),3);
                    String matchStadium = getCellValue(row.getCell(4),4);

                    // Create a new MatchDTO and populate it
                    TimeTableImportDTO timeTableImportDTO = new TimeTableImportDTO();
                    if(!date.isEmpty() && !time.isEmpty()) {
                        timeTableImportDTO.setDate(LocalDate.parse(date));
                        timeTableImportDTO.setTime(LocalTime.parse(time));
                    } else {
                        log.error("Date or Time should be null in excel row {}",row.getRowNum());
                        throw new IllegalArgumentException("Date or Time is missing in Excel row " + row.getRowNum());
                    }
                    timeTableImportDTO.setTeam1(team1);
                    timeTableImportDTO.setTeam2(team2);
                    timeTableImportDTO.setStadium(matchStadium);

                    // Add the MatchDTO to the list
                    timeTableImports.add(timeTableImportDTO);
                }
            }
            log.info("Ended processing excel import");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return timeTableImports;
    }


    private String getCellValue(Cell cell, int index) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> handleDiffRentCaseOfNumeric(cell, index);
            default -> "";
        };
    }

    private String handleDiffRentCaseOfNumeric(Cell cell, int index) {
        if (DateUtil.isCellDateFormatted(cell)) {
            // condition for date and time
            if(index == 0) {
                return cell.getLocalDateTimeCellValue().toLocalDate().toString(); // Returns ISO-8601 format
            } else {
                return cell.getLocalDateTimeCellValue().toLocalTime().toString();
            }
        } else {
            return String.valueOf(cell.getNumericCellValue());
        }
    }

}
