package com.LabJavaReact.TP2_API.repository;

import com.LabJavaReact.TP2_API.model.Empleado;
import com.LabJavaReact.TP2_API.model.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    List<Jornada> findAllByEmpleadoAndFecha(Empleado empleado, LocalDate fecha);
    List<Jornada> findAllByEmpleado(Empleado empleado);


}
