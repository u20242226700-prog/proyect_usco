package com.uscoproyecto.hibrido.model.postgresql;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;
    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;
    @Column(name = "precio_propuesto", precision = 10, scale = 2)
    private BigDecimal precioPropuesto;
    @Column(name = "precio_acordado", precision = 10, scale = 2)
    private BigDecimal precioAcordado;
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";
    @Column(length = 500)
    private String mensaje;
    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    @PrePersist
    public void prePersist() {
        this.fechaSolicitud = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }
    public BigDecimal getPrecioPropuesto() { return precioPropuesto; }
    public void setPrecioPropuesto(BigDecimal precioPropuesto) { this.precioPropuesto = precioPropuesto; }
    public BigDecimal getPrecioAcordado() { return precioAcordado; }
    public void setPrecioAcordado(BigDecimal precioAcordado) { this.precioAcordado = precioAcordado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}