package com.project.MplTournament.ExcpetionHandler;

public class UsernameMismatchException extends RuntimeException {
    public UsernameMismatchException(String message) {
        super(message);
    }
}