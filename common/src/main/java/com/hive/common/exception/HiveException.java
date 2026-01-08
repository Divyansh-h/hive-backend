package com.hive.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HiveException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public HiveException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HiveException(String message, HttpStatus status) {
        this(message, status, "INTERNAL_ERROR");
    }
}
