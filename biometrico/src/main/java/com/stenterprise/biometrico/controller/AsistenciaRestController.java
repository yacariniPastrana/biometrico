package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.dto.AsistenciaDTO;
import com.stenterprise.biometrico.model.Marcacion;
import com.stenterprise.biometrico.repository.MarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/asistencias")
@CrossOrigin(origins = "*") // Permite que el frontend acceda sin bloqueos de CORS
public class AsistenciaRestController {

    @Autowired
    private MarcacionRepository marcacionRepository;

    /**
     * 1. OBTIENE LA ASISTENCIA DE HOY
     * GET /api/v1/asistencias/hoy
     */
    @GetMapping("/hoy")
    public List<AsistenciaDTO> obtenerAsistenciaHoy() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
        return marcacionRepository.findAll().stream()
                .filter(m -> m.getFechaDia().equals(hoy))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * 2. OBTIENE EL HISTORIAL POR RANGO DE FECHAS
     * GET /api/v1/asistencias/historial?desde=2024-03-01&hasta=2024-03-15
     */
    @GetMapping("/historial")
    public List<AsistenciaDTO> obtenerHistorial(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        return marcacionRepository.findAll().stream()
                .filter(m -> !m.getFechaDia().isBefore(desde) && !m.getFechaDia().isAfter(hasta))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * MÉTODO AUXILIAR PARA MAPEAR ENTIDAD A DTO
     * Aquí unimos la marca con los datos del empleado (Nombre, DNI)
     */
    private AsistenciaDTO convertirADTO(Marcacion m) {
        AsistenciaDTO dto = new AsistenciaDTO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        dto.setId(m.getId());
        dto.setIdBio(m.getIdBiometrico());
        dto.setTipoRegistro(m.getTipoRegistro());
        dto.setHora(m.getFechaHora().format(formatter));
        dto.setFecha(m.getFechaDia().toString());

        if (m.getEmpleado() != null) {
            dto.setNombreEmpleado(m.getEmpleado().getNombreCompleto());
            // Formateamos el documento para que el frontend lo muestre directo
            String tipoDoc = m.getEmpleado().getTipoDocumento() != null ? m.getEmpleado().getTipoDocumento() : "DNI";
            String numDoc = m.getEmpleado().getNumeroDocumento() != null ? m.getEmpleado().getNumeroDocumento() : "---";
            dto.setDocumento(tipoDoc + ": " + numDoc);
        } else {
            dto.setNombreEmpleado("USUARIO NO SINCRONIZADO");
            dto.setDocumento("N/A");
        }

        return dto;
    }
}