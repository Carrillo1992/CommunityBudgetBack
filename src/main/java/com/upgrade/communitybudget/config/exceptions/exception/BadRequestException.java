package com.upgrade.communitybudget.config.exceptions.exception;


public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
