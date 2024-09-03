package com.LabJavaReact.TP2_API.service.impl;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;
import com.LabJavaReact.TP2_API.exception.BadCustomerRequestException;
import com.LabJavaReact.TP2_API.exception.ConflictStateResourceException;
import com.LabJavaReact.TP2_API.mapper.EmpleadoMapper;
import com.LabJavaReact.TP2_API.model.Empleado;
import com.LabJavaReact.TP2_API.repository.EmpleadoRepository;
import com.LabJavaReact.TP2_API.service.IEmpleadoService;
import com.LabJavaReact.TP2_API.validation.EmailValidator;
import com.LabJavaReact.TP2_API.validation.NombreValidator;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ConcurrentModificationException;
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
        validarEmpleado(empleadoDTO);
        Empleado empleado = repository.save(toEntity(empleadoDTO));
        return EmpleadoMapper.toDTO(empleado);
    }

    private void validarEmpleado(EmpleadoDTO empleadoDTO){
        verificarEmailEmpleado(empleadoDTO.getEmail());
        verificarEdadEmpleado(empleadoDTO.getFechaNacimiento());
        verificarFechaIngreso(empleadoDTO.getFechaIngreso());

        NombreValidator.validateNombre(empleadoDTO.getNombre(), "nombre");
        NombreValidator.validateNombre(empleadoDTO.getApellido(), "apellido");

        if(repository.existsByNroDocumento(empleadoDTO.getNroDocumento())){
            throw new ConflictStateResourceException("Ya existe un empleado con el documento ingresado");
        }

    }

    public void verificarEdadEmpleado(LocalDate fechaNacimiento){

        verificarFechaNoPosterior(fechaNacimiento, "La fecha de nacimiento no puede ser posterior al día de la fecha.");

        int edad = calcularEdad(fechaNacimiento);
        if(edad < 18){
            throw new BadCustomerRequestException("La edad del empleado no puede ser menor a 18 años");
        }
    }
    public void verificarEmailEmpleado(String email){
        EmailValidator.validateEmail(email);

        if(repository.existsByEmail(email)){
            throw new ConflictStateResourceException("Ya existe un empleado con el email ingresado");
        }
    }

    private int calcularEdad(LocalDate fechaNacimiento){
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    private void verificarFechaIngreso(LocalDate fechaIngreso){
        verificarFechaNoPosterior(fechaIngreso,"La fecha de ingreso no puede ser posterior al día de la fecha." );
    }

    private void verificarFechaNoPosterior(LocalDate fecha, String mensajeError){
        if(fecha.isAfter(LocalDate.now())){
            throw new BadCustomerRequestException(mensajeError);
        }
    }

}
