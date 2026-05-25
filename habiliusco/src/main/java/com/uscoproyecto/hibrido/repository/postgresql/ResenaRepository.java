package com.uscoproyecto.hibrido.repository.postgresql;

import com.uscoproyecto.hibrido.model.postgresql.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByReceptorId(Long receptorId);
    Optional<Resena> findByReservaId(Long reservaId);
    boolean existsByReservaId(Long reservaId);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.receptor.id = :userId")
    Double promedioCalificacion(@Param("userId") Long userId);
}
