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

    @NotNull(message = "'nroDocumento' es obligatorio.")
    private Long nroDocumento;

    @NotBlank(message = "'nombre' es obligatorio.")
    private String nombre;

    @NotBlank(message = "'apellido' es obligatorio.")
    private String apellido;

    @NotBlank(message = "'email' es obligatorio.")
    private String email;

    @NotNull(message = "'fecha de nacimiento' es obligatorio.")
    private LocalDate fechaNacimiento;

    @NotNull(message = "'fecha ingreso' es obligatorio.")
    private LocalDate fechaIngreso;

    private LocalDate fechaCreacion;





}
