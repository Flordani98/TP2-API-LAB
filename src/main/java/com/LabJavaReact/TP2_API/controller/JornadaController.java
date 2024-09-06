package com.LabJavaReact.TP2_API.controller;

import com.LabJavaReact.TP2_API.dto.JornadaCreateDTO;
import com.LabJavaReact.TP2_API.dto.JornadaViewDTO;
import com.LabJavaReact.TP2_API.service.impl.JornadaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("jornada")
public class JornadaController {

    private JornadaService jornadaService;

    public JornadaController(JornadaService jornadaService){
        this.jornadaService = jornadaService;
    }

    @GetMapping
    public ResponseEntity<List<JornadaViewDTO>> obtenerJornadas(){
        List<JornadaViewDTO> listaJornadas = jornadaService.obtenerJornadas();
        return ResponseEntity.ok(listaJornadas);
    }

    @PostMapping
    public ResponseEntity<JornadaViewDTO> crearJornada(@Valid @RequestBody JornadaCreateDTO jornadaCreateDTO){
        JornadaViewDTO jornadaDTO = jornadaService.guardarJornada(jornadaCreateDTO);
        return ResponseEntity.created(URI.create("/jornada/" + jornadaDTO.getId())).body(jornadaDTO);
    }


}
