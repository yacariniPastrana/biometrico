package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iclock")
public class BiometricController {

    @Autowired
    private AsistenciaService asistenciaService;

    /**
     * Handshake: El dispositivo pregunta por opciones de configuración.
     * Es el primer paso para establecer la conexión en tiempo real.
     */
    @GetMapping("/cdata")
    public ResponseEntity<String> handshake(@RequestParam("SN") String sn) {
        // Realtime=1 le dice al equipo que envíe los datos apenas se marque
        return ResponseEntity.ok("GET OPTION FROM: " + sn + "\nRealtime=1\nDelay=30");
    }

    /**
     * Recepción de Datos: Aquí llega la trama de texto del biométrico.
     * Formato esperado: ID_BIOMETRICO \t FECHA_HORA \t ...
     */
    @PostMapping("/cdata")
    public ResponseEntity<String> receiveData(
            @RequestParam("table") String table, 
            @RequestBody String body) {

        // Solo procesamos logs de asistencia (ATTLOG)
        if ("ATTLOG".equals(table) && body != null) {
            String[] lineas = body.split("\n");
            
            for (String linea : lineas) {
                if (linea.trim().isEmpty()) continue;
                
                String[] campos = linea.split("\t");
                if (campos.length >= 1) {
                    String idBiometrico = campos[0].trim();
                    
                    // Delegamos toda la lógica al servicio
                    // Él buscará al empleado, contará las marcas y guardará en Supabase
                    asistenciaService.registrarAsistencia(idBiometrico);
                }
            }
        }
        
        // El dispositivo espera un "OK" para confirmar la recepción
        return ResponseEntity.ok("OK");
    }

    /**
     * Heartbeat: El dispositivo avisa que sigue conectado.
     */
    @GetMapping("/getrequest")
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok("OK");
    }
}