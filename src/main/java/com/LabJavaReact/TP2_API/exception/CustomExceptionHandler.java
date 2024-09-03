package com.LabJavaReact.TP2_API.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Intercepta las excepciones lanzadas en cualquier controlador de la aplicación, además hereda y sobreescribe
 * métodos para manejar las excepciones de una forma personalizada.
 */

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     *Método parte de la clase "ResponseEntityExceptionHandler", es llamado automáticamente por Spring cuando una
     * excepción "MethodArgumentNotValidException" es lanzada.
     * Personaliza la respuesta que se envía al cliente en caso de que ocurra una validación fallida.
     *
     * @param ex: es un objeto lanzado cuando un argumento de un método anotado como @Valid no pasa la validación,
     *          utilizado para acceder a los detalles de los errores de validación
     * @param headers: Encabezados HTTP que acompañan la solicitud o respuesta (contenedor de pares clave-valor)
     * @param status: Representa el código de estado HTTP que se debe devolver en la respuesta.
     * @param request: Representa la solicitud web actual. WebRequest es una interfaz abstracta que define métodos
     *               generales para interactuar con una solicitud HTTP.
     * @return un ResponseEntity<Object> que contiene el responseBody, los encabezados HTTP y el estado HTTP
     * dentro del responseBody se encuentra la lista(String) de errores, el timestamp y el estado HTTP(este status
     * es para informar al cliente).
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request){

        Map<String, Object> responseBody = new LinkedHashMap<>();
//        responseBody.put("timestamp", new Date()); //newDate(): captura el momento en el q fue instanciado
        responseBody.put("Status Code", status.value() + "(Bad Request)");

        //getBindingResult retorna un objeto bindingResult que contiene detalles sobre los resultados de la validacion
        //incluyendo los errores que se produjeron
        //getFieldErrors: retorna una lista de objetos FieldError, q son los errores de validación
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        responseBody.put("Mensaje", errors);

        return new ResponseEntity<>(responseBody, headers, status);
    }

    /**
     * Este método es un manejador de excepciones específico para la clase "BadCustomerRequestException", cuando se
     * lanza una excepción de este tipo en cualquier parte de la aplicación, Spring invocará automáticamente
     * este método para manejarla.
     * @param ex: objeto de la excepción que fue lanzada de tipo "BadCustomerRequestException".
     * @param request: Contiene información sobre la solicitud web que provocó la excepción.
     * @return un ResponseEntity que contiene el responseBody y el código de estado HTTP. Él response body
     * contiene el timestamp y el mensaje de error.
     */
    //Centraliza la lógica de manejo de excepciones, haciendo que la respuesta a ciertas excepciones sea
    //coherente en toda la aplicación.
    @ExceptionHandler(BadCustomerRequestException.class)
    public ResponseEntity<Object> handleBadCustomerRequestException(BadCustomerRequestException ex, WebRequest request){
        Map<String, Object> responseBody = new LinkedHashMap<>();
        int statusCode = ex.getStatus().value();
//        responseBody.put("timestamp", new Date());
        responseBody.put("Status Code", statusCode + " (Bad Request)");
        responseBody.put("Mensaje", ex.getMessage());

        return new ResponseEntity<>(responseBody, ex.getStatus());
    }

    @ExceptionHandler(ConflictStateResourceException.class)
    public ResponseEntity<Object> handleConflictStateResourceException(ConflictStateResourceException ex, WebRequest request){
        Map<String, Object> responseBody = new LinkedHashMap<>();
        int statusCode = ex.getStatus().value();
        responseBody.put("Status Code", statusCode + " (Conflict)");
        responseBody.put("Mensaje", ex.getMessage());

        return new ResponseEntity<>(responseBody, ex.getStatus());
    }


}
