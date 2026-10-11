package model.services;

import data.dao.EstudianteDAO;
import data.dao.PrestamoDAO;
import model.core.Persona;
import model.entities.Estudiante;
import model.enums.Permiso;
import model.resources.Libro;
import model.transactions.Prestamo;

import java.time.LocalDate;
import java.util.List;

/**
 * Registro de préstamos: verifica disponibilidad, descuenta stock, crea el préstamo
 * y lo agrega al historial del estudiante.
 *
 * La pauta pide ejecutar esto en un hilo aparte para no congelar la interfaz:
 * eso se hará en el controlador con un SwingWorker que llame a registrarPrestamo().
 */
public class ServicioPrestamo {

    private final ServicioGestion gestion;
    private final EstudianteDAO estudianteDAO;
    private final PrestamoDAO prestamoDAO;

    public ServicioPrestamo(ServicioGestion gestion, EstudianteDAO estudianteDAO, PrestamoDAO prestamoDAO) {
        this.gestion = gestion;
        this.estudianteDAO = estudianteDAO;
        this.prestamoDAO = prestamoDAO;
    }

    /**
     * synchronized: el préstamo completo (validar -> descontar stock -> registrar) es atómico
     * respecto de otros préstamos simultáneos.
     */
    public synchronized Prestamo registrarPrestamo(Persona solicitante, int idEstudiante, int idLibro) {
        ServicioGestion.verificarAccesoAEstudiante(solicitante, idEstudiante, Permiso.SOLICITAR_PRESTAMO);

        Estudiante estudiante = estudianteDAO.buscarPorId(idEstudiante)
                .orElseThrow(() -> new ReglaNegocioException("No existe el estudiante " + idEstudiante));
        if (!estudiante.puedeSolicitarPrestamo()) {
            throw new ReglaNegocioException(estudiante.getNombreCompleto()
                    + " ya tiene el máximo de " + Estudiante.MAX_LIBROS_PRESTADOS + " libros");
        }
        if (estudiante.tieneLibroPrestado(idLibro)) {
            throw new ReglaNegocioException("El estudiante ya tiene un ejemplar de este libro");
        }

        Libro libro = gestion.descontarStock(idLibro); // lanza excepción si no hay stock
        try {
            estudiante.agregarLibroPrestado(libro);
            Prestamo prestamo = prestamoDAO.insertar(new Prestamo(estudiante, libro, LocalDate.now()));
            estudianteDAO.actualizar(estudiante);
            return prestamo;
        } catch (RuntimeException e) {
            // Compensación: si algo falla después de descontar, se devuelve el ejemplar.
            gestion.reponerStock(idLibro);
            estudiante.quitarLibroPrestado(libro);
            throw e;
        }
    }

    public List<Prestamo> historialDe(Persona solicitante, int idEstudiante) {
        ServicioGestion.verificarAccesoAEstudiante(solicitante, idEstudiante,
                Permiso.CONSULTAR_PROPIOS_PRESTAMOS);
        return prestamoDAO.listarPorEstudiante(idEstudiante);
    }
}