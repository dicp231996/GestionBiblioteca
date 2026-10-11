package model.services;

import data.dao.EstudianteDAO;
import data.dao.PrestamoDAO;
import model.core.Persona;
import model.entities.Estudiante;
import model.enums.Permiso;
import model.transactions.Prestamo;

import java.time.LocalDate;
import java.util.List;

/** Devoluciones: cierra el préstamo, detecta atraso, repone stock y actualiza al estudiante. */
public class ServicioDevolucion {

    private final ServicioGestion gestion;
    private final EstudianteDAO estudianteDAO;
    private final PrestamoDAO prestamoDAO;

    public ServicioDevolucion(ServicioGestion gestion, EstudianteDAO estudianteDAO, PrestamoDAO prestamoDAO) {
        this.gestion = gestion;
        this.estudianteDAO = estudianteDAO;
        this.prestamoDAO = prestamoDAO;
    }

    /** Devolución con la fecha de hoy. */
    public Prestamo registrarDevolucion(Persona solicitante, int idPrestamo) {
        return procesar(solicitante, idPrestamo, LocalDate.now());
    }

    /**
     * Sobrecarga: el bibliotecario registra una devolución con otra fecha
     * (por ejemplo, un libro que dejaron ayer en el buzón).
     */
    public Prestamo registrarDevolucion(Persona solicitante, int idPrestamo, LocalDate fecha) {
        solicitante.exigirPermiso(Permiso.GESTIONAR_PRESTAMOS);
        return procesar(solicitante, idPrestamo, fecha);
    }

    private synchronized Prestamo procesar(Persona solicitante, int idPrestamo, LocalDate fecha) {
        Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo)
                .orElseThrow(() -> new ReglaNegocioException("No existe el préstamo " + idPrestamo));
        Estudiante estudiante = prestamo.getEstudiante();
        ServicioGestion.verificarAccesoAEstudiante(solicitante, estudiante.getId(), Permiso.DEVOLVER_LIBRO);

        prestamo.registrarDevolucion(fecha);          // calcula atraso y deja constancia
        gestion.reponerStock(prestamo.getLibro().getId());
        estudiante.quitarLibroPrestado(prestamo.getLibro());
        estudianteDAO.actualizar(estudiante);
        prestamoDAO.actualizar(prestamo);
        return prestamo;
    }

    public List<Prestamo> prestamosPendientesDe(Persona solicitante, int idEstudiante) {
        ServicioGestion.verificarAccesoAEstudiante(solicitante, idEstudiante,
                Permiso.CONSULTAR_PROPIOS_PRESTAMOS);
        return prestamoDAO.listarPorEstudiante(idEstudiante).stream()
                .filter(Prestamo::estaActivo)
                .toList();
    }
}