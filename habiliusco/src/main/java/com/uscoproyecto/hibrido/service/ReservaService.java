package com.uscoproyecto.hibrido.service;

import com.uscoproyecto.hibrido.model.postgresql.*;
import com.uscoproyecto.hibrido.repository.postgresql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    public Reserva solicitar(Long servicioId, String solicitanteUsername,
                             LocalDate fecha, BigDecimal precioPropuesto, String mensaje) {
        Servicio servicio = servicioRepository.findById(servicioId).orElseThrow();
        Usuario solicitante = usuarioRepository.findByUsername(solicitanteUsername).orElseThrow();

        // Verificar disponibilidad
        List<Reserva> ocupadas = reservaRepository.findFechaOcupada(servicioId, fecha);
        if (!ocupadas.isEmpty()) {
            throw new RuntimeException("La fecha " + fecha + " ya está ocupada.");
        }

        // No puede reservar su propio servicio
        if (servicio.getUsuario().getId().equals(solicitante.getId())) {
            throw new RuntimeException("No puedes reservar tu propio servicio.");
        }

        Reserva reserva = new Reserva();
        reserva.setSolicitante(solicitante);
        reserva.setServicio(servicio);
        reserva.setFechaReserva(fecha);
        reserva.setPrecioPropuesto(precioPropuesto);
        reserva.setMensaje(mensaje);
        reserva.setEstado("PENDIENTE");

        Reserva saved = reservaRepository.save(reserva);

        // Mensaje inicial
        Mensaje msg = new Mensaje();
        msg.setEmisor(solicitante);
        msg.setReceptor(servicio.getUsuario());
        msg.setReserva(saved);
        msg.setContenido("📅 Nueva solicitud para el " + fecha + 
            (precioPropuesto != null ? " | 💰 Precio propuesto: $" + precioPropuesto : "") +
            (mensaje != null && !mensaje.isBlank() ? " | " + mensaje : ""));
        msg.setTipo("TEXTO");
        mensajeRepository.save(msg);

        return saved;
    }

    public Reserva aceptar(Long reservaId, String proveedorUsername) {
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();
        validarProveedor(reserva, proveedorUsername);
        reserva.setEstado("ACEPTADA");
        if (reserva.getPrecioAcordado() == null) {
            reserva.setPrecioAcordado(reserva.getPrecioPropuesto());
        }
        return reservaRepository.save(reserva);
    }

    public Reserva rechazar(Long reservaId, String proveedorUsername) {
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();
        validarProveedor(reserva, proveedorUsername);
        reserva.setEstado("RECHAZADA");
        return reservaRepository.save(reserva);
    }

    public Reserva negociarPrecio(Long reservaId, String username, BigDecimal nuevoPrecio) {
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();
        reserva.setPrecioPropuesto(nuevoPrecio);
        reserva.setEstado("NEGOCIANDO");
        Reserva saved = reservaRepository.save(reserva);

        // Identificar emisor y receptor
        Usuario emisor = usuarioRepository.findByUsername(username).orElseThrow();
        boolean esProveedor = reserva.getServicio().getUsuario().getUsername().equals(username);
        Usuario receptor = esProveedor ? reserva.getSolicitante() : reserva.getServicio().getUsuario();

        Mensaje msg = new Mensaje();
        msg.setEmisor(emisor);
        msg.setReceptor(receptor);
        msg.setReserva(saved);
        msg.setContenido("💰 Propuesta de precio: $" + nuevoPrecio);
        msg.setTipo("PRECIO_PROPUESTO");
        mensajeRepository.save(msg);

        return saved;
    }

    public Reserva aceptarPrecio(Long reservaId, String username) {
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();
        reserva.setPrecioAcordado(reserva.getPrecioPropuesto());
        reserva.setEstado("ACEPTADA");
        return reservaRepository.save(reserva);
    }

    public Reserva finalizar(Long reservaId, String username) {
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();
        reserva.setEstado("FINALIZADA");
        return reservaRepository.save(reserva);
    }

    public Reserva cancelar(Long reservaId, String username) {
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();
        reserva.setEstado("CANCELADA");
        return reservaRepository.save(reserva);
    }

    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> misReservas(String username) {
        Usuario u = usuarioRepository.findByUsername(username).orElseThrow();
        return reservaRepository.findBySolicitanteId(u.getId());
    }

    public List<Reserva> reservasComoProveedor(String username) {
        Usuario u = usuarioRepository.findByUsername(username).orElseThrow();
        return reservaRepository.findByServicioUsuarioId(u.getId());
    }

    public List<LocalDate> fechasOcupadas(Long servicioId) {
        return reservaRepository.findByServicioId(servicioId).stream()
            .filter(r -> !r.getEstado().equals("RECHAZADA") && !r.getEstado().equals("CANCELADA"))
            .map(Reserva::getFechaReserva)
            .toList();
    }

    private void validarProveedor(Reserva reserva, String username) {
        if (!reserva.getServicio().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }
    }
}
