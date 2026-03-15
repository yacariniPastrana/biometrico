package com.stenterprise.biometrico.controller;

import com.stenterprise.biometrico.model.Marcacion;
import com.stenterprise.biometrico.repository.MarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/iclock")
public class BiometricController {

    @Autowired
    private MarcacionRepository marcacionRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/cdata")
    public ResponseEntity<String> handshake(@RequestParam("SN") String sn) {
        // Respuesta estándar para dispositivos ZK / TodoMicro
        return ResponseEntity.ok("GET OPTION FROM: " + sn + "\nRealtime=1\nDelay=30");
    }

    @PostMapping("/cdata")
    public ResponseEntity<String> receiveData(
            @RequestParam("table") String table, 
            @RequestBody String body) {

        if ("ATTLOG".equals(table) && body != null) {
            String[] lineas = body.split("\n");
            for (String linea : lineas) {
                String[] campos = linea.split("\t");
                if (campos.length >= 2) {
                    Marcacion m = new Marcacion();
                    m.setIdBiometrico(campos[0].trim());
                    m.setFechaHora(LocalDateTime.parse(campos[1].trim(), formatter));
                    m.setEstadoCalculado("PENDIENTE");
                    
                    marcacionRepository.save(m);
                }
            }
        }
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/getrequest")
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok("OK");
    }
}