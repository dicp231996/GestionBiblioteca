package model.entities;

import model.core.Persona;
import model.enums.Genero;
import model.enums.Rol;
import model.exceptions.ReglaNegocioException;
import model.interfaces.Registrable;
import model.resources.Libro;
import model.security.Usuario;
import model.valueobjects.Curso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Estudiante del liceo. Hereda de Persona y además implementa Registrable
 * (puede registrarse por su cuenta o ser registrado por un bibliotecario).
 */
public final class Estudiante extends Persona implements Registrable {

    public static final int MAX_LIBROS_PRESTADOS = 3;
    public static final int EDAD_MINIMA = 10;
    public static final int EDAD_MAXIMA = 21;

    private Curso curso;
    // ArrayList y no un array fijo: la cantidad de libros prestados cambia en el tiempo.
    private final List<Libro> librosPrestados = new ArrayList<>();

    /** Estudiante nuevo: genera su usuario a partir de sus datos. */
    public Estudiante(String nombres, String apellidoPaterno, String apellidoMaterno,
                      LocalDate fechaNacimiento, String rut, String direccion,
                      String nacionalidad, Genero genero, Curso curso, String contrasena) {
        super(nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, rut,
                direccion, nacionalidad, genero);
        setCurso(curso);
        setUsuario(Usuario.crear(generarNombreUsuario(), contrasena, Rol.ESTUDIANTE));
    }

    /** Estudiante leído desde la BD (sobrecarga de constructor). */
    public Estudiante(int id, String nombres, String apellidoPaterno, String apellidoMaterno,
                      LocalDate fechaNacimiento, String rut, String direccion,
                      String nacionalidad, Genero genero, Curso curso, Usuario usuario) {
        super(id, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, rut,
                direccion, nacionalidad, genero, usuario);
        setCurso(curso);
    }

    /** Usuario de estudiante: base + últimos 3 dígitos del RUT. Ej: "jperezg678". */
    @Override
    protected String generarNombreUsuario() {
        String cuerpoRut = getRut().split("-")[0];
        return baseNombreUsuario() + cuerpoRut.substring(cuerpoRut.length() - 3);
    }

    // ------------------------------------------------------------------ préstamos
    // synchronized: préstamos y devoluciones pueden tocar esta lista desde hilos distintos.

    public synchronized boolean tieneLibrosPrestados() {
        return !librosPrestados.isEmpty();
    }

    public synchronized int cantidadLibrosPrestados() {
        return librosPrestados.size();
    }

    public synchronized boolean puedeSolicitarPrestamo() {
        return getUsuario().isActivo() && librosPrestados.size() < MAX_LIBROS_PRESTADOS;
    }

    public synchronized boolean tieneLibroPrestado(int idLibro) {
        return librosPrestados.stream().anyMatch(l -> l.getId() == idLibro);
    }

    public synchronized void agregarLibroPrestado(Libro libro) {
        Objects.requireNonNull(libro);
        if (!puedeSolicitarPrestamo()) {
            throw new ReglaNegocioException(getNombreCompleto()
                    + " alcanzó el máximo de " + MAX_LIBROS_PRESTADOS + " libros prestados");
        }
        librosPrestados.add(libro);
    }

    public synchronized boolean quitarLibroPrestado(Libro libro) {
        return librosPrestados.removeIf(l -> l.getId() == libro.getId());
    }

    /** Lista de solo lectura: se modifica únicamente con agregar/quitar. */
    public List<Libro> getLibrosPrestados() {
        return Collections.unmodifiableList(librosPrestados);
    }

    // ------------------------------------------------------------------ registro

    @Override
    public List<String> validarDatosRegistro() {
        List<String> errores = new ArrayList<>();
        int edad = getEdad();
        if (edad < EDAD_MINIMA || edad > EDAD_MAXIMA) {
            errores.add("La edad de un estudiante debe estar entre " + EDAD_MINIMA
                    + " y " + EDAD_MAXIMA + " años (tiene " + edad + ")");
        }
        if (curso == null) {
            errores.add("El estudiante debe tener un curso asignado");
        }
        return errores;
    }

    // ------------------------------------------------------------------ información

    /** Solo la parte académica de la ficha. */
    public String datosEstudiante() {
        StringBuilder sb = new StringBuilder();
        sb.append("Curso           : ").append(curso).append('\n')
                .append("Libros prestados: ").append(librosPrestados.size())
                .append(" de ").append(MAX_LIBROS_PRESTADOS).append('\n');
        for (Libro libro : librosPrestados) {
            sb.append("   - ").append(libro.getTitulo()).append('\n');
        }
        return sb.toString();
    }

    /** Sobrescritura polimórfica: datos comunes (super) + datos académicos. */
    @Override
    public String datosPersonales() {
        return new StringBuilder("===== FICHA ESTUDIANTE =====\n")
                .append(super.datosPersonales())
                .append(datosEstudiante())
                .toString();
    }

    // ------------------------------------------------------------------ get / set

    public Curso getCurso() { return curso; }

    public void setCurso(Curso curso) {
        this.curso = Objects.requireNonNull(curso, "El curso es obligatorio");
    }
}