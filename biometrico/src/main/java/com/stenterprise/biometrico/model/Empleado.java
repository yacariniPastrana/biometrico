package com.stenterprise.biometrico.model;

import jakarta.persistence.*;
import java.time.LocalDate;
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

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "numero_documento", unique = true)
    private String numeroDocumento;
    
    @Column(name = "fecha_cumpleanos")
    private LocalDate fechaCumpleanos;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "password")
    private String password;

    public Empleado() {}

    // GETTERS Y SETTERS
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getIdBiometrico() { return idBiometrico; }
    public void setIdBiometrico(String idBiometrico) { this.idBiometrico = idBiometrico; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getPrivilegio() { return privilegio; }
    public void setPrivilegio(String privilegio) { this.privilegio = privilegio; }
    public String getModoVerificacion() { return modoVerificacion; }
    public void setModoVerificacion(String modoVerificacion) { this.modoVerificacion = modoVerificacion; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDate getFechaCumpleanos() { return fechaCumpleanos; }
    public void setFechaCumpleanos(LocalDate fechaCumpleanos) { this.fechaCumpleanos = fechaCumpleanos; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}