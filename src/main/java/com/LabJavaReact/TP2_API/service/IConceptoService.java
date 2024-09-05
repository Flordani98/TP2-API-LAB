package com.LabJavaReact.TP2_API.service;

import com.LabJavaReact.TP2_API.dto.ConceptoDTO;

import java.util.List;

public interface IConceptoService {
    public List<ConceptoDTO> getConceptos();
    public List<ConceptoDTO> getFilteredConcepts(Long id, String nombre);
}
