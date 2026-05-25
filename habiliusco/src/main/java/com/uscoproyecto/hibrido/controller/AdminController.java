package com.uscoproyecto.hibrido.controller;

import com.uscoproyecto.hibrido.model.postgresql.Servicio;
import com.uscoproyecto.hibrido.model.postgresql.Usuario;
import com.uscoproyecto.hibrido.repository.postgresql.ReservaRepository;
import com.uscoproyecto.hibrido.repository.postgresql.ServicioRepository;
import com.uscoproyecto.hibrido.repository.postgresql.UsuarioRepository;
import com.uscoproyecto.hibrido.repository.mysql.AuditoriaLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalServicios", servicioRepository.countAll());
        model.addAttribute("serviciosActivos", servicioRepository.countActivos());
        model.addAttribute("totalReservas", reservaRepository.countAll());
        model.addAttribute("reservasPendientes", reservaRepository.countByEstado("PENDIENTE"));
        model.addAttribute("reservasAceptadas", reservaRepository.countByEstado("ACEPTADA"));
        model.addAttribute("reservasFinalizadas", reservaRepository.countByEstado("FINALIZADA"));
        model.addAttribute("ultimosLogs", auditoriaLogRepository.findTop20ByOrderByFechaEventoDesc());
        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/toggle")
    public String toggleUsuario(@PathVariable Long id, RedirectAttributes ra) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        u.setActivo(!u.getActivo());
        usuarioRepository.save(u);
        ra.addFlashAttribute("exito", "Usuario " + (u.getActivo() ? "activado" : "desactivado") + ".");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/rol")
    public String cambiarRol(@PathVariable Long id, @RequestParam String rol, RedirectAttributes ra) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        if (u.getRoles().contains(rol)) {
            u.getRoles().remove(rol);
        } else {
            u.getRoles().add(rol);
        }
        usuarioRepository.save(u);
        ra.addFlashAttribute("exito", "Rol actualizado.");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/servicios")
    public String servicios(Model model) {
        model.addAttribute("servicios", servicioRepository.findAll());
        return "admin/servicios";
    }

    @PostMapping("/servicios/{id}/estado")
    public String cambiarEstadoServicio(@PathVariable Long id, @RequestParam String estado, RedirectAttributes ra) {
        Servicio s = servicioRepository.findById(id).orElseThrow();
        s.setEstado(estado);
        servicioRepository.save(s);
        ra.addFlashAttribute("exito", "Estado actualizado.");
        return "redirect:/admin/servicios";
    }

    @PostMapping("/servicios/{id}/eliminar")
    public String eliminarServicio(@PathVariable Long id, RedirectAttributes ra) {
        servicioRepository.deleteById(id);
        ra.addFlashAttribute("exito", "Servicio eliminado.");
        return "redirect:/admin/servicios";
    }

    @GetMapping("/reservas")
    public String reservas(Model model) {
        model.addAttribute("reservas", reservaRepository.findAll());
        return "admin/reservas";
    }

    @GetMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("logs", auditoriaLogRepository.findAll());
        return "admin/logs";
    }
}
