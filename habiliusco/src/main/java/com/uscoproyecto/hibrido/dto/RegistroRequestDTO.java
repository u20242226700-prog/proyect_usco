package com.uscoproyecto.hibrido.dto;

public class RegistroRequestDTO {
    private String username;
    private String email;
    private String password;
    private String nombreCompleto;
    private String telefono;
    private String facultad;
    private String programa;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getFacultad() { return facultad; }
    public void setFacultad(String facultad) { this.facultad = facultad; }
    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }
}
