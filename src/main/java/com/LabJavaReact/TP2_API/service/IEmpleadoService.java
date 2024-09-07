package com.LabJavaReact.TP2_API.service;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;

import java.util.List;

public interface IEmpleadoService {
    public EmpleadoDTO obtenerEmpleado(long id);
    public List<EmpleadoDTO> obtenerEmpleados();
    public EmpleadoDTO guardarEmpleado(EmpleadoDTO empleadoDTO);
    public EmpleadoDTO actualizarEmpleado(long id, EmpleadoDTO dto);
    public void eliminarEmpleado(long id);
}
