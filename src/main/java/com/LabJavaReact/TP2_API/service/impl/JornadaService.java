package com.LabJavaReact.TP2_API.service.impl;

import com.LabJavaReact.TP2_API.dto.JornadaCreateDTO;
import com.LabJavaReact.TP2_API.dto.JornadaViewDTO;
import com.LabJavaReact.TP2_API.exception.BadCustomerRequestException;
import com.LabJavaReact.TP2_API.exception.ResourceNotFoundException;
import com.LabJavaReact.TP2_API.mapper.JornadaCreateDTOMapper;
import com.LabJavaReact.TP2_API.mapper.JornadaViewDTOMapper;
import com.LabJavaReact.TP2_API.model.Concepto;
import com.LabJavaReact.TP2_API.model.Empleado;
import com.LabJavaReact.TP2_API.model.Jornada;
import com.LabJavaReact.TP2_API.repository.ConceptoRepository;
import com.LabJavaReact.TP2_API.repository.EmpleadoRepository;
import com.LabJavaReact.TP2_API.repository.JornadaRepository;
import com.LabJavaReact.TP2_API.service.IJornadaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JornadaService implements IJornadaService {
    private JornadaRepository jornadaRepository;
    private EmpleadoRepository empleadoRepository;
    private ConceptoRepository conceptoRepository;

    public JornadaService(JornadaRepository jornadaRepository, EmpleadoRepository empleadoRepository,
                          ConceptoRepository conceptoRepository ){

        this.jornadaRepository = jornadaRepository;
        this.empleadoRepository = empleadoRepository;
        this.conceptoRepository = conceptoRepository;

    }
    @Override
    public List<JornadaViewDTO> obtenerJornadas() {
        List<Jornada> jornadas = jornadaRepository.findAll();
        List<JornadaViewDTO> jornadaViewDTOS = new ArrayList<>();

        for (Jornada jornada: jornadas) {
            jornadaViewDTOS.add(JornadaViewDTOMapper.toDTO(jornada));

        }
        return jornadaViewDTOS;
    }

    @Override
    public JornadaViewDTO guardarJornada(JornadaCreateDTO jornadaCreateDTO) {

        Empleado empleado = empleadoRepository.findById(jornadaCreateDTO.getIdEmpleado())
                .orElseThrow(() -> new BadCustomerRequestException("No existe el empleado ingresado"));
        Concepto concepto = conceptoRepository.findById(jornadaCreateDTO.getIdConcepto())
                .orElseThrow(() -> new BadCustomerRequestException("No existe el concepto ingresado"));

        validarHsTrabajadasSegunConcepto(concepto.getNombre(), jornadaCreateDTO.getHsTrabajadas());

        Jornada jornada = jornadaRepository.save(JornadaCreateDTOMapper.toEntity(jornadaCreateDTO, empleado, concepto));

        return JornadaViewDTOMapper.toDTO(jornada);
    }

    private void validarHsTrabajadasSegunConcepto(String concepto, Integer hsTrabajadas){
        boolean esConceptoLaborable = !concepto.equals("Día Libre");

        if(hsTrabajadas == null && esConceptoLaborable){
            throw new BadCustomerRequestException("'hsTrabajadas' es obligatorio para el concepto ingresado");
        }else if(!esConceptoLaborable && hsTrabajadas != null){
            throw new BadCustomerRequestException("El concepto ingresado no requiere el ingreso de 'hsTrabajadas'");

        }
    }


}
