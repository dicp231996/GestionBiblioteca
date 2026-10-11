package model.services;

import data.dao.EstudianteDAO;
import data.dao.PrestamoDAO;
import model.core.Persona;
import model.entities.Estudiante;
import model.enums.Permiso;
import model.exceptions.ReglaNegocioException;
import model.transactions.Prestamo;

import java.time.LocalDate;

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

    /** Registra la devolución con la fecha de hoy. */
    public synchronized Prestamo registrarDevolucion(Persona solicitante, int idPrestamo) {
        Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo)
                .orElseThrow(() -> new ReglaNegocioException("No existe el préstamo " + idPrestamo));
        Estudiante estudiante = prestamo.getEstudiante();
        ServicioGestion.verificarAccesoAEstudiante(solicitante, estudiante.getId(), Permiso.DEVOLVER_LIBRO);

        prestamo.registrarDevolucion(LocalDate.now());   // calcula atraso y deja constancia
        gestion.reponerStock(prestamo.getLibro().getId());
        estudiante.quitarLibroPrestado(prestamo.getLibro());
        estudianteDAO.actualizar(estudiante);
        prestamoDAO.actualizar(prestamo);
        return prestamo;
    }
}