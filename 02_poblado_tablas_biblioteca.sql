-- =====================================================================
--  Sistema de Gestión de Biblioteca Escolar
--  Script 2/2: poblado de tablas con datos de prueba (MySQL 8)
--  Ejecutar DESPUÉS de 01_crea_tablas_biblioteca.sql
-- =====================================================================
--  Contenido: 6 bibliotecarios, 48 estudiantes, 8 categorías,
--             59 libros y 124 préstamos (34 activos).
--
--  Contraseñas de prueba:
--    bibliotecarios -> biblio123
--    estudiantes    -> clave123
--  El listado de usuarios está al final de este script.
--
--  Las fechas de préstamo son RELATIVAS al día en que se ejecuta el script
--  (CURDATE() - INTERVAL n DAY), así siempre hay préstamos vigentes y atrasados.
--  Los RUT tienen dígito verificador válido; los ISBN son de ejemplo
--  (dígito de control válido, pero no corresponden a ediciones reales).
-- =====================================================================

USE biblioteca_escolar;

-- ---------------------------------------------------------------- personas
INSERT INTO personas (id_persona, rut, nombres, apellido_paterno, apellido_materno,
                      fecha_nacimiento, direccion, nacionalidad, genero) VALUES
    (1, '16606110-5', 'Fernanda Maite', 'Molina', 'Contreras', '1984-11-13', 'Av. Macul 9947, Peñalolén', 'Haitiana', 'FEMENINO'),
    (2, '18495380-3', 'Amaru Benjamín', 'Bravo', 'Soto', '1966-07-08', 'Av. Vicuña Mackenna 4806, Ñuñoa', 'Peruana', 'MASCULINO'),
    (3, '16027228-7', 'Joaquín Felipe', 'Reyes', 'Castro', '1991-01-03', 'Av. La Florida 1350, Puente Alto', 'Chilena', 'MASCULINO'),
    (4, '13690306-3', 'Bastián Daniel', 'Valenzuela', 'Muñoz', '1970-01-25', 'Santa Rosa 6056, San Miguel', 'Chilena', 'MASCULINO'),
    (5, '15690383-3', 'Martín Ignacio', 'Pizarro', 'Herrera', '1989-01-04', 'Av. Matta 6980, Peñalolén', 'Venezolana', 'MASCULINO'),
    (6, '9389286-0', 'Rayén Josefa', 'Díaz', 'Catrileo', '1987-02-01', 'Av. La Florida 1780, Peñalolén', 'Chilena', 'FEMENINO'),
    (7, '24236124-5', 'Nicolás', 'Cortés', 'Huenchumilla', '2013-08-29', 'Av. Quilín 6685, Macul', 'Chilena', 'MASCULINO'),
    (8, '24068051-3', 'Benjamín Alonso', 'Herrera', 'Carrasco', '2013-11-25', 'Santa Rosa 3374, Maipú', 'Venezolana', 'MASCULINO'),
    (9, '24037460-9', 'Gaspar Vicente', 'Espinoza', 'Castillo', '2013-08-11', 'Av. Larraín 1711, San Miguel', 'Colombiana', 'MASCULINO'),
    (10, '24191932-3', 'Trinidad', 'Ramírez', 'Pérez', '2013-07-09', 'Av. Matta 8266, Puente Alto', 'Chilena', 'FEMENINO'),
    (11, '24766164-6', 'Agustín Benjamín', 'Soto', 'González', '2014-02-07', 'Av. Larraín 9613, La Reina', 'Chilena', 'MASCULINO'),
    (12, '24655694-6', 'Catalina Valentina', 'Núñez', 'Flores', '2014-03-07', 'Santa Rosa 2635, Santiago', 'Chilena', 'FEMENINO'),
    (13, '24433949-2', 'Agustín Ignacio', 'Figueroa', 'Martínez', '2014-03-09', 'Av. La Florida 4862, Ñuñoa', 'Chilena', 'MASCULINO'),
    (14, '24004130-8', 'Rayén Trinidad', 'Castro', 'Sepúlveda', '2013-05-25', 'Av. La Florida 3322, Macul', 'Chilena', 'FEMENINO'),
    (15, '23477335-6', 'Bastián Martín', 'Carrasco', 'Fernández', '2012-07-15', 'Gran Avenida José Miguel Carrera 2621, Peñalolén', 'Colombiana', 'MASCULINO'),
    (16, '24085614-K', 'Bastián Matías', 'Jara', 'Pizarro', '2013-02-26', 'Av. Irarrázaval 3302, Providencia', 'Chilena', 'MASCULINO'),
    (17, '23661780-7', 'Amanda', 'Molina', 'López', '2012-07-21', 'Av. Quilín 435, Providencia', 'Colombiana', 'FEMENINO'),
    (18, '23467100-6', 'Gaspar', 'Martínez', 'Morales', '2012-12-29', 'Gran Avenida José Miguel Carrera 6489, Maipú', 'Chilena', 'MASCULINO'),
    (19, '23342779-9', 'Constanza Javiera', 'Rodríguez', 'Araya', '2012-11-26', 'Av. Larraín 8252, Macul', 'Chilena', 'FEMENINO'),
    (20, '23710650-4', 'Ignacio Martín', 'Herrera', 'Flores', '2012-05-18', 'Av. Vicuña Mackenna 3611, La Florida', 'Chilena', 'MASCULINO'),
    (21, '23913530-7', 'Diego Tomás', 'Muñoz', 'Reyes', '2013-01-01', 'Gran Avenida José Miguel Carrera 5862, La Reina', 'Boliviana', 'MASCULINO'),
    (22, '23601434-7', 'Daniela', 'Jara', 'Vergara', '2012-08-16', 'José Pedro Alessandri 9436, Maipú', 'Boliviana', 'FEMENINO'),
    (23, '23188540-4', 'Tomás', 'Contreras', 'Cortés', '2011-08-28', 'Av. Departamental 4634, Peñalolén', 'Chilena', 'MASCULINO'),
    (24, '22823325-0', 'Tomás Matías', 'González', 'Álvarez', '2011-05-20', 'Av. Matta 6891, Santiago', 'Chilena', 'MASCULINO'),
    (25, '22865708-5', 'Agustina Rayén', 'Vergara', 'Reyes', '2011-10-24', 'Av. Departamental 7571, Peñalolén', 'Chilena', 'FEMENINO'),
    (26, '22730215-1', 'Daniela Antonia', 'Cárcamo', 'Gutiérrez', '2011-09-30', 'Av. La Florida 4221, Macul', 'Chilena', 'FEMENINO'),
    (27, '23723411-1', 'Benjamín Sebastián', 'Espinoza', 'Painemal', '2012-01-25', 'Av. Vicuña Mackenna 4965, Macul', 'Chilena', 'MASCULINO'),
    (28, '22719565-7', 'Maite', 'López', 'Jara', '2011-05-31', 'Av. Irarrázaval 1944, Providencia', 'Chilena', 'FEMENINO'),
    (29, '22825465-7', 'Ignacia Agustina', 'Muñoz', 'Vásquez', '2011-10-11', 'Av. Pajaritos 5932, Macul', 'Chilena', 'FEMENINO'),
    (30, '23112407-1', 'Maximiliano', 'Rodríguez', 'Núñez', '2011-08-09', 'Av. Grecia 8969, La Reina', 'Chilena', 'MASCULINO'),
    (31, '22481877-7', 'Diego Daniel', 'Soto', 'Rodríguez', '2010-09-22', 'Av. Concha y Toro 722, La Reina', 'Chilena', 'MASCULINO'),
    (32, '22966663-0', 'Vicente', 'González', 'Vera', '2011-03-14', 'Av. Macul 7809, Santiago', 'Chilena', 'MASCULINO'),
    (33, '22338276-2', 'Constanza', 'Gutiérrez', 'Hernández', '2010-10-09', 'Av. Concha y Toro 4618, Puente Alto', 'Chilena', 'FEMENINO'),
    (34, '22404204-3', 'Bastián Ignacio', 'Cárcamo', 'Figueroa', '2010-06-18', 'Av. Tobalaba 3178, Providencia', 'Chilena', 'MASCULINO'),
    (35, '22345451-8', 'Javiera', 'Herrera', 'Huenchumilla', '2010-09-01', 'Pedro de Valdivia 714, Providencia', 'Chilena', 'FEMENINO'),
    (36, '22723405-9', 'Agustín', 'Contreras', 'Muñoz', '2011-01-19', 'Av. Concha y Toro 3657, Peñalolén', 'Chilena', 'MASCULINO'),
    (37, '22534080-3', 'Tomás Bastián', 'Molina', 'Saavedra', '2010-06-12', 'Gran Avenida José Miguel Carrera 8435, Ñuñoa', 'Chilena', 'MASCULINO'),
    (38, '22849725-8', 'Diego Lucas', 'Rojas', 'Ñancupil', '2011-01-03', 'José Pedro Alessandri 2757, Maipú', 'Chilena', 'MASCULINO'),
    (39, '22065144-4', 'Nicolás Ignacio', 'Muñoz', 'Rodríguez', '2009-04-15', 'Av. Departamental 8932, Providencia', 'Chilena', 'MASCULINO'),
    (40, '21713346-7', 'Ignacia', 'Catrileo', 'Molina', '2009-09-02', 'Av. Grecia 6236, Ñuñoa', 'Chilena', 'FEMENINO'),
    (41, '21737662-9', 'Daniela', 'Martínez', 'Contreras', '2009-10-13', 'Av. Departamental 8039, Ñuñoa', 'Chilena', 'FEMENINO'),
    (42, '22330941-0', 'Martín Ignacio', 'Silva', 'Jara', '2010-01-25', 'Av. La Florida 4147, Puente Alto', 'Peruana', 'MASCULINO'),
    (43, '21826805-6', 'Isidora', 'Soto', 'Carrasco', '2009-09-14', 'Av. Tobalaba 4446, La Florida', 'Chilena', 'FEMENINO'),
    (44, '22022774-K', 'Diego', 'Painemal', 'Saavedra', '2009-07-10', 'Av. Quilín 6703, Puente Alto', 'Chilena', 'MASCULINO'),
    (45, '22494374-1', 'Maximiliano', 'Reyes', 'Álvarez', '2010-01-16', 'Av. Ossa 3587, Providencia', 'Peruana', 'MASCULINO'),
    (46, '22290601-6', 'Trinidad Antonia', 'Castillo', 'Flores', '2010-02-15', 'Av. Larraín 4419, Providencia', 'Haitiana', 'FEMENINO'),
    (47, '21160054-3', 'Felipe Diego', 'Cárcamo', 'Álvarez', '2008-12-21', 'Av. Departamental 1656, San Miguel', 'Haitiana', 'MASCULINO'),
    (48, '21732211-1', 'Sebastián', 'Díaz', 'Pizarro', '2009-03-26', 'Gran Avenida José Miguel Carrera 272, Maipú', 'Haitiana', 'MASCULINO'),
    (49, '22046636-1', 'Fernanda Amanda', 'Ramírez', 'Tapia', '2009-03-05', 'Av. Grecia 714, La Florida', 'Chilena', 'FEMENINO'),
    (50, '21698384-K', 'Diego Joaquín', 'Fernández', 'Cortés', '2009-03-21', 'Av. Matta 6152, Ñuñoa', 'Haitiana', 'MASCULINO'),
    (51, '21423203-0', 'Antonia Emilia', 'Fernández', 'Fuentes', '2008-10-31', 'Gran Avenida José Miguel Carrera 5169, Ñuñoa', 'Boliviana', 'FEMENINO'),
    (52, '21947629-9', 'Antonia Amanda', 'Rojas', 'Rodríguez', '2009-03-03', 'Av. Concha y Toro 8511, La Florida', 'Venezolana', 'FEMENINO'),
    (53, '21061415-K', 'Diego Ignacio', 'Araya', 'Carrasco', '2008-06-08', 'Av. Departamental 4197, Santiago', 'Venezolana', 'MASCULINO'),
    (54, '21355000-4', 'Sebastián Alonso', 'López', 'Gutiérrez', '2008-07-21', 'Av. Matta 8493, Macul', 'Chilena', 'MASCULINO');

