package com.LabJavaReact.TP2_API.repository;

import com.LabJavaReact.TP2_API.model.Concepto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConceptoRepository extends JpaRepository<Concepto, Long> {
    List<Concepto> findAllByNombre(String nombre);
    List<Concepto> findAllByIdAndNombre(Long id, String nombre);

}
