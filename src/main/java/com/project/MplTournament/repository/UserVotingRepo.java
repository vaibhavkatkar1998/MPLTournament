package com.project.MplTournament.repository;

import com.project.MplTournament.entity.UserVoting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserVotingRepo extends JpaRepository<UserVoting, Integer> {

    Optional<UserVoting> findByMatchDetails_IdAndUserId(Integer matchId,Integer userId);

    List<UserVoting> findByMatchDetails_Id(Integer id);

    List<UserVoting> findByUser_Id(Integer id);


    List<UserVoting> findTop10ByUserIdOrderByVotedOnDesc(Integer userId);
}
