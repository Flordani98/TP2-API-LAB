package com.LabJavaReact.TP2_API.repository;

import com.LabJavaReact.TP2_API.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    boolean existsById(Long id);
    Boolean existsByEmail(String email);
    Boolean existsByNroDocumento(Long nroDocumento);

}
