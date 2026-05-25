package com.uscoproyecto.hibrido.model.postgresql;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receptor_id", nullable = false)
    private Usuario receptor;

    @Column(nullable = false)
    private Integer calificacion; // 1-5

    @Column(length = 500)
    private String comentario;

    @Column(name = "fecha_resena")
    private LocalDateTime fechaResena;

    @PrePersist
    public void prePersist() { this.fechaResena = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public Usuario getReceptor() { return receptor; }
    public void setReceptor(Usuario receptor) { this.receptor = receptor; }
    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getFechaResena() { return fechaResena; }
    public void setFechaResena(LocalDateTime fechaResena) { this.fechaResena = fechaResena; }
}
