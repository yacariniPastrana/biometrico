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

    /**
     * Handshake: El biométrico se presenta ante Render.
     */
    @GetMapping("/cdata")
    public ResponseEntity<String> handshake(@RequestParam("SN") String sn) {
        System.out.println(">>> Conexión establecida con equipo SN: " + sn);
        return ResponseEntity.ok("GET OPTION FROM: " + sn + "\nRealtime=1\nDelay=30");
    }

    /**
     * Heartbeat: Mantiene la conexión activa entre el equipo y la nube.
     */
    @GetMapping("/getrequest")
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Recepción de Datos: Aquí capturamos a los nuevos empleados (tabla USER).
     */
    @PostMapping("/cdata")
    public ResponseEntity<String> receiveData(
            @RequestParam("table") String table, 
            @RequestBody String body) {

        System.out.println(">>> Procesando tabla: " + table);

        if ("USER".equals(table) && body != null) {
            String[] lineas = body.split("\n");
            for (String linea : lineas) {
                if (linea.trim().isEmpty()) continue;
                
                try {
                    String[] campos = linea.split("\t");
                    if (campos.length >= 1) {
                        String idBio = campos[0].trim();
                        
                        // Buscamos si ya existe para actualizar, sino creamos nuevo
                        Empleado emp = empleadoRepository.findByIdBiometrico(idBio)
                                .orElse(new Empleado());
                        
                        emp.setIdBiometrico(idBio);
                        
                        // Nombre (Campo 1)
                        if (campos.length > 1) emp.setNombreCompleto(campos[1].trim());
                        
                        // Privilegio (Campo 2 -> 14 es Admin, el resto Usuario)
                        if (campos.length > 2) {
                            String pri = campos[2].trim();
                            emp.setPrivilegio(pri.equals("14") ? "ADMINISTRADOR" : "USUARIO NORMAL");
                        }
                        
                        // Modo de Verificación (Campo 7 en protocolo estándar)
                        if (campos.length > 7) {
                            emp.setModoVerificacion(interpretarModo(campos[7].trim()));
                        }

                        // Los campos DNI y CE quedan nulos para ser llenados vía SQL/Web
                        if (emp.getFechaCreacion() == null) {
                            emp.setFechaCreacion(LocalDateTime.now());
                        }

                        empleadoRepository.save(emp);
                        System.out.println(">>> Empleado sincronizado: " + idBio + " - " + emp.getNombreCompleto());
                    }
                } catch (Exception e) {
                    System.err.println("Error procesando línea: " + e.getMessage());
                }
            }
        }
        
        // BIODATA son las huellas, por ahora solo confirmamos recepción
        if ("BIODATA".equals(table)) {
            System.out.println(">>> Huella dactilar (BIODATA) recibida correctamente.");
        }

        return ResponseEntity.ok("OK");
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