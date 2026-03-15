package com.stenterprise.biometrico.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_biometrico", unique = true, nullable = false)
    private String idBiometrico;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "privilegio")
    private String privilegio;

    @Column(name = "modo_verificacion")
    private String modoVerificacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public Empleado() {}

    // --- GETTERS Y SETTERS (Indispensables para que el controlador no marque error) ---

    public String getIdBiometrico() { return idBiometrico; }
    public void setIdBiometrico(String idBiometrico) { this.idBiometrico = idBiometrico; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getPrivilegio() { return privilegio; }
    public void setPrivilegio(String privilegio) { this.privilegio = privilegio; }

    public String getModoVerificacion() { return modoVerificacion; }
    public void setModoVerificacion(String modoVerificacion) { this.modoVerificacion = modoVerificacion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}