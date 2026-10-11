# Sistema de Gestión de Biblioteca Escolar

Aplicación de escritorio en **Java (Swing)** con base de datos **MySQL** para administrar la biblioteca de un colegio: libros, categorías, estudiantes, préstamos, devoluciones y reportes.

Proyecto de la Evaluación Final Transversal de **Desarrollo Orientado a Objetos II (PRY2203)**, Duoc UC.

---

## Funcionalidades

| Módulo | Qué permite |
|---|---|
| **Inicio de sesión** | Ingreso de bibliotecarios y estudiantes. Cada rol ve solo las opciones que tiene permitidas. Los estudiantes también pueden registrarse por su cuenta. |
| **Libros** | CRUD completo: registrar, listar, buscar por título, editar y eliminar. Filtro "solo disponibles". |
| **Categorías** | CRUD completo desde el botón *Categorías* del inventario. |
| **Estudiantes** | CRUD completo, con filtro por estudiantes con o sin préstamos y una ficha con su historial. |
| **Préstamos** | Registro del préstamo en un hilo en segundo plano y registro de la devolución. Los préstamos atrasados se marcan en rojo. |
| **Reportes** | Libros más prestados, libros actualmente en préstamo e historial por estudiante. |
| **Vista del estudiante** | Catálogo, solicitud de préstamos y consulta de sus propios préstamos. |

### Reglas de negocio principales

- Un estudiante puede tener como máximo **3 libros** prestados a la vez, y no puede pedir dos veces el mismo libro.
- El plazo de devolución es de **7 días**. Si se devuelve después, el préstamo queda como *devuelto con atraso*.
- No se presta un libro sin stock o que no esté en estado *Activo*.
- No se puede eliminar un libro o un estudiante que tenga historial de préstamos, para no perder ese historial. En el caso del libro, se le cambia el estado a *Dado de baja*.
- No se puede eliminar una categoría que tenga libros asociados, ni crear dos categorías con el mismo nombre.
- El RUT se valida por formato (`12.345.678-K`, `12345678-K` o `12345678K`).

---

## Solución técnica

### Programación orientada a objetos

- **Herencia y clase abstracta:** `Persona` es abstracta y reúne los datos comunes: nombres, apellidos, RUT, fecha de nacimiento, dirección, nacionalidad y género. La edad se calcula a partir de la fecha de nacimiento. `Estudiante` y `Bibliotecario` heredan de ella.
- **Polimorfismo:** cada subclase sobrescribe `datosPersonales()`. El estudiante agrega su curso y sus libros prestados; el bibliotecario agrega su año de contratación.
- **Interfaces:**
  - `Registrable` define cómo se valida el registro de cada persona.
  - `Autenticable` define el inicio de sesión y los permisos, con métodos `default`.
  - `Identificable` define el id de las entidades persistidas.
- **Encapsulamiento:** los atributos son privados y los *setters* validan los datos antes de asignarlos.
- **Sobrecarga:** hay constructores para objetos nuevos y para objetos que vienen de la base de datos. `GeneradorUsuario.generar(...)` también está sobrecargado.
- **Roles y permisos:** el enum `Rol` contiene un `EnumSet` de `Permiso`. Cada servicio verifica el permiso antes de operar.

### Patrones de diseño

| Patrón | Dónde | Para qué |
|---|---|---|
| **MVC** | `model` (modelo), `ui.panels` y `ui.dialogs` (vista), `ui.controller` (controlador) | La vista nunca accede directo a la base de datos. El controlador recibe la acción, llama al servicio y devuelve el resultado. |
| **DAO** | `data.dao` (interfaces) y `data.mysql` (implementaciones) | `CrudDAO<T>` define `insertar`, `actualizar`, `eliminar`, `buscarPorId` y `listarTodos`. Los servicios dependen de la interfaz, no de MySQL. |
| **Singleton** | `data.DatabaseConnection` | El constructor es privado y `getInstance()` devuelve siempre la misma instancia, así que toda la aplicación comparte una sola conexión. |

### Concurrencia

- **Hilo en segundo plano:** registrar un préstamo o una devolución se ejecuta con `SwingWorker` (`PrestamoController`). El trabajo pesado corre fuera del hilo de la interfaz (EDT), así la ventana no se congela. En la consola se muestra el nombre del hilo que ejecuta la operación.
- **Sincronización:**
  - `ServicioGestion.descontarStock` y `reponerStock` son `synchronized`. Si varios préstamos del mismo libro llegan al mismo tiempo, entran de a uno y nunca se presta un ejemplar que ya no existe.
  - `ServicioPrestamo.registrarPrestamo` y `ServicioDevolucion.registrarDevolucion` también están sincronizados.
  - En la capa de datos, las operaciones que tocan varias tablas usan transacciones JDBC (`commit` / `rollback`).

### Base de datos

La herencia se representa con **una tabla por clase**:

- `personas` guarda los datos comunes.
- `estudiantes` y `bibliotecarios` guardan los datos propios de cada uno y se enlazan con `personas` por clave foránea.
- `usuarios` guarda las credenciales y el rol.
- `categorias`, `libros` y `prestamos` completan el modelo.

Los reportes se apoyan en las vistas `v_libros_mas_prestados`, `v_libros_en_prestamo` y `v_historial_estudiante`.

---

## Estructura de carpetas

```
BibliotecaEscolar/
├── README.md
├── sql/
│   ├── 01_crea_tablas_biblioteca.sql      # crea la BD, tablas, restricciones y vistas
│   └── 02_poblado_tablas_biblioteca.sql   # datos de prueba (usuarios, libros, préstamos)
└── src/
    ├── db.properties                      # URL, usuario y contraseña de MySQL
    ├── app/
    │   └── Biblioteca.java                # clase main: arma DAOs → servicios → controladores
    ├── model/                             # MODELO
    │   ├── core/          Persona (clase abstracta)
    │   ├── entities/      Estudiante, Bibliotecario (subclases de Persona)
    │   ├── interfaces/    Registrable, Autenticable, Identificable
    │   ├── resources/     Libro
    │   ├── classification/ Categoria
    │   ├── transactions/  Prestamo
    │   ├── valueobjects/  Curso (nivel + letra)
    │   ├── security/      Usuario, GeneradorUsuario
    │   ├── enums/         Rol, Permiso, Genero, NivelEscolar, EstadoLibro, EstadoPrestamo
    │   ├── exceptions/    ReglaNegocioException, AccesoDenegadoException
    │   └── services/      ServicioGestion, ServicioPrestamo, ServicioDevolucion
    ├── services/                          # servicios de apoyo a la UI
    │   ├── AutenticacionService.java
    │   └── ReporteService.java
    ├── data/                              # ACCESO A DATOS
    │   ├── DatabaseConnection.java        # Singleton
    │   ├── PersistenciaException.java
    │   ├── dao/           CrudDAO<T> y una interfaz DAO por entidad
    │   └── mysql/         implementaciones JDBC de cada DAO
    ├── ui/                                # VISTA + CONTROLADOR
    │   ├── LoginFrame.java, MainFrame.java
    │   ├── controller/    LoginController, LibroController, EstudianteController,
    │   │                  PrestamoController (SwingWorker), ReporteController, Sesion
    │   ├── panels/        CatalogoPanel, EstudiantesPanel, PrestamosPanel,
    │   │                  MisPrestamosPanel, ReportesPanel
    │   ├── dialogs/       LibroDialog, EstudianteDialog, CategoriasDialog, FichaDialog
    │   └── components/    tabla genérica, formularios, mensajes, barra de estado
    └── util/              ValidadorRut, ValidadorIsbn, FormatoFecha, TextoUtil
```

---

## Requisitos

- JDK 17 o superior (desarrollado con JDK 23).
- MySQL 8 y MySQL Workbench.
- MySQL Connector/J 8.4.0. En IntelliJ se agrega en *Project Structure → Libraries → + → From Maven*, buscando `com.mysql:mysql-connector-j:8.4.0`.

## Instalación y ejecución

1. **Base de datos:** en MySQL Workbench, ejecuta `sql/01_crea_tablas_biblioteca.sql` y luego `sql/02_poblado_tablas_biblioteca.sql`.
2. **Conexión:** edita `src/db.properties` con tus datos:
   ```properties
   db.url=jdbc:mysql://localhost:3306/biblioteca_escolar?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
   db.usuario=root
   db.contrasena=TU_CONTRASEÑA
   ```
   > No subas tu contraseña real al repositorio.
3. **Ejecutar:**
   - desde IntelliJ, ejecuta `app.Biblioteca`;
   - o con el ejecutable: `java -jar BibliotecaEscolar.jar`.

Si la conexión falla, la aplicación muestra un mensaje con la causa probable: el servidor está apagado, las credenciales son incorrectas o falta el driver.

## Usuarios de prueba

| Rol | Usuario | Contraseña |
|---|---|---|
| Bibliotecario | `femolinac1660111984` | `biblio123` |
| Estudiante | `nicortesh2423082013` | `clave123` |

Todos los bibliotecarios usan `biblio123` y todos los estudiantes usan `clave123`. La lista completa está al final de `02_poblado_tablas_biblioteca.sql`.

El nombre de usuario se genera automáticamente con:

- las 2 primeras letras del primer nombre;
- el apellido paterno;
- la inicial del apellido materno;
- los 4 primeros dígitos del RUT;
- el mes y el año de nacimiento.

Por ejemplo, *Juan Pablo Pérez González*, RUT 21.345.678-4, nacido el 02-06-2011 → `juperezg2134062011`.

---

## Autor

Daniel. Desarrollo Orientado a Objetos II, Duoc UC, 2026.
