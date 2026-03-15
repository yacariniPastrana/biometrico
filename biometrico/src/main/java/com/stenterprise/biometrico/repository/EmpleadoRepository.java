package com.stenterprise.biometrico.repository;

import com.stenterprise.biometrico.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByIdBiometrico(String idBiometrico);
}