package com.uscoproyecto.hibrido.model.postgresql;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id", nullable = false)
    private Usuario emisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptor_id", nullable = false)
    private Usuario receptor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @Column(nullable = false, length = 1000)
    private String contenido;

    // TEXTO, PRECIO_PROPUESTO, PRECIO_ACEPTADO, PRECIO_RECHAZADO
    @Column(length = 30)
    private String tipo = "TEXTO";

    @Column(name = "leido")
    private Boolean leido = false;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @PrePersist
    public void prePersist() {
        this.fechaEnvio = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getEmisor() { return emisor; }
    public void setEmisor(Usuario emisor) { this.emisor = emisor; }
    public Usuario getReceptor() { return receptor; }
    public void setReceptor(Usuario receptor) { this.receptor = receptor; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Boolean getLeido() { return leido; }
    public void setLeido(Boolean leido) { this.leido = leido; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}
