package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.model.Marcacion;
import com.stenterprise.biometrico.dto.AsistenciaDTO;
import com.stenterprise.biometrico.dto.EmpleadoDTO;
import com.stenterprise.biometrico.model.Empleado;
import com.stenterprise.biometrico.repository.EmpleadoRepository;
import com.stenterprise.biometrico.repository.MarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;
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

    //1. LISTAR EMPLEADOS
     
    @GetMapping("/empleados")
    public List<EmpleadoDTO> listarEmpleados() {
        return empleadoRepository.findAll().stream().map(e -> {
            EmpleadoDTO dto = new EmpleadoDTO();
            dto.setId(e.getId());
            dto.setIdBiometrico(e.getIdBiometrico());
            dto.setNombreCompleto(e.getNombreCompleto());
            dto.setTipoDocumento(e.getTipoDocumento());
            dto.setNumeroDocumento(e.getNumeroDocumento());
            dto.setPrivilegio(e.getPrivilegio());
            if (e.getFechaCumpleanos() != null) {
                dto.setFechaCumpleanos(e.getFechaCumpleanos().toString());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    //2. ASISTENCIA DE HOY

    @GetMapping("/asistencia/hoy")
    public List<AsistenciaDTO> asistenciaHoy() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Usamos el método optimizado del repositorio
        return marcacionRepository.findByFechaDia(hoy)
                .stream() 
                .map(m -> {
                    AsistenciaDTO dto = new AsistenciaDTO();
                    dto.setId(m.getId());
                    dto.setIdBio(m.getIdBiometrico());
                    dto.setTipoRegistro(m.getTipoRegistro());
                    dto.setHora(m.getFechaHora().format(formatoHora));
                    dto.setFecha(m.getFechaDia().toString());
                    
                    if (m.getEmpleado() != null) {
                        dto.setNombreEmpleado(m.getEmpleado().getNombreCompleto());
                        String tipo = m.getEmpleado().getTipoDocumento() != null ? m.getEmpleado().getTipoDocumento() : "DNI";
                        String num = m.getEmpleado().getNumeroDocumento() != null ? m.getEmpleado().getNumeroDocumento() : "S/N";
                        dto.setDocumento(tipo + ": " + num);
                    } else {
                        dto.setNombreEmpleado("DESCONOCIDO");
                        dto.setDocumento("SIN DNI");
                    }
                    return dto;
                }).collect(Collectors.toList());
    }

    //3. ACTUALIZAR DOCUMENTO
    //Mejorado con ResponseEntity para manejo de errores en el frontend.
    
    @PutMapping("/empleados/{id}/documento")
    public ResponseEntity<String> actualizarDocumento(
            @PathVariable Integer id, 
            @RequestParam String tipo, 
            @RequestParam String numero) {
        
        return empleadoRepository.findById(id)
            .map(e -> {
                e.setTipoDocumento(tipo);
                e.setNumeroDocumento(numero);
                empleadoRepository.save(e);
                return ResponseEntity.ok("Documento actualizado correctamente");
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado"));
    }
    
    //4. LOGIN SEGURO
     
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String usuario = credenciales.get("usuario");
        String password = credenciales.get("password");

        return empleadoRepository.findByNombreCompleto(usuario)
            .map(emp -> {
                if (emp.getPassword() != null && emp.getPassword().equals(password)) {
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("id", emp.getId());
                    resp.put("nombre", emp.getNombreCompleto());
                    resp.put("privilegio", emp.getPrivilegio());
                    resp.put("idBiometrico", emp.getIdBiometrico());
                    return ResponseEntity.ok(resp);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"));
    }

    //5. EDICIÓN MANUAL
     
    @PostMapping("/asistencia/corregir")
    public ResponseEntity<?> corregirAsistencia(@RequestBody Marcacion nuevaMarca) {
        return empleadoRepository.findByIdBiometrico(nuevaMarca.getIdBiometrico())
            .map(emp -> {
                nuevaMarca.setEmpleado(emp);
                nuevaMarca.setEsManual(true);
                if (nuevaMarca.getFechaHora() != null) {
                    nuevaMarca.setFechaDia(nuevaMarca.getFechaHora().toLocalDate());
                }
                marcacionRepository.save(nuevaMarca);
                return ResponseEntity.ok("Marca registrada manualmente");
            })
            .orElse(ResponseEntity.badRequest().body("ID Biométrico no reconocido"));
    }
}