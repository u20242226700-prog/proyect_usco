package com.uscoproyecto.hibrido.service;

import com.uscoproyecto.hibrido.model.postgresql.Mensaje;
import com.uscoproyecto.hibrido.model.postgresql.Reserva;
import com.uscoproyecto.hibrido.model.postgresql.Usuario;
import com.uscoproyecto.hibrido.repository.postgresql.MensajeRepository;
import com.uscoproyecto.hibrido.repository.postgresql.ReservaRepository;
import com.uscoproyecto.hibrido.repository.postgresql.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Mensaje> getMensajesReserva(Long reservaId) {
        return mensajeRepository.findByReservaIdOrderByFechaEnvioAsc(reservaId);
    }

    public Mensaje enviar(Long reservaId, String emisorUsername, String contenido) {
        Usuario emisor = usuarioRepository.findByUsername(emisorUsername).orElseThrow();
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();

        // Determinar receptor
        Usuario receptor = reserva.getServicio().getUsuario().getUsername().equals(emisorUsername)
            ? reserva.getSolicitante()
            : reserva.getServicio().getUsuario();

        Mensaje msg = new Mensaje();
        msg.setEmisor(emisor);
        msg.setReceptor(receptor);
        msg.setReserva(reserva);
        msg.setContenido(contenido);
        msg.setTipo("TEXTO");
        return mensajeRepository.save(msg);
    }

    public Long countUnread(String username) {
        Usuario u = usuarioRepository.findByUsername(username).orElseThrow();
        return mensajeRepository.countUnreadByUser(u.getId());
    }

    public void marcarLeidos(Long reservaId, String username) {
        Usuario u = usuarioRepository.findByUsername(username).orElseThrow();
        List<Mensaje> msgs = mensajeRepository.findByReservaIdOrderByFechaEnvioAsc(reservaId);
        msgs.stream()
            .filter(m -> m.getReceptor().getId().equals(u.getId()) && !m.getLeido())
            .forEach(m -> {
                m.setLeido(true);
                mensajeRepository.save(m);
            });
    }
}
