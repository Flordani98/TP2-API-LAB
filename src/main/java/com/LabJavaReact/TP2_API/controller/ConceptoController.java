package com.LabJavaReact.TP2_API.controller;

import com.LabJavaReact.TP2_API.dto.ConceptoDTO;
import com.LabJavaReact.TP2_API.service.impl.ConceptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/concepto")
public class ConceptoController {

    ConceptoService conceptoService;

    public ConceptoController(ConceptoService conceptoService){
        this.conceptoService = conceptoService;
    }


    @GetMapping
    public ResponseEntity<List<ConceptoDTO>> obtenerConceptosFiltrados(@RequestParam(required = false) Long id,
                                                                       @RequestParam(required = false) String nombre){
        List<ConceptoDTO> conceptos = conceptoService.obtenerConceptosFiltrados(id, nombre);
        return ResponseEntity.ok(conceptos);
    }


}
