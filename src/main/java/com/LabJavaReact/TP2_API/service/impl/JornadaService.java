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
        return convertirListaJornadaAJornadaViewDTO(jornadas);
    }

    @Override
    public List<JornadaViewDTO> obtenerJornadasFiltradas(Long nroDocumento, LocalDate fechaDesde, LocalDate fechaHasta) {
        verificarFechaDesdeEsMayorFechaHasta(fechaDesde, fechaHasta);
        List<Jornada> jornadas = jornadaRepository.findAll();
        boolean existeNroDoc = existeNroDocumentoListaJornadas(nroDocumento);

        if(fechaDesde != null && fechaHasta != null && nroDocumento != null && existeNroDoc){
            jornadas = filtrarListaJornadasSegunNroDocumentoYFecha(jornadas, nroDocumento, fechaDesde, fechaHasta);

        }else if(fechaDesde != null && fechaHasta != null){
            jornadas =  filtrarJornadasSegunFechaDesdeHasta(jornadas, fechaDesde, fechaHasta);

        }else if(nroDocumento != null){
            jornadas = filtrarListaJornadasSegunNroDocumento(jornadas, nroDocumento);

        }else if(fechaDesde != null | fechaHasta != null){
            if(fechaDesde != null){
                jornadas = filtrarJornadasSegunFechaDesde(jornadas, fechaDesde);
            }else{
                jornadas = filtrarJornadasSegunFechaHasta(jornadas, fechaHasta);
            }
        }
        //cuando fecha desde o hasta es != null && nroDocumento es != null se retorna
        //la lista de jornadas correspondientes a ese nroDocumento, ya que no se especifico en los
        //criterios de aceptación que pasaría si fecha(desde o hasta) y nroDocumento es definido.

        return convertirListaJornadaAJornadaViewDTO(jornadas);
    }

    public void verificarFechaDesdeEsMayorFechaHasta(LocalDate fechaDesde, LocalDate fechaHasta){
        if(fechaDesde != null && fechaHasta != null){
            if(fechaDesde.isAfter(fechaHasta)){
                throw new BadCustomerRequestException("El campo ‘fechaDesde’ no puede ser mayor que ‘fechaHasta’.");
            }
        }
    }

    @Override
    public JornadaViewDTO guardarJornada(JornadaCreateDTO jornadaCreateDTO) {

        Empleado empleado = empleadoRepository.findById(jornadaCreateDTO.getIdEmpleado())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado ingresado"));
        Concepto concepto = conceptoRepository.findById(jornadaCreateDTO.getIdConcepto())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el concepto ingresado"));

        validarHsTrabajadasSegunConcepto(concepto.getNombre(), jornadaCreateDTO.getHsTrabajadas());

        if(esConceptoLaborable(concepto.getNombre())){
            // Validaciones necesarias para jornadas laborales, asegurando que 'hsTrabajadas' no sea null,
            // ya que es utilizado en operaciones aritméticas y lógicas.
            validarConceptoLaborable(empleado, concepto.getHsMinimo(), concepto.getHsMaximo(),
                                     jornadaCreateDTO.getFecha(), jornadaCreateDTO.getHsTrabajadas());
        }
        validarReglasJornada(empleado, jornadaCreateDTO.getFecha(),concepto.getNombre() );

        Jornada jornada = jornadaRepository.save(JornadaCreateDTOMapper.toEntity(jornadaCreateDTO, empleado, concepto));

        return JornadaViewDTOMapper.toDTO(jornada);
    }

    public void validarReglasJornada(Empleado empleado, LocalDate fecha, String nombreConcepto){
        validarCantTurnosExtraSemanal(empleado, fecha, nombreConcepto);
        validarCantTurnosNormalSemanal(empleado, fecha, nombreConcepto);
        verificarSiFechaSolicitadaEsDiaLibre(empleado, fecha);
        validarCantDiasLibresSemanal(empleado, fecha);
        validarCantDiasLibresMensual(empleado, fecha);
        validarCantEmpleadoPorDiaYConcepto(fecha, nombreConcepto);
        validarCantJornadaDeEmpleadoSegunConcepto(empleado, fecha, nombreConcepto);
    }

    public void validarConceptoLaborable(Empleado empleado, Integer hsMinimo, Integer hsMaximo, LocalDate fecha,
                                         Integer hsTrabajadasSolicitadas){

        validarhsTrabajadasDentroRangoHorario(hsMinimo, hsMaximo, hsTrabajadasSolicitadas);
        validarHsTrabajadasDiaEspecifico(empleado, fecha, hsTrabajadasSolicitadas);
        validarHsTrabajadasSemanal(empleado, hsTrabajadasSolicitadas, fecha);
        validarHsTrabajadasMensual(empleado, hsTrabajadasSolicitadas, fecha);
    }

    public boolean esConceptoLaborable(String nombreConcepto){
        return !nombreConcepto.equals("Día Libre");
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
        boolean esConceptoLaborable = esConceptoLaborable(concepto);

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

    private void validarCantTurnosExtraSemanal(Empleado empleado, LocalDate fecha, String concepto){
        if (!concepto.equals("Turno Extra")) {
            return;
        }
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
    private void validarCantTurnosNormalSemanal(Empleado empleado, LocalDate fecha, String concepto){
        if (!concepto.equals("Turno Normal")) {
            return;
        }
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

    private void validarCantEmpleadoPorDiaYConcepto(LocalDate fecha, String nombreConcepto){
        List<Jornada> listaJornadasSegunFecha = jornadaRepository.findAllByFecha(fecha);
        int cantEmpleados = contarCantEmpleadosSegunConcepto(listaJornadasSegunFecha, nombreConcepto);
        if(cantEmpleados > 1){
            throw new BadCustomerRequestException("Ya existen 2 empleados registrados para este concepto en la fecha ingresada.");
        }

    }
    private void validarCantJornadaDeEmpleadoSegunConcepto(Empleado empleado, LocalDate fecha, String nombreConcepto){
        List<Jornada> listaJornadasEmpleadoSegunFecha = jornadaRepository.findAllByEmpleadoAndFecha(empleado, fecha);
        int cantJornadas = contarCantTurnoEspecifico(listaJornadasEmpleadoSegunFecha, nombreConcepto);
        if(cantJornadas > 0){
            throw new BadCustomerRequestException("El empleado ya tiene registrado una jornada con este concepto en la fecha ingresada.");
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

    public List<Jornada> filtrarListaJornadasSegunNroDocumentoYFecha(List<Jornada> jornadas, Long nroDocumento,
                                                                     LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Jornada> jornadasFiltradas = filtrarListaJornadasSegunNroDocumento(jornadas, nroDocumento);
        jornadasFiltradas = filtrarJornadasSegunFechaDesdeHasta(jornadasFiltradas, fechaDesde, fechaHasta);

        return jornadasFiltradas;
    }
    public List<Jornada> filtrarListaJornadasSegunNroDocumento(List<Jornada> jornadas, Long nroDocumento){
        List<Jornada> jornadasFiltradasPorNroDocumento = new ArrayList<>();

        for (Jornada jornada: jornadas) {
            if(jornada.getEmpleado().getNroDocumento().equals(nroDocumento)){
                jornadasFiltradasPorNroDocumento.add(jornada);

            }
        }
        return jornadasFiltradasPorNroDocumento;
    }

    public List<Jornada> filtrarJornadasSegunFechaDesdeHasta(List<Jornada> jornadas, LocalDate fechaDesde, LocalDate fechaHasta){
        List<Jornada> jornadasFiltradasPorFecha = new ArrayList<>();

        for (Jornada jornada: jornadas) {
            if(jornada.getFecha().isAfter(fechaDesde) && jornada.getFecha().isBefore(fechaHasta) ){
                jornadasFiltradasPorFecha.add(jornada);

            }

        }
        return jornadasFiltradasPorFecha;
    }

    public List<Jornada> filtrarJornadasSegunFechaDesde(List<Jornada> jornadas, LocalDate fechaDesde){
        List<Jornada> jornadasFiltradasPorFecha = new ArrayList<>();

        for (Jornada jornada: jornadas) {
            if(jornada.getFecha().isAfter(fechaDesde)){
                jornadasFiltradasPorFecha.add(jornada);

            }

        }
        return jornadasFiltradasPorFecha;
    }
    public List<Jornada> filtrarJornadasSegunFechaHasta(List<Jornada> jornadas, LocalDate fechaHasta){
        List<Jornada> jornadasFiltradasPorFecha = new ArrayList<>();

        for (Jornada jornada: jornadas) {
            if(jornada.getFecha().isBefore(fechaHasta) ){
                jornadasFiltradasPorFecha.add(jornada);
            }

        }
        return jornadasFiltradasPorFecha;
    }
    public List<JornadaViewDTO> convertirListaJornadaAJornadaViewDTO(List<Jornada> jornadas){
        List<JornadaViewDTO> jornadaViewDTOS = new ArrayList<>();

        for (Jornada jornada: jornadas) {
            jornadaViewDTOS.add(JornadaViewDTOMapper.toDTO(jornada));

        }
        return jornadaViewDTOS;
    }
    public boolean existeNroDocumentoListaJornadas(Long nroDocumento){
        List<Jornada> jornadas = jornadaRepository.findAll();
        boolean resp = false;
        for (Jornada jornada: jornadas) {
            if(nroDocumento != null){
                if (jornada.getEmpleado().getNroDocumento().equals(nroDocumento)) {
                    resp = true;
                    break;
                }
            }
        }
        return resp;
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
    private int contarCantEmpleadosSegunConcepto(List<Jornada> jornadas, String nombreConcepto){
        int cantEmpleados = 0;
        List<Empleado> empleadosContados = new ArrayList<>();

        for (Jornada jornada: jornadas) {
            if(jornada.getConceptoLaboral().getNombre().equals(nombreConcepto)){
                if(!empleadosContados.contains(jornada.getEmpleado())){
                    empleadosContados.add(jornada.getEmpleado());
                    cantEmpleados++;
                }
            }

        }
        return cantEmpleados;
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
