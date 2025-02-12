package com.project.MplTournament.repository;

import com.project.MplTournament.entity.MatchDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepo extends JpaRepository<MatchDetails, Integer> {

    List<MatchDetails> findAllByMatchDate(LocalDate matchDate);

    List<MatchDetails> findAllByMatchDateBetween(LocalDate yesterdayDate, LocalDate todayDate);
}
