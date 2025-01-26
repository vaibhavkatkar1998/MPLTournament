package com.project.MplTournament.repository;

import com.project.MplTournament.entity.TimeTableImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeTableImportRepo extends JpaRepository<TimeTableImport, Integer> {

    List<TimeTableImport> findAllByMatchDate(LocalDate matchDate);
}
