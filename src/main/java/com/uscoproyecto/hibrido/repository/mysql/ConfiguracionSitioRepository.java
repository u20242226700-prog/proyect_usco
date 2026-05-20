package com.uscoproyecto.hibrido.repository.mysql;

import com.uscoproyecto.hibrido.model.mysql.ConfiguracionSitio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionSitioRepository extends JpaRepository<ConfiguracionSitio, Long> {
}
