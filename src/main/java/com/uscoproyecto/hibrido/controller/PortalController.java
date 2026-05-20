package com.uscoproyecto.hibrido.controller;

import com.uscoproyecto.hibrido.model.postgresql.Servicio;
import com.uscoproyecto.hibrido.service.ServicioService;
import com.uscoproyecto.hibrido.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PortalController {

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private MensajeService mensajeService;

    @GetMapping("/portal")
    public String portal(@RequestParam(required = false) String q,
                         @RequestParam(required = false) String categoria,
                         Model model, Authentication auth) {
        List<Servicio> servicios;
        if (q != null && !q.isBlank()) {
            servicios = servicioService.buscar(q);
            model.addAttribute("busqueda", q);
        } else if (categoria != null && !categoria.isBlank()) {
            servicios = servicioService.porCategoria(categoria);
            model.addAttribute("categoriaActiva", categoria);
        } else {
            servicios = servicioService.listarActivos();
        }

        model.addAttribute("servicios", servicios);
        model.addAttribute("categorias", servicioService.getCategorias());
        if (auth != null) {
            model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        }
        return "portal/index";
    }

    @GetMapping("/portal/servicio/{id}")
    public String detalleServicio(@PathVariable Long id, Model model, Authentication auth) {
        Servicio servicio = servicioService.findById(id).orElseThrow();
        model.addAttribute("servicio", servicio);
        if (auth != null) {
            model.addAttribute("esPropio", servicio.getUsuario().getUsername().equals(auth.getName()));
            model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        }
        return "portal/detalle-servicio";
    }

    @GetMapping("/mis-servicios")
    public String misServicios(Model model, Authentication auth) {
        model.addAttribute("servicios", servicioService.misServicios(auth.getName()));
        model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        return "portal/mis-servicios";
    }

    @GetMapping("/mis-servicios/nuevo")
    public String nuevoServicioForm(Model model, Authentication auth) {
        model.addAttribute("servicio", new Servicio());
        model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        return "portal/form-servicio";
    }

    @PostMapping("/mis-servicios/crear")
    public String crearServicio(@ModelAttribute Servicio servicio, Authentication auth) {
        servicioService.crear(servicio, auth.getName());
        return "redirect:/mis-servicios?creado=true";
    }

    @GetMapping("/mis-servicios/editar/{id}")
    public String editarServicioForm(@PathVariable Long id, Model model, Authentication auth) {
        Servicio servicio = servicioService.findById(id).orElseThrow();
        if (!servicio.getUsuario().getUsername().equals(auth.getName())) {
            return "redirect:/mis-servicios";
        }
        model.addAttribute("servicio", servicio);
        model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        return "portal/form-servicio";
    }

    @PostMapping("/mis-servicios/actualizar/{id}")
    public String actualizarServicio(@PathVariable Long id, @ModelAttribute Servicio form, Authentication auth) {
        Servicio existing = servicioService.findById(id).orElseThrow();
        if (!existing.getUsuario().getUsername().equals(auth.getName())) {
            return "redirect:/mis-servicios";
        }
        existing.setTitulo(form.getTitulo());
        existing.setDescripcion(form.getDescripcion());
        existing.setCategoria(form.getCategoria());
        existing.setEstado(form.getEstado());
        existing.setPrecioBase(form.getPrecioBase());
        servicioService.actualizar(existing);
        return "redirect:/mis-servicios?actualizado=true";
    }

    @PostMapping("/mis-servicios/eliminar/{id}")
    public String eliminarServicio(@PathVariable Long id, Authentication auth) {
        Servicio servicio = servicioService.findById(id).orElseThrow();
        if (servicio.getUsuario().getUsername().equals(auth.getName())) {
            servicioService.eliminar(id);
        }
        return "redirect:/mis-servicios?eliminado=true";
    }
}
