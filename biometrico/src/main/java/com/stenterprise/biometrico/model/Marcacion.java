package com.stenterprise.biometrico.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marcaciones")
public class Marcacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación con Empleado usando el campo id_biometrico de la tabla
    @ManyToOne
    @JoinColumn(name = "id_biometrico", referencedColumnName = "id_biometrico")
    private Empleado empleado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "estado_calculado", length = 30)
    private String estadoCalculado;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public Marcacion() {}
    
    // Getters y Setters

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String getEstadoCalculado() {
		return estadoCalculado;
	}

	public void setEstadoCalculado(String estadoCalculado) {
		this.estadoCalculado = estadoCalculado;
	}

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

    
    
    
}