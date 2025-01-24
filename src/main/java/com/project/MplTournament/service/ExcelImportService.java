package com.project.MplTournament.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ExcelImportService {

    private static final Logger log = LoggerFactory.getLogger(ExcelImportService.class);
    public void processExcelFile(MultipartFile excelFile) {
        log.info("Started processing excel import");
    }
}