-- ---------------------------------------------------------------- bibliotecarios
INSERT INTO bibliotecarios (id_persona, anio_contratacion) VALUES
    (1, 2023),
    (2, 2017),
    (3, 2017),
    (4, 2020),
    (5, 2023),
    (6, 2024);

-- ---------------------------------------------------------------- estudiantes
INSERT INTO estudiantes (id_persona, nivel, letra) VALUES
    (7, 'SEPTIMO_BASICO', 'D'),
    (8, 'SEPTIMO_BASICO', 'G'),
    (9, 'SEPTIMO_BASICO', 'A'),
    (10, 'SEPTIMO_BASICO', 'H'),
    (11, 'SEPTIMO_BASICO', 'E'),
    (12, 'SEPTIMO_BASICO', 'C'),
    (13, 'SEPTIMO_BASICO', 'B'),
    (14, 'SEPTIMO_BASICO', 'F'),
    (15, 'OCTAVO_BASICO', 'B'),
    (16, 'OCTAVO_BASICO', 'A'),
    (17, 'OCTAVO_BASICO', 'C'),
    (18, 'OCTAVO_BASICO', 'F'),
    (19, 'OCTAVO_BASICO', 'G'),
    (20, 'OCTAVO_BASICO', 'D'),
    (21, 'OCTAVO_BASICO', 'H'),
    (22, 'OCTAVO_BASICO', 'E'),
    (23, 'PRIMERO_MEDIO', 'C'),
    (24, 'PRIMERO_MEDIO', 'A'),
    (25, 'PRIMERO_MEDIO', 'H'),
    (26, 'PRIMERO_MEDIO', 'G'),
    (27, 'PRIMERO_MEDIO', 'D'),
    (28, 'PRIMERO_MEDIO', 'E'),
    (29, 'PRIMERO_MEDIO', 'F'),
    (30, 'PRIMERO_MEDIO', 'B'),
    (31, 'SEGUNDO_MEDIO', 'C'),
    (32, 'SEGUNDO_MEDIO', 'B'),
    (33, 'SEGUNDO_MEDIO', 'A'),
    (34, 'SEGUNDO_MEDIO', 'F'),
    (35, 'SEGUNDO_MEDIO', 'H'),
    (36, 'SEGUNDO_MEDIO', 'D'),
    (37, 'SEGUNDO_MEDIO', 'G'),
    (38, 'SEGUNDO_MEDIO', 'E'),
    (39, 'TERCERO_MEDIO', 'F'),
    (40, 'TERCERO_MEDIO', 'B'),
    (41, 'TERCERO_MEDIO', 'D'),
    (42, 'TERCERO_MEDIO', 'G'),
    (43, 'TERCERO_MEDIO', 'A'),
    (44, 'TERCERO_MEDIO', 'C'),
    (45, 'TERCERO_MEDIO', 'E'),
    (46, 'TERCERO_MEDIO', 'H'),
    (47, 'CUARTO_MEDIO', 'D'),
    (48, 'CUARTO_MEDIO', 'C'),
    (49, 'CUARTO_MEDIO', 'B'),
    (50, 'CUARTO_MEDIO', 'E'),
    (51, 'CUARTO_MEDIO', 'A'),
    (52, 'CUARTO_MEDIO', 'F'),
    (53, 'CUARTO_MEDIO', 'G'),
    (54, 'CUARTO_MEDIO', 'H');

