package model.services;

import data.dao.BibliotecarioDAO;
import data.dao.CategoriaDAO;
import data.dao.EstudianteDAO;
import data.dao.LibroDAO;
import data.dao.PrestamoDAO;
import model.classification.Categoria;
import model.core.Persona;
import model.entities.Bibliotecario;
import model.entities.Estudiante;
import model.enums.Permiso;
import model.interfaces.Registrable;
import model.resources.Libro;
import model.transactions.Prestamo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gestión de inventario (libros, categorías, stock) y de personas (estudiantes y bibliotecarios).
 * Cada operación recibe a quien la solicita y verifica su nivel de privilegio.
 */
public class ServicioGestion {

    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;
    private final EstudianteDAO estudianteDAO;
    private final BibliotecarioDAO bibliotecarioDAO;
    private final PrestamoDAO prestamoDAO;

    public ServicioGestion(LibroDAO libroDAO, CategoriaDAO categoriaDAO, EstudianteDAO estudianteDAO,
                           BibliotecarioDAO bibliotecarioDAO, PrestamoDAO prestamoDAO) {
        this.libroDAO = libroDAO;
        this.categoriaDAO = categoriaDAO;
        this.estudianteDAO = estudianteDAO;
        this.bibliotecarioDAO = bibliotecarioDAO;
        this.prestamoDAO = prestamoDAO;
    }

    // =================================================================== INVENTARIO

    public Libro registrarLibro(Persona solicitante, Libro libro) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_LIBROS);
        if (libroDAO.buscarPorIsbn(libro.getIsbn()).isPresent()) {
            throw new ReglaNegocioException("Ya existe un libro con ISBN " + libro.getIsbn());
        }
        return libroDAO.insertar(libro);
    }

    public boolean actualizarLibro(Persona solicitante, Libro libro) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_LIBROS);
        return libroDAO.actualizar(libro);
    }

    public boolean eliminarLibro(Persona solicitante, int idLibro) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_LIBROS);
        if (!prestamoDAO.listarActivosPorLibro(idLibro).isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar: el libro tiene préstamos activos");
        }
        return libroDAO.eliminar(idLibro);
    }

    public List<Libro> listarLibros(Persona solicitante) {
        solicitante.exigirPermiso(Permiso.CONSULTAR_CATALOGO);
        return libroDAO.listarTodos();
    }

    public List<Libro> buscarLibrosPorTitulo(Persona solicitante, String texto) {
        solicitante.exigirPermiso(Permiso.CONSULTAR_CATALOGO);
        return libroDAO.buscarPorTitulo(texto);
    }

    public Optional<Libro> buscarLibroPorIsbn(Persona solicitante, String isbn) {
        solicitante.exigirPermiso(Permiso.CONSULTAR_CATALOGO);
        return libroDAO.buscarPorIsbn(isbn);
    }

    public Categoria registrarCategoria(Persona solicitante, Categoria categoria) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_LIBROS);
        if (categoriaDAO.buscarPorNombre(categoria.getNombre()).isPresent()) {
            throw new ReglaNegocioException("La categoría '" + categoria + "' ya existe");
        }
        return categoriaDAO.insertar(categoria);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listarTodos();
    }

    // =================================================================== STOCK (sección crítica)

    /**
     * ÚNICO punto que descuenta stock. synchronized: si varios hilos piden el mismo libro a la vez,
     * entran de a uno, así nunca se presta un ejemplar que ya no existe (condición de carrera).
     * Es package-private: solo lo usan ServicioPrestamo y ServicioDevolucion.
     */
    synchronized Libro descontarStock(int idLibro) {
        Libro libro = libroDAO.buscarPorId(idLibro)
                .orElseThrow(() -> new ReglaNegocioException("No existe el libro con id " + idLibro));
        libro.descontarStock();          // valida disponibilidad y resta
        libroDAO.actualizar(libro);
        return libro;
    }

    /** Devuelve un ejemplar al stock. Mismo candado (this) que descontarStock. */
    synchronized Libro reponerStock(int idLibro) {
        Libro libro = libroDAO.buscarPorId(idLibro)
                .orElseThrow(() -> new ReglaNegocioException("No existe el libro con id " + idLibro));
        libro.reponerStock();
        libroDAO.actualizar(libro);
        return libro;
    }

    // =================================================================== ESTUDIANTES

    /** Registro hecho por un bibliotecario. */
    public Estudiante registrarEstudiante(Persona solicitante, Estudiante estudiante) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_ESTUDIANTES);
        validarNuevoRegistro(estudiante);
        return estudianteDAO.insertar(estudiante);
    }

    /** Autorregistro: el estudiante se registra por su cuenta desde la pantalla de login. */
    public Estudiante autoregistrarEstudiante(Estudiante estudiante) {
        validarNuevoRegistro(estudiante);
        return estudianteDAO.insertar(estudiante);
    }

    public boolean actualizarEstudiante(Persona solicitante, Estudiante estudiante) {
        // Un bibliotecario edita a cualquiera; un estudiante solo sus propios datos.
        if (!solicitante.tienePermiso(Permiso.GESTIONAR_ESTUDIANTES)
                && solicitante.getId() != estudiante.getId()) {
            throw new AccesoDenegadoException("Solo puedes editar tus propios datos");
        }
        return estudianteDAO.actualizar(estudiante);
    }

    public boolean eliminarEstudiante(Persona solicitante, int idEstudiante) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_ESTUDIANTES);
        Estudiante est = estudianteDAO.buscarPorId(idEstudiante)
                .orElseThrow(() -> new ReglaNegocioException("No existe el estudiante " + idEstudiante));
        if (est.tieneLibrosPrestados()) {
            throw new ReglaNegocioException("No se puede eliminar: el estudiante tiene libros sin devolver");
        }
        return estudianteDAO.eliminar(idEstudiante);
    }

    public List<Estudiante> listarEstudiantes(Persona solicitante) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_ESTUDIANTES);
        return estudianteDAO.listarTodos();
    }

    public Optional<Estudiante> buscarEstudiantePorRut(Persona solicitante, String rut) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_ESTUDIANTES);
        return estudianteDAO.buscarPorRut(rut);
    }

    public List<Estudiante> estudiantesConPrestamos(Persona solicitante) {
        return listarEstudiantes(solicitante).stream()
                .filter(Estudiante::tieneLibrosPrestados)
                .collect(Collectors.toList());
    }

    public List<Estudiante> estudiantesSinPrestamos(Persona solicitante) {
        return listarEstudiantes(solicitante).stream()
                .filter(e -> !e.tieneLibrosPrestados())
                .collect(Collectors.toList());
    }

    // =================================================================== PERSONAL

    public Bibliotecario registrarBibliotecario(Persona solicitante, Bibliotecario bibliotecario) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_PERSONAL);
        validarNuevoRegistro(bibliotecario);
        return bibliotecarioDAO.insertar(bibliotecario);
    }

    /** Solo se usa para crear el primer bibliotecario del sistema (cuando aún no hay nadie). */
    public Bibliotecario registrarAdministradorInicial(Bibliotecario bibliotecario) {
        if (!bibliotecarioDAO.listarTodos().isEmpty()) {
            throw new AccesoDenegadoException("Ya existe personal registrado");
        }
        validarNuevoRegistro(bibliotecario);
        return bibliotecarioDAO.insertar(bibliotecario);
    }

    public List<Bibliotecario> listarBibliotecarios(Persona solicitante) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_PERSONAL);
        return bibliotecarioDAO.listarTodos();
    }

    // =================================================================== PRÉSTAMOS (consulta)

    public List<Prestamo> listarPrestamosActivos(Persona solicitante) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_PRESTAMOS);
        return prestamoDAO.listarActivos();
    }

    public List<Prestamo> listarPrestamosAtrasados(Persona solicitante) {
        return listarPrestamosActivos(solicitante).stream()
                .filter(Prestamo::estaAtrasado)
                .collect(Collectors.toList());
    }

    // =================================================================== apoyo

    /**
     * Valida cualquier Persona que además sea Registrable (genérico con doble cota).
     * Las reglas concretas las pone cada subclase en validarDatosRegistro().
     */
    private <T extends Persona & Registrable> void validarNuevoRegistro(T persona) {
        List<String> errores = persona.validarDatosRegistro();
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException("Registro inválido: " + String.join("; ", errores));
        }
        if (estudianteDAO.buscarPorRut(persona.getRut()).isPresent()
                || bibliotecarioDAO.buscarPorRut(persona.getRut()).isPresent()) {
            throw new ReglaNegocioException("El RUT " + persona.getRut() + " ya está registrado");
        }
        if (estudianteDAO.buscarPorNombreUsuario(persona.getNombreUsuario()).isPresent()
                || bibliotecarioDAO.buscarPorNombreUsuario(persona.getNombreUsuario()).isPresent()) {
            throw new ReglaNegocioException("El usuario " + persona.getNombreUsuario() + " ya existe");
        }
    }

    /**
     * Regla compartida por préstamo y devolución: quien tiene GESTIONAR_PRESTAMOS opera sobre
     * cualquier estudiante; si no, solo sobre sí mismo y con el permiso "propio" indicado.
     */
    static void verificarAccesoAEstudiante(Persona solicitante, int idEstudiante, Permiso permisoPropio) {
        if (solicitante.tienePermiso(Permiso.GESTIONAR_PRESTAMOS)) return;
        solicitante.exigirPermiso(permisoPropio);
        if (solicitante.getId() != idEstudiante) {
            throw new AccesoDenegadoException("Solo puedes operar sobre tus propios préstamos");
        }
    }
}