package com.infodation.userservice.components;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}