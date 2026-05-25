package com.uscoproyecto.hibrido.repository.postgresql;

import com.uscoproyecto.hibrido.model.postgresql.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByReservaIdOrderByFechaEnvioAsc(Long reservaId);

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.receptor.id = :userId AND m.leido = false")
    Long countUnreadByUser(@Param("userId") Long userId);

    @Query("SELECT m FROM Mensaje m WHERE (m.emisor.id = :u1 AND m.receptor.id = :u2) OR (m.emisor.id = :u2 AND m.receptor.id = :u1) ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversacion(@Param("u1") Long u1, @Param("u2") Long u2);
}
