package com.project.MplTournament.ExcpetionHandler;

public class VotingTimeExceedException extends RuntimeException {
    public VotingTimeExceedException(String message) {
        super(message);
    }
}
