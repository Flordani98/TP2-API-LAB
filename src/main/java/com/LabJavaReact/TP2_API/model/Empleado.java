package com.LabJavaReact.TP2_API.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nro_documento", nullable = false, unique = true)
    private Long nroDocumento;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false)
    private String apellido;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotNull
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @NotNull
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @NotNull
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    public Empleado() {
        this.fechaCreacion = LocalDate.now();
    }



}

