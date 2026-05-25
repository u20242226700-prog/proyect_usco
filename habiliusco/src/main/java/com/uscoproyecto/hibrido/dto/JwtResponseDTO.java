package com.uscoproyecto.hibrido.dto;

public class JwtResponseDTO {
    private String token;
    private String username;
    private String tipo = "Bearer";

    public JwtResponseDTO(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
