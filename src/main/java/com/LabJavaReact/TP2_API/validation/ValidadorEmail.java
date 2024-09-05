package com.LabJavaReact.TP2_API.validation;

import com.LabJavaReact.TP2_API.exception.BadCustomerRequestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidadorEmail {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


    public static void validarEmail(String email){
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new BadCustomerRequestException("El email ingresado no es correcto");
        }
    }


}
