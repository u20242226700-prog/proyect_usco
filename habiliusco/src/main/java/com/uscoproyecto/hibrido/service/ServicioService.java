package com.uscoproyecto.hibrido.service;

import com.uscoproyecto.hibrido.model.postgresql.Servicio;
import com.uscoproyecto.hibrido.model.postgresql.Usuario;
import com.uscoproyecto.hibrido.repository.postgresql.ServicioRepository;
import com.uscoproyecto.hibrido.repository.postgresql.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Servicio> listarActivos() {
        return servicioRepository.findByEstado("ACTIVO");
    }

    public List<Servicio> listarTodos() {
        return servicioRepository.findAll();
    }

    public List<Servicio> buscar(String query) {
        if (query == null || query.isBlank()) return listarActivos();
        return servicioRepository.buscar(query);
    }

    public List<Servicio> porCategoria(String categoria) {
        return servicioRepository.findByCategoriaAndEstado(categoria, "ACTIVO");
    }

    public Optional<Servicio> findById(Long id) {
        return servicioRepository.findById(id);
    }

    public Servicio crear(Servicio servicio, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        servicio.setUsuario(usuario);
        servicio.setEstado("ACTIVO");
        return servicioRepository.save(servicio);
    }

    public Servicio actualizar(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    public void eliminar(Long id) {
        servicioRepository.deleteById(id);
    }

    public List<Servicio> misServicios(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        return servicioRepository.findByUsuarioId(usuario.getId());
    }

    public List<String> getCategorias() {
        return servicioRepository.findCategorias();
    }
}
