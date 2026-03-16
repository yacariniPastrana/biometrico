package com.stenterprise.biometrico.dto;

public class AsistenciaDTO {
    private Integer id;
    private String nombreEmpleado;
    private String documento;
    private String tipoRegistro;
    private String hora;
    private String fecha;
    private String idBio;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getTipoRegistro() { return tipoRegistro; }
    public void setTipoRegistro(String tipoRegistro) { this.tipoRegistro = tipoRegistro; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getIdBio() { return idBio; }
    public void setIdBio(String idBio) { this.idBio = idBio; }
}