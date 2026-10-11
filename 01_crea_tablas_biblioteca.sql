-- =====================================================================
--  Sistema de Gestión de Biblioteca Escolar
--  Script 1/2: creación de la base de datos y sus tablas (MySQL 8)
--  Ejecutar en MySQL Workbench ANTES del script de poblado.
-- =====================================================================
--  Herencia Persona -> Estudiante / Bibliotecario (estrategia "una tabla por clase"):
--    personas        datos comunes (la superclase)
--    estudiantes     datos propios del estudiante (comparte la PK con personas)
--    bibliotecarios  datos propios del bibliotecario (comparte la PK con personas)
--    usuarios        credenciales y rol de cada persona (1 a 1)
-- =====================================================================

DROP DATABASE IF EXISTS biblioteca_escolar;
CREATE DATABASE biblioteca_escolar
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE biblioteca_escolar;

-- ---------------------------------------------------------------------
-- Personas (superclase)
-- ---------------------------------------------------------------------
CREATE TABLE personas (
    id_persona        INT          NOT NULL AUTO_INCREMENT,
    rut               VARCHAR(10)  NOT NULL COMMENT 'Sin puntos y con guion: 12345678-K',
    nombres           VARCHAR(60)  NOT NULL,
    apellido_paterno  VARCHAR(40)  NOT NULL,
    apellido_materno  VARCHAR(40)  NOT NULL,
    fecha_nacimiento  DATE         NOT NULL,
    direccion         VARCHAR(120) NOT NULL,
    nacionalidad      VARCHAR(40)  NOT NULL,
    genero            ENUM('FEMENINO','MASCULINO') NOT NULL,
    CONSTRAINT pk_personas PRIMARY KEY (id_persona),
    CONSTRAINT uq_personas_rut UNIQUE (rut)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Usuarios (credenciales de acceso).
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario       INT         NOT NULL AUTO_INCREMENT,
    id_persona       INT         NOT NULL,
    nombre_usuario   VARCHAR(40) NOT NULL,
    contrasena       VARCHAR(60) NOT NULL,
    rol              ENUM('ESTUDIANTE','BIBLIOTECARIO') NOT NULL,
    activo           BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuarios_persona UNIQUE (id_persona),
    CONSTRAINT uq_usuarios_nombre UNIQUE (nombre_usuario),
    CONSTRAINT fk_usuarios_persona FOREIGN KEY (id_persona)
        REFERENCES personas (id_persona) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Estudiantes (subclase). Curso = nivel + letra (A a H).
-- ---------------------------------------------------------------------
CREATE TABLE estudiantes (
    id_persona  INT     NOT NULL,
    nivel       ENUM('SEPTIMO_BASICO','OCTAVO_BASICO','PRIMERO_MEDIO',
                     'SEGUNDO_MEDIO','TERCERO_MEDIO','CUARTO_MEDIO') NOT NULL,
    letra       CHAR(1) NOT NULL,
    CONSTRAINT pk_estudiantes PRIMARY KEY (id_persona),
    CONSTRAINT ck_estudiantes_letra CHECK (letra BETWEEN 'A' AND 'H'),
    CONSTRAINT fk_estudiantes_persona FOREIGN KEY (id_persona)
        REFERENCES personas (id_persona) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Bibliotecarios (subclase)
-- ---------------------------------------------------------------------
CREATE TABLE bibliotecarios (
    id_persona         INT      NOT NULL,
    anio_contratacion  SMALLINT NOT NULL,
    CONSTRAINT pk_bibliotecarios PRIMARY KEY (id_persona),
    CONSTRAINT ck_bibliotecarios_anio CHECK (anio_contratacion >= 1950),
    CONSTRAINT fk_bibliotecarios_persona FOREIGN KEY (id_persona)
        REFERENCES personas (id_persona) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Categorías de libros
-- ---------------------------------------------------------------------
CREATE TABLE categorias (
    id_categoria  INT          NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(50)  NOT NULL,
    descripcion   VARCHAR(200) NOT NULL DEFAULT '',
    CONSTRAINT pk_categorias PRIMARY KEY (id_categoria),
    CONSTRAINT uq_categorias_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Libros. "stock" = ejemplares DISPONIBLES para préstamo en este momento.
-- ---------------------------------------------------------------------
CREATE TABLE libros (
    id_libro          INT          NOT NULL AUTO_INCREMENT,
    titulo            VARCHAR(150) NOT NULL,
    autor             VARCHAR(100) NOT NULL,
    editorial         VARCHAR(80)  NOT NULL,
    id_categoria      INT          NOT NULL,
    isbn              VARCHAR(13)  NOT NULL COMMENT 'ISBN-10 o ISBN-13 sin guiones',
    cantidad_paginas  INT          NOT NULL,
    stock             INT          NOT NULL DEFAULT 0,
    estado            ENUM('ACTIVO','EN_REPARACION','DADO_DE_BAJA') NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT pk_libros PRIMARY KEY (id_libro),
    CONSTRAINT uq_libros_isbn UNIQUE (isbn),
    CONSTRAINT ck_libros_paginas CHECK (cantidad_paginas > 0),
    CONSTRAINT ck_libros_stock CHECK (stock >= 0),
    CONSTRAINT fk_libros_categoria FOREIGN KEY (id_categoria)
        REFERENCES categorias (id_categoria)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Préstamos (también es el historial de cada estudiante)
-- ---------------------------------------------------------------------
CREATE TABLE prestamos (
    id_prestamo        INT          NOT NULL AUTO_INCREMENT,
    id_estudiante      INT          NOT NULL,
    id_libro           INT          NOT NULL,
    fecha_prestamo     DATE         NOT NULL,
    fecha_vencimiento  DATE         NOT NULL,
    fecha_devolucion   DATE         NULL COMMENT 'NULL mientras el préstamo está activo',
    estado             ENUM('ACTIVO','DEVUELTO','DEVUELTO_CON_ATRASO') NOT NULL DEFAULT 'ACTIVO',
    observacion        VARCHAR(200) NOT NULL DEFAULT '',
    CONSTRAINT pk_prestamos PRIMARY KEY (id_prestamo),
    CONSTRAINT ck_prestamos_vencimiento CHECK (fecha_vencimiento >= fecha_prestamo),
    CONSTRAINT ck_prestamos_devolucion CHECK (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_prestamo),
    CONSTRAINT fk_prestamos_estudiante FOREIGN KEY (id_estudiante)
        REFERENCES estudiantes (id_persona),
    CONSTRAINT fk_prestamos_libro FOREIGN KEY (id_libro)
        REFERENCES libros (id_libro)
) ENGINE = InnoDB;

CREATE INDEX ix_prestamos_estado ON prestamos (estado);

-- =====================================================================
--  Vistas para los reportes que pide la pauta
-- =====================================================================

-- Libros más prestados (todos los préstamos, activos e históricos)
CREATE VIEW v_libros_mas_prestados AS
SELECT l.id_libro,
       l.titulo,
       l.autor,
       c.nombre               AS categoria,
       COUNT(p.id_prestamo)   AS total_prestamos
FROM libros l
JOIN categorias c ON c.id_categoria = l.id_categoria
LEFT JOIN prestamos p ON p.id_libro = l.id_libro
GROUP BY l.id_libro, l.titulo, l.autor, c.nombre;

-- Libros actualmente en préstamo, con días de atraso calculados a hoy
CREATE VIEW v_libros_en_prestamo AS
SELECT p.id_prestamo,
       l.titulo,
       CONCAT(pe.nombres, ' ', pe.apellido_paterno, ' ', pe.apellido_materno) AS estudiante,
       e.nivel,
       e.letra,
       p.fecha_prestamo,
       p.fecha_vencimiento,
       GREATEST(0, DATEDIFF(CURDATE(), p.fecha_vencimiento)) AS dias_atraso
FROM prestamos p
JOIN libros l       ON l.id_libro    = p.id_libro
JOIN estudiantes e  ON e.id_persona  = p.id_estudiante
JOIN personas pe    ON pe.id_persona = p.id_estudiante
WHERE p.estado = 'ACTIVO';

-- Historial completo por estudiante
CREATE VIEW v_historial_estudiante AS
SELECT pe.id_persona AS id_estudiante,
       pe.rut,
       CONCAT(pe.nombres, ' ', pe.apellido_paterno, ' ', pe.apellido_materno) AS estudiante,
       l.titulo,
       p.fecha_prestamo,
       p.fecha_vencimiento,
       p.fecha_devolucion,
       p.estado,
       p.observacion
FROM prestamos p
JOIN personas pe ON pe.id_persona = p.id_estudiante
JOIN libros l    ON l.id_libro    = p.id_libro;
