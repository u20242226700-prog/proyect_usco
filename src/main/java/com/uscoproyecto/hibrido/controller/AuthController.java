package com.uscoproyecto.hibrido.controller;

import com.uscoproyecto.hibrido.dto.JwtResponseDTO;
import com.uscoproyecto.hibrido.dto.LoginRequestDTO;
import com.uscoproyecto.hibrido.dto.RegistroRequestDTO;
import com.uscoproyecto.hibrido.model.postgresql.Usuario;
import com.uscoproyecto.hibrido.repository.postgresql.UsuarioRepository;
import com.uscoproyecto.hibrido.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponseDTO(token, userDetails.getUsername()));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequestDTO request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Error: El username ya existe.");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setTelefono(request.getTelefono());
        usuario.setFacultad(request.getFacultad());
        usuario.setPrograma(request.getPrograma());
        usuario.setActivo(true);

        Set<String> roles = new HashSet<>();
        roles.add("USER");
        usuario.setRoles(roles);

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }
}
