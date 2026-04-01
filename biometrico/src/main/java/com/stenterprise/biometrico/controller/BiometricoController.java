package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.model.Empleado;
import com.stenterprise.biometrico.model.Marcacion;
import com.stenterprise.biometrico.repository.EmpleadoRepository;
import com.stenterprise.biometrico.repository.MarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/iclock")
public class BiometricoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private MarcacionRepository marcacionRepository;

    // Formateador estándar para asistencia (yyyy-MM-dd HH:mm:ss)
    private static final DateTimeFormatter FORMATO_BIOMETRICO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/cdata")
    public ResponseEntity<String> handshake(@RequestParam("SN") String sn) {
        System.out.println(">>> Handshake SN: " + sn);
        // Respuesta estricta TEXT_PLAIN_VALUE para hardware 
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("GET OPTION FROM: " + sn + "\nRealtime=1\nDelay=30");
    }

    @GetMapping("/getrequest")
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("OK");
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
        else if ("ATTLOG".equals(table) && body != null) {
            procesarMarcacionAsistencia(body);
        }

        // Respuesta obligatoria para confirmar recepción al hardware 
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("OK\n");
    }

    private void procesarUsuarioIncrustado(String body) {
        try {
            String idBio = extraerValor(body, "PIN=");
            if (idBio != null) {
                Empleado emp = empleadoRepository.findByIdBiometrico(idBio).orElse(new Empleado());
                emp.setIdBiometrico(idBio);
                emp.setNombreCompleto(extraerValor(body, "Name="));
                emp.setPrivilegio("14".equals(extraerValor(body, "Pri=")) ? "ADMINISTRADOR" : "USUARIO NORMAL");
                emp.setModoVerificacion(interpretarModo(extraerValor(body, "Verify=")));
                empleadoRepository.save(emp);
                System.out.println(">>> SINCRONIZACIÓN: Usuario " + idBio + " actualizado.");
            }
        } catch (Exception e) {
            System.err.println(">>> ERROR OPERLOG: " + e.getMessage());
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
                    Empleado emp = empleadoRepository.findByIdBiometrico(idBio).orElse(new Empleado());
                    emp.setIdBiometrico(idBio);
                    if (campos.length > 1) emp.setNombreCompleto(campos[1].trim());
                    
                    if (emp.getFechaCreacion() == null) {
                        emp.setFechaCreacion(LocalDateTime.now());
                    }
                    empleadoRepository.save(emp);
                }
            } catch (Exception e) {
                System.err.println(">>> ERROR USER: " + e.getMessage());
            }
        }
    }

    private void procesarMarcacionAsistencia(String body) {
        String[] lineas = body.split("\n");
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;
            try {
                String[] campos = linea.split("\t");
                String idBio = campos[0].trim();
                
                // OPTIMIZACIÓN CRÍTICA: Extraer la fecha/hora real de la trama 
                // El hardware envía la hora en el segundo campo (índice 1)
                String fechaTexto = campos[1].trim(); 
                LocalDateTime fechaRealHardware = LocalDateTime.parse(fechaTexto, FORMATO_BIOMETRICO);
                LocalDate diaEvento = fechaRealHardware.toLocalDate();

                // Consultamos marcas existentes del día para determinar la secuencia
                List<Marcacion> marcasHoy = marcacionRepository.findByIdBiometricoAndFechaDiaOrderByFechaHoraAsc(idBio, diaEvento);
                int contador = marcasHoy.size();

                String tipo = determinarTipoRegistro(contador);

                Empleado emp = empleadoRepository.findByIdBiometrico(idBio).orElse(null);

                Marcacion m = new Marcacion();
                m.setIdBiometrico(idBio);
                m.setFechaHora(fechaRealHardware); // GUARDAMOS DATOS DEL HARDWARE 
                m.setFechaDia(diaEvento);
                m.setTipoRegistro(tipo);
                m.setEmpleado(emp);

                marcacionRepository.save(m);
                System.out.println(">>> ASISTENCIA REGISTRADA: " + idBio + " | " + tipo + " | Hora: " + fechaTexto);

            } catch (Exception e) {
                System.err.println(">>> ERROR ATTLOG: " + e.getMessage());
            }
        }
    }

    private String determinarTipoRegistro(int contador) {
        return switch (contador) {
            case 0 -> "INGRESO LABORAL";
            case 1 -> "INICIO REFRIGERIO";
            case 2 -> "FIN REFRIGERIO";
            case 3 -> "SALIDA LABORAL";
            default -> "MARCA EXTRA #" + (contador + 1);
        };
    }

    private String extraerValor(String texto, String etiqueta) {
        try {
            if (!texto.contains(etiqueta)) return null;
            int inicio = texto.indexOf(etiqueta) + etiqueta.length();
            int finTab = texto.indexOf("\t", inicio);
            int finEsp = texto.indexOf(" ", inicio);
            int fin = (finTab != -1 && finEsp != -1) ? Math.min(finTab, finEsp) : (finTab != -1 ? finTab : (finEsp != -1 ? finEsp : texto.length()));
            return texto.substring(inicio, fin).trim();
        } catch (Exception e) { return null; }
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