package com.LabJavaReact.TP2_API.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException{
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    public ResourceNotFoundException(String message) {super(message);}

    public HttpStatus getStatus(){
        return STATUS;
    }
}
