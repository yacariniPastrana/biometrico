package com.stenterprise.biometrico.repository;

import com.stenterprise.biometrico.model.Marcacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MarcacionRepository extends JpaRepository<Marcacion, Integer> {
    // Busca marcas de hoy para un usuario específico para aplicar la lógica de las 4 marcas
    List<Marcacion> findByIdBiometricoAndFechaDiaOrderByFechaHoraAsc(String idBiometrico, LocalDate fechaDia);
}