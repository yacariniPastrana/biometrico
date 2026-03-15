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
     * PASO 1: Handshake
     * El biométrico pregunta: "¿Estás ahí?"
     */
    @GetMapping("/cdata")
    public ResponseEntity<String> handshake(@RequestParam("SN") String sn) {
        System.out.println(">>> Handshake recibido del equipo SN: " + sn);
        // Respondemos con las opciones básicas para que el equipo empiece a enviar datos
        return ResponseEntity.ok("GET OPTION FROM: " + sn + "\nRealtime=1\nDelay=30");
    }

    /**
     * PASO 2: Recepción de Datos (Registro de Empleados)
     * Cuando guardas un usuario en el menú del equipo, este envía un POST con table=USER
     */
    @PostMapping("/cdata")
    public ResponseEntity<String> receiveData(
            @RequestParam("table") String table, 
            @RequestBody String body) {

        System.out.println(">>> Datos recibidos de la tabla: " + table);

        if ("USER".equals(table) && body != null) {
            // El body viene con formato: PIN \t Name \t Pri \t Pass \t Card \t Grp \t TZ \t Verify
            String[] lineas = body.split("\n");
            
            for (String linea : lineas) {
                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split("\t");
                if (campos.length >= 1) {
                    String idBio = campos[0].trim(); // ID de usuario que incrementa
                    
                    // Buscamos si ya existe para no duplicar, sino creamos nuevo
                    Empleado emp = empleadoRepository.findByIdBiometrico(idBio)
                            .orElse(new Empleado());
                    
                    emp.setIdBiometrico(idBio);
                    
                    // Mapeo según los datos que me diste:
                    if (campos.length > 1) emp.setNombreCompleto(campos[1].trim());
                    
                    // Privilegio (0: Usuario, 14: Admin)
                    if (campos.length > 2) {
                        String pri = campos[2].trim();
                        emp.setPrivilegio(pri.equals("14") ? "ADMINISTRADOR" : "USUARIO NORMAL");
                    }
                    
                    // Modo de verificación (campo 7 en protocolo ZK estándar)
                    if (campos.length > 7) {
                        emp.setModoVerificacion(interpretarModo(campos[7].trim()));
                    }
                    
                    emp.setFechaCreacion(LocalDateTime.now());
                    
                    empleadoRepository.save(emp);
                    System.out.println(">>> Empleado guardado exitosamente: " + idBio + " - " + emp.getNombreCompleto());
                }
            }
        }
        
        // El equipo espera un "OK" para confirmar que el servidor recibió la info
        return ResponseEntity.ok("OK");
    }

    /**
     * PASO 3: Heartbeat (Mantener conexión viva)
     */
    @GetMapping("/getrequest")
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Función auxiliar para que la DB no guarde solo números, sino texto legible
     */
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