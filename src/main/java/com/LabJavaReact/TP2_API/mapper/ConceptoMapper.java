package com.LabJavaReact.TP2_API.mapper;

import com.LabJavaReact.TP2_API.dto.ConceptoDTO;
import com.LabJavaReact.TP2_API.model.Concepto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConceptoMapper {
    public static Concepto toEntity(ConceptoDTO dto){
        Concepto concepto = new Concepto();

        concepto.setNombre(dto.getNombre());
        concepto.setHsMaximo(dto.getHsMaximo());
        concepto.setHsMinimo(dto.getHsMinimo());
        concepto.setLaborable(dto.isLaborable());
        return concepto;
    }

    public static ConceptoDTO toDTO(Concepto concepto){
        ConceptoDTO dto = new ConceptoDTO();

        dto.setId(concepto.getId());
        dto.setNombre(concepto.getNombre());
        dto.setHsMaximo(concepto.getHsMaximo());
        dto.setHsMinimo(concepto.getHsMinimo());
        dto.setLaborable(concepto.isLaborable());

        return dto;
    }
}
