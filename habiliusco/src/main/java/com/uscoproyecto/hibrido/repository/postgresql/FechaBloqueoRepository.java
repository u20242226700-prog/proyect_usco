package com.uscoproyecto.hibrido.repository.postgresql;

import com.uscoproyecto.hibrido.model.postgresql.FechaBloqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FechaBloqueoRepository extends JpaRepository<FechaBloqueo, Long> {
    List<FechaBloqueo> findByUsuarioId(Long usuarioId);
    void deleteByUsuarioIdAndFechaBloqueada(Long usuarioId, LocalDate fecha);
}
