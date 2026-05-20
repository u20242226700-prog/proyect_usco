package com.uscoproyecto.hibrido.service;

import com.uscoproyecto.hibrido.model.postgresql.Usuario;
import com.uscoproyecto.hibrido.repository.postgresql.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        var authorities = usuario.getRoles().stream()
            .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
            .collect(Collectors.toList());

        return new User(usuario.getUsername(), usuario.getPassword(), usuario.getActivo(),
            true, true, true, authorities);
    }
}
