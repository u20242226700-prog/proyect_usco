-- HabiliUSCO V5: Reseñas y Fechas de Bloqueo
-- Ejecutar en pgAdmin → usco_hibrido_pg

CREATE TABLE IF NOT EXISTS resenas (
    id          BIGSERIAL PRIMARY KEY,
    reserva_id  BIGINT NOT NULL UNIQUE REFERENCES reservas(id) ON DELETE CASCADE,
    autor_id    BIGINT NOT NULL REFERENCES usuarios(id),
    receptor_id BIGINT NOT NULL REFERENCES usuarios(id),
    calificacion INTEGER NOT NULL CHECK (calificacion BETWEEN 1 AND 5),
    comentario  VARCHAR(500),
    fecha_resena TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fechas_bloqueo (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha_bloqueada DATE NOT NULL,
    motivo          VARCHAR(100),
    UNIQUE (usuario_id, fecha_bloqueada)
);

CREATE INDEX IF NOT EXISTS idx_resenas_receptor ON resenas(receptor_id);
CREATE INDEX IF NOT EXISTS idx_bloqueos_usuario ON fechas_bloqueo(usuario_id);

SELECT 'resenas' AS tabla, COUNT(*) FROM resenas
UNION ALL SELECT 'fechas_bloqueo', COUNT(*) FROM fechas_bloqueo;
