package com.project.MplTournament.repository;

import com.project.MplTournament.entity.TimeTableImport;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelImportRepo extends JpaRepository<TimeTableImport, Integer> {
}
