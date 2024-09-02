package com.LabJavaReact.TP2_API.controller;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;
import com.LabJavaReact.TP2_API.service.impl.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class EmpleadoController {

    EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService){
        this.empleadoService = empleadoService;
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<EmpleadoDTO>> getEmpleados(){
        List<EmpleadoDTO> empleados = empleadoService.getEmpleados();
        return ResponseEntity.ok(empleados);
    }

//    @GetMapping("empleado/{id}")
//    public ResponseEntity<EmpleadoDTO> getEmpleado(){
//
//    }

    @PostMapping("/empleado")
    public ResponseEntity<EmpleadoDTO> addEmpleado(@Valid @RequestBody EmpleadoDTO empleadoDTO){
        EmpleadoDTO dto = empleadoService.saveEmpleado(empleadoDTO);
        return ResponseEntity.created(URI.create("/empleado/" + dto.getId())).body(dto);
    }


}
