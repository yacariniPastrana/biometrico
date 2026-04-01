package com.stenterprise.biometrico.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "marcaciones")
@EntityListeners(AuditingEntityListener.class)
public class Marcacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_biometrico")
    private String idBiometrico;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "fecha_dia")
    private LocalDate fechaDia;

    @Column(name = "tipo_registro")
    private String tipoRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @Column(name = "es_manual")
    private Boolean esManual = false;

    @Column(name = "motivo_edicion")
    private String motivoEdicion;
    
    public Marcacion() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getIdBiometrico() { return idBiometrico; }
    public void setIdBiometrico(String idBiometrico) { this.idBiometrico = idBiometrico; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public LocalDate getFechaDia() { return fechaDia; }
    public void setFechaDia(LocalDate fechaDia) { this.fechaDia = fechaDia; }
    public String getTipoRegistro() { return tipoRegistro; }
    public void setTipoRegistro(String tipoRegistro) { this.tipoRegistro = tipoRegistro; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public Boolean getEsManual() { return esManual; }
    public void setEsManual(Boolean esManual) { this.esManual = esManual; }
    public String getMotivoEdicion() { return motivoEdicion; }
    public void setMotivoEdicion(String motivoEdicion) { this.motivoEdicion = motivoEdicion; }
}