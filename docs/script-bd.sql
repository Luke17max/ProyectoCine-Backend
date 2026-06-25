-- =========================================================
-- CINE-MS - SCRIPT DE BASES DE DATOS
-- Sistema de microservicios para gestión de cine
-- MySQL / XAMPP
-- =========================================================

-- =========================================================
-- CREACIÓN DE BASES DE DATOS
-- =========================================================

CREATE DATABASE IF NOT EXISTS db_usuarios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_peliculas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_sucursales
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_salas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_funciones
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_reservas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_pagos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_confiteria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_pago_confiteria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_notificaciones
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;


-- =========================================================
-- DB_USUARIOS  (ms-usuarios)
-- =========================================================

USE db_usuarios;

CREATE TABLE IF NOT EXISTS usuarios (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    nombre   VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol      VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO usuarios (id, nombre, email, password, rol)
VALUES (1, 'Admin Principal', 'admin@cine.com', 'admin123', 'ADMIN');

INSERT IGNORE INTO usuarios (id, nombre, email, password, rol)
VALUES (2, 'Cliente Prueba', 'cliente@correo.com', 'user123', 'CLIENTE');


-- =========================================================
-- DB_PELICULAS  (ms-peliculas)
-- =========================================================

USE db_peliculas;

CREATE TABLE IF NOT EXISTS peliculas (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    titulo        VARCHAR(150) NOT NULL,
    genero        VARCHAR(50)  NOT NULL,
    duracion      INT          NOT NULL,
    clasificacion VARCHAR(10)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO peliculas (id, titulo, genero, duracion, clasificacion)
VALUES (1, 'Inception', 'Sci-Fi', 148, 'TE');

INSERT IGNORE INTO peliculas (id, titulo, genero, duracion, clasificacion)
VALUES (2, 'The Joker', 'Drama', 122, 'R');

INSERT IGNORE INTO peliculas (id, titulo, genero, duracion, clasificacion)
VALUES (3, 'Toy Story 4', 'Animacion', 100, 'TE');


-- =========================================================
-- DB_SUCURSALES  (ms-sucursales)
-- =========================================================

USE db_sucursales;

CREATE TABLE IF NOT EXISTS sucursales (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(150) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    ciudad    VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO sucursales (id, nombre, direccion, ciudad)
VALUES (1, 'Cine Center Mall', 'Av. Central 450', 'Santiago');

INSERT IGNORE INTO sucursales (id, nombre, direccion, ciudad)
VALUES (2, 'Cine Plaza Norte', 'Ruta 5 Norte KM 10', 'Antofagasta');

INSERT IGNORE INTO sucursales (id, nombre, direccion, ciudad)
VALUES (3, 'Cine Sur', 'Av. Los Pinos 789', 'Concepcion');


-- =========================================================
-- DB_SALAS  (ms-salas)
-- =========================================================

USE db_salas;

CREATE TABLE IF NOT EXISTS salas (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    capacidad   INT          NOT NULL,
    sucursal_id BIGINT       NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO salas (id, nombre, capacidad, sucursal_id)
VALUES (1, 'Sala 1 - IMAX', 200, 1);

INSERT IGNORE INTO salas (id, nombre, capacidad, sucursal_id)
VALUES (2, 'Sala 2 - 3D', 120, 1);


-- =========================================================
-- DB_FUNCIONES  (ms-funciones)
-- =========================================================

USE db_funciones;

CREATE TABLE IF NOT EXISTS funciones (
    id          BIGINT         NOT NULL AUTO_INCREMENT,
    fecha_hora  DATETIME       NOT NULL,
    precio_base DECIMAL(10, 2) NOT NULL,
    pelicula_id BIGINT         NOT NULL,
    sala_id     BIGINT         NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO funciones (id, fecha_hora, precio_base, pelicula_id, sala_id)
VALUES (1, '2027-12-01 18:30:00', 5500.00, 1, 1);

INSERT IGNORE INTO funciones (id, fecha_hora, precio_base, pelicula_id, sala_id)
VALUES (2, '2027-12-01 21:00:00', 6800.00, 2, 2);


-- =========================================================
-- DB_RESERVAS  (ms-reservas)
-- =========================================================

USE db_reservas;

CREATE TABLE IF NOT EXISTS reservas (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    usuario_id        BIGINT      NOT NULL,
    funcion_id        BIGINT      NOT NULL,
    cantidad_asientos INT         NOT NULL,
    estado            VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO reservas (id, usuario_id, funcion_id, cantidad_asientos, estado)
VALUES (1, 1, 1, 2, 'PENDIENTE');

INSERT IGNORE INTO reservas (id, usuario_id, funcion_id, cantidad_asientos, estado)
VALUES (2, 1, 1, 4, 'PAGADA');


-- =========================================================
-- DB_PAGOS  (ms-pago)
-- =========================================================

USE db_pagos;

CREATE TABLE IF NOT EXISTS pagos (
    id          BIGINT         NOT NULL AUTO_INCREMENT,
    reserva_id  BIGINT         NOT NULL UNIQUE,
    monto_total DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(50)    NOT NULL,
    estado      VARCHAR(30)    NOT NULL,
    fecha_pago  DATETIME       NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO pagos (id, reserva_id, monto_total, metodo_pago, estado, fecha_pago)
VALUES (1, 1, 15000.00, 'TARJETA_CREDITO', 'COMPLETADO', NOW());

INSERT IGNORE INTO pagos (id, reserva_id, monto_total, metodo_pago, estado, fecha_pago)
VALUES (2, 2, 7500.00, 'EFECTIVO', 'PENDIENTE', NOW());


-- =========================================================
-- DB_CONFITERIA  (ms-confiteria)
-- =========================================================

USE db_confiteria;

CREATE TABLE IF NOT EXISTS confiteria (
    id        BIGINT         NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(100)   NOT NULL,
    precio    DECIMAL(10, 2) NOT NULL,
    stock     INT            NOT NULL,
    categoria VARCHAR(50)    NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO confiteria (id, nombre, precio, stock, categoria)
VALUES (1, 'Palomitas Grandes', 2500.00, 100, 'Comida');

INSERT IGNORE INTO confiteria (id, nombre, precio, stock, categoria)
VALUES (2, 'Bebida Mediana', 5900.00, 200, 'Bebida');

INSERT IGNORE INTO confiteria (id, nombre, precio, stock, categoria)
VALUES (3, 'Combo Pareja (2 Bebidas + 1 Palomita)', 8500.00, 50, 'Promocion');

INSERT IGNORE INTO confiteria (id, nombre, precio, stock, categoria)
VALUES (4, 'Chocolate Barra', 2200.00, 300, 'Dulce');


-- =========================================================
-- DB_PAGO_CONFITERIA  (ms-pago_confiteria)
-- =========================================================

USE db_pago_confiteria;

CREATE TABLE IF NOT EXISTS pagos_confiteria (
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    usuario_id   BIGINT         NOT NULL,
    producto_id  BIGINT         NOT NULL,
    cantidad     INT            NOT NULL,
    total_pagado DECIMAL(10, 2) NOT NULL,
    fecha_compra DATETIME       NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO pagos_confiteria (id, usuario_id, producto_id, cantidad, total_pagado, fecha_compra)
VALUES (1, 1, 3, 2, 8500.00, NOW());

INSERT IGNORE INTO pagos_confiteria (id, usuario_id, producto_id, cantidad, total_pagado, fecha_compra)
VALUES (2, 2, 1, 1, 2500.00, NOW());


-- =========================================================
-- DB_NOTIFICACIONES  (ms-notificaciones)
-- =========================================================

USE db_notificaciones;

CREATE TABLE IF NOT EXISTS notificaciones (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    reserva_id     BIGINT       NOT NULL,
    pago_id        BIGINT       NULL,
    tipo           VARCHAR(50)  NOT NULL,
    mensaje        VARCHAR(500) NOT NULL,
    fecha_creacion DATETIME     NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT IGNORE INTO notificaciones (id, reserva_id, pago_id, tipo, mensaje, fecha_creacion)
VALUES (1, 1, 1, 'CONFIRMACION_PAGO', 'Su pago ha sido procesado exitosamente. Disfrute la funcion.', NOW());

INSERT IGNORE INTO notificaciones (id, reserva_id, pago_id, tipo, mensaje, fecha_creacion)
VALUES (2, 2, NULL, 'RECORDATORIO_RESERVA', 'Recuerde que tiene una reserva pendiente de pago.', NOW());


-- =========================================================
-- FIN DEL SCRIPT
-- =========================================================
