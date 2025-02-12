package com.project.MplTournament.repository;

import com.project.MplTournament.entity.UserVoting;
import com.project.MplTournament.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    Users findByUserName(String username);

    Optional<Users> findByUserNameAndUserPassword(String userName, String userPassword);
}
