package com.project.MplTournament.utility;

import com.project.MplTournament.ExcpetionHandler.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<String> handleExpiredToken(ExpiredTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Token expired: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid token: " + ex.getMessage());
    }

    @ExceptionHandler(UsernameMismatchException.class)
    public ResponseEntity<String> handleUsernameMismatch(UsernameMismatchException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Username mismatch: " + ex.getMessage());
    }

    @ExceptionHandler(UserNameNotFoundException.class)
    public ResponseEntity<String> handleUserNameNotFound(UserNameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Username not found: " + ex.getMessage());
    }

    @ExceptionHandler(VotingTimeExceedException.class)
    public ResponseEntity<String> handleVotingTimeExceeds(VotingTimeExceedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Voting Time Exceeds: " + ex.getMessage());
    }

    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<String> handleMatchNotFound(MatchNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Match details not found: " + ex.getMessage());
    }

    @ExceptionHandler(MatchResultUpdateException.class)
    public ResponseEntity<String> matchResultUpdateException(MatchResultUpdateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Match result update exception: " + ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> runTimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Run Time Exception: " + ex.getMessage());
    }
}
