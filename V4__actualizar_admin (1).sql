-- ============================================================
-- HabiliUSCO - V4: Actualizar admin y agregar campos nuevos
-- Ejecutar en: pgAdmin 4 → usco_hibrido_pg
-- ============================================================

-- Agregar columnas nuevas a usuarios
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS bio VARCHAR(500);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS foto_url VARCHAR(255);

-- Actualizar admin: usuario=admin, clave=123123 (BCrypt)
UPDATE usuarios SET
    password = '$2a$10$a6oBr00q.u9zcBebLSKiKenbsG0ONwUOpgMfp4Ctb3LGMayVF3Zaq',
    nombre_completo = 'Administrador USCO',
    email = 'admin@usco.edu.co'
WHERE username = 'admin';

-- Asegurar que admin tenga rol ADMIN
INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ADMIN' FROM usuarios WHERE username = 'admin'
ON CONFLICT DO NOTHING;

-- Eliminar rol USER del admin si lo tiene
DELETE FROM usuario_roles
WHERE usuario_id = (SELECT id FROM usuarios WHERE username = 'admin')
AND rol = 'USER';

-- Si no existe el admin, crearlo
INSERT INTO usuarios (username, email, password, nombre_completo, facultad, programa, activo)
VALUES (
    'admin',
    'admin@usco.edu.co',
    '$2a$10$a6oBr00q.u9zcBebLSKiKenbsG0ONwUOpgMfp4Ctb3LGMayVF3Zaq',
    'Administrador USCO',
    'Ingeniería',
    'Ingeniería de Software',
    TRUE
) ON CONFLICT DO NOTHING;

-- Verificar
SELECT u.username, u.nombre_completo, u.email, ur.rol
FROM usuarios u
JOIN usuario_roles ur ON u.id = ur.usuario_id
WHERE u.username = 'admin';
