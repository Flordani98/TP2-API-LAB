package com.LabJavaReact.TP2_API.service;

import com.LabJavaReact.TP2_API.dto.JornadaCreateDTO;
import com.LabJavaReact.TP2_API.dto.JornadaViewDTO;

import java.util.List;

public interface IJornadaService {

    public List<JornadaViewDTO> obtenerJornadas();
    public JornadaViewDTO guardarJornada(JornadaCreateDTO jornadaCreateDTO);
}
