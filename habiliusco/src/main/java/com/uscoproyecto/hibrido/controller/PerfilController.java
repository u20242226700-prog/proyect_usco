package com.uscoproyecto.hibrido.controller;

import com.uscoproyecto.hibrido.model.postgresql.Usuario;
import com.uscoproyecto.hibrido.repository.postgresql.ReservaRepository;
import com.uscoproyecto.hibrido.repository.postgresql.ServicioRepository;
import com.uscoproyecto.hibrido.repository.postgresql.UsuarioRepository;
import com.uscoproyecto.hibrido.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "uploads/fotos/";

    @GetMapping
    public String perfil(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("usuario", usuario);
        model.addAttribute("totalServicios", servicioRepository.findByUsuarioId(usuario.getId()).size());
        model.addAttribute("totalReservas", reservaRepository.findBySolicitanteId(usuario.getId()).size());
        model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        return "perfil/perfil";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam String nombreCompleto,
                              @RequestParam String telefono,
                              @RequestParam String facultad,
                              @RequestParam String programa,
                              @RequestParam(required = false) String bio,
                              Authentication auth, RedirectAttributes ra) {
        Usuario u = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        u.setNombreCompleto(nombreCompleto);
        u.setTelefono(telefono);
        u.setFacultad(facultad);
        u.setPrograma(programa);
        u.setBio(bio);
        usuarioRepository.save(u);
        ra.addFlashAttribute("exito", "Perfil actualizado correctamente.");
        return "redirect:/perfil";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                   @RequestParam String passwordNueva,
                                   @RequestParam String passwordConfirm,
                                   Authentication auth, RedirectAttributes ra) {
        Usuario u = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        if (!passwordEncoder.matches(passwordActual, u.getPassword())) {
            ra.addFlashAttribute("errorPassword", "La contraseña actual es incorrecta.");
            return "redirect:/perfil";
        }
        if (!passwordNueva.equals(passwordConfirm)) {
            ra.addFlashAttribute("errorPassword", "Las contraseñas nuevas no coinciden.");
            return "redirect:/perfil";
        }
        if (passwordNueva.length() < 6) {
            ra.addFlashAttribute("errorPassword", "La contraseña debe tener al menos 6 caracteres.");
            return "redirect:/perfil";
        }
        u.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(u);
        ra.addFlashAttribute("exitoPassword", "Contraseña cambiada exitosamente.");
        return "redirect:/perfil";
    }

    @PostMapping("/foto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto,
                             Authentication auth, RedirectAttributes ra) {
        if (foto.isEmpty()) {
            ra.addFlashAttribute("errorFoto", "Selecciona una imagen.");
            return "redirect:/perfil";
        }
        String contentType = foto.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            ra.addFlashAttribute("errorFoto", "Solo se permiten imágenes (JPG, PNG, GIF).");
            return "redirect:/perfil";
        }
        if (foto.getSize() > 5 * 1024 * 1024) {
            ra.addFlashAttribute("errorFoto", "La imagen no puede superar 5MB.");
            return "redirect:/perfil";
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String extension = foto.getOriginalFilename() != null
                ? foto.getOriginalFilename().substring(foto.getOriginalFilename().lastIndexOf("."))
                : ".jpg";
            String filename = auth.getName() + "_" + System.currentTimeMillis() + extension;
            Path filePath = uploadPath.resolve(filename);
            Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Usuario u = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
            u.setFotoUrl("/uploads/fotos/" + filename);
            usuarioRepository.save(u);
            ra.addFlashAttribute("exitoFoto", "Foto actualizada correctamente.");
        } catch (IOException e) {
            ra.addFlashAttribute("errorFoto", "Error al subir la imagen: " + e.getMessage());
        }
        return "redirect:/perfil";
    }

    @GetMapping("/{username}")
    public String perfilPublico(@PathVariable String username, Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        model.addAttribute("usuario", usuario);
        model.addAttribute("servicios", servicioRepository.findByUsuarioId(usuario.getId()));
        model.addAttribute("esPropio", auth != null && auth.getName().equals(username));
        if (auth != null) model.addAttribute("mensajesNoLeidos", mensajeService.countUnread(auth.getName()));
        return "perfil/perfil-publico";
    }
}
