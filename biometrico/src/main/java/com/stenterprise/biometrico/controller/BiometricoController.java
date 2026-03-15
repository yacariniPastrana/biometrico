package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.model.Empleado;
import com.stenterprise.biometrico.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

        // --- DIAGNÓSTICO EN LOGS ---
        System.out.println("\n--- NUEVA TRAMA RECIBIDA ---");
        System.out.println("TABLA: " + table);
        System.out.println("CONTENIDO:");
        System.out.println(body); 
        System.out.println("---------------------------\n");

        if ("USER".equals(table) && body != null) {
            procesarUsuario(body);
        }

        return ResponseEntity.ok("OK");
    }

    private void procesarUsuario(String body) {
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
                    if (campos.length > 2) {
                        emp.setPrivilegio(campos[2].trim().equals("14") ? "ADMINISTRADOR" : "USUARIO NORMAL");
                    }
                    if (campos.length > 7) {
                        emp.setModoVerificacion(interpretarModo(campos[7].trim()));
                    }
                    if (emp.getFechaCreacion() == null) {
                        emp.setFechaCreacion(LocalDateTime.now());
                    }
                    empleadoRepository.save(emp);
                    System.out.println(">>> ÉXITO: Empleado " + idBio + " guardado en DB.");
                }
            } catch (Exception e) {
                System.out.println(">>> ERROR al procesar línea: " + e.getMessage());
            }
        }
    }

    private String interpretarModo(String modo) {
        return switch (modo) {
            case "1" -> "SOLO HUELLA";
            case "3" -> "SOLO PASSWORD";
            case "4" -> "SOLO TARJETA";
            case "128" -> "HUELLA + PASSWORD";
            default -> "MODO " + modo;
        };
    }
}