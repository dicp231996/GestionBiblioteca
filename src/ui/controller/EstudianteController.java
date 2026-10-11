package ui.controller;

import model.entities.Estudiante;
import model.services.ServicioGestion;
import model.services.ServicioPrestamo;
import model.transactions.Prestamo;

import java.util.List;

/** Gestión de estudiantes (solo bibliotecarios). */
public class EstudianteController {

    /** Filtro para ver qué estudiantes tienen libros prestados y cuáles no. */
    public enum Filtro {
        TODOS("Todos"), CON_PRESTAMOS("Con libros prestados"), SIN_PRESTAMOS("Sin libros prestados");

        private final String etiqueta;

        Filtro(String etiqueta) { this.etiqueta = etiqueta; }

        @Override
        public String toString() { return etiqueta; }
    }

    private final ServicioGestion gestion;
    private final ServicioPrestamo servicioPrestamo;
    private final Sesion sesion;

    public EstudianteController(ServicioGestion gestion, ServicioPrestamo servicioPrestamo, Sesion sesion) {
        this.gestion = gestion;
        this.servicioPrestamo = servicioPrestamo;
        this.sesion = sesion;
    }

    public List<Estudiante> listar(Filtro filtro) {
        switch (filtro) {
            case CON_PRESTAMOS: return gestion.estudiantesConPrestamos(sesion.getUsuarioActual());
            case SIN_PRESTAMOS: return gestion.estudiantesSinPrestamos(sesion.getUsuarioActual());
            default: return gestion.listarEstudiantes(sesion.getUsuarioActual());
        }
    }

    public Estudiante registrar(Estudiante estudiante) {
        return gestion.registrarEstudiante(sesion.getUsuarioActual(), estudiante);
    }

    public void actualizar(Estudiante estudiante) {
        gestion.actualizarEstudiante(sesion.getUsuarioActual(), estudiante);
    }

    public void eliminar(Estudiante estudiante) {
        gestion.eliminarEstudiante(sesion.getUsuarioActual(), estudiante.getId());
    }

    public List<Prestamo> historial(Estudiante estudiante) {
        return servicioPrestamo.historialDe(sesion.getUsuarioActual(), estudiante.getId());
    }
}