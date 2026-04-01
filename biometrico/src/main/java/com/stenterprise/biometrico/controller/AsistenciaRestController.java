package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.dto.AsistenciaDTO;
import com.stenterprise.biometrico.model.Marcacion;
import com.stenterprise.biometrico.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/asistencias")
@CrossOrigin(origins = "*")
public class AsistenciaRestController {

    @Autowired
    private AsistenciaService asistenciaService;

    
    //1. OBTIENE LA ASISTENCIA DE HOY
    //Delega la búsqueda filtrada por fecha al servicio.
   
    @GetMapping("/hoy")
    public List<AsistenciaDTO> obtenerAsistenciaHoy() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
        return asistenciaService.obtenerAsistenciaPorFecha(hoy);
    }

    
    //2. OBTIENE EL HISTORIAL POR RANGO DE FECHAS
    //Delega la lógica de búsqueda por rango al servicio.
    
    @GetMapping("/historial")
    public List<AsistenciaDTO> obtenerHistorial(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        
        return asistenciaService.obtenerHistorial(desde, hasta);
    }
    
    //3. CÁLCULO DE HORAS POR DÍA
    //Ejecuta el cálculo complejo (refrigerio, horas netas/brutas) encapsulado en el servicio.
     
    @GetMapping("/procesar-dia")
    public Map<String, Object> procesarDia(
            @RequestParam("idBiometrico") String idBiometrico,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return asistenciaService.calcularResumenDelDia(idBiometrico, fecha);
    }

    
    //4. GUARDAR MARCA MANUAL
    //Permite la inserción de marcas de auditoría.
    //Nota: Se podría mover a un Service de Marcaciones para mayor pureza arquitectónica.
    
    @PostMapping("/guardar-manual")
    public Marcacion guardarMarcaManual(@RequestBody Marcacion nuevaMarca) {
        nuevaMarca.setEsManual(true);
        if (nuevaMarca.getFechaHora() != null) {
            nuevaMarca.setFechaDia(nuevaMarca.getFechaHora().toLocalDate());
        }
        
        return asistenciaService.guardarMarcaManual(nuevaMarca);
    }
}