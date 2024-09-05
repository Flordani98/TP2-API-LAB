package com.LabJavaReact.TP2_API.service;

import com.LabJavaReact.TP2_API.dto.ConceptoDTO;

import java.util.List;

public interface IConceptoService {
    public List<ConceptoDTO> obtenerConceptos();
    public List<ConceptoDTO> obtenerConceptosFiltrados(Long id, String nombre);
}
