
--  FRUIT CATCHER - Script SQL

-- 1. Crear la base de datos
CREATE DATABASE IF NOT EXISTS fruit_catcher_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fruit_catcher_db;


-- 2. Tabla Usuarios

CREATE TABLE IF NOT EXISTS Usuarios (
    id_usuario      INT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(50)  NOT NULL,
    fecha_registro  DATETIME     NOT NULL,

    PRIMARY KEY (id_usuario)
);


-- 3. Tabla Partidas

CREATE TABLE IF NOT EXISTS Partidas (
    id_partida  INT      NOT NULL AUTO_INCREMENT,
    id_usuario  INT      NOT NULL,
    puntuacion  INT      NOT NULL DEFAULT 0,
    fecha       DATETIME NOT NULL,
    duracion    INT      NOT NULL DEFAULT 0,  

    PRIMARY KEY (id_partida),

    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


-- 4. Vista: Top 10 puntuaciones (opcional, muy útil para consultar)

CREATE OR REPLACE VIEW v_ranking AS
    SELECT
        u.nombre        AS jugador,
        p.puntuacion    AS puntos,
        p.duracion      AS tiempo_seg,
        p.fecha         AS fecha_partida
    FROM Partidas p
    JOIN Usuarios u ON p.id_usuario = u.id_usuario
    ORDER BY p.puntuacion DESC
    LIMIT 10;



-- 5. Consulta de comprobación: ver el ranking

SELECT * FROM v_ranking;
