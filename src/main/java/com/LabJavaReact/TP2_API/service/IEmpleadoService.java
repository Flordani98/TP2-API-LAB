package com.LabJavaReact.TP2_API.service;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;

import java.util.List;

public interface IEmpleadoService {
    public EmpleadoDTO getEmpleado(int id);
    public List<EmpleadoDTO> getEmpleados();
    public EmpleadoDTO saveEmpleado(EmpleadoDTO empleadoDTO);
}
