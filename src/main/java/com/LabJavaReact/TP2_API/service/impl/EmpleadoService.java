package com.LabJavaReact.TP2_API.service.impl;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;
import com.LabJavaReact.TP2_API.exception.BadCustomerRequestException;
import com.LabJavaReact.TP2_API.mapper.EmpleadoMapper;
import com.LabJavaReact.TP2_API.model.Empleado;
import com.LabJavaReact.TP2_API.repository.EmpleadoRepository;
import com.LabJavaReact.TP2_API.service.IEmpleadoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import static com.LabJavaReact.TP2_API.mapper.EmpleadoMapper.toEntity;

@Service
public class EmpleadoService implements IEmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository){
        this.repository = repository;
    }


    @Override
    public EmpleadoDTO getEmpleado(int id) {
        return null;
    }

    @Override
    public List<EmpleadoDTO> getEmpleados() {
        List<Empleado> empleados = repository.findAll();
        return empleados.stream().map(EmpleadoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public EmpleadoDTO saveEmpleado(EmpleadoDTO empleadoDTO) {
        int edad = calcularEdad(empleadoDTO.getFechaNacimiento());
        if(edad < 18){
            throw new BadCustomerRequestException("La edad del empleado no puede ser menor a 18 años");
        }

        Empleado empleado = repository.save(toEntity(empleadoDTO));
        return EmpleadoMapper.toDTO(empleado);
    }

    private int calcularEdad(LocalDate fechaNacimiento){
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
