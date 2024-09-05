package com.LabJavaReact.TP2_API.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "concepto_laboral")
public class Concepto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "hs_maximo")
    private Integer hsMaximo;

    @Column(name = "hs_minimo")
    private Integer hsMinimo;

    @NotNull
    private boolean laborable;

    @NotBlank
    @Column(nullable = false)
    private String nombre;


}
