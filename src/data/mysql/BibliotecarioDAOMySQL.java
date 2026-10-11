package data.mysql;

import data.dao.BibliotecarioDAO;
import model.entities.Bibliotecario;
import model.enums.Genero;
import util.ValidadorRut;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Un Bibliotecario se guarda en personas + usuarios + bibliotecarios. */
public class BibliotecarioDAOMySQL extends ConexionMySQL implements BibliotecarioDAO {

    private static final String SELECT = "SELECT " + PersonaSQL.COLUMNAS + ", b.anio_contratacion "
            + "FROM personas p "
            + "JOIN bibliotecarios b ON b.id_persona = p.id_persona "
            + "JOIN usuarios u ON u.id_persona = p.id_persona";

    @Override
    public Bibliotecario insertar(Bibliotecario bibliotecario) {
        return enTransaccion("No se pudo registrar el bibliotecario", c -> {
            int id = PersonaSQL.insertar(c, bibliotecario);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO bibliotecarios (id_persona, anio_contratacion) VALUES (?, ?)")) {
                ps.setInt(1, id);
                ps.setInt(2, bibliotecario.getAnioContratacion());
                ps.executeUpdate();
            }
            bibliotecario.setId(id);
            return bibliotecario;
        });
    }

    @Override
    public boolean actualizar(Bibliotecario bibliotecario) {
        return enTransaccion("No se pudo actualizar el bibliotecario", c -> {
            boolean existe = PersonaSQL.actualizar(c, bibliotecario);
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE bibliotecarios SET anio_contratacion = ? WHERE id_persona = ?")) {
                ps.setInt(1, bibliotecario.getAnioContratacion());
                ps.setInt(2, bibliotecario.getId());
                ps.executeUpdate();
            }
            return existe;
        });
    }

    @Override
    public boolean eliminar(int id) {
        return enTransaccion("No se pudo eliminar el bibliotecario", c -> PersonaSQL.eliminar(c, id));
    }

    @Override
    public Optional<Bibliotecario> buscarPorId(int id) {
        return primero(consultar(SELECT + " WHERE p.id_persona = ?", id));
    }

    @Override
    public Optional<Bibliotecario> buscarPorRut(String rut) {
        if (!ValidadorRut.esValido(rut)) return Optional.empty();
        return primero(consultar(SELECT + " WHERE p.rut = ?", ValidadorRut.normalizar(rut)));
    }

    @Override
    public Optional<Bibliotecario> buscarPorNombreUsuario(String nombreUsuario) {
        return primero(consultar(SELECT + " WHERE u.nombre_usuario = ?", nombreUsuario.trim()));
    }

    @Override
    public List<Bibliotecario> listarTodos() {
        return consultar(SELECT + " ORDER BY p.apellido_paterno, p.nombres");
    }

    private List<Bibliotecario> consultar(String sql, Object... parametros) {
        List<Bibliotecario> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) ps.setObject(i + 1, parametros[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw error("No se pudieron consultar los bibliotecarios", e);
        }
    }

    private static Bibliotecario mapear(ResultSet rs) throws SQLException {
        return new Bibliotecario(rs.getInt("id_persona"), rs.getString("nombres"), rs.getString("apellido_paterno"),
                rs.getString("apellido_materno"), rs.getDate("fecha_nacimiento").toLocalDate(),
                rs.getString("rut"), rs.getString("direccion"), rs.getString("nacionalidad"),
                Genero.valueOf(rs.getString("genero")), rs.getInt("anio_contratacion"), PersonaSQL.leerUsuario(rs));
    }

    private static <T> Optional<T> primero(List<T> lista) {
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }
}