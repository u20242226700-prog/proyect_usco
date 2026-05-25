-- ============================================================
--  HabiliUSCO - V3: Nuevas tablas (Reservas, Mensajes)
--  Ejecutar en: pgAdmin 4 → usco_hibrido_pg
-- ============================================================

-- Agregar columnas a servicios (si no existen)
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS precio_base NUMERIC(10,2);
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS imagen_url VARCHAR(255);
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- ============================================================
-- TABLA: reservas
-- ============================================================
CREATE TABLE IF NOT EXISTS reservas (
    id                  BIGSERIAL       PRIMARY KEY,
    solicitante_id      BIGINT          NOT NULL REFERENCES usuarios(id),
    servicio_id         BIGINT          NOT NULL REFERENCES servicios(id),
    fecha_reserva       DATE            NOT NULL,
    precio_propuesto    NUMERIC(10,2),
    precio_acordado     NUMERIC(10,2),
    estado              VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE'
                            CHECK (estado IN ('PENDIENTE','NEGOCIANDO','ACEPTADA','RECHAZADA','FINALIZADA','CANCELADA')),
    mensaje             VARCHAR(500),
    fecha_solicitud     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLA: mensajes
-- ============================================================
CREATE TABLE IF NOT EXISTS mensajes (
    id          BIGSERIAL   PRIMARY KEY,
    emisor_id   BIGINT      NOT NULL REFERENCES usuarios(id),
    receptor_id BIGINT      NOT NULL REFERENCES usuarios(id),
    reserva_id  BIGINT      REFERENCES reservas(id) ON DELETE CASCADE,
    contenido   VARCHAR(1000) NOT NULL,
    tipo        VARCHAR(30) DEFAULT 'TEXTO',
    leido       BOOLEAN     DEFAULT FALSE,
    fecha_envio TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_reservas_solicitante ON reservas(solicitante_id);
CREATE INDEX IF NOT EXISTS idx_reservas_servicio    ON reservas(servicio_id);
CREATE INDEX IF NOT EXISTS idx_reservas_fecha       ON reservas(fecha_reserva);
CREATE INDEX IF NOT EXISTS idx_mensajes_reserva     ON mensajes(reserva_id);
CREATE INDEX IF NOT EXISTS idx_mensajes_receptor    ON mensajes(receptor_id);

-- Actualizar precio en servicios de prueba
UPDATE servicios SET precio_base = 40000 WHERE titulo = 'Monitoría de Cálculo I';
UPDATE servicios SET precio_base = 70000 WHERE titulo = 'Reparación de computadores';

SELECT 'reservas'  AS tabla, COUNT(*) FROM reservas
UNION ALL
SELECT 'mensajes', COUNT(*) FROM mensajes;
