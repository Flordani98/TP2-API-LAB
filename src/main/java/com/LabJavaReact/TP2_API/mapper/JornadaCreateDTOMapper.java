package com.LabJavaReact.TP2_API.mapper;

import com.LabJavaReact.TP2_API.dto.JornadaCreateDTO;
import com.LabJavaReact.TP2_API.model.Concepto;
import com.LabJavaReact.TP2_API.model.Empleado;
import com.LabJavaReact.TP2_API.model.Jornada;

public class JornadaCreateDTOMapper {
    public static Jornada toEntity(JornadaCreateDTO dto, Empleado empleado, Concepto concepto){
        Jornada jornada = new Jornada();
        jornada.setEmpleado(empleado);
        jornada.setConceptoLaboral(concepto);
        jornada.setFecha(dto.getFecha());
        jornada.setHsTrabajadas(dto.getHsTrabajadas());

        return jornada;
    }

    public static JornadaCreateDTO toDTO(Jornada jornada){
        JornadaCreateDTO dto = new JornadaCreateDTO();
        dto.setIdEmpleado(jornada.getEmpleado().getId());
        dto.setIdConcepto(jornada.getConceptoLaboral().getId());
        dto.setFecha(jornada.getFecha());
        dto.setHsTrabajadas(jornada.getHsTrabajadas());

        return dto;
    }
}
