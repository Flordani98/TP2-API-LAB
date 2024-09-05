package com.LabJavaReact.TP2_API.validation;

import com.LabJavaReact.TP2_API.exception.BadCustomerRequestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidadorNombre {
    private static final String NOMBRE_REGEX = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1\\s]+$";
    private static final Pattern NOMBRE_PATTERN = Pattern.compile(NOMBRE_REGEX);


    public static void validarNombre(String nombre, String nombreAtributo){
        Matcher matcher = NOMBRE_PATTERN.matcher(nombre);
        if (!matcher.matches()) {
            throw new BadCustomerRequestException("Solo se permiten letras en el campo " + "'" + nombreAtributo + "'");
        }
    }
}
