package com.LabJavaReact.TP2_API.exception;

import org.springframework.http.HttpStatus;

public class BadCustomerRequestException extends RuntimeException{
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    public BadCustomerRequestException(String message) {super(message);}

    public HttpStatus getStatus(){
        return STATUS;
    }

}
