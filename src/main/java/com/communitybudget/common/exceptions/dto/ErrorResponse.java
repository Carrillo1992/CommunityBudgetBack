package com.communitybudget.common.exceptions.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String message;

    public ErrorResponse(final int status, final String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

}
