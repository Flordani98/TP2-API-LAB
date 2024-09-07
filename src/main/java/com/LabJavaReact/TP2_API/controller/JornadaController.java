package com.LabJavaReact.TP2_API.controller;

import com.LabJavaReact.TP2_API.dto.JornadaCreateDTO;
import com.LabJavaReact.TP2_API.dto.JornadaViewDTO;
import com.LabJavaReact.TP2_API.service.impl.JornadaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("jornada")
public class JornadaController {

    private JornadaService jornadaService;

    public JornadaController(JornadaService jornadaService){
        this.jornadaService = jornadaService;
    }

    @GetMapping
    public ResponseEntity<List<JornadaViewDTO>> obtenerJornadas(@RequestParam(required = false) Long nroDocumento,
                                                                @RequestParam(required = false) LocalDate fechaDesde,
                                                                @RequestParam(required = false) LocalDate fechaHasta){

        List<JornadaViewDTO> listaJornadas = jornadaService.obtenerJornadasFiltradas(nroDocumento, fechaDesde, fechaHasta);
        return ResponseEntity.ok(listaJornadas);

    }

    @PostMapping
    public ResponseEntity<JornadaViewDTO> crearJornada(@Valid @RequestBody JornadaCreateDTO jornadaCreateDTO){
        JornadaViewDTO jornadaDTO = jornadaService.guardarJornada(jornadaCreateDTO);
        return ResponseEntity.created(URI.create("/jornada/" + jornadaDTO.getId())).body(jornadaDTO);
    }


}