-- ---------------------------------------------------------------- usuarios
-- nombre_usuario = 2 letras del 1er nombre + apellido paterno + inicial del materno
--                  + 4 primeros dígitos del RUT + mes (2 dígitos) + año de nacimiento
INSERT INTO usuarios (id_usuario, id_persona, nombre_usuario, contrasena, rol, activo) VALUES
    (1, 1, 'femolinac1660111984', 'biblio123', 'BIBLIOTECARIO', TRUE),
    (2, 2, 'ambravos1849071966', 'biblio123', 'BIBLIOTECARIO', TRUE),
    (3, 3, 'joreyesc1602011991', 'biblio123', 'BIBLIOTECARIO', TRUE),
    (4, 4, 'bavalenzuelam1369011970', 'biblio123', 'BIBLIOTECARIO', TRUE),
    (5, 5, 'mapizarroh1569011989', 'biblio123', 'BIBLIOTECARIO', TRUE),
    (6, 6, 'radiazc9389021987', 'biblio123', 'BIBLIOTECARIO', TRUE),
    (7, 7, 'nicortesh2423082013', 'clave123', 'ESTUDIANTE', TRUE),
    (8, 8, 'beherrerac2406112013', 'clave123', 'ESTUDIANTE', TRUE),
    (9, 9, 'gaespinozac2403082013', 'clave123', 'ESTUDIANTE', TRUE),
    (10, 10, 'trramirezp2419072013', 'clave123', 'ESTUDIANTE', TRUE),
    (11, 11, 'agsotog2476022014', 'clave123', 'ESTUDIANTE', TRUE),
    (12, 12, 'canunezf2465032014', 'clave123', 'ESTUDIANTE', TRUE),
    (13, 13, 'agfigueroam2443032014', 'clave123', 'ESTUDIANTE', TRUE),
    (14, 14, 'racastros2400052013', 'clave123', 'ESTUDIANTE', TRUE),
    (15, 15, 'bacarrascof2347072012', 'clave123', 'ESTUDIANTE', TRUE),
    (16, 16, 'bajarap2408022013', 'clave123', 'ESTUDIANTE', TRUE),
    (17, 17, 'ammolinal2366072012', 'clave123', 'ESTUDIANTE', TRUE),
    (18, 18, 'gamartinezm2346122012', 'clave123', 'ESTUDIANTE', TRUE),
    (19, 19, 'corodrigueza2334112012', 'clave123', 'ESTUDIANTE', TRUE),
    (20, 20, 'igherreraf2371052012', 'clave123', 'ESTUDIANTE', TRUE),
    (21, 21, 'dimunozr2391012013', 'clave123', 'ESTUDIANTE', TRUE),
    (22, 22, 'dajarav2360082012', 'clave123', 'ESTUDIANTE', TRUE),
    (23, 23, 'tocontrerasc2318082011', 'clave123', 'ESTUDIANTE', TRUE),
    (24, 24, 'togonzaleza2282052011', 'clave123', 'ESTUDIANTE', TRUE),
    (25, 25, 'agvergarar2286102011', 'clave123', 'ESTUDIANTE', TRUE),
    (26, 26, 'dacarcamog2273092011', 'clave123', 'ESTUDIANTE', TRUE),
    (27, 27, 'beespinozap2372012012', 'clave123', 'ESTUDIANTE', TRUE),
    (28, 28, 'malopezj2271052011', 'clave123', 'ESTUDIANTE', TRUE),
    (29, 29, 'igmunozv2282102011', 'clave123', 'ESTUDIANTE', TRUE),
    (30, 30, 'marodriguezn2311082011', 'clave123', 'ESTUDIANTE', TRUE),
    (31, 31, 'disotor2248092010', 'clave123', 'ESTUDIANTE', TRUE),
    (32, 32, 'vigonzalezv2296032011', 'clave123', 'ESTUDIANTE', TRUE),
    (33, 33, 'cogutierrezh2233102010', 'clave123', 'ESTUDIANTE', TRUE),
    (34, 34, 'bacarcamof2240062010', 'clave123', 'ESTUDIANTE', TRUE),
    (35, 35, 'jaherrerah2234092010', 'clave123', 'ESTUDIANTE', TRUE),
    (36, 36, 'agcontrerasm2272012011', 'clave123', 'ESTUDIANTE', TRUE),
    (37, 37, 'tomolinas2253062010', 'clave123', 'ESTUDIANTE', TRUE),
    (38, 38, 'dirojasn2284012011', 'clave123', 'ESTUDIANTE', TRUE),
    (39, 39, 'nimunozr2206042009', 'clave123', 'ESTUDIANTE', TRUE),
    (40, 40, 'igcatrileom2171092009', 'clave123', 'ESTUDIANTE', TRUE),
    (41, 41, 'damartinezc2173102009', 'clave123', 'ESTUDIANTE', TRUE),
    (42, 42, 'masilvaj2233012010', 'clave123', 'ESTUDIANTE', TRUE),
    (43, 43, 'issotoc2182092009', 'clave123', 'ESTUDIANTE', TRUE),
    (44, 44, 'dipainemals2202072009', 'clave123', 'ESTUDIANTE', TRUE),
    (45, 45, 'mareyesa2249012010', 'clave123', 'ESTUDIANTE', TRUE),
    (46, 46, 'trcastillof2229022010', 'clave123', 'ESTUDIANTE', TRUE),
    (47, 47, 'fecarcamoa2116122008', 'clave123', 'ESTUDIANTE', TRUE),
    (48, 48, 'sediazp2173032009', 'clave123', 'ESTUDIANTE', TRUE),
    (49, 49, 'feramirezt2204032009', 'clave123', 'ESTUDIANTE', TRUE),
    (50, 50, 'difernandezc2169032009', 'clave123', 'ESTUDIANTE', TRUE),
    (51, 51, 'anfernandezf2142102008', 'clave123', 'ESTUDIANTE', TRUE),
    (52, 52, 'anrojasr2194032009', 'clave123', 'ESTUDIANTE', TRUE),
    (53, 53, 'diarayac2106062008', 'clave123', 'ESTUDIANTE', TRUE),
    (54, 54, 'selopezg2135072008', 'clave123', 'ESTUDIANTE', FALSE);

-- ---------------------------------------------------------------- categorías
INSERT INTO categorias (id_categoria, nombre, descripcion) VALUES
    (1, 'Fantasía', 'Mundos imaginarios, magia y criaturas fantásticas'),
    (2, 'Novela juvenil', 'Aventuras y novelas pensadas para lectores jóvenes'),
    (3, 'Literatura chilena', 'Narrativa de autores chilenos'),
    (4, 'Poesía', 'Obras poéticas'),
    (5, 'Clásico universal', 'Obras fundamentales de la literatura mundial'),
    (6, 'Ciencia ficción', 'Futuros posibles, distopías y tecnología'),
    (7, 'Terror', 'Suspenso, misterio y relatos de miedo'),
    (8, 'Teatro', 'Obras dramáticas');

-- ---------------------------------------------------------------- libros
-- stock = ejemplares disponibles (total de ejemplares menos préstamos activos)
INSERT INTO libros (id_libro, titulo, autor, editorial, id_categoria, isbn,
                    cantidad_paginas, stock, estado) VALUES
    (1, 'Harry Potter y la piedra filosofal', 'J. K. Rowling', 'Salamandra', 1, '9789560000019', 264, 4, 'ACTIVO'),
    (2, 'Harry Potter y la cámara secreta', 'J. K. Rowling', 'Salamandra', 1, '9789560000026', 296, 4, 'ACTIVO'),
    (3, 'Harry Potter y el prisionero de Azkaban', 'J. K. Rowling', 'Salamandra', 1, '9789560000033', 360, 5, 'ACTIVO'),
    (4, 'Harry Potter y el cáliz de fuego', 'J. K. Rowling', 'Salamandra', 1, '9789560000040', 672, 3, 'ACTIVO'),
    (5, 'Harry Potter y la Orden del Fénix', 'J. K. Rowling', 'Salamandra', 1, '9789560000057', 896, 0, 'ACTIVO'),
    (6, 'Harry Potter y el misterio del príncipe', 'J. K. Rowling', 'Salamandra', 1, '9789560000064', 608, 3, 'ACTIVO'),
    (7, 'Harry Potter y las Reliquias de la Muerte', 'J. K. Rowling', 'Salamandra', 1, '9789560000071', 640, 1, 'ACTIVO'),
    (8, 'Las Crónicas de Narnia: El sobrino del mago', 'C. S. Lewis', 'Destino', 1, '9789560000088', 240, 2, 'ACTIVO'),
    (9, 'Las Crónicas de Narnia: El león, la bruja y el armario', 'C. S. Lewis', 'Destino', 1, '9789560000095', 208, 5, 'ACTIVO'),
    (10, 'Las Crónicas de Narnia: El caballo y el muchacho', 'C. S. Lewis', 'Destino', 1, '9789560000101', 240, 1, 'ACTIVO'),
    (11, 'Las Crónicas de Narnia: El príncipe Caspian', 'C. S. Lewis', 'Destino', 1, '9789560000118', 256, 3, 'ACTIVO'),
    (12, 'Las Crónicas de Narnia: La travesía del Viajero del Alba', 'C. S. Lewis', 'Destino', 1, '9789560000125', 272, 2, 'ACTIVO'),
    (13, 'Las Crónicas de Narnia: La silla de plata', 'C. S. Lewis', 'Destino', 1, '9789560000132', 256, 2, 'ACTIVO'),
    (14, 'Las Crónicas de Narnia: La última batalla', 'C. S. Lewis', 'Destino', 1, '9789560000149', 224, 2, 'ACTIVO'),
    (15, 'Juego de tronos', 'George R. R. Martin', 'Plaza & Janés', 1, '9789560000156', 800, 2, 'ACTIVO'),
    (16, 'Choque de reyes', 'George R. R. Martin', 'Plaza & Janés', 1, '9789560000163', 960, 2, 'ACTIVO'),
    (17, 'Tormenta de espadas', 'George R. R. Martin', 'Plaza & Janés', 1, '9789560000170', 1184, 1, 'ACTIVO'),
    (18, 'Festín de cuervos', 'George R. R. Martin', 'Plaza & Janés', 1, '9789560000187', 864, 0, 'ACTIVO'),
    (19, 'Danza de dragones', 'George R. R. Martin', 'Plaza & Janés', 1, '9789560000194', 1120, 0, 'ACTIVO'),
    (20, 'Fuego y sangre', 'George R. R. Martin', 'Plaza & Janés', 1, '9789560000200', 880, 0, 'ACTIVO'),
    (21, 'El hobbit', 'J. R. R. Tolkien', 'Minotauro', 1, '9789560000217', 320, 4, 'ACTIVO'),
    (22, 'El Señor de los Anillos: La Comunidad del Anillo', 'J. R. R. Tolkien', 'Minotauro', 1, '9789560000224', 576, 2, 'ACTIVO'),
    (23, 'El Señor de los Anillos: Las dos torres', 'J. R. R. Tolkien', 'Minotauro', 1, '9789560000231', 448, 3, 'ACTIVO'),
    (24, 'El Señor de los Anillos: El retorno del rey', 'J. R. R. Tolkien', 'Minotauro', 1, '9789560000248', 512, 3, 'ACTIVO'),
    (25, 'El Silmarillion', 'J. R. R. Tolkien', 'Minotauro', 1, '9789560000255', 448, 1, 'ACTIVO'),
    (26, 'La casa de los espíritus', 'Isabel Allende', 'Sudamericana', 3, '9789560000262', 496, 3, 'ACTIVO'),
    (27, 'La Ciudad de las Bestias', 'Isabel Allende', 'Sudamericana', 2, '9789560000279', 336, 3, 'ACTIVO'),
    (28, 'El Reino del Dragón de Oro', 'Isabel Allende', 'Sudamericana', 2, '9789560000286', 352, 2, 'ACTIVO'),
    (29, 'El Bosque de los Pigmeos', 'Isabel Allende', 'Sudamericana', 2, '9789560000293', 256, 3, 'ACTIVO'),
    (30, 'Eva Luna', 'Isabel Allende', 'Plaza & Janés', 3, '9789560000309', 320, 1, 'ACTIVO'),
    (31, 'Paula', 'Isabel Allende', 'Plaza & Janés', 3, '9789560000316', 448, 2, 'ACTIVO'),
    (32, 'Santa María de las flores negras', 'Hernán Rivera Letelier', 'Seix Barral', 3, '9789560000323', 208, 4, 'ACTIVO'),
    (33, 'La reina Isabel cantaba rancheras', 'Hernán Rivera Letelier', 'Planeta', 3, '9789560000330', 224, 2, 'ACTIVO'),
    (34, 'Hijo de ladrón', 'Manuel Rojas', 'Zig-Zag', 3, '9789560000347', 352, 3, 'ACTIVO'),
    (35, 'Martín Rivas', 'Alberto Blest Gana', 'Zig-Zag', 3, '9789560000354', 432, 5, 'ACTIVO'),
    (36, 'Subterra', 'Baldomero Lillo', 'Zig-Zag', 3, '9789560000361', 176, 4, 'ACTIVO'),
    (37, 'Papelucho', 'Marcela Paz', 'Universitaria', 3, '9789560000378', 120, 6, 'ACTIVO'),
    (38, 'La amortajada', 'María Luisa Bombal', 'Seix Barral', 3, '9789560000385', 128, 2, 'ACTIVO'),
    (39, 'Ardiente paciencia', 'Antonio Skármeta', 'Debolsillo', 3, '9789560000392', 144, 3, 'ACTIVO'),
    (40, 'Mala onda', 'Alberto Fuguet', 'Alfaguara', 3, '9789560000408', 320, 2, 'ACTIVO'),
    (41, 'Estrella distante', 'Roberto Bolaño', 'Anagrama', 3, '9789560000415', 160, 0, 'ACTIVO'),
    (42, 'Veinte poemas de amor y una canción desesperada', 'Pablo Neruda', 'Seix Barral', 4, '9789560000422', 104, 4, 'ACTIVO'),
    (43, 'Desolación', 'Gabriela Mistral', 'Zig-Zag', 4, '9789560000439', 192, 2, 'EN_REPARACION'),
    (44, 'Rebelión en la granja', 'George Orwell', 'Debolsillo', 5, '9789560000446', 144, 3, 'ACTIVO'),
    (45, '1984', 'George Orwell', 'Debolsillo', 6, '9789560000453', 352, 3, 'ACTIVO'),
    (46, 'Fahrenheit 451', 'Ray Bradbury', 'Minotauro', 6, '9789560000460', 176, 2, 'ACTIVO'),
    (47, 'Un mundo feliz', 'Aldous Huxley', 'Debolsillo', 6, '9789560000477', 256, 2, 'ACTIVO'),
    (48, 'El principito', 'Antoine de Saint-Exupéry', 'Salamandra', 5, '9789560000484', 96, 6, 'ACTIVO'),
    (49, 'Don Quijote de la Mancha', 'Miguel de Cervantes', 'Alfaguara', 5, '9789560000491', 1376, 2, 'ACTIVO'),
    (50, 'Cien años de soledad', 'Gabriel García Márquez', 'Sudamericana', 5, '9789560000507', 496, 3, 'ACTIVO'),
    (51, 'Crónica de una muerte anunciada', 'Gabriel García Márquez', 'Debolsillo', 5, '9789560000514', 144, 3, 'ACTIVO'),
    (52, 'La metamorfosis', 'Franz Kafka', 'Alianza', 5, '9789560000521', 112, 2, 'ACTIVO'),
    (53, 'Romeo y Julieta', 'William Shakespeare', 'Austral', 8, '9789560000538', 160, 3, 'ACTIVO'),
    (54, 'La casa de Bernarda Alba', 'Federico García Lorca', 'Cátedra', 8, '9789560000545', 176, 1, 'ACTIVO'),
    (55, 'Frankenstein', 'Mary Shelley', 'Alianza', 7, '9789560000552', 304, 2, 'ACTIVO'),
    (56, 'Drácula', 'Bram Stoker', 'Penguin Clásicos', 7, '9789560000569', 544, 2, 'ACTIVO'),
    (57, 'Cuentos de amor, de locura y de muerte', 'Horacio Quiroga', 'Zig-Zag', 7, '9789560000576', 192, 2, 'ACTIVO'),
    (58, 'Narraciones extraordinarias', 'Edgar Allan Poe', 'Alianza', 7, '9789560000583', 336, 2, 'ACTIVO'),
    (59, 'El extraño caso del Dr. Jekyll y Mr. Hyde', 'Robert Louis Stevenson', 'Anaya', 7, '9789560000590', 128, 1, 'DADO_DE_BAJA');

