package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.model.Empleado;
import com.stenterprise.biometrico.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/iclock")
public class BiometricoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping("/cdata")
    public ResponseEntity<String> handshake(@RequestParam("SN") String sn) {
        System.out.println(">>> Handshake SN: " + sn);
        return ResponseEntity.ok("GET OPTION FROM: " + sn + "\nRealtime=1\nDelay=30");
    }

    @GetMapping("/getrequest")
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/cdata")
    public ResponseEntity<String> receiveData(
            @RequestParam("table") String table, 
            @RequestBody String body) {

        System.out.println("\n--- TRAMA RECIBIDA: " + table + " ---");
        
        if ("OPERLOG".equals(table) && body != null && body.contains("USER PIN=")) {
            procesarUsuarioIncrustado(body);
        } 
        else if ("USER".equals(table) && body != null) {
            procesarUsuarioEstandar(body);
        }

        return ResponseEntity.ok("OK");
    }

    private void procesarUsuarioIncrustado(String body) {
        try {
            String idBio = extraerValor(body, "PIN=");
            
            if (idBio != null) {
                Empleado emp = empleadoRepository.findByIdBiometrico(idBio)
                        .orElse(new Empleado());
                
                emp.setIdBiometrico(idBio);
                emp.setNombreCompleto(extraerValor(body, "Name="));
                
                String pri = extraerValor(body, "Pri=");
                emp.setPrivilegio("14".equals(pri) ? "ADMINISTRADOR" : "USUARIO NORMAL");
                
                String modo = extraerValor(body, "Verify=");
                emp.setModoVerificacion(interpretarModo(modo));

                // FORZAR HORA DE LIMA EXPLÍCITAMENTE
                if (emp.getFechaCreacion() == null) {
                    emp.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
                }

                empleadoRepository.save(emp);
                System.out.println(">>> ÉXITO: Empleado " + idBio + " guardado a las " + emp.getFechaCreacion());
            }
        } catch (Exception e) {
            System.err.println(">>> ERROR en procesarUsuarioIncrustado: " + e.getMessage());
        }
    }

    private void procesarUsuarioEstandar(String body) {
        String[] lineas = body.split("\n");
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;
            try {
                String[] campos = linea.split("\t");
                if (campos.length >= 1) {
                    String idBio = campos[0].trim();
                    Empleado emp = empleadoRepository.findByIdBiometrico(idBio)
                            .orElse(new Empleado());
                    
                    emp.setIdBiometrico(idBio);
                    if (campos.length > 1) emp.setNombreCompleto(campos[1].trim());
                    
                    if (emp.getFechaCreacion() == null) {
                        emp.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
                    }
                    
                    empleadoRepository.save(emp);
                    System.out.println(">>> ÉXITO: Empleado " + idBio + " (Estándar) guardado.");
                }
            } catch (Exception e) {
                System.err.println(">>> ERROR en procesarUsuarioEstandar: " + e.getMessage());
            }
        }
    }

    private String extraerValor(String texto, String etiqueta) {
        try {
            if (!texto.contains(etiqueta)) return null;
            int inicio = texto.indexOf(etiqueta) + etiqueta.length();
            int finTab = texto.indexOf("\t", inicio);
            int finEsp = texto.indexOf(" ", inicio);
            
            int fin;
            if (finTab != -1 && finEsp != -1) fin = Math.min(finTab, finEsp);
            else if (finTab != -1) fin = finTab;
            else if (finEsp != -1) fin = finEsp;
            else fin = texto.length();
            
            return texto.substring(inicio, fin).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private String interpretarModo(String modo) {
        if (modo == null) return "DESCONOCIDO";
        return switch (modo) {
            case "1" -> "SOLO HUELLA";
            case "3" -> "SOLO PASSWORD";
            case "4" -> "SOLO TARJETA";
            case "128" -> "HUELLA + PASSWORD";
            default -> "MODO " + modo;
        };
    }
}