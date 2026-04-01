package com.stenterprise.biometrico.service;

import com.stenterprise.biometrico.dto.AsistenciaDTO;
import com.stenterprise.biometrico.model.Marcacion;
import com.stenterprise.biometrico.repository.MarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AsistenciaService {

    @Autowired
    private MarcacionRepository marcacionRepository;

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public List<AsistenciaDTO> obtenerAsistenciaPorFecha(LocalDate fecha) {
        return marcacionRepository.findByFechaDia(fecha).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaDTO> obtenerHistorial(LocalDate desde, LocalDate hasta) {
        return marcacionRepository.findByFechaDiaBetween(desde, hasta).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Map<String, Object> calcularResumenDelDia(String idBiometrico, LocalDate fecha) {
        List<Marcacion> marcas = marcacionRepository.findByIdBiometricoAndFechaDiaOrderByFechaHoraAsc(idBiometrico, fecha);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("fecha", fecha.toString());
        resultado.put("idBiometrico", idBiometrico);

        if (marcas.isEmpty()) return resultado;

        LocalTime entrada = marcas.get(0).getFechaHora().toLocalTime();
        resultado.put("entrada", entrada.toString());

        if (marcas.size() == 1) {
            resultado.put("horasTrabajadas", 0.0);
            resultado.put("observacion", "Solo tiene marca de ingreso");
            return resultado;
        }

        LocalTime salida = marcas.get(marcas.size() - 1).getFechaHora().toLocalTime();
        resultado.put("salida", salida.toString());

        if (marcas.size() >= 3) resultado.put("inicioRefrigerio", marcas.get(1).getFechaHora().toLocalTime().toString());
        if (marcas.size() >= 4) resultado.put("finRefrigerio", marcas.get(2).getFechaHora().toLocalTime().toString());

        double horasBrutas = Duration.between(marcas.get(0).getFechaHora(), marcas.get(marcas.size() - 1).getFechaHora()).toMinutes() / 60.0;
        double horasNetas = (salida.isAfter(LocalTime.of(15, 0))) ? Math.max(0, horasBrutas - 1.0) : horasBrutas;

        resultado.put("horasTrabajadas", Math.round(horasNetas * 100.0) / 100.0);
        return resultado;
    }

    public AsistenciaDTO convertirADTO(Marcacion m) {
        AsistenciaDTO dto = new AsistenciaDTO();
        dto.setId(m.getId());
        dto.setIdBio(m.getIdBiometrico());
        dto.setTipoRegistro(m.getTipoRegistro());
        dto.setHora(m.getFechaHora().format(HORA_FORMATTER));
        dto.setFecha(m.getFechaDia().toString());

        if (m.getEmpleado() != null) {
            dto.setNombreEmpleado(m.getEmpleado().getNombreCompleto());
            String tipo = m.getEmpleado().getTipoDocumento() != null ? m.getEmpleado().getTipoDocumento() : "DNI";
            String num = m.getEmpleado().getNumeroDocumento() != null ? m.getEmpleado().getNumeroDocumento() : "---";
            dto.setDocumento(tipo + ": " + num);
        } else {
            dto.setNombreEmpleado("USUARIO NO SINCRONIZADO");
        }
        return dto;
    }
    
    public Marcacion guardarMarcaManual(Marcacion nuevaMarca) {
        return marcacionRepository.save(nuevaMarca);
    }
}