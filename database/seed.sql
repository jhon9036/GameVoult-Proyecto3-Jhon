-- ─────────────────────────────────────────────────────────────
-- GameVault — Datos de prueba
-- ─────────────────────────────────────────────────────────────

INSERT INTO categoria (nombre) VALUES
('Acción'),
('Aventura'),
('RPG'),
('Deportes'),
('Estrategia'),
('Terror')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO plataforma (nombre, fabricante) VALUES
('PC',       'Varios'),
('PS5',      'Sony'),
('Xbox',     'Microsoft'),
('Nintendo Switch', 'Nintendo'),
('PS4',      'Sony')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO videojuego (titulo, anio, descripcion, imagen_url, estado, categoria_id, plataforma_id) VALUES
('The Witcher 3: Wild Hunt', 2015,
 'RPG de mundo abierto ambientado en un universo de fantasía oscura.',
 'https://upload.wikimedia.org/wikipedia/en/0/0c/Witcher_3_cover_art.jpg',
 'TERMINADO', 3, 1),

('FIFA 23', 2023,
 'Simulador de fútbol con licencias oficiales de la FIFA.',
 NULL,
 'JUGANDO', 4, 2),

('Halo Infinite', 2021,
 'FPS de ciencia ficción protagonizado por el Jefe Maestro.',
 NULL,
 'PENDIENTE', 1, 3),

('The Legend of Zelda: Breath of the Wild', 2017,
 'Aventura de mundo abierto en el reino de Hyrule.',
 NULL,
 'FAVORITO', 2, 4),

('Resident Evil Village', 2021,
 'Horror de supervivencia en primera persona ambientado en un misterioso pueblo europeo.',
 NULL,
 'TERMINADO', 6, 2);

INSERT INTO resena (comentario, autor, puntuacion, videojuego_id) VALUES
('Una obra maestra absoluta, la mejor historia que he jugado.', 'Brandon', 10, 1),
('Muy entretenido pero le falta profundidad táctica.', 'Brandon', 7, 2),
('Narrativa épica, gráficos increíbles.', 'Brandon', 9, 4);

INSERT INTO wishlist (titulo, prioridad, notas, plataforma_id, categoria_id) VALUES
('Elden Ring',        'ALTA',  'El GOTY que tengo pendiente.', 1, 3),
('God of War Ragnarök', 'MEDIA', 'Cuando baje de precio.', 2, 2),
('Starfield',         'BAJA',  'Esperar reviews definitivas.', 1, NULL);

-- Los datos de ejemplo se asignan al usuario admin (creado por la app al arrancar).
-- Sin dueño, no se mostrarían a ningún usuario porque la biblioteca es por-usuario.
-- Ejecuta este seed DESPUÉS de que la aplicación haya arrancado al menos una vez.
UPDATE videojuego SET usuario_id = (SELECT id FROM usuario_app WHERE username = 'admin')
  WHERE usuario_id IS NULL;
UPDATE wishlist   SET usuario_id = (SELECT id FROM usuario_app WHERE username = 'admin')
  WHERE usuario_id IS NULL;
