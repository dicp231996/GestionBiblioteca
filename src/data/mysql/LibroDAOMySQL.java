package data.mysql;

import data.dao.LibroDAO;
import model.classification.Categoria;
import model.enums.EstadoLibro;
import model.resources.Libro;
import util.ValidadorIsbn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroDAOMySQL extends ConexionMySQL implements LibroDAO {

    /** SELECT con la categoría incluida (JOIN), reutilizado por otros DAO. */
    static final String SELECT = "SELECT l.id_libro, l.titulo, l.autor, l.editorial, l.isbn, l.cantidad_paginas, "
            + "l.stock, l.estado, c.id_categoria, c.nombre AS categoria_nombre, c.descripcion AS categoria_descripcion "
            + "FROM libros l JOIN categorias c ON c.id_categoria = l.id_categoria";

    @Override
    public Libro insertar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, editorial, id_categoria, isbn, cantidad_paginas, stock, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(ps, libro);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) libro.setId(claves.getInt(1));
            }
            return libro;
        } catch (SQLException e) {
            throw error("No se pudo registrar el libro", e);
        }
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, editorial = ?, id_categoria = ?, isbn = ?, "
                + "cantidad_paginas = ?, stock = ?, estado = ? WHERE id_libro = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            asignarDatos(ps, libro);
            ps.setInt(9, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw error("No se pudo actualizar el libro", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM libros WHERE id_libro = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw error("No se pudo eliminar el libro", e);
        }
    }

    @Override
    public Optional<Libro> buscarPorId(int id) {
        List<Libro> r = consultar(SELECT + " WHERE l.id_libro = ?", id);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        List<Libro> r = consultar(SELECT + " WHERE l.isbn = ?", ValidadorIsbn.limpiar(isbn));
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override
    public List<Libro> buscarPorTitulo(String texto) {
        return consultar(SELECT + " WHERE l.titulo LIKE ? ORDER BY l.titulo", "%" + texto.trim() + "%");
    }

    @Override
    public List<Libro> listarTodos() {
        return consultar(SELECT + " ORDER BY l.titulo");
    }

    private List<Libro> consultar(String sql, Object... parametros) {
        List<Libro> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) ps.setObject(i + 1, parametros[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw error("No se pudieron consultar los libros", e);
        }
    }

    /** Convierte una fila del SELECT en un Libro (también lo usa EstudianteDAOMySQL). */
    static Libro mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("categoria_nombre"),
                rs.getString("categoria_descripcion"));
        return new Libro(rs.getInt("id_libro"), rs.getString("titulo"), rs.getString("autor"),
                rs.getString("editorial"), categoria, rs.getString("isbn"), rs.getInt("cantidad_paginas"),
                rs.getInt("stock"), EstadoLibro.valueOf(rs.getString("estado")));
    }

    private static void asignarDatos(PreparedStatement ps, Libro l) throws SQLException {
        ps.setString(1, l.getTitulo());
        ps.setString(2, l.getAutor());
        ps.setString(3, l.getEditorial());
        ps.setInt(4, l.getCategoria().getId());
        ps.setString(5, l.getIsbn());
        ps.setInt(6, l.getCantidadPaginas());
        ps.setInt(7, l.getStock());
        ps.setString(8, l.getEstado().name());
    }
}