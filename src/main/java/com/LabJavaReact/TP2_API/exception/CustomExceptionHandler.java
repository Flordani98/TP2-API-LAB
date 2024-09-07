package com.LabJavaReact.TP2_API.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Intercepta las excepciones lanzadas en cualquier controlador de la aplicación, además hereda y sobreescribe
 * métodos para manejar las excepciones de una forma personalizada.
 */

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    //region explicación por la que se añadieron los métodos: handleExceptionInternal y handleHandlerMethodValidation

    // Cuando se realizan las solicitudes POST de un empleado, si no se le pasa alguno de los atributos requeridos,
    // la excepción es manejada por el método sobreescrito de ResponseEntityExceptionHandler: handleMethodArgumentNotValid

    //Este método el cual me personaliza la respuesta, no funcionaba al realizar el PUT del empleado.
    //Cuando se realizaba el PUT y le pasaba un body sin algunos de los atributos requeridos
    // nunca se ejecutaba mi método handleMethodArgumentNotValid, sino que lo manejaba otra excepción
    //y me devolvía una respuesta genérica de la excepción.
    //
    //Me devolvía la respuesta de la excepción: HandlerMethodValidationException, para poder manejar esta excepción
    //y devolver una respuesta personalizada la solución que encontre fue implementar estos dos métodos:

    //-> handleExceptionInternal: método para el manejo de excepciones dentro de ResponseEntityExceptionHandler
    //como la excepción HandlerMethodValidationException es manejada por defecto por ResponseEntityExceptionHandler
    //para capturarla y redirigirla a un método personalizado dentro de mi clase CustomExceptionHandler,
    //la sobreescribí en mi clase.

    //-> handleHandlerMethodValidation: se encarga de manejar la excepción HandlerMethodValidationException
    //que surge cuando se validan los métodos de los controladores (en mi proyecto lo estoy utilizando en el put y post)
    //con este método se pudo manejar la excepción y me devuelve una respuesta personalizada.
    //
    //La razón principal por la que se implementaron fue debido a que handleMethodArgumentNotValid no me capturo,
    // ni manejo todas las excepciones de validación que se lanzaron, específicamente en el método updateEmpleado
    // (PUT) que fue donde me di cuenta del error.

    //endregion

    /** Para interceptar y personalizar las excepciones que de otro modo serían manejadas por defecto por Spring.
     * Específicamente se la está utilizando para manejar la excepción HandlerMethodValidationException.
     * Al sobrescribir handleExceptionInternal se puede capturar la excepción y redirigirla a un método personalizado
     * dentro del CustomExceptionHandler
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof HandlerMethodValidationException) {
            return handleHandlerMethodValidation((HandlerMethodValidationException) ex, headers, status, request);
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * Este es el método que se llama cuando se detecta una excepción HandlerMethodValidationException.
     * Personaliza la respuesta del error
     */
    private ResponseEntity<Object> handleHandlerMethodValidation(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = ex.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> responseBody = new LinkedHashMap<>();
        String statusName = HttpStatus.valueOf(status.value()).getReasonPhrase();

        responseBody.put("Status Code", HttpStatus.BAD_REQUEST.value() + " (" + statusName + ")");
        responseBody.put("Mensaje", errors);

        return new ResponseEntity<>(responseBody, headers, status);
    }

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
        String statusName = HttpStatus.valueOf(status.value()).getReasonPhrase();
//        responseBody.put("timestamp", new Date()); //newDate(): captura el momento en el q fue instanciado
        responseBody.put("Status Code", status.value() + " (" + statusName + ")");

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
        String statusName = HttpStatus.valueOf(statusCode).getReasonPhrase();

        responseBody.put("Status Code", statusCode + " (" + statusName + ")");
        responseBody.put("Mensaje", ex.getMessage());

        return new ResponseEntity<>(responseBody, ex.getStatus());
    }

    @ExceptionHandler(ConflictStateResourceException.class)
    public ResponseEntity<Object> handleConflictStateResourceException(ConflictStateResourceException ex, WebRequest request){
        Map<String, Object> responseBody = new LinkedHashMap<>();
        int statusCode = ex.getStatus().value();
        String statusName = HttpStatus.valueOf(statusCode).getReasonPhrase();

        responseBody.put("Status Code", statusCode + " (" + statusName + ")");
        responseBody.put("Mensaje", ex.getMessage());

        return new ResponseEntity<>(responseBody, ex.getStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        Map<String, Object> responseBody = new LinkedHashMap<>();
        int statusCode = ex.getStatus().value();
        String statusName = HttpStatus.valueOf(statusCode).getReasonPhrase();

        responseBody.put("Status Code", statusCode + " (" + statusName + ")");
        responseBody.put("Mensaje", ex.getMessage());

        return new ResponseEntity<>(responseBody, ex.getStatus());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleQueryParamConversionError(MethodArgumentTypeMismatchException ex) {

        Map<String, String> errorResponse = new LinkedHashMap<>();

        if(ex.getRequiredType() != null) {
            if ((ex.getName().equals("fechaDesde") || ex.getName().equals("fechaHasta"))
                    && ex.getRequiredType().equals(LocalDate.class)) {
                int statusCode = HttpStatus.BAD_REQUEST.value();
                String statusName = HttpStatus.valueOf(statusCode).getReasonPhrase();

                errorResponse.put("Status Code", statusCode + " (" + statusName + ")");
                errorResponse.put("Mensaje", "Los campos ‘fechaDesde’ y ‘fechaHasta’ deben respetar el formato yyyy-mm-dd.");

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            if (ex.getName().equals("nroDocumento") && ex.getRequiredType().equals(Long.class)) {
                int statusCode = HttpStatus.BAD_REQUEST.value();
                String statusName = HttpStatus.valueOf(statusCode).getReasonPhrase();

                errorResponse.put("Status Code", statusCode + " (" + statusName + ")");
                errorResponse.put("Mensaje", "El campo ‘nroDocumento’ solo puede contener números enteros.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

}


}
