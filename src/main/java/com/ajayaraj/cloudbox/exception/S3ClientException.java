package com.ajayaraj.cloudbox.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class S3ClientException extends RuntimeException{
    @Getter
    HttpStatus status;

    public S3ClientException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
