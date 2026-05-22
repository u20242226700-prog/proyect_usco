-- ============================================================
--  HabiliUSCO - Script PostgreSQL
--  Importar en: pgAdmin 4 → Query Tool
--  Base de datos: usco_hibrido_pg
--  Rol: usuarios, servicios, intercambios (datos principales)
-- ============================================================

-- 1. Crear la base de datos (ejecutar como superusuario)
-- CREATE DATABASE usco_hibrido_pg
--     WITH OWNER = postgres
--          ENCODING = 'UTF8'
--          LC_COLLATE = 'es_CO.UTF-8'
--          LC_CTYPE = 'es_CO.UTF-8'
--          TEMPLATE = template0;

-- Conectarse a la base de datos antes de continuar:
-- \c usco_hibrido_pg

-- ============================================================
-- TABLA: usuarios
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id                  BIGSERIAL       PRIMARY KEY,
    username            VARCHAR(50)     NOT NULL UNIQUE,
    email               VARCHAR(100)    NOT NULL UNIQUE,
    password            VARCHAR(255)    NOT NULL,
    nombre_completo     VARCHAR(100)    NOT NULL,
    telefono            VARCHAR(20),
    facultad            VARCHAR(100),
    programa            VARCHAR(100),
    activo              BOOLEAN         NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLA: usuario_roles (roles del usuario, ej: USER, ADMIN)
-- ============================================================
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id  BIGINT      NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol         VARCHAR(30) NOT NULL,
    PRIMARY KEY (usuario_id, rol)
);

-- ============================================================
-- TABLA: servicios
-- ============================================================
CREATE TABLE IF NOT EXISTS servicios (
    id                  BIGSERIAL       PRIMARY KEY,
    titulo              VARCHAR(100)    NOT NULL,
    descripcion         VARCHAR(1000)   NOT NULL,
    categoria           VARCHAR(50)     NOT NULL,
    estado              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVO'
                            CHECK (estado IN ('ACTIVO', 'PAUSADO', 'INACTIVO')),
    usuario_id          BIGINT          NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha_publicacion   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLA: intercambios
-- ============================================================
CREATE TABLE IF NOT EXISTS intercambios (
    id                  BIGSERIAL       PRIMARY KEY,
    solicitante_id      BIGINT          NOT NULL REFERENCES usuarios(id),
    servicio_id         BIGINT          NOT NULL REFERENCES servicios(id),
    estado              VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE'
                            CHECK (estado IN ('PENDIENTE', 'ACEPTADO', 'RECHAZADO', 'FINALIZADO')),
    mensaje             VARCHAR(500),
    fecha_solicitud     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    fecha_finalizacion  TIMESTAMP
);

-- ============================================================
-- ÍNDICES para mejorar el rendimiento
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_servicios_estado    ON servicios(estado);
CREATE INDEX IF NOT EXISTS idx_servicios_categoria ON servicios(categoria);
CREATE INDEX IF NOT EXISTS idx_servicios_usuario   ON servicios(usuario_id);
CREATE INDEX IF NOT EXISTS idx_intercambios_estado ON intercambios(estado);
CREATE INDEX IF NOT EXISTS idx_intercambios_solicitante ON intercambios(solicitante_id);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- Insertar usuario admin (password: admin123 → BCrypt)
INSERT INTO usuarios (username, email, password, nombre_completo, facultad, programa, activo)
VALUES (
    'admin',
    'admin@usco.edu.co',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6yMiK',
    'Administrador USCO',
    'Ingeniería de Software',
    'Ingeniería de Software',
    TRUE
) ON CONFLICT DO NOTHING;

-- Insertar usuario de prueba (password: 123456 → BCrypt)
INSERT INTO usuarios (username, email, password, nombre_completo, facultad, programa, activo)
VALUES (
    'daniel',
    'daniel@usco.edu.co',
    '$2a$10$slYQmyNdgTY18kkkkEJaBOPp/nWMJFdpkrfIFbDOgkiT2KHtYbkKW',
    'Daniel Santiago Jimenez Suarez',
    'Ingeniería de Software',
    'Ingeniería de Software',
    TRUE
) ON CONFLICT DO NOTHING;

-- Roles
INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ADMIN' FROM usuarios WHERE username = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'USER' FROM usuarios WHERE username = 'daniel'
ON CONFLICT DO NOTHING;

-- Servicios de ejemplo
INSERT INTO servicios (titulo, descripcion, categoria, estado, usuario_id)
SELECT
    'Monitoría de Cálculo I',
    'Ofrezco tutorías de Cálculo Diferencial e Integral para primer semestre. Clases personalizadas con ejercicios prácticos.',
    'MONITORIA',
    'ACTIVO',
    u.id
FROM usuarios u WHERE u.username = 'daniel'
ON CONFLICT DO NOTHING;

INSERT INTO servicios (titulo, descripcion, categoria, estado, usuario_id)
SELECT
    'Reparación de computadores',
    'Instalación de sistemas operativos, limpieza de virus, optimización y reparación de hardware y software.',
    'REPARACION',
    'ACTIVO',
    u.id
FROM usuarios u WHERE u.username = 'daniel'
ON CONFLICT DO NOTHING;

-- ============================================================
-- VERIFICAR
-- ============================================================
SELECT 'usuarios' AS tabla, COUNT(*) FROM usuarios
UNION ALL
SELECT 'servicios', COUNT(*) FROM servicios
UNION ALL
SELECT 'intercambios', COUNT(*) FROM intercambios;
