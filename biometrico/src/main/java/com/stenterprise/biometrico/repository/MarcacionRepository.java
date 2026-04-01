package com.stenterprise.biometrico.repository;

import com.stenterprise.biometrico.model.Marcacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MarcacionRepository extends JpaRepository<Marcacion, Integer> {

    @EntityGraph(attributePaths = {"empleado"})
    List<Marcacion> findByFechaDia(LocalDate fechaDia);

    @EntityGraph(attributePaths = {"empleado"})
    List<Marcacion> findByFechaDiaBetween(LocalDate inicio, LocalDate fin);

    List<Marcacion> findByIdBiometricoAndFechaDiaOrderByFechaHoraAsc(String idBiometrico, LocalDate fechaDia);

    List<Marcacion> findByIdBiometricoAndFechaDiaBetweenOrderByFechaHoraAsc(String idBiometrico, LocalDate inicio, LocalDate fin);
}