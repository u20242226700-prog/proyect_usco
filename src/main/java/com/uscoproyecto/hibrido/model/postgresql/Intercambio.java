package com.uscoproyecto.hibrido.model.postgresql;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "intercambios")
public class Intercambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(length = 500)
    private String mensaje;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @PrePersist
    public void prePersist() {
        this.fechaSolicitud = LocalDateTime.now();
    }

    // ---- Getters y Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
}
