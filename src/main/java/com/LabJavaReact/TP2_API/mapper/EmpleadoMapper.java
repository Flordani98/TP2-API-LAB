package com.LabJavaReact.TP2_API.mapper;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;
import com.LabJavaReact.TP2_API.model.Empleado;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EmpleadoMapper {
    public static Empleado toEntity(EmpleadoDTO dto){
        Empleado empleado = new Empleado();
        empleado.setNroDocumento(dto.getNroDocumento());
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setEmail(dto.getEmail());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setFechaIngreso(dto.getFechaIngreso());
        return empleado;
    }

    public static EmpleadoDTO toDTO(Empleado empleado){
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(empleado.getId());
        dto.setNroDocumento(empleado.getNroDocumento());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setEmail(empleado.getEmail());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setFechaIngreso(empleado.getFechaIngreso());
        dto.setFechaCreacion(empleado.getFechaCreacion());

        return dto;
    }
}
