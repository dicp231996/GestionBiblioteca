package data.mysql;

import data.dao.CategoriaDAO;
import model.classification.Categoria;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDAOMySQL extends ConexionMySQL implements CategoriaDAO {

    private static final String SELECT = "SELECT id_categoria, nombre, descripcion FROM categorias";

    @Override
    public Categoria insertar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) categoria.setId(claves.getInt(1));
            }
            return categoria;
        } catch (SQLException e) {
            throw error("No se pudo registrar la categoría", e);
        }
    }

    @Override
    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id_categoria = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw error("No se pudo actualizar la categoría", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM categorias WHERE id_categoria = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw error("No se pudo eliminar la categoría (¿tiene libros asociados?)", e);
        }
    }

    @Override
    public Optional<Categoria> buscarPorId(int id) {
        return buscarUno(SELECT + " WHERE id_categoria = ?", id);
    }

    @Override
    public Optional<Categoria> buscarPorNombre(String nombre) {
        return buscarUno(SELECT + " WHERE nombre = ?", nombre.trim());
    }

    @Override
    public List<Categoria> listarTodos() {
        List<Categoria> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(SELECT + " ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw error("No se pudieron listar las categorías", e);
        }
    }

    private Optional<Categoria> buscarUno(String sql, Object parametro) {
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw error("No se pudo buscar la categoría", e);
        }
    }

    private static Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id_categoria"), rs.getString("nombre"), rs.getString("descripcion"));
    }
}