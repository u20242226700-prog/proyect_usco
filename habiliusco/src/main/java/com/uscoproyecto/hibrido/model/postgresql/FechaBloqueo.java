package com.uscoproyecto.hibrido.model.postgresql;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "fechas_bloqueo")
public class FechaBloqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_bloqueada", nullable = false)
    private LocalDate fechaBloqueada;

    @Column(length = 100)
    private String motivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDate getFechaBloqueada() { return fechaBloqueada; }
    public void setFechaBloqueada(LocalDate fechaBloqueada) { this.fechaBloqueada = fechaBloqueada; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
