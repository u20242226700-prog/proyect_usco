package com.uscoproyecto.hibrido.controller;

import com.uscoproyecto.hibrido.model.postgresql.*;
import com.uscoproyecto.hibrido.repository.postgresql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/resenas")
public class ResenaController {

    @Autowired private ResenaRepository resenaRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @PostMapping("/crear")
    public String crear(@RequestParam Long reservaId,
                        @RequestParam Integer calificacion,
                        @RequestParam(required = false) String comentario,
                        Authentication auth, RedirectAttributes ra) {
        if (resenaRepository.existsByReservaId(reservaId)) {
            ra.addFlashAttribute("error", "Ya dejaste una reseña para esta reserva.");
            return "redirect:/reservas/mis-reservas";
        }
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();
        if (!reserva.getEstado().equals("FINALIZADA")) {
            ra.addFlashAttribute("error", "Solo puedes reseñar reservas finalizadas.");
            return "redirect:/reservas/mis-reservas";
        }
        Usuario autor = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        // El solicitante reseña al proveedor
        Usuario receptor = reserva.getServicio().getUsuario();

        Resena resena = new Resena();
        resena.setReserva(reserva);
        resena.setAutor(autor);
        resena.setReceptor(receptor);
        resena.setCalificacion(Math.min(5, Math.max(1, calificacion)));
        resena.setComentario(comentario);
        resenaRepository.save(resena);

        ra.addFlashAttribute("exito", "¡Reseña publicada! Gracias por tu opinión.");
        return "redirect:/reservas/mis-reservas";
    }
}
