package com.LabJavaReact.TP2_API.mapper;

import com.LabJavaReact.TP2_API.dto.JornadaCreateDTO;
import com.LabJavaReact.TP2_API.dto.JornadaViewDTO;
import com.LabJavaReact.TP2_API.model.Concepto;
import com.LabJavaReact.TP2_API.model.Empleado;
import com.LabJavaReact.TP2_API.model.Jornada;

public class JornadaViewDTOMapper {
    public static Jornada toEntity(JornadaViewDTO dto, Empleado empleado, Concepto concepto){
        Jornada jornada = new Jornada();
        jornada.setEmpleado(empleado);
        jornada.setConceptoLaboral(concepto);
        jornada.setFecha(dto.getFecha());
        jornada.setHsTrabajadas(dto.getHsTrabajadas());

        return jornada;
    }

    public static JornadaViewDTO toDTO(Jornada jornada){
        JornadaViewDTO dto = new JornadaViewDTO();
        dto.setId(jornada.getId());
        dto.setNroDocumento(jornada.getEmpleado().getNroDocumento());
        dto.setNombreCompleto(jornada.getEmpleado().getNombre() + " " + jornada.getEmpleado().getApellido());
        dto.setFecha(jornada.getFecha());
        dto.setConcepto(jornada.getConceptoLaboral().getNombre());
        dto.setHsTrabajadas(jornada.getHsTrabajadas());

        return dto;
    }
}
