package com.stenterprise.biometrico.repository;

import com.stenterprise.biometrico.model.Marcacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcacionRepository extends JpaRepository<Marcacion, Integer> {
}