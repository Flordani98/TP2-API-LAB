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

        validarHsTrabajadas(concepto, jornadaCreateDTO.getHsTrabajadas());
        validarHsTrabajadasDiaEspecifico(empleado, jornadaCreateDTO.getFecha(), jornadaCreateDTO.getHsTrabajadas());
        validarHsTrabajadasSemanal(empleado, jornadaCreateDTO.getHsTrabajadas(), jornadaCreateDTO.getFecha());
        validarHsTrabajadasMensual(empleado, jornadaCreateDTO.getHsTrabajadas(), jornadaCreateDTO.getFecha());

        Jornada jornada = jornadaRepository.save(JornadaCreateDTOMapper.toEntity(jornadaCreateDTO, empleado, concepto));

        return JornadaViewDTOMapper.toDTO(jornada);
    }


    private void validarHsTrabajadas(Concepto concepto, Integer hsTrabajadas){
        validarHsTrabajadasSegunConcepto(concepto.getNombre(), hsTrabajadas);
        validarhsTrabajadasDentroRangoHorario(concepto.getHsMinimo(), concepto.getHsMaximo(), hsTrabajadas);
    }

    private void validarHsTrabajadasDiaEspecifico(Empleado empleado, LocalDate fecha,  Integer hsTrabajadasSolicitadas){
        List<Jornada> jornadasDiaEspecifico = jornadaRepository.findAllByEmpleadoAndFecha(empleado, fecha);
        int horasTotalesEmpleado = 0;

        for (Jornada jornada: jornadasDiaEspecifico) {
            horasTotalesEmpleado = horasTotalesEmpleado + jornada.getHsTrabajadas();
        }
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

    private int sumarHsTrabajadasListaJornadas(List<Jornada> jornadas){
        int hsTotales = 0;
        for (Jornada jornada : jornadas) {
            hsTotales = hsTotales + jornada.getHsTrabajadas();
        }
        return hsTotales;
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
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        List<Jornada> jornadasMismoAnio = filtrarJornadasSegunAnio(jornadas, anio);


        return jornadasMismoAnio.stream().filter(jornada -> jornada.getFecha().getMonthValue() == mes).collect(Collectors.toList());

    }
//    private List<Jornada> filtrarJornadasSegunSemana(List<Jornada> jornadasMismoAnio, int semana){
//        WeekFields weekFields = WeekFields.of(Locale.getDefault());
//        return jornadasMismoAnio.stream().filter(jornada -> jornada.getFecha().get(weekFields.weekOfYear()) == semana).collect(Collectors.toList());
//
//    }

    private void validarHsTrabajadasSegunConcepto(String concepto, Integer hsTrabajadas){
        boolean esConceptoLaborable = !concepto.equals("Día Libre");

        if(hsTrabajadas == null && esConceptoLaborable){
            throw new BadCustomerRequestException("'hsTrabajadas' es obligatorio para el concepto ingresado");
        }else if(!esConceptoLaborable && hsTrabajadas != null){
            throw new BadCustomerRequestException("El concepto ingresado no requiere el ingreso de 'hsTrabajadas'");

        }
    }

    private void validarhsTrabajadasDentroRangoHorario(int hsMinimo, int hsMaximo, Integer hsTrabajadas){
//        boolean estaDentroRango = hsMinimo <= hsTrabajadas && hsMaximo <= hsTrabajadas;
        boolean estaDentroRango = hsTrabajadas >= hsMinimo && hsTrabajadas <= hsMaximo;

        if(!estaDentroRango){
            throw new BadCustomerRequestException("El rango de horas que se puede cargar para este concepto es de "
            + hsMinimo + " - " + hsMaximo);
        }


    }


}
