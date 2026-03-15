package com.stenterprise.biometrico.repository;

import com.stenterprise.biometrico.model.Empleado;
import com.stenterprise.biometrico.model.Marcacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface MarcacionRepository extends JpaRepository<Marcacion, Integer> {
    // Cuenta marcaciones de un empleado entre dos momentos (inicio y fin del día)
    long countByEmpleadoAndFechaHoraBetween(Empleado empleado, LocalDateTime inicio, LocalDateTime fin);
}