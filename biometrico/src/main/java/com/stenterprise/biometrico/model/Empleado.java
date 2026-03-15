package com.stenterprise.biometrico.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_biometrico", unique = true, length = 10, nullable = false)
    private String idBiometrico;

    @Column(name = "nombre_completo", length = 150, nullable = false)
    private String nombreCompleto;

    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    @Column(name = "numero_documento", unique = true, length = 20, nullable = false)
    private String numeroDocumento;

    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    // Getters y Setters
    public String getIdBiometrico() { return idBiometrico; }
    public void setIdBiometrico(String idBiometrico) { this.idBiometrico = idBiometrico; }
}