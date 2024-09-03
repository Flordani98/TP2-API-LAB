package com.LabJavaReact.TP2_API.exception;

import org.springframework.http.HttpStatus;

public class ConflictStateResourceException extends RuntimeException{
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;
    public ConflictStateResourceException(String message) {super(message);}

    public HttpStatus getStatus(){
        return STATUS;
    }
}
