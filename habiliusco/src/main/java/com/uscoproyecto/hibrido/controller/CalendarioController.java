package com.uscoproyecto.hibrido.controller;

import com.uscoproyecto.hibrido.model.postgresql.FechaBloqueo;
import com.uscoproyecto.hibrido.model.postgresql.Usuario;
import com.uscoproyecto.hibrido.repository.postgresql.FechaBloqueoRepository;
import com.uscoproyecto.hibrido.repository.postgresql.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/calendario")
public class CalendarioController {

    @Autowired private FechaBloqueoRepository fechaBloqueoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping
    public String calendario(Model model, Authentication auth) {
        Usuario u = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        List<FechaBloqueo> bloqueos = fechaBloqueoRepository.findByUsuarioId(u.getId());
        model.addAttribute("bloqueos", bloqueos);
        model.addAttribute("fechasBloqueadas",
            bloqueos.stream().map(b -> b.getFechaBloqueada().toString()).collect(Collectors.toList()));
        return "calendario/calendario";
    }

    @PostMapping("/bloquear")
    public String bloquear(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                           @RequestParam(required = false) String motivo,
                           Authentication auth, RedirectAttributes ra) {
        if (fecha.isBefore(LocalDate.now())) {
            ra.addFlashAttribute("error", "No puedes bloquear fechas pasadas.");
            return "redirect:/calendario";
        }
        Usuario u = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        FechaBloqueo bloqueo = new FechaBloqueo();
        bloqueo.setUsuario(u);
        bloqueo.setFechaBloqueada(fecha);
        bloqueo.setMotivo(motivo);
        fechaBloqueoRepository.save(bloqueo);
        ra.addFlashAttribute("exito", "Fecha " + fecha + " bloqueada.");
        return "redirect:/calendario";
    }

    @PostMapping("/desbloquear/{id}")
    public String desbloquear(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        FechaBloqueo b = fechaBloqueoRepository.findById(id).orElseThrow();
        if (b.getUsuario().getUsername().equals(auth.getName())) {
            fechaBloqueoRepository.deleteById(id);
            ra.addFlashAttribute("exito", "Fecha desbloqueada.");
        }
        return "redirect:/calendario";
    }
}
