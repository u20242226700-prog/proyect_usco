package com.uscoproyecto.hibrido.controller;

import com.uscoproyecto.hibrido.model.postgresql.Usuario;
import com.uscoproyecto.hibrido.repository.postgresql.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroPage() {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String nombreCompleto,
                           @RequestParam(required = false) String telefono,
                           @RequestParam(required = false) String facultad,
                           @RequestParam(required = false) String programa,
                           Model model, RedirectAttributes ra) {

        // Validaciones
        if (usuarioRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "❌ El usuario '" + username + "' ya existe.");
            return "auth/registro";
        }
        if (usuarioRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "❌ El email ya está registrado.");
            return "auth/registro";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "❌ La contraseña debe tener al menos 6 caracteres.");
            return "auth/registro";
        }

        Usuario u = new Usuario();
        u.setUsername(username.trim().toLowerCase());
        u.setEmail(email.trim().toLowerCase());
        u.setPassword(passwordEncoder.encode(password));
        u.setNombreCompleto(nombreCompleto.trim());
        u.setTelefono(telefono);
        u.setFacultad(facultad);
        u.setPrograma(programa);
        u.setActivo(true);

        Set<String> roles = new HashSet<>();
        roles.add("USER");
        u.setRoles(roles);

        usuarioRepository.save(u);

        ra.addFlashAttribute("exito", "✅ Cuenta creada. ¡Ahora inicia sesión!");
        return "redirect:/login";
    }
}
