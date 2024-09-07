package com.LabJavaReact.TP2_API.service;

import com.LabJavaReact.TP2_API.dto.JornadaCreateDTO;
import com.LabJavaReact.TP2_API.dto.JornadaViewDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface IJornadaService {

    public List<JornadaViewDTO> obtenerJornadas();

    public List<JornadaViewDTO> obtenerJornadasFiltradas(Long nroDocumento, LocalDate fechaDesde, LocalDate fechaHasta);
    public JornadaViewDTO guardarJornada(JornadaCreateDTO jornadaCreateDTO);
}
