package com.LabJavaReact.TP2_API.service.impl;

import com.LabJavaReact.TP2_API.dto.EmpleadoDTO;
import com.LabJavaReact.TP2_API.exception.BadCustomerRequestException;
import com.LabJavaReact.TP2_API.exception.ConflictStateResourceException;
import com.LabJavaReact.TP2_API.exception.ResourceNotFoundException;
import com.LabJavaReact.TP2_API.mapper.EmpleadoMapper;
import com.LabJavaReact.TP2_API.model.Empleado;
import com.LabJavaReact.TP2_API.model.Jornada;
import com.LabJavaReact.TP2_API.repository.EmpleadoRepository;
import com.LabJavaReact.TP2_API.repository.JornadaRepository;
import com.LabJavaReact.TP2_API.service.IEmpleadoService;
import com.LabJavaReact.TP2_API.validation.ValidadorEmail;
import com.LabJavaReact.TP2_API.validation.ValidadorNombre;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.LabJavaReact.TP2_API.mapper.EmpleadoMapper.toDTO;
import static com.LabJavaReact.TP2_API.mapper.EmpleadoMapper.toEntity;

@Service
public class EmpleadoService implements IEmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final JornadaRepository jornadaRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, JornadaRepository jornadaRepository){
        this.empleadoRepository = empleadoRepository;
        this.jornadaRepository = jornadaRepository;
    }


    @Override
    public EmpleadoDTO obtenerEmpleado(long id) {
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        if(empleado.isPresent()){
            return toDTO(empleado.get());
        } else{
            throw new ResourceNotFoundException("No se encontro el empleado con Id: " + id);
        }

    }
    @Override
    public List<EmpleadoDTO> obtenerEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        return empleados.stream().map(EmpleadoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public EmpleadoDTO guardarEmpleado(EmpleadoDTO empleadoDTO) {
        validarEmpleado(empleadoDTO);
        Empleado empleado = empleadoRepository.save(toEntity(empleadoDTO));
        return toDTO(empleado);
    }

    @Override
    public EmpleadoDTO actualizarEmpleado(long id, EmpleadoDTO dto) {
        Empleado empleadoExistente = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el empleado con Id: " + id));

        validarEmpleado(dto);

        empleadoExistente.setNroDocumento(dto.getNroDocumento());
        empleadoExistente.setNombre(dto.getNombre());
        empleadoExistente.setApellido(dto.getApellido());
        empleadoExistente.setEmail(dto.getEmail());
        empleadoExistente.setFechaNacimiento(dto.getFechaNacimiento());
        empleadoExistente.setFechaIngreso(dto.getFechaIngreso());

        Empleado empleadoModificado = empleadoRepository.save(empleadoExistente);

        return toDTO(empleadoModificado);
    }

    @Override
    public void eliminarEmpleado(long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro al empleado con id: " + id));

        boolean existeJornadaEmpleado = this.jornadaRepository.existsByEmpleado(empleado);

        if(existeJornadaEmpleado){
            throw new BadCustomerRequestException("No es posible eliminar un empleado con jornadas asociadas");
        }
        this.empleadoRepository.deleteById(id);
    }


    private void validarEmpleado(EmpleadoDTO empleadoDTO){
        verificarEmailEmpleado(empleadoDTO.getEmail());
        verificarEdadEmpleado(empleadoDTO.getFechaNacimiento());
        verificarFechaIngreso(empleadoDTO.getFechaIngreso());

        ValidadorNombre.validarNombre(empleadoDTO.getNombre(), "nombre");
        ValidadorNombre.validarNombre(empleadoDTO.getApellido(), "apellido");

        if(empleadoRepository.existsByNroDocumento(empleadoDTO.getNroDocumento())){
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
        ValidadorEmail.validarEmail(email);

        if(empleadoRepository.existsByEmail(email)){
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
