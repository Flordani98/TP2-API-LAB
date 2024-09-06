package com.LabJavaReact.TP2_API.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JornadaCreateDTO {

    @NotNull(message = "'idEmpleado' es obligatorio")
    private Long idEmpleado;
    @NotNull(message = "'idConcepto' es obligatorio")
    private Long idConcepto;
    @NotNull(message = "'fecha' es obligatorio")
    private LocalDate fecha;
    private Integer hsTrabajadas;
}
