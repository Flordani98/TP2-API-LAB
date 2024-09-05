package com.LabJavaReact.TP2_API.service.impl;

import com.LabJavaReact.TP2_API.dto.ConceptoDTO;
import com.LabJavaReact.TP2_API.mapper.ConceptoMapper;
import com.LabJavaReact.TP2_API.model.Concepto;
import com.LabJavaReact.TP2_API.repository.ConceptoRepository;
import com.LabJavaReact.TP2_API.service.IConceptoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConceptoService implements IConceptoService {

    ConceptoRepository repository;

    public ConceptoService(ConceptoRepository repository){
        this.repository = repository;
    }

    @Override
    public List<ConceptoDTO> obtenerConceptos() {
        List<Concepto> conceptos = repository.findAll();
        return conceptos.stream().map(ConceptoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ConceptoDTO> obtenerConceptosFiltrados(Long id, String nombre) {
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

        //criterio adicional: devuelve todos los conceptos en el que sus atributos hs_minimo y hs_maximo no son nulos
        List<Concepto> conceptosFiltrados = obtenerConceptosConHorasDefinidas(conceptos);


        return conceptosFiltrados.stream().map(ConceptoMapper::toDTO).collect(Collectors.toList());
    }


    private List<Concepto> obtenerConceptosConHorasDefinidas(List<Concepto> conceptos){
        return conceptos.stream()
                .filter(concepto -> concepto.getHsMaximo() != null && concepto.getHsMinimo() != null)
                .toList();
    }
}


