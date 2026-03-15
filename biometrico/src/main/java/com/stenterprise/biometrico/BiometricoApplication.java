package com.stenterprise.biometrico;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class BiometricoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiometricoApplication.class, args);
    }

    /**
     * Ajuste de zona horaria global para que LocalDateTime.now() 
     * coincida con la hora de Perú.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        
        System.out.println("------------------------------------------------");
        System.out.println(">>> SISTEMA INICIADO CORRECTAMENTE");
        System.out.println(">>> Zona Horaria: " + TimeZone.getDefault().getID());
        System.out.println("------------------------------------------------");
    }
}