package ui.controller;

import model.entities.Estudiante;
import model.resources.Libro;
import model.transactions.Prestamo;
import services.ReporteService;

import java.util.List;
import java.util.Map;

/** Reportes: libros más prestados, historial por estudiante y libros en préstamo. */
public class ReporteController {

    private final ReporteService reportes;
    private final Sesion sesion;

    public ReporteController(ReporteService reportes, Sesion sesion) {
        this.reportes = reportes;
        this.sesion = sesion;
    }

    public Map<Libro, Long> librosMasPrestados(int top) {
        return reportes.librosMasPrestados(sesion.getUsuarioActual(), top);
    }

    public List<Prestamo> historialPorEstudiante(Estudiante estudiante) {
        return reportes.historialPorEstudiante(sesion.getUsuarioActual(), estudiante.getId());
    }

    public List<Prestamo> librosEnPrestamo() {
        return reportes.librosActualmenteEnPrestamo(sesion.getUsuarioActual());
    }
}