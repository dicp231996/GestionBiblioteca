package data.mysql;

import data.dao.EstudianteDAO;
import model.entities.Estudiante;
import model.enums.Genero;
import model.enums.NivelEscolar;
import model.resources.Libro;
import model.valueobjects.Curso;
import util.ValidadorRut;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Un Estudiante se guarda en 3 tablas: personas (datos comunes), usuarios (credenciales)
 * y estudiantes (curso). Sus libros prestados se reconstruyen desde los préstamos activos.
 */
public class EstudianteDAOMySQL extends ConexionMySQL implements EstudianteDAO {

    private static final String SELECT = "SELECT " + PersonaSQL.COLUMNAS + ", e.nivel, e.letra "
            + "FROM personas p "
            + "JOIN estudiantes e ON e.id_persona = p.id_persona "
            + "JOIN usuarios u ON u.id_persona = p.id_persona";

    private static final String ORDEN = " ORDER BY p.apellido_paterno, p.apellido_materno, p.nombres";

    @Override
    public Estudiante insertar(Estudiante estudiante) {
        return enTransaccion("No se pudo registrar el estudiante", c -> {
            int id = PersonaSQL.insertar(c, estudiante);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO estudiantes (id_persona, nivel, letra) VALUES (?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setString(2, estudiante.getCurso().getNivel().name());
                ps.setString(3, String.valueOf(estudiante.getCurso().getLetra()));
                ps.executeUpdate();
            }
            estudiante.setId(id);
            return estudiante;
        });
    }

    @Override
    public boolean actualizar(Estudiante estudiante) {
        return enTransaccion("No se pudo actualizar el estudiante", c -> {
            boolean existe = PersonaSQL.actualizar(c, estudiante);
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE estudiantes SET nivel = ?, letra = ? WHERE id_persona = ?")) {
                ps.setString(1, estudiante.getCurso().getNivel().name());
                ps.setString(2, String.valueOf(estudiante.getCurso().getLetra()));
                ps.setInt(3, estudiante.getId());
                ps.executeUpdate();
            }
            return existe;
        });
    }

    @Override
    public boolean eliminar(int id) {
        return enTransaccion("No se pudo eliminar el estudiante", c -> PersonaSQL.eliminar(c, id));
    }

    @Override
    public Optional<Estudiante> buscarPorId(int id) {
        return primero(consultar(SELECT + " WHERE p.id_persona = ?", id));
    }

    @Override
    public Optional<Estudiante> buscarPorRut(String rut) {
        if (!ValidadorRut.esValido(rut)) return Optional.empty();
        return primero(consultar(SELECT + " WHERE p.rut = ?", ValidadorRut.normalizar(rut)));
    }

    @Override
    public Optional<Estudiante> buscarPorNombreUsuario(String nombreUsuario) {
        return primero(consultar(SELECT + " WHERE u.nombre_usuario = ?", nombreUsuario.trim()));
    }

    @Override
    public List<Estudiante> listarTodos() {
        return consultar(SELECT + ORDEN);
    }

    // ------------------------------------------------------------------ apoyo

    private List<Estudiante> consultar(String sql, Object... parametros) {
        List<Estudiante> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) ps.setObject(i + 1, parametros[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            cargarLibrosPrestados(lista);
            return lista;
        } catch (SQLException e) {
            throw error("No se pudieron consultar los estudiantes", e);
        }
    }

    /** Una sola consulta trae los libros en préstamo activo de todos los estudiantes de la lista. */
    private void cargarLibrosPrestados(List<Estudiante> estudiantes) throws SQLException {
        if (estudiantes.isEmpty()) return;
        Map<Integer, List<Libro>> porEstudiante = new HashMap<>();
        StringBuilder sql = new StringBuilder(LibroDAOMySQL.SELECT.replace("SELECT ", "SELECT pr.id_estudiante, "))
                .append(" JOIN prestamos pr ON pr.id_libro = l.id_libro")
                .append(" WHERE pr.estado = 'ACTIVO' AND pr.id_estudiante IN (");
        for (int i = 0; i < estudiantes.size(); i++) sql.append(i == 0 ? "?" : ", ?");
        sql.append(")");
        try (PreparedStatement ps = conexion().prepareStatement(sql.toString())) {
            for (int i = 0; i < estudiantes.size(); i++) ps.setInt(i + 1, estudiantes.get(i).getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    porEstudiante.computeIfAbsent(rs.getInt("id_estudiante"), k -> new ArrayList<>())
                            .add(LibroDAOMySQL.mapear(rs));
                }
            }
        }
        for (Estudiante e : estudiantes) {
            e.restaurarLibrosPrestados(porEstudiante.getOrDefault(e.getId(), List.of()));
        }
    }

    private static Estudiante mapear(ResultSet rs) throws SQLException {
        Curso curso = new Curso(NivelEscolar.valueOf(rs.getString("nivel")), rs.getString("letra").charAt(0));
        return new Estudiante(rs.getInt("id_persona"), rs.getString("nombres"), rs.getString("apellido_paterno"),
                rs.getString("apellido_materno"), rs.getDate("fecha_nacimiento").toLocalDate(),
                rs.getString("rut"), rs.getString("direccion"), rs.getString("nacionalidad"),
                Genero.valueOf(rs.getString("genero")), curso, PersonaSQL.leerUsuario(rs));
    }

    private static <T> Optional<T> primero(List<T> lista) {
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }
}