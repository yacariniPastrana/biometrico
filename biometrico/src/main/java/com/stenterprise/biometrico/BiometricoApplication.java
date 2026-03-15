package com.stenterprise.biometrico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BiometricoApplication {
    public static void main(String[] args) {
        // Esto fuerza los parámetros si el properties falla
        System.setProperty("spring.datasource.url", "jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:6543/postgres?prepareThreshold=0");
        System.setProperty("spring.datasource.username", "postgres.kofruwexvelbhrgnsjer");
        System.setProperty("spring.datasource.password", "TU_PASSWORD_AQUI");
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        
        SpringApplication.run(BiometricoApplication.class, args);
    }
}
