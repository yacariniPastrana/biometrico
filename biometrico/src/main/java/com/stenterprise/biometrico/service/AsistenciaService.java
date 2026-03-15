package com.stenterprise.biometrico.service;

import com.stenterprise.biometrico.model.Empleado;
import com.stenterprise.biometrico.model.Marcacion;
import com.stenterprise.biometrico.repository.EmpleadoRepository;
import com.stenterprise.biometrico.repository.MarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class AsistenciaService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private MarcacionRepository marcacionRepository;

    /**
     * Registra una nueva asistencia basada en el conteo de marcas del día.
     */
    public String registrarAsistencia(String idBiometrico) {
        // 1. Validar existencia del empleado
        Optional<Empleado> empleadoOpt = empleadoRepository.findByIdBiometrico(idBiometrico);

        if (empleadoOpt.isEmpty()) {
            return "Error: El ID biométrico " + idBiometrico + " no está registrado.";
        }

        Empleado empleado = empleadoOpt.get();
        
        // 2. Preparar la nueva marcación
        Marcacion nuevaMarcacion = new Marcacion();
        nuevaMarcacion.setEmpleado(empleado);
        nuevaMarcacion.setFechaHora(LocalDateTime.now());
        nuevaMarcacion.setFechaRegistro(LocalDateTime.now());

        // 3. Calcular estado según el orden de marcas de HOY
        String estado = calcularEstadoPorOrden(empleado);
        nuevaMarcacion.setEstadoCalculado(estado);

        // 4. Persistir en Supabase
        marcacionRepository.save(nuevaMarcacion);

        return "Registro Exitoso: " + estado + " para " + empleado.getNombreCompleto();
    }

    /**
     * Lógica de negocio: Define el estado según cuántas veces ha marcado hoy.
     */
    private String calcularEstadoPorOrden(Empleado empleado) {
        // Rango de tiempo: Hoy desde las 00:00 hasta las 23:59
        LocalDateTime inicioDia = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime finDia = LocalDateTime.now().with(LocalTime.MAX);

        // Consultamos al repositorio cuántas marcas existen en ese rango
        long totalHoy = marcacionRepository.countByEmpleadoAndFechaHoraBetween(empleado, inicioDia, finDia);

        // Determinamos el flujo según el contador
        if (totalHoy == 0) return "INGRESO LABORAL";
        if (totalHoy == 1) return "INICIO REFRIGERIO";
        if (totalHoy == 2) return "FIN REFRIGERIO";
        if (totalHoy == 3) return "SALIDA LABORAL";
        
        return "MARCACIÓN ADICIONAL (FUERA DE FLUJO)";
    }
}