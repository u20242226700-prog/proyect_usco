package com.uscoproyecto.hibrido.model.mysql;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_logs")
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(length = 50)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "usuario_username", length = 50)
    private String usuarioUsername;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(length = 1000)
    private String detalle;

    @Column(nullable = false, length = 10)
    private String nivel = "INFO";

    @Column(name = "fecha_evento")
    private LocalDateTime fechaEvento;

    @PrePersist
    public void prePersist() {
        this.fechaEvento = LocalDateTime.now();
    }

    // ---- Getters y Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }

    public String getUsuarioUsername() { return usuarioUsername; }
    public void setUsuarioUsername(String usuarioUsername) { this.usuarioUsername = usuarioUsername; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }
}
