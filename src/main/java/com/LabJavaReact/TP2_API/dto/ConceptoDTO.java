package com.LabJavaReact.TP2_API.dto;

import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConceptoDTO {
    private Long id;

    @NotBlank
    private String nombre;

    private Integer hsMinimo;
    private Integer hsMaximo;

    @NotNull
    private boolean laborable;
}
