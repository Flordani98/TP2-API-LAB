package com.LabJavaReact.TP2_API.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class EmpleadoDTO {

    private Long id;

    @NotNull
    private Long nroDocumento;

    @NotBlank(message = "Nombre es requerido")
    private String nombre;

    @NotBlank(message = "Apellido es requerido")
    private String apellido;

    @NotBlank(message = "Email es requerido")
    @Email
    private String email;

    @NotNull(message = "Fecha de nacimiento es requerido")
    @Past
    private LocalDate fechaNacimiento;

    @NotNull(message = "Fecha ingreso es requerido")
    private LocalDate fechaIngreso;

    private LocalDate fechaCreacion;





}
