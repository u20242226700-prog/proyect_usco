package com.uscoproyecto.hibrido.repository.postgresql;

import com.uscoproyecto.hibrido.model.postgresql.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByEstado(String estado);
    List<Servicio> findByUsuarioId(Long usuarioId);
    List<Servicio> findByCategoriaAndEstado(String categoria, String estado);

    @Query("SELECT s FROM Servicio s WHERE s.estado = 'ACTIVO' AND (LOWER(s.titulo) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(s.descripcion) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(s.categoria) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Servicio> buscar(@Param("q") String query);

    @Query("SELECT DISTINCT s.categoria FROM Servicio s WHERE s.estado = 'ACTIVO'")
    List<String> findCategorias();

    @Query("SELECT COUNT(s) FROM Servicio s")
    Long countAll();

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.estado = 'ACTIVO'")
    Long countActivos();
}
