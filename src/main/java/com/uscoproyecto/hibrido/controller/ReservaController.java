package com.uscoproyecto.hibrido.controller;

import com.uscoproyecto.hibrido.model.postgresql.Reserva;
import com.uscoproyecto.hibrido.service.MensajeService;
import com.uscoproyecto.hibrido.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private MensajeService mensajeService;

    @GetMapping("/solicitar/{servicioId}")
    public String formSolicitar(@PathVariable Long servicioId, Model model, Authentication auth) {
        model.addAttribute("servicioId", servicioId);
        model.addAttribute("fechasOcupadas", reservaService.fechasOcupadas(servicioId));
        model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        return "reservas/solicitar";
    }

    @PostMapping("/solicitar")
    public String solicitar(@RequestParam Long servicioId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                            @RequestParam(required = false) BigDecimal precioPropuesto,
                            @RequestParam(required = false) String mensaje,
                            Authentication auth, RedirectAttributes ra) {
        try {
            Reserva r = reservaService.solicitar(servicioId, auth.getName(), fecha, precioPropuesto, mensaje);
            ra.addFlashAttribute("exito", "Solicitud enviada correctamente.");
            return "redirect:/reservas/chat/" + r.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/solicitar/" + servicioId;
        }
    }

    @GetMapping("/mis-reservas")
    public String misReservas(Model model, Authentication auth) {
        model.addAttribute("reservas", reservaService.misReservas(auth.getName()));
        model.addAttribute("recibidas", reservaService.reservasComoProveedor(auth.getName()));
        model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        return "reservas/mis-reservas";
    }

    @GetMapping("/chat/{reservaId}")
    public String chat(@PathVariable Long reservaId, Model model, Authentication auth) {
        Reserva reserva = reservaService.findById(reservaId).orElseThrow();
        String username = auth.getName();

        // Verificar participante
        boolean esSolicitante = reserva.getSolicitante().getUsername().equals(username);
        boolean esProveedor = reserva.getServicio().getUsuario().getUsername().equals(username);
        if (!esSolicitante && !esProveedor) return "redirect:/portal";

        mensajeService.marcarLeidos(reservaId, username);

        model.addAttribute("reserva", reserva);
        model.addAttribute("mensajes", mensajeService.getMensajesReserva(reservaId));
        model.addAttribute("esSolicitante", esSolicitante);
        model.addAttribute("esProveedor", esProveedor);
        model.addAttribute("username", username);
        model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(username));
        return "reservas/chat";
    }

    @PostMapping("/chat/{reservaId}/enviar")
    public String enviarMensaje(@PathVariable Long reservaId,
                                @RequestParam String contenido,
                                Authentication auth) {
        mensajeService.enviar(reservaId, auth.getName(), contenido);
        return "redirect:/reservas/chat/" + reservaId;
    }

    @PostMapping("/{reservaId}/aceptar")
    public String aceptar(@PathVariable Long reservaId, Authentication auth, RedirectAttributes ra) {
        reservaService.aceptar(reservaId, auth.getName());
        ra.addFlashAttribute("exito", "Reserva aceptada.");
        return "redirect:/reservas/chat/" + reservaId;
    }

    @PostMapping("/{reservaId}/rechazar")
    public String rechazar(@PathVariable Long reservaId, Authentication auth, RedirectAttributes ra) {
        reservaService.rechazar(reservaId, auth.getName());
        ra.addFlashAttribute("exito", "Reserva rechazada.");
        return "redirect:/reservas/mis-reservas";
    }

    @PostMapping("/{reservaId}/negociar")
    public String negociar(@PathVariable Long reservaId,
                           @RequestParam BigDecimal nuevoPrecio,
                           Authentication auth, RedirectAttributes ra) {
        reservaService.negociarPrecio(reservaId, auth.getName(), nuevoPrecio);
        ra.addFlashAttribute("exito", "Propuesta de precio enviada.");
        return "redirect:/reservas/chat/" + reservaId;
    }

    @PostMapping("/{reservaId}/aceptar-precio")
    public String aceptarPrecio(@PathVariable Long reservaId, Authentication auth, RedirectAttributes ra) {
        reservaService.aceptarPrecio(reservaId, auth.getName());
        ra.addFlashAttribute("exito", "Precio aceptado. ¡Reserva confirmada!");
        return "redirect:/reservas/chat/" + reservaId;
    }

    @PostMapping("/{reservaId}/finalizar")
    public String finalizar(@PathVariable Long reservaId, Authentication auth, RedirectAttributes ra) {
        reservaService.finalizar(reservaId, auth.getName());
        ra.addFlashAttribute("exito", "Servicio marcado como finalizado.");
        return "redirect:/reservas/mis-reservas";
    }

    @PostMapping("/{reservaId}/cancelar")
    public String cancelar(@PathVariable Long reservaId, Authentication auth, RedirectAttributes ra) {
        reservaService.cancelar(reservaId, auth.getName());
        ra.addFlashAttribute("exito", "Reserva cancelada.");
        return "redirect:/reservas/mis-reservas";
    }
}
