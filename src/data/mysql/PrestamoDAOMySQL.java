package data.mysql;

import data.dao.EstudianteDAO;
import data.dao.LibroDAO;
import data.dao.PrestamoDAO;
import model.entities.Estudiante;
import model.enums.EstadoPrestamo;
import model.resources.Libro;
import model.transactions.Prestamo;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Préstamos. Para armar cada Prestamo se necesitan su Estudiante y su Libro, que se piden a
 * sus propios DAO (cada DAO conoce solo su tabla; aquí se reutilizan, no se duplica el SQL).
 */
public class PrestamoDAOMySQL extends ConexionMySQL implements PrestamoDAO {

    private static final String SELECT = "SELECT id_prestamo, id_estudiante, id_libro, fecha_prestamo, "
            + "fecha_vencimiento, fecha_devolucion, estado, observacion FROM prestamos";

    private final EstudianteDAO estudianteDAO;
    private final LibroDAO libroDAO;

    public PrestamoDAOMySQL(EstudianteDAO estudianteDAO, LibroDAO libroDAO) {
        this.estudianteDAO = estudianteDAO;
        this.libroDAO = libroDAO;
    }

    @Override
    public Prestamo insertar(Prestamo p) {
        String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_vencimiento, "
                + "fecha_devolucion, estado, observacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getEstudiante().getId());
            ps.setInt(2, p.getLibro().getId());
            ps.setDate(3, Date.valueOf(p.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(p.getFechaVencimiento()));
            ps.setDate(5, p.getFechaDevolucion() == null ? null : Date.valueOf(p.getFechaDevolucion()));
            ps.setString(6, p.getEstado().name());
            ps.setString(7, p.getObservacion());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) p.setId(claves.getInt(1));
            }
            return p;
        } catch (SQLException e) {
            throw error("No se pudo registrar el préstamo", e);
        }
    }

    /** Solo cambia lo que puede cambiar en un préstamo: la devolución, el estado y la observación. */
    @Override
    public boolean actualizar(Prestamo p) {
        String sql = "UPDATE prestamos SET fecha_devolucion = ?, estado = ?, observacion = ? WHERE id_prestamo = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setDate(1, p.getFechaDevolucion() == null ? null : Date.valueOf(p.getFechaDevolucion()));
            ps.setString(2, p.getEstado().name());
            ps.setString(3, p.getObservacion());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw error("No se pudo actualizar el préstamo", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM prestamos WHERE id_prestamo = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw error("No se pudo eliminar el préstamo", e);
        }
    }

    @Override
    public Optional<Prestamo> buscarPorId(int id) {
        List<Prestamo> r = consultar(SELECT + " WHERE id_prestamo = ?", id);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override
    public List<Prestamo> listarTodos() {
        return consultar(SELECT + " ORDER BY fecha_prestamo DESC, id_prestamo DESC");
    }

    @Override
    public List<Prestamo> listarActivos() {
        return consultar(SELECT + " WHERE estado = 'ACTIVO' ORDER BY fecha_vencimiento");
    }

    @Override
    public List<Prestamo> listarPorEstudiante(int idEstudiante) {
        return consultar(SELECT + " WHERE id_estudiante = ? ORDER BY fecha_prestamo DESC", idEstudiante);
    }

    @Override
    public List<Prestamo> listarActivosPorLibro(int idLibro) {
        return consultar(SELECT + " WHERE estado = 'ACTIVO' AND id_libro = ?", idLibro);
    }

    @Override
    public boolean existenPrestamosDeLibro(int idLibro) {
        try (PreparedStatement ps = conexion().prepareStatement(
                "SELECT 1 FROM prestamos WHERE id_libro = ? LIMIT 1")) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw error("No se pudo consultar el historial del libro", e);
        }
    }

    // ------------------------------------------------------------------ apoyo

    /** Fila leída de la tabla, antes de resolver el Estudiante y el Libro. */
    private record Fila(int id, int idEstudiante, int idLibro, LocalDate prestamo, LocalDate vencimiento,
                        LocalDate devolucion, EstadoPrestamo estado, String observacion) { }

    private List<Prestamo> consultar(String sql, Object... parametros) {
        List<Fila> filas = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) ps.setObject(i + 1, parametros[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date devolucion = rs.getDate("fecha_devolucion");
                    filas.add(new Fila(rs.getInt("id_prestamo"), rs.getInt("id_estudiante"), rs.getInt("id_libro"),
                            rs.getDate("fecha_prestamo").toLocalDate(), rs.getDate("fecha_vencimiento").toLocalDate(),
                            devolucion == null ? null : devolucion.toLocalDate(),
                            EstadoPrestamo.valueOf(rs.getString("estado")), rs.getString("observacion")));
                }
            }
        } catch (SQLException e) {
            throw error("No se pudieron consultar los préstamos", e);
        }

        // Cachés: si un estudiante o libro aparece en varias filas, se carga una sola vez.
        Map<Integer, Estudiante> estudiantes = new HashMap<>();
        Map<Integer, Libro> libros = new HashMap<>();
        List<Prestamo> resultado = new ArrayList<>();
        for (Fila f : filas) {
            Estudiante est = estudiantes.computeIfAbsent(f.idEstudiante(), id -> estudianteDAO.buscarPorId(id)
                    .orElseThrow(() -> new IllegalStateException("Préstamo sin estudiante: " + f.id())));
            Libro libro = libros.computeIfAbsent(f.idLibro(), id -> libroDAO.buscarPorId(id)
                    .orElseThrow(() -> new IllegalStateException("Préstamo sin libro: " + f.id())));
            resultado.add(new Prestamo(f.id(), est, libro, f.prestamo(), f.vencimiento(), f.devolucion(),
                    f.estado(), f.observacion()));
        }
        return resultado;
    }
}