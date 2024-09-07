package com.LabJavaReact.TP2_API.controller;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;
import com.LabJavaReact.TP2_API.service.impl.EmpleadoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/empleado")
public class EmpleadoController {

    EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService){
        this.empleadoService = empleadoService;
    }

    @GetMapping(value="{id}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorId(@NotNull @PathVariable Long id){
        EmpleadoDTO empleadoDTO = empleadoService.obtenerEmpleado(id);
        return ResponseEntity.ok(empleadoDTO);
    }
    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> obtenerEmpleados(){
        List<EmpleadoDTO> empleados = empleadoService.obtenerEmpleados();
        return ResponseEntity.ok(empleados);
    }

    @PostMapping
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@Valid @RequestBody EmpleadoDTO empleadoDTO){
        EmpleadoDTO dto = empleadoService.guardarEmpleado(empleadoDTO);
        return ResponseEntity.created(URI.create("/empleado/" + dto.getId())).body(dto);
    }

    @PutMapping(value="{id}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@NotNull @PathVariable long id, @Valid @RequestBody EmpleadoDTO dto){
        EmpleadoDTO dtoModificado = empleadoService.actualizarEmpleado(id, dto);
        return ResponseEntity.ok(dtoModificado);
    }

    @DeleteMapping(value="{id}")
    public ResponseEntity<String> eliminarEmpleado(@NotNull @PathVariable long id){
        this.empleadoService.eliminarEmpleado(id);
        return ResponseEntity.noContent().build();

    }


}
