-- ─────────────────────────────────────────────────────────────
-- GameVault — Schema PostgreSQL
-- Cloud SQL (us-central1) | game-list-cloud:us-central1:gamelist-db
-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS categoria (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS plataforma (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    fabricante  VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS usuario_app (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(30) NOT NULL UNIQUE,
    display_name  VARCHAR(80),
    password_hash VARCHAR(260) NOT NULL,
    role          VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','USER')),
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS videojuego (
    id           SERIAL PRIMARY KEY,
    titulo       VARCHAR(255) NOT NULL,
    anio         INTEGER,
    descripcion  TEXT,
    imagen_url   TEXT,
    estado       VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE','JUGANDO','TERMINADO','FAVORITO')),
    usuario_id   INTEGER REFERENCES usuario_app(id),
    categoria_id INTEGER REFERENCES categoria(id),
    plataforma_id INTEGER REFERENCES plataforma(id)
);

-- Para bases de datos ya existentes (los datos previos quedan sin dueño y dejan de
-- mostrarse a los usuarios; cada usuario verá solo lo que cree a partir de ahora):
--   ALTER TABLE videojuego ADD COLUMN IF NOT EXISTS usuario_id INTEGER REFERENCES usuario_app(id);
--   ALTER TABLE wishlist   ADD COLUMN IF NOT EXISTS usuario_id INTEGER REFERENCES usuario_app(id);
-- Opcional, para asignar los datos antiguos a un usuario concreto:
--   UPDATE videojuego SET usuario_id = (SELECT id FROM usuario_app WHERE username = 'admin') WHERE usuario_id IS NULL;
--   UPDATE wishlist   SET usuario_id = (SELECT id FROM usuario_app WHERE username = 'admin') WHERE usuario_id IS NULL;
CREATE INDEX IF NOT EXISTS idx_videojuego_usuario ON videojuego(usuario_id);

CREATE TABLE IF NOT EXISTS resena (
    id             SERIAL PRIMARY KEY,
    comentario     TEXT NOT NULL,
    autor          VARCHAR(100) NOT NULL,
    puntuacion     INTEGER NOT NULL CHECK (puntuacion BETWEEN 1 AND 10),
    videojuego_id  INTEGER NOT NULL REFERENCES videojuego(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS wishlist (
    id            SERIAL PRIMARY KEY,
    titulo        VARCHAR(255) NOT NULL,
    prioridad     VARCHAR(10) NOT NULL CHECK (prioridad IN ('ALTA','MEDIA','BAJA')),
    notas         TEXT,
    usuario_id    INTEGER REFERENCES usuario_app(id),
    plataforma_id INTEGER REFERENCES plataforma(id),
    categoria_id  INTEGER REFERENCES categoria(id)
);

CREATE INDEX IF NOT EXISTS idx_wishlist_usuario ON wishlist(usuario_id);
