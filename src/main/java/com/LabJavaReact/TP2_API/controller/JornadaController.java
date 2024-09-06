package com.LabJavaReact.TP2_API.controller;

import com.LabJavaReact.TP2_API.dto.JornadaViewDTO;
import com.LabJavaReact.TP2_API.service.impl.JornadaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
