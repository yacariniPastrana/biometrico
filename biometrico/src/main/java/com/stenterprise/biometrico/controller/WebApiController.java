package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.dto.AsistenciaDTO;
import com.stenterprise.biometrico.dto.EmpleadoDTO;
import com.stenterprise.biometrico.model.Empleado;
import com.stenterprise.biometrico.repository.EmpleadoRepository;
import com.stenterprise.biometrico.repository.MarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*") 
public class WebApiController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private MarcacionRepository marcacionRepository;

    // 1. LISTAR EMPLEADOS (Para que el frontend los muestre en una tabla)
    @GetMapping("/empleados")
    public List<EmpleadoDTO> listarEmpleados() {
        return empleadoRepository.findAll().stream().map(e -> {
            EmpleadoDTO dto = new EmpleadoDTO();
            dto.setId(e.getId());
            dto.setIdBiometrico(e.getIdBiometrico());
            dto.setNombreCompleto(e.getNombreCompleto());
            dto.setTipoDocumento(e.getTipoDocumento());
            dto.setNumeroDocumento(e.getNumeroDocumento());
            return dto;
        }).collect(Collectors.toList());
    }

    // 2. ASISTENCIA DE HOY (Con Nombres y DNI pegados)
    @GetMapping("/asistencia/hoy")
    public List<AsistenciaDTO> asistenciaHoy() {
        LocalDate hoy = LocalDate.now(java.time.ZoneId.of("America/Lima"));
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");

        return marcacionRepository.findByIdBiometricoAndFechaDiaOrderByFechaHoraAsc("", hoy) // Truco: el repo necesita un ajuste o usar findAll
                .stream() 
                .map(m -> {
                    AsistenciaDTO dto = new AsistenciaDTO();
                    dto.setId(m.getId());
                    dto.setIdBio(m.getIdBiometrico());
                    dto.setTipoRegistro(m.getTipoRegistro());
                    dto.setHora(m.getFechaHora().format(formatoHora));
                    
                    if (m.getEmpleado() != null) {
                        dto.setNombreEmpleado(m.getEmpleado().getNombreCompleto());
                        dto.setDocumento(m.getEmpleado().getTipoDocumento() + ": " + m.getEmpleado().getNumeroDocumento());
                    } else {
                        dto.setNombreEmpleado("DESCONOCIDO");
                        dto.setDocumento("SIN DNI");
                    }
                    return dto;
                }).collect(Collectors.toList());
    }

    // 3. ACTUALIZAR DNI (Para que la secretaria complete los datos desde la web)
    @PutMapping("/empleados/{id}/documento")
    public String actualizarDocumento(@PathVariable Integer id, @RequestParam String tipo, @RequestParam String numero) {
        Empleado e = empleadoRepository.findById(id).orElseThrow();
        e.setTipoDocumento(tipo);
        e.setNumeroDocumento(numero);
        empleadoRepository.save(e);
        return "Documento actualizado correctamente";
    }
}