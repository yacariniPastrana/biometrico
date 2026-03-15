package com.stenterprise.biometrico.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marcaciones")
public class Marcacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_biometrico", length = 10)
    private String idBiometrico;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "estado_calculado", length = 30)
    private String estadoCalculado;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Getters y Setters
    public void setIdBiometrico(String idBiometrico) { this.idBiometrico = idBiometrico; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public void setEstadoCalculado(String estadoCalculado) { this.estadoCalculado = estadoCalculado; }
}