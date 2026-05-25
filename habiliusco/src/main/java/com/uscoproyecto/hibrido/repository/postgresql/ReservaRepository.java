package com.uscoproyecto.hibrido.repository.postgresql;

import com.uscoproyecto.hibrido.model.postgresql.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findBySolicitanteId(Long solicitanteId);
    List<Reserva> findByServicioId(Long servicioId);
    List<Reserva> findByServicioUsuarioId(Long proveedorId);

    @Query("SELECT r FROM Reserva r WHERE r.servicio.id = :servicioId AND r.fechaReserva = :fecha AND r.estado NOT IN ('RECHAZADA','CANCELADA')")
    List<Reserva> findFechaOcupada(@Param("servicioId") Long servicioId, @Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(r) FROM Reserva r")
    Long countAll();

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.estado = :estado")
    Long countByEstado(@Param("estado") String estado);
}
