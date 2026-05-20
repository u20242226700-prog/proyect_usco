package com.uscoproyecto.hibrido.repository.postgresql;

import com.uscoproyecto.hibrido.model.postgresql.Intercambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntercambioRepository extends JpaRepository<Intercambio, Long> {
    List<Intercambio> findBySolicitanteId(Long solicitanteId);
    List<Intercambio> findByEstado(String estado);
}