-- ---------------------------------------------------------------- préstamos
-- Plazo de préstamo: 7 días (igual que Prestamo.DIAS_PRESTAMO en Java)
INSERT INTO prestamos (id_prestamo, id_estudiante, id_libro, fecha_prestamo, fecha_vencimiento,
                       fecha_devolucion, estado, observacion) VALUES
    (1, 12, 41, CURDATE() - INTERVAL 176 DAY, CURDATE() - INTERVAL 169 DAY, CURDATE() - INTERVAL 173 DAY, 'DEVUELTO', ''),
    (2, 18, 38, CURDATE() - INTERVAL 176 DAY, CURDATE() - INTERVAL 169 DAY, CURDATE() - INTERVAL 170 DAY, 'DEVUELTO', ''),
    (3, 23, 9, CURDATE() - INTERVAL 173 DAY, CURDATE() - INTERVAL 166 DAY, CURDATE() - INTERVAL 167 DAY, 'DEVUELTO', ''),
    (4, 54, 42, CURDATE() - INTERVAL 170 DAY, CURDATE() - INTERVAL 163 DAY, CURDATE() - INTERVAL 150 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 13 día(s) de atraso'),
    (5, 38, 32, CURDATE() - INTERVAL 170 DAY, CURDATE() - INTERVAL 163 DAY, CURDATE() - INTERVAL 167 DAY, 'DEVUELTO', ''),
    (6, 50, 28, CURDATE() - INTERVAL 170 DAY, CURDATE() - INTERVAL 163 DAY, CURDATE() - INTERVAL 164 DAY, 'DEVUELTO', ''),
    (7, 9, 6, CURDATE() - INTERVAL 168 DAY, CURDATE() - INTERVAL 161 DAY, CURDATE() - INTERVAL 164 DAY, 'DEVUELTO', ''),
    (8, 17, 3, CURDATE() - INTERVAL 165 DAY, CURDATE() - INTERVAL 158 DAY, CURDATE() - INTERVAL 163 DAY, 'DEVUELTO', ''),
    (9, 11, 6, CURDATE() - INTERVAL 164 DAY, CURDATE() - INTERVAL 157 DAY, CURDATE() - INTERVAL 160 DAY, 'DEVUELTO', ''),
    (10, 42, 8, CURDATE() - INTERVAL 163 DAY, CURDATE() - INTERVAL 156 DAY, CURDATE() - INTERVAL 159 DAY, 'DEVUELTO', ''),
    (11, 45, 11, CURDATE() - INTERVAL 162 DAY, CURDATE() - INTERVAL 155 DAY, CURDATE() - INTERVAL 157 DAY, 'DEVUELTO', ''),
    (12, 33, 6, CURDATE() - INTERVAL 161 DAY, CURDATE() - INTERVAL 154 DAY, CURDATE() - INTERVAL 145 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 9 día(s) de atraso'),
    (13, 45, 24, CURDATE() - INTERVAL 160 DAY, CURDATE() - INTERVAL 153 DAY, CURDATE() - INTERVAL 159 DAY, 'DEVUELTO', ''),
    (14, 42, 31, CURDATE() - INTERVAL 159 DAY, CURDATE() - INTERVAL 152 DAY, CURDATE() - INTERVAL 155 DAY, 'DEVUELTO', ''),
    (15, 36, 47, CURDATE() - INTERVAL 156 DAY, CURDATE() - INTERVAL 149 DAY, CURDATE() - INTERVAL 155 DAY, 'DEVUELTO', ''),
    (16, 9, 44, CURDATE() - INTERVAL 156 DAY, CURDATE() - INTERVAL 149 DAY, CURDATE() - INTERVAL 153 DAY, 'DEVUELTO', ''),
    (17, 25, 44, CURDATE() - INTERVAL 154 DAY, CURDATE() - INTERVAL 147 DAY, CURDATE() - INTERVAL 151 DAY, 'DEVUELTO', ''),
    (18, 44, 26, CURDATE() - INTERVAL 151 DAY, CURDATE() - INTERVAL 144 DAY, CURDATE() - INTERVAL 135 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 9 día(s) de atraso'),
    (19, 19, 34, CURDATE() - INTERVAL 148 DAY, CURDATE() - INTERVAL 141 DAY, CURDATE() - INTERVAL 147 DAY, 'DEVUELTO', ''),
    (20, 22, 45, CURDATE() - INTERVAL 146 DAY, CURDATE() - INTERVAL 139 DAY, CURDATE() - INTERVAL 136 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 3 día(s) de atraso'),
    (21, 46, 34, CURDATE() - INTERVAL 145 DAY, CURDATE() - INTERVAL 138 DAY, CURDATE() - INTERVAL 136 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 2 día(s) de atraso'),
    (22, 22, 4, CURDATE() - INTERVAL 144 DAY, CURDATE() - INTERVAL 137 DAY, CURDATE() - INTERVAL 135 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 2 día(s) de atraso'),
    (23, 12, 1, CURDATE() - INTERVAL 143 DAY, CURDATE() - INTERVAL 136 DAY, CURDATE() - INTERVAL 142 DAY, 'DEVUELTO', ''),
    (24, 48, 18, CURDATE() - INTERVAL 143 DAY, CURDATE() - INTERVAL 136 DAY, CURDATE() - INTERVAL 137 DAY, 'DEVUELTO', ''),
    (25, 20, 15, CURDATE() - INTERVAL 142 DAY, CURDATE() - INTERVAL 135 DAY, CURDATE() - INTERVAL 136 DAY, 'DEVUELTO', ''),
    (26, 28, 35, CURDATE() - INTERVAL 142 DAY, CURDATE() - INTERVAL 135 DAY, CURDATE() - INTERVAL 141 DAY, 'DEVUELTO', ''),
    (27, 51, 7, CURDATE() - INTERVAL 140 DAY, CURDATE() - INTERVAL 133 DAY, CURDATE() - INTERVAL 135 DAY, 'DEVUELTO', ''),
    (28, 35, 22, CURDATE() - INTERVAL 139 DAY, CURDATE() - INTERVAL 132 DAY, CURDATE() - INTERVAL 133 DAY, 'DEVUELTO', ''),
    (29, 42, 35, CURDATE() - INTERVAL 137 DAY, CURDATE() - INTERVAL 130 DAY, CURDATE() - INTERVAL 136 DAY, 'DEVUELTO', ''),
    (30, 13, 56, CURDATE() - INTERVAL 135 DAY, CURDATE() - INTERVAL 128 DAY, CURDATE() - INTERVAL 120 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 8 día(s) de atraso'),
    (31, 42, 44, CURDATE() - INTERVAL 133 DAY, CURDATE() - INTERVAL 126 DAY, CURDATE() - INTERVAL 123 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 3 día(s) de atraso'),
    (32, 37, 1, CURDATE() - INTERVAL 130 DAY, CURDATE() - INTERVAL 123 DAY, CURDATE() - INTERVAL 125 DAY, 'DEVUELTO', ''),
    (33, 40, 8, CURDATE() - INTERVAL 129 DAY, CURDATE() - INTERVAL 122 DAY, CURDATE() - INTERVAL 121 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 1 día(s) de atraso'),
    (34, 13, 58, CURDATE() - INTERVAL 129 DAY, CURDATE() - INTERVAL 122 DAY, CURDATE() - INTERVAL 123 DAY, 'DEVUELTO', ''),
    (35, 51, 7, CURDATE() - INTERVAL 128 DAY, CURDATE() - INTERVAL 121 DAY, CURDATE() - INTERVAL 122 DAY, 'DEVUELTO', ''),
    (36, 29, 6, CURDATE() - INTERVAL 125 DAY, CURDATE() - INTERVAL 118 DAY, CURDATE() - INTERVAL 123 DAY, 'DEVUELTO', ''),
    (37, 11, 58, CURDATE() - INTERVAL 124 DAY, CURDATE() - INTERVAL 117 DAY, CURDATE() - INTERVAL 120 DAY, 'DEVUELTO', ''),
    (38, 9, 1, CURDATE() - INTERVAL 123 DAY, CURDATE() - INTERVAL 116 DAY, CURDATE() - INTERVAL 119 DAY, 'DEVUELTO', ''),
    (39, 15, 1, CURDATE() - INTERVAL 122 DAY, CURDATE() - INTERVAL 115 DAY, CURDATE() - INTERVAL 116 DAY, 'DEVUELTO', ''),
    (40, 22, 27, CURDATE() - INTERVAL 121 DAY, CURDATE() - INTERVAL 114 DAY, CURDATE() - INTERVAL 115 DAY, 'DEVUELTO', ''),
    (41, 44, 10, CURDATE() - INTERVAL 121 DAY, CURDATE() - INTERVAL 114 DAY, CURDATE() - INTERVAL 114 DAY, 'DEVUELTO', ''),
    (42, 31, 32, CURDATE() - INTERVAL 118 DAY, CURDATE() - INTERVAL 111 DAY, CURDATE() - INTERVAL 98 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 13 día(s) de atraso'),
    (43, 42, 35, CURDATE() - INTERVAL 116 DAY, CURDATE() - INTERVAL 109 DAY, CURDATE() - INTERVAL 97 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 12 día(s) de atraso'),
    (44, 29, 15, CURDATE() - INTERVAL 114 DAY, CURDATE() - INTERVAL 107 DAY, CURDATE() - INTERVAL 94 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 13 día(s) de atraso'),
    (45, 19, 22, CURDATE() - INTERVAL 111 DAY, CURDATE() - INTERVAL 104 DAY, CURDATE() - INTERVAL 105 DAY, 'DEVUELTO', ''),
    (46, 10, 3, CURDATE() - INTERVAL 109 DAY, CURDATE() - INTERVAL 102 DAY, CURDATE() - INTERVAL 105 DAY, 'DEVUELTO', ''),
    (47, 26, 54, CURDATE() - INTERVAL 104 DAY, CURDATE() - INTERVAL 97 DAY, CURDATE() - INTERVAL 99 DAY, 'DEVUELTO', ''),
    (48, 45, 31, CURDATE() - INTERVAL 103 DAY, CURDATE() - INTERVAL 96 DAY, CURDATE() - INTERVAL 96 DAY, 'DEVUELTO', ''),
    (49, 35, 43, CURDATE() - INTERVAL 101 DAY, CURDATE() - INTERVAL 94 DAY, CURDATE() - INTERVAL 95 DAY, 'DEVUELTO', ''),
    (50, 46, 17, CURDATE() - INTERVAL 100 DAY, CURDATE() - INTERVAL 93 DAY, CURDATE() - INTERVAL 79 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 14 día(s) de atraso'),
    (51, 8, 48, CURDATE() - INTERVAL 100 DAY, CURDATE() - INTERVAL 93 DAY, CURDATE() - INTERVAL 95 DAY, 'DEVUELTO', ''),
    (52, 42, 40, CURDATE() - INTERVAL 98 DAY, CURDATE() - INTERVAL 91 DAY, CURDATE() - INTERVAL 95 DAY, 'DEVUELTO', ''),
    (53, 17, 34, CURDATE() - INTERVAL 96 DAY, CURDATE() - INTERVAL 89 DAY, CURDATE() - INTERVAL 93 DAY, 'DEVUELTO', ''),
    (54, 33, 8, CURDATE() - INTERVAL 93 DAY, CURDATE() - INTERVAL 86 DAY, CURDATE() - INTERVAL 88 DAY, 'DEVUELTO', ''),
    (55, 54, 48, CURDATE() - INTERVAL 91 DAY, CURDATE() - INTERVAL 84 DAY, CURDATE() - INTERVAL 84 DAY, 'DEVUELTO', ''),
    (56, 26, 51, CURDATE() - INTERVAL 90 DAY, CURDATE() - INTERVAL 83 DAY, CURDATE() - INTERVAL 88 DAY, 'DEVUELTO', ''),
    (57, 22, 3, CURDATE() - INTERVAL 87 DAY, CURDATE() - INTERVAL 80 DAY, CURDATE() - INTERVAL 70 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 10 día(s) de atraso'),
    (58, 34, 44, CURDATE() - INTERVAL 82 DAY, CURDATE() - INTERVAL 75 DAY, CURDATE() - INTERVAL 75 DAY, 'DEVUELTO', ''),
    (59, 8, 3, CURDATE() - INTERVAL 80 DAY, CURDATE() - INTERVAL 73 DAY, CURDATE() - INTERVAL 75 DAY, 'DEVUELTO', ''),
    (60, 26, 1, CURDATE() - INTERVAL 75 DAY, CURDATE() - INTERVAL 68 DAY, CURDATE() - INTERVAL 68 DAY, 'DEVUELTO', ''),
    (61, 22, 1, CURDATE() - INTERVAL 75 DAY, CURDATE() - INTERVAL 68 DAY, CURDATE() - INTERVAL 72 DAY, 'DEVUELTO', ''),
    (62, 48, 3, CURDATE() - INTERVAL 73 DAY, CURDATE() - INTERVAL 66 DAY, CURDATE() - INTERVAL 57 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 9 día(s) de atraso'),
    (63, 38, 11, CURDATE() - INTERVAL 71 DAY, CURDATE() - INTERVAL 64 DAY, CURDATE() - INTERVAL 68 DAY, 'DEVUELTO', ''),
    (64, 10, 2, CURDATE() - INTERVAL 70 DAY, CURDATE() - INTERVAL 63 DAY, CURDATE() - INTERVAL 67 DAY, 'DEVUELTO', ''),
    (65, 50, 44, CURDATE() - INTERVAL 69 DAY, CURDATE() - INTERVAL 62 DAY, CURDATE() - INTERVAL 68 DAY, 'DEVUELTO', ''),
    (66, 50, 32, CURDATE() - INTERVAL 68 DAY, CURDATE() - INTERVAL 61 DAY, CURDATE() - INTERVAL 63 DAY, 'DEVUELTO', ''),
    (67, 13, 12, CURDATE() - INTERVAL 68 DAY, CURDATE() - INTERVAL 61 DAY, CURDATE() - INTERVAL 66 DAY, 'DEVUELTO', ''),
    (68, 51, 39, CURDATE() - INTERVAL 67 DAY, CURDATE() - INTERVAL 60 DAY, CURDATE() - INTERVAL 62 DAY, 'DEVUELTO', ''),
    (69, 53, 29, CURDATE() - INTERVAL 66 DAY, CURDATE() - INTERVAL 59 DAY, CURDATE() - INTERVAL 61 DAY, 'DEVUELTO', ''),
    (70, 37, 41, CURDATE() - INTERVAL 65 DAY, CURDATE() - INTERVAL 58 DAY, CURDATE() - INTERVAL 60 DAY, 'DEVUELTO', ''),
    (71, 32, 21, CURDATE() - INTERVAL 65 DAY, CURDATE() - INTERVAL 58 DAY, CURDATE() - INTERVAL 63 DAY, 'DEVUELTO', ''),
    (72, 35, 31, CURDATE() - INTERVAL 63 DAY, CURDATE() - INTERVAL 56 DAY, CURDATE() - INTERVAL 57 DAY, 'DEVUELTO', ''),
    (73, 34, 35, CURDATE() - INTERVAL 57 DAY, CURDATE() - INTERVAL 50 DAY, CURDATE() - INTERVAL 53 DAY, 'DEVUELTO', ''),
    (74, 7, 20, CURDATE() - INTERVAL 56 DAY, CURDATE() - INTERVAL 49 DAY, CURDATE() - INTERVAL 49 DAY, 'DEVUELTO', ''),
    (75, 51, 9, CURDATE() - INTERVAL 56 DAY, CURDATE() - INTERVAL 49 DAY, CURDATE() - INTERVAL 52 DAY, 'DEVUELTO', ''),
    (76, 46, 26, CURDATE() - INTERVAL 51 DAY, CURDATE() - INTERVAL 44 DAY, CURDATE() - INTERVAL 46 DAY, 'DEVUELTO', ''),
    (77, 49, 10, CURDATE() - INTERVAL 50 DAY, CURDATE() - INTERVAL 43 DAY, CURDATE() - INTERVAL 48 DAY, 'DEVUELTO', ''),
    (78, 40, 58, CURDATE() - INTERVAL 50 DAY, CURDATE() - INTERVAL 43 DAY, CURDATE() - INTERVAL 47 DAY, 'DEVUELTO', ''),
    (79, 26, 49, CURDATE() - INTERVAL 49 DAY, CURDATE() - INTERVAL 42 DAY, CURDATE() - INTERVAL 29 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 13 día(s) de atraso'),
    (80, 48, 4, CURDATE() - INTERVAL 48 DAY, CURDATE() - INTERVAL 41 DAY, CURDATE() - INTERVAL 47 DAY, 'DEVUELTO', ''),
    (81, 30, 1, CURDATE() - INTERVAL 46 DAY, CURDATE() - INTERVAL 39 DAY, CURDATE() - INTERVAL 37 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 2 día(s) de atraso'),
    (82, 11, 17, CURDATE() - INTERVAL 46 DAY, CURDATE() - INTERVAL 39 DAY, CURDATE() - INTERVAL 41 DAY, 'DEVUELTO', ''),
    (83, 49, 21, CURDATE() - INTERVAL 42 DAY, CURDATE() - INTERVAL 35 DAY, CURDATE() - INTERVAL 37 DAY, 'DEVUELTO', ''),
    (84, 27, 20, CURDATE() - INTERVAL 42 DAY, CURDATE() - INTERVAL 35 DAY, CURDATE() - INTERVAL 30 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 5 día(s) de atraso'),
    (85, 33, 6, CURDATE() - INTERVAL 42 DAY, CURDATE() - INTERVAL 35 DAY, CURDATE() - INTERVAL 22 DAY, 'DEVUELTO_CON_ATRASO', 'Devuelto con 13 día(s) de atraso'),
    (86, 47, 22, CURDATE() - INTERVAL 40 DAY, CURDATE() - INTERVAL 33 DAY, CURDATE() - INTERVAL 34 DAY, 'DEVUELTO', ''),
    (87, 40, 32, CURDATE() - INTERVAL 38 DAY, CURDATE() - INTERVAL 31 DAY, CURDATE() - INTERVAL 37 DAY, 'DEVUELTO', ''),
    (88, 17, 31, CURDATE() - INTERVAL 37 DAY, CURDATE() - INTERVAL 30 DAY, CURDATE() - INTERVAL 32 DAY, 'DEVUELTO', ''),
    (89, 41, 1, CURDATE() - INTERVAL 32 DAY, CURDATE() - INTERVAL 25 DAY, CURDATE() - INTERVAL 26 DAY, 'DEVUELTO', ''),
    (90, 49, 27, CURDATE() - INTERVAL 29 DAY, CURDATE() - INTERVAL 22 DAY, NULL, 'ACTIVO', ''),
    (91, 31, 24, CURDATE() - INTERVAL 28 DAY, CURDATE() - INTERVAL 21 DAY, CURDATE() - INTERVAL 24 DAY, 'DEVUELTO', ''),
    (92, 48, 44, CURDATE() - INTERVAL 27 DAY, CURDATE() - INTERVAL 20 DAY, NULL, 'ACTIVO', ''),
    (93, 18, 20, CURDATE() - INTERVAL 26 DAY, CURDATE() - INTERVAL 19 DAY, NULL, 'ACTIVO', ''),
    (94, 24, 44, CURDATE() - INTERVAL 26 DAY, CURDATE() - INTERVAL 19 DAY, NULL, 'ACTIVO', ''),
    (95, 10, 8, CURDATE() - INTERVAL 24 DAY, CURDATE() - INTERVAL 17 DAY, NULL, 'ACTIVO', ''),
    (96, 16, 27, CURDATE() - INTERVAL 22 DAY, CURDATE() - INTERVAL 15 DAY, NULL, 'ACTIVO', ''),
    (97, 37, 7, CURDATE() - INTERVAL 22 DAY, CURDATE() - INTERVAL 15 DAY, NULL, 'ACTIVO', ''),
    (98, 40, 4, CURDATE() - INTERVAL 18 DAY, CURDATE() - INTERVAL 11 DAY, NULL, 'ACTIVO', ''),
    (99, 15, 10, CURDATE() - INTERVAL 18 DAY, CURDATE() - INTERVAL 11 DAY, NULL, 'ACTIVO', ''),
    (100, 40, 46, CURDATE() - INTERVAL 16 DAY, CURDATE() - INTERVAL 9 DAY, NULL, 'ACTIVO', ''),
    (101, 8, 15, CURDATE() - INTERVAL 13 DAY, CURDATE() - INTERVAL 6 DAY, NULL, 'ACTIVO', ''),
    (102, 10, 19, CURDATE() - INTERVAL 11 DAY, CURDATE() - INTERVAL 4 DAY, NULL, 'ACTIVO', ''),
    (103, 51, 5, CURDATE() - INTERVAL 11 DAY, CURDATE() - INTERVAL 4 DAY, NULL, 'ACTIVO', ''),
    (104, 17, 21, CURDATE() - INTERVAL 11 DAY, CURDATE() - INTERVAL 4 DAY, NULL, 'ACTIVO', ''),
    (105, 29, 5, CURDATE() - INTERVAL 9 DAY, CURDATE() - INTERVAL 2 DAY, NULL, 'ACTIVO', ''),
    (106, 29, 22, CURDATE() - INTERVAL 6 DAY, CURDATE() + INTERVAL 1 DAY, NULL, 'ACTIVO', ''),
    (107, 12, 21, CURDATE() - INTERVAL 6 DAY, CURDATE() + INTERVAL 1 DAY, NULL, 'ACTIVO', ''),
    (108, 51, 7, CURDATE() - INTERVAL 6 DAY, CURDATE() + INTERVAL 1 DAY, NULL, 'ACTIVO', ''),
    (109, 31, 30, CURDATE() - INTERVAL 5 DAY, CURDATE() + INTERVAL 2 DAY, NULL, 'ACTIVO', ''),
    (110, 27, 18, CURDATE() - INTERVAL 4 DAY, CURDATE() + INTERVAL 3 DAY, NULL, 'ACTIVO', ''),
    (111, 30, 45, CURDATE() - INTERVAL 4 DAY, CURDATE() + INTERVAL 3 DAY, NULL, 'ACTIVO', ''),
    (112, 44, 5, CURDATE() - INTERVAL 3 DAY, CURDATE() + INTERVAL 4 DAY, NULL, 'ACTIVO', ''),
    (113, 40, 52, CURDATE() - INTERVAL 3 DAY, CURDATE() + INTERVAL 4 DAY, NULL, 'ACTIVO', ''),
    (114, 32, 41, CURDATE() - INTERVAL 2 DAY, CURDATE() + INTERVAL 5 DAY, NULL, 'ACTIVO', ''),
    (115, 47, 44, CURDATE() - INTERVAL 2 DAY, CURDATE() + INTERVAL 5 DAY, NULL, 'ACTIVO', ''),
    (116, 32, 1, CURDATE() - INTERVAL 2 DAY, CURDATE() + INTERVAL 5 DAY, NULL, 'ACTIVO', ''),
    (117, 37, 26, CURDATE() - INTERVAL 2 DAY, CURDATE() + INTERVAL 5 DAY, NULL, 'ACTIVO', ''),
    (118, 15, 1, CURDATE() - INTERVAL 1 DAY, CURDATE() + INTERVAL 6 DAY, NULL, 'ACTIVO', ''),
    (119, 33, 17, CURDATE() - INTERVAL 1 DAY, CURDATE() + INTERVAL 6 DAY, NULL, 'ACTIVO', ''),
    (120, 24, 51, CURDATE() - INTERVAL 1 DAY, CURDATE() + INTERVAL 6 DAY, NULL, 'ACTIVO', ''),
    (121, 25, 2, CURDATE(), CURDATE() + INTERVAL 7 DAY, NULL, 'ACTIVO', ''),
    (122, 41, 48, CURDATE(), CURDATE() + INTERVAL 7 DAY, NULL, 'ACTIVO', ''),
    (123, 37, 28, CURDATE(), CURDATE() + INTERVAL 7 DAY, NULL, 'ACTIVO', ''),
    (124, 38, 54, CURDATE(), CURDATE() + INTERVAL 7 DAY, NULL, 'ACTIVO', '');

-- =====================================================================
--  Verificación rápida (debería mostrar los totales de cada tabla)
-- =====================================================================
SELECT 'personas' AS tabla, COUNT(*) AS registros FROM personas
UNION ALL SELECT 'usuarios', COUNT(*) FROM usuarios
UNION ALL SELECT 'estudiantes', COUNT(*) FROM estudiantes
UNION ALL SELECT 'bibliotecarios', COUNT(*) FROM bibliotecarios
UNION ALL SELECT 'categorias', COUNT(*) FROM categorias
UNION ALL SELECT 'libros', COUNT(*) FROM libros
UNION ALL SELECT 'prestamos', COUNT(*) FROM prestamos;

-- =====================================================================
--  Usuarios de prueba
-- =====================================================================
--  BIBLIOTECARIO femolinac1660111984          clave: biblio123  Fernanda Maite Molina Contreras 
--  BIBLIOTECARIO ambravos1849071966           clave: biblio123  Amaru Benjamín Bravo Soto 
--  BIBLIOTECARIO joreyesc1602011991           clave: biblio123  Joaquín Felipe Reyes Castro 
--  BIBLIOTECARIO bavalenzuelam1369011970      clave: biblio123  Bastián Daniel Valenzuela Muñoz 
--  BIBLIOTECARIO mapizarroh1569011989         clave: biblio123  Martín Ignacio Pizarro Herrera 
--  BIBLIOTECARIO radiazc9389021987            clave: biblio123  Rayén Josefa Díaz Catrileo 
--  ESTUDIANTE    nicortesh2423082013          clave: clave123   Nicolás Cortés Huenchumilla SEPTIMO_BASICO D
--  ESTUDIANTE    beherrerac2406112013         clave: clave123   Benjamín Alonso Herrera Carrasco SEPTIMO_BASICO G
--  ESTUDIANTE    gaespinozac2403082013        clave: clave123   Gaspar Vicente Espinoza Castillo SEPTIMO_BASICO A
--  ESTUDIANTE    trramirezp2419072013         clave: clave123   Trinidad Ramírez Pérez SEPTIMO_BASICO H
--  ESTUDIANTE    agsotog2476022014            clave: clave123   Agustín Benjamín Soto González SEPTIMO_BASICO E
--  ESTUDIANTE    canunezf2465032014           clave: clave123   Catalina Valentina Núñez Flores SEPTIMO_BASICO C
--  ESTUDIANTE    agfigueroam2443032014        clave: clave123   Agustín Ignacio Figueroa Martínez SEPTIMO_BASICO B
--  ESTUDIANTE    racastros2400052013          clave: clave123   Rayén Trinidad Castro Sepúlveda SEPTIMO_BASICO F
--  ESTUDIANTE    bacarrascof2347072012        clave: clave123   Bastián Martín Carrasco Fernández OCTAVO_BASICO B
--  ESTUDIANTE    bajarap2408022013            clave: clave123   Bastián Matías Jara Pizarro OCTAVO_BASICO A
--  ESTUDIANTE    ammolinal2366072012          clave: clave123   Amanda Molina López OCTAVO_BASICO C
--  ESTUDIANTE    gamartinezm2346122012        clave: clave123   Gaspar Martínez Morales OCTAVO_BASICO F
--  ESTUDIANTE    corodrigueza2334112012       clave: clave123   Constanza Javiera Rodríguez Araya OCTAVO_BASICO G
--  ESTUDIANTE    igherreraf2371052012         clave: clave123   Ignacio Martín Herrera Flores OCTAVO_BASICO D
--  ESTUDIANTE    dimunozr2391012013           clave: clave123   Diego Tomás Muñoz Reyes OCTAVO_BASICO H
--  ESTUDIANTE    dajarav2360082012            clave: clave123   Daniela Jara Vergara OCTAVO_BASICO E
--  ESTUDIANTE    tocontrerasc2318082011       clave: clave123   Tomás Contreras Cortés PRIMERO_MEDIO C
--  ESTUDIANTE    togonzaleza2282052011        clave: clave123   Tomás Matías González Álvarez PRIMERO_MEDIO A
--  ESTUDIANTE    agvergarar2286102011         clave: clave123   Agustina Rayén Vergara Reyes PRIMERO_MEDIO H
--  ESTUDIANTE    dacarcamog2273092011         clave: clave123   Daniela Antonia Cárcamo Gutiérrez PRIMERO_MEDIO G
--  ESTUDIANTE    beespinozap2372012012        clave: clave123   Benjamín Sebastián Espinoza Painemal PRIMERO_MEDIO D
--  ESTUDIANTE    malopezj2271052011           clave: clave123   Maite López Jara PRIMERO_MEDIO E
--  ESTUDIANTE    igmunozv2282102011           clave: clave123   Ignacia Agustina Muñoz Vásquez PRIMERO_MEDIO F
--  ESTUDIANTE    marodriguezn2311082011       clave: clave123   Maximiliano Rodríguez Núñez PRIMERO_MEDIO B
--  ESTUDIANTE    disotor2248092010            clave: clave123   Diego Daniel Soto Rodríguez SEGUNDO_MEDIO C
--  ESTUDIANTE    vigonzalezv2296032011        clave: clave123   Vicente González Vera SEGUNDO_MEDIO B
--  ESTUDIANTE    cogutierrezh2233102010       clave: clave123   Constanza Gutiérrez Hernández SEGUNDO_MEDIO A
--  ESTUDIANTE    bacarcamof2240062010         clave: clave123   Bastián Ignacio Cárcamo Figueroa SEGUNDO_MEDIO F
--  ESTUDIANTE    jaherrerah2234092010         clave: clave123   Javiera Herrera Huenchumilla SEGUNDO_MEDIO H
--  ESTUDIANTE    agcontrerasm2272012011       clave: clave123   Agustín Contreras Muñoz SEGUNDO_MEDIO D
--  ESTUDIANTE    tomolinas2253062010          clave: clave123   Tomás Bastián Molina Saavedra SEGUNDO_MEDIO G
--  ESTUDIANTE    dirojasn2284012011           clave: clave123   Diego Lucas Rojas Ñancupil SEGUNDO_MEDIO E
--  ESTUDIANTE    nimunozr2206042009           clave: clave123   Nicolás Ignacio Muñoz Rodríguez TERCERO_MEDIO F
--  ESTUDIANTE    igcatrileom2171092009        clave: clave123   Ignacia Catrileo Molina TERCERO_MEDIO B
--  ESTUDIANTE    damartinezc2173102009        clave: clave123   Daniela Martínez Contreras TERCERO_MEDIO D
--  ESTUDIANTE    masilvaj2233012010           clave: clave123   Martín Ignacio Silva Jara TERCERO_MEDIO G
--  ESTUDIANTE    issotoc2182092009            clave: clave123   Isidora Soto Carrasco TERCERO_MEDIO A
--  ESTUDIANTE    dipainemals2202072009        clave: clave123   Diego Painemal Saavedra TERCERO_MEDIO C
--  ESTUDIANTE    mareyesa2249012010           clave: clave123   Maximiliano Reyes Álvarez TERCERO_MEDIO E
--  ESTUDIANTE    trcastillof2229022010        clave: clave123   Trinidad Antonia Castillo Flores TERCERO_MEDIO H
--  ESTUDIANTE    fecarcamoa2116122008         clave: clave123   Felipe Diego Cárcamo Álvarez CUARTO_MEDIO D
--  ESTUDIANTE    sediazp2173032009            clave: clave123   Sebastián Díaz Pizarro CUARTO_MEDIO C
--  ESTUDIANTE    feramirezt2204032009         clave: clave123   Fernanda Amanda Ramírez Tapia CUARTO_MEDIO B
--  ESTUDIANTE    difernandezc2169032009       clave: clave123   Diego Joaquín Fernández Cortés CUARTO_MEDIO E
--  ESTUDIANTE    anfernandezf2142102008       clave: clave123   Antonia Emilia Fernández Fuentes CUARTO_MEDIO A
--  ESTUDIANTE    anrojasr2194032009           clave: clave123   Antonia Amanda Rojas Rodríguez CUARTO_MEDIO F
--  ESTUDIANTE    diarayac2106062008           clave: clave123   Diego Ignacio Araya Carrasco CUARTO_MEDIO G
--  ESTUDIANTE    selopezg2135072008           clave: clave123   Sebastián Alonso López Gutiérrez CUARTO_MEDIO H  [INACTIVO]
