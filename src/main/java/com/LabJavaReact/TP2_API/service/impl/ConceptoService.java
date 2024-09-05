package com.LabJavaReact.TP2_API.service.impl;

import com.LabJavaReact.TP2_API.dto.ConceptoDTO;
import com.LabJavaReact.TP2_API.mapper.ConceptoMapper;
import com.LabJavaReact.TP2_API.model.Concepto;
import com.LabJavaReact.TP2_API.repository.ConceptoRepository;
import com.LabJavaReact.TP2_API.service.IConceptoService;
import com.LabJavaReact.TP2_API.validation.NombreValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.LabJavaReact.TP2_API.validation.NombreValidator.validateNombre;

@Service
public class ConceptoService implements IConceptoService {

    ConceptoRepository repository;

    public ConceptoService(ConceptoRepository repository){
        this.repository = repository;
    }

    @Override
    public List<ConceptoDTO> getConceptos() {
        List<Concepto> conceptos = repository.findAll();
        return conceptos.stream().map(ConceptoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ConceptoDTO> getFilteredConcepts(Long id, String nombre) {
        List<Concepto> conceptos = new ArrayList<>();

        if(id == null && nombre == null) {

            conceptos = repository.findAll();

        }else if(id != null && nombre == null){

            Optional<Concepto> conceptoOptional = repository.findById(id);
            if(conceptoOptional.isPresent()){
                conceptos.add(conceptoOptional.get());
            }

        }else if(id == null){

            conceptos = repository.findAllByNombre(nombre);

        }else{
            conceptos = repository.findAllByIdAndNombre(id, nombre);

        }

        return conceptos.stream().map(ConceptoMapper::toDTO).collect(Collectors.toList());
    }


}
