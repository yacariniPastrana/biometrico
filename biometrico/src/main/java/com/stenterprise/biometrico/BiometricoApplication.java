package com.stenterprise.biometrico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BiometricoApplication {
    public static void main(String[] args) {
        // Configuración directa para asegurar la conexión con el nuevo proyecto
        System.setProperty("spring.datasource.url", "jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:6543/postgres?prepareThreshold=0&sslmode=require");
        System.setProperty("spring.datasource.username", "postgres.huphjwgokykjjfpqrfty");
        System.setProperty("spring.datasource.password", "PaYN1252PaYN");
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        
        SpringApplication.run(BiometricoApplication.class, args);
    }
}