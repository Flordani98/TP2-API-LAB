package com.LabJavaReact.TP2_API.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JornadaCreateDTO {

    @NotNull
    private Long idEmpleado;
    @NotNull
    private Long idConcepto;
    @NotNull
    private LocalDate fecha;
    private Integer hsTrabajadas;
}
