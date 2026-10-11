package services;

import data.dao.PrestamoDAO;
import model.core.Persona;
import model.enums.Permiso;
import model.resources.Libro;
import model.transactions.Prestamo;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reportes exigidos por la pauta. Por ahora se calculan con streams sobre el DAO;
 * con MySQL se pueden reemplazar por consultas SQL (GROUP BY / ORDER BY) en el DAO.
 */
public class ReporteService {

    private final PrestamoDAO prestamoDAO;

    public ReporteService(PrestamoDAO prestamoDAO) {
        this.prestamoDAO = prestamoDAO;
    }

    /** Libros más prestados (de mayor a menor), limitado a "top" resultados. */
    public Map<Libro, Long> librosMasPrestados(Persona solicitante, int top) {
        solicitante.exigirPermiso(Permiso.VER_REPORTES);
        return prestamoDAO.listarTodos().stream()
                .collect(Collectors.groupingBy(Prestamo::getLibro, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Libro, Long>comparingByValue().reversed())
                .limit(top)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public List<Prestamo> historialPorEstudiante(Persona solicitante, int idEstudiante) {
        solicitante.exigirPermiso(Permiso.VER_REPORTES);
        return prestamoDAO.listarPorEstudiante(idEstudiante).stream()
                .sorted(Comparator.comparing(Prestamo::getFechaPrestamo).reversed())
                .collect(Collectors.toList());
    }

    public List<Prestamo> librosActualmenteEnPrestamo(Persona solicitante) {
        solicitante.exigirPermiso(Permiso.VER_REPORTES);
        return prestamoDAO.listarActivos().stream()
                .sorted(Comparator.comparing(Prestamo::getFechaVencimiento))
                .collect(Collectors.toList());
    }

    /** Texto plano del reporte de más prestados (StringBuilder), útil para un JTextArea. */
    public String textoLibrosMasPrestados(Persona solicitante, int top) {
        StringBuilder sb = new StringBuilder("=== LIBROS MÁS PRESTADOS ===\n");
        int posicion = 1;
        for (Map.Entry<Libro, Long> e : librosMasPrestados(solicitante, top).entrySet()) {
            sb.append(posicion++).append(". ").append(e.getKey().getTitulo())
                    .append(" (").append(e.getValue()).append(" préstamos)\n");
        }
        return sb.toString();
    }
}