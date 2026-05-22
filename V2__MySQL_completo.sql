-- ============================================================
--  HabiliUSCO - Script MySQL COMPLETO
--  Importar en: MySQL Workbench → Query Editor
--             o phpMyAdmin → pestaña SQL
--  Base de datos: usco_hibrido_mysql
-- ============================================================

CREATE DATABASE IF NOT EXISTS usco_hibrido_mysql
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE usco_hibrido_mysql;

-- ============================================================
-- TABLA: auditoria_logs
-- ============================================================
CREATE TABLE IF NOT EXISTS auditoria_logs (
    id                  BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    accion              VARCHAR(50)     NOT NULL,
    entidad             VARCHAR(50)     NULL,
    entidad_id          BIGINT          NULL,
    usuario_username    VARCHAR(50)     NULL,
    ip_address          VARCHAR(45)     NULL,
    detalle             VARCHAR(1000)   NULL,
    nivel               VARCHAR(10)     NOT NULL DEFAULT 'INFO',
    fecha_evento        DATETIME        DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_auditoria_usuario  (usuario_username),
    INDEX idx_auditoria_accion   (accion),
    INDEX idx_auditoria_nivel    (nivel),
    INDEX idx_auditoria_fecha    (fecha_evento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: configuraciones_sitio
-- ============================================================
CREATE TABLE IF NOT EXISTS configuraciones_sitio (
    id                  BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    clave               VARCHAR(100)    NOT NULL UNIQUE,
    valor               VARCHAR(500)    NOT NULL,
    descripcion         VARCHAR(200)    NULL,
    fecha_actualizacion DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_config_clave (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- DATOS: Configuraciones del sistema
-- ============================================================
INSERT INTO configuraciones_sitio (clave, valor, descripcion) VALUES
('idioma_default',          'es',               'Idioma por defecto'),
('max_servicios_usuario',   '10',               'Máximo de servicios por usuario'),
('jwt_expiracion_horas',    '24',               'Horas de validez del token JWT'),
('version_plataforma',      '1.0.0',            'Versión de HabiliUSCO'),
('correo_soporte',          'soporte@usco.edu.co', 'Correo de soporte'),
('mantenimiento_activo',    'false',            'Modo mantenimiento'),
('registros_por_pagina',    '10',               'Registros por página'),
('categorias_activas',      'MONITORIA,REPARACION,TRADUCCION,DISEÑO,PROGRAMACION,TUTORIA,OTRO', 'Categorías habilitadas')
ON DUPLICATE KEY UPDATE valor = VALUES(valor);

-- ============================================================
-- DATOS: Logs de auditoría iniciales
-- ============================================================
INSERT INTO auditoria_logs (accion, entidad, entidad_id, usuario_username, ip_address, detalle, nivel) VALUES
('LOGIN',           'Usuario',   1, 'admin',  '127.0.0.1', 'Login exitoso del administrador',        'INFO'),
('LOGIN',           'Usuario',   2, 'daniel', '127.0.0.1', 'Login exitoso',                          'INFO'),
('CREAR_SERVICIO',  'Servicio',  1, 'daniel', '127.0.0.1', 'Creó servicio: Monitoría de Cálculo I',  'INFO'),
('CREAR_SERVICIO',  'Servicio',  2, 'daniel', '127.0.0.1', 'Creó servicio: Reparación de computadores', 'INFO'),
('REGISTRO',        'Usuario',   2, 'daniel', '127.0.0.1', 'Nuevo usuario registrado',               'INFO');

-- ============================================================
-- VERIFICAR
-- ============================================================
SELECT 'auditoria_logs'      AS tabla, COUNT(*) AS registros FROM auditoria_logs
UNION ALL
SELECT 'configuraciones_sitio',         COUNT(*)             FROM configuraciones_sitio;
