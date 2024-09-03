package com.LabJavaReact.TP2_API.controller;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;
import com.LabJavaReact.TP2_API.service.impl.EmpleadoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.coyote.Response;
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

    @GetMapping("/empleado/{id}")
    public ResponseEntity<EmpleadoDTO> getEmpleadoById(@NotNull @PathVariable Long id){
        EmpleadoDTO empleadoDTO = empleadoService.getEmpleado(id);
        return ResponseEntity.ok(empleadoDTO);
    }
    @GetMapping("/empleado")
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

    @PutMapping("/empleado/{id}")
    public ResponseEntity<EmpleadoDTO> updateEmpleado(@NotNull @PathVariable long idEmpleado, @RequestBody EmpleadoDTO dto){
        EmpleadoDTO dtoModificado = empleadoService.updateAllEmpleado(idEmpleado, dto);
        return ResponseEntity.ok(dtoModificado);
    }


}
