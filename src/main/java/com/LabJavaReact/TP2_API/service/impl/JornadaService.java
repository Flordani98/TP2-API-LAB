package com.LabJavaReact.TP2_API.service.impl;

import com.LabJavaReact.TP2_API.dto.JornadaCreateDTO;
import com.LabJavaReact.TP2_API.dto.JornadaViewDTO;
import com.LabJavaReact.TP2_API.exception.BadCustomerRequestException;
import com.LabJavaReact.TP2_API.mapper.JornadaCreateDTOMapper;
import com.LabJavaReact.TP2_API.mapper.JornadaViewDTOMapper;
import com.LabJavaReact.TP2_API.model.Concepto;
import com.LabJavaReact.TP2_API.model.Empleado;
import com.LabJavaReact.TP2_API.model.Jornada;
import com.LabJavaReact.TP2_API.repository.ConceptoRepository;
import com.LabJavaReact.TP2_API.repository.EmpleadoRepository;
import com.LabJavaReact.TP2_API.repository.JornadaRepository;
import com.LabJavaReact.TP2_API.service.IJornadaService;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

        //si el concepto es laborable
        //validaciones para jornadas laborales hsTrabajadas no pueden ser null, ya q se realizan operaciones
        //TODO: dentro de varios métodos llamo al repo para que me traiga las jornadas, modificar para no hacer tantas llamadas al repo
        if(jornadaCreateDTO.getHsTrabajadas() != null){
            validarhsTrabajadasDentroRangoHorario(concepto.getHsMinimo(), concepto.getHsMaximo(), jornadaCreateDTO.getHsTrabajadas());
            validarHsTrabajadasDiaEspecifico(empleado, jornadaCreateDTO.getFecha(), jornadaCreateDTO.getHsTrabajadas());
            validarHsTrabajadasSemanal(empleado, jornadaCreateDTO.getHsTrabajadas(), jornadaCreateDTO.getFecha());
            validarHsTrabajadasMensual(empleado, jornadaCreateDTO.getHsTrabajadas(), jornadaCreateDTO.getFecha());
        }
        //TODO: refactorizar
        validarCantTurnosExtraSemanal(empleado, jornadaCreateDTO.getFecha());
        validarCantTurnosNormalSemanal(empleado, jornadaCreateDTO.getFecha());
        verificarSiFechaSolicitadaEsDiaLibre(empleado, jornadaCreateDTO.getFecha());
        validarCantDiasLibresSemanal(empleado, jornadaCreateDTO.getFecha());
        validarCantDiasLibresMensual(empleado, jornadaCreateDTO.getFecha());

        Jornada jornada = jornadaRepository.save(JornadaCreateDTOMapper.toEntity(jornadaCreateDTO, empleado, concepto));

        return JornadaViewDTOMapper.toDTO(jornada);
    }

    public void verificarSiFechaSolicitadaEsDiaLibre(Empleado empleado, LocalDate fechaSolicitada){
        List<Jornada> jornadasDiaEspecifico = jornadaRepository.findAllByEmpleadoAndFecha(empleado, fechaSolicitada);
        for (Jornada jornada: jornadasDiaEspecifico) {
            if(jornada.getConceptoLaboral().getNombre().equals("Día Libre")){
                throw new BadCustomerRequestException("El empleado ingresado cuenta con un día libre en esa fecha.");
            }

        }
    }

    private void validarHsTrabajadasDiaEspecifico(Empleado empleado, LocalDate fecha,  Integer hsTrabajadasSolicitadas){
        List<Jornada> jornadasDiaEspecifico = jornadaRepository.findAllByEmpleadoAndFecha(empleado, fecha);
        int horasTotalesEmpleado = sumarHsTrabajadasListaJornadas(jornadasDiaEspecifico);

        if(horasTotalesEmpleado + hsTrabajadasSolicitadas > 14){
            throw new BadCustomerRequestException("Un empleado no puede cargar más de 14 horas trabajadas en un día.");
        }

    }
    private void validarHsTrabajadasSemanal(Empleado empleado, Integer hsTrabajadasSolicitadas, LocalDate fechaSolicitada){
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semana = fechaSolicitada.get(weekFields.weekOfYear());
        int anio = fechaSolicitada.getYear();

        List<Jornada> jornadasEmpleado = jornadaRepository.findAllByEmpleado(empleado);
        List<Jornada> jornadasSemanaEspecifica = filtrarJornadasSegunSemanayAnio(jornadasEmpleado, anio, semana);

        int hsTrabajadasSemanal = sumarHsTrabajadasListaJornadas(jornadasSemanaEspecifica);

        //sumar las hsTrabajadas de la semana de la fecha solicitada + hsTrabajadasSolicitadas
        if(hsTrabajadasSemanal + hsTrabajadasSolicitadas > 52){
            throw new BadCustomerRequestException("El empleado ingresado supera las 52 horas semanales.");
        }

    }
    private void validarHsTrabajadasMensual(Empleado empleado, Integer hsTrabajadasSolicitadas, LocalDate fechaSolicitada){
        int mes = fechaSolicitada.getMonthValue();
        int anio = fechaSolicitada.getYear();

        List<Jornada> jornadasEmpleado = jornadaRepository.findAllByEmpleado(empleado);
        List<Jornada> jornadasMesEspecifico = filtrarJornadasSegunMesyAnio(jornadasEmpleado, anio, mes);

        int hsTrabajadasMensual = sumarHsTrabajadasListaJornadas(jornadasMesEspecifico);

        //sumar las hsTrabajadas de la semana de la fecha solicitada + hsTrabajadasSolicitadas
        if(hsTrabajadasMensual + hsTrabajadasSolicitadas > 190){
            throw new BadCustomerRequestException("El empleado ingresado supera las 190 horas mensuales.");
        }

    }
    private void validarHsTrabajadasSegunConcepto(String concepto, Integer hsTrabajadas){
        boolean esConceptoLaborable = !concepto.equals("Día Libre");

        if(hsTrabajadas == null && esConceptoLaborable){
            throw new BadCustomerRequestException("'hsTrabajadas' es obligatorio para el concepto ingresado");
        }else if(!esConceptoLaborable && hsTrabajadas != null){
            throw new BadCustomerRequestException("El concepto ingresado no requiere el ingreso de 'hsTrabajadas'");

        }
    }

    private void validarhsTrabajadasDentroRangoHorario(int hsMinimo, int hsMaximo, Integer hsTrabajadas){
        boolean estaDentroRango = hsTrabajadas >= hsMinimo && hsTrabajadas <= hsMaximo;

            if(!estaDentroRango){
                throw new BadCustomerRequestException("El rango de horas que se puede cargar para este concepto es de "
                        + hsMinimo + " - " + hsMaximo);
            }


    }

    private void validarCantTurnosExtraSemanal(Empleado empleado, LocalDate fecha){
        List<Jornada> jornadasEmpleado = jornadaRepository.findAllByEmpleado(empleado);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semana = fecha.get(weekFields.weekOfYear());
        int anio = fecha.getYear();

        List<Jornada> jornadasSemanaEspecifica = filtrarJornadasSegunSemanayAnio(jornadasEmpleado, anio, semana);
        int cantTurnos = contarCantTurnoEspecifico(jornadasSemanaEspecifica, "Turno Extra");

        if(cantTurnos > 2){
            throw new BadCustomerRequestException("El empleado ingresado ya cuenta con 3 turnos extra esta semana.");
        }

    }
    private void validarCantTurnosNormalSemanal(Empleado empleado, LocalDate fecha){
        List<Jornada> jornadasEmpleado = jornadaRepository.findAllByEmpleado(empleado);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semana = fecha.get(weekFields.weekOfYear());
        int anio = fecha.getYear();

        List<Jornada> jornadasSemanaEspecifica = filtrarJornadasSegunSemanayAnio(jornadasEmpleado, anio, semana);
        int cantTurnos = contarCantTurnoEspecifico(jornadasSemanaEspecifica, "Turno Normal");

        if(cantTurnos > 4){
            throw new BadCustomerRequestException("El empleado ingresado ya cuenta con 5 turnos normales esta semana.");
        }

    }
    private void validarCantDiasLibresSemanal(Empleado empleado, LocalDate fecha){
        List<Jornada> jornadasEmpleado = jornadaRepository.findAllByEmpleado(empleado);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semana = fecha.get(weekFields.weekOfYear());
        int anio = fecha.getYear();

        List<Jornada> jornadasSemanaEspecifica = filtrarJornadasSegunSemanayAnio(jornadasEmpleado, anio, semana);
        int cantTurnos = contarCantTurnoEspecifico(jornadasSemanaEspecifica, "Día Libre");

        if(cantTurnos > 1){
            throw new BadCustomerRequestException("El empleado no cuenta con más días libres esta semana.");
        }
    }

    private void validarCantDiasLibresMensual(Empleado empleado, LocalDate fecha){
        List<Jornada> jornadasEmpleado = jornadaRepository.findAllByEmpleado(empleado);
        int mes = fecha.getMonthValue();
        int anio = fecha.getYear();

        List<Jornada> jornadasSemanaEspecifica = filtrarJornadasSegunMesyAnio(jornadasEmpleado, anio, mes);
        int cantTurnos = contarCantTurnoEspecifico(jornadasSemanaEspecifica, "Día Libre");

        if(cantTurnos > 4){
            throw new BadCustomerRequestException("El empleado no cuenta con más días libres este mes.");
        }
    }

    private List<Jornada> filtrarJornadasSegunAnio(List<Jornada> jornadas, int anio){
        return jornadas.stream().filter(jornada -> jornada.getFecha().getYear() == anio).collect(Collectors.toList());
    }
    private List<Jornada> filtrarJornadasSegunSemanayAnio(List<Jornada> jornadas, int anio, int semana){
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        List<Jornada> jornadasMismoAnio = filtrarJornadasSegunAnio(jornadas, anio);

        return jornadasMismoAnio.stream().filter(jornada -> jornada.getFecha().get(weekFields.weekOfYear()) == semana).collect(Collectors.toList());

    }
    private List<Jornada> filtrarJornadasSegunMesyAnio(List<Jornada> jornadas, int anio, int mes){
        List<Jornada> jornadasMismoAnio = filtrarJornadasSegunAnio(jornadas, anio);

        return jornadasMismoAnio.stream().filter(jornada -> jornada.getFecha().getMonthValue() == mes).collect(Collectors.toList());

    }

    private int contarCantTurnoEspecifico(List<Jornada> jornadas, String nombreTurno){
        int cont = 0;
        for (Jornada jornada:jornadas) {
            if(jornada.getConceptoLaboral().getNombre().equals(nombreTurno)){
                cont++;
            }
        }
        return cont;
    }
    private int sumarHsTrabajadasListaJornadas(List<Jornada> jornadas){
        int hsTotales = 0;
        for (Jornada jornada : jornadas) {
            if(jornada.getHsTrabajadas() != null){
                hsTotales = hsTotales + jornada.getHsTrabajadas();
            }
        }
        return hsTotales;
    }


}
