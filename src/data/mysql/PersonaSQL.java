package data.mysql;

import model.core.Persona;
import model.enums.Rol;
import model.security.Usuario;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQL compartido por EstudianteDAOMySQL y BibliotecarioDAOMySQL: las tablas personas y
 * usuarios guardan la parte común (la superclase Persona y su Usuario).
 */
final class PersonaSQL {

    /** Columnas comunes que deben venir en el SELECT de cada subclase. */
    static final String COLUMNAS = "p.id_persona, p.rut, p.nombres, p.apellido_paterno, p.apellido_materno, "
            + "p.fecha_nacimiento, p.direccion, p.nacionalidad, p.genero, "
            + "u.nombre_usuario, u.contrasena, u.rol, u.activo";

    private PersonaSQL() { }

    static Usuario leerUsuario(ResultSet rs) throws SQLException {
        return Usuario.desdeBaseDeDatos(rs.getString("nombre_usuario"), rs.getString("contrasena"),
                Rol.valueOf(rs.getString("rol")), rs.getBoolean("activo"));
    }

    /** Inserta en personas y usuarios; devuelve el id_persona generado. */
    static int insertar(Connection c, Persona p) throws SQLException {
        String sql = "INSERT INTO personas (rut, nombres, apellido_paterno, apellido_materno, fecha_nacimiento, "
                + "direccion, nacionalidad, genero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int id;
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(ps, p);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                id = claves.getInt(1);
            }
        }
        String sqlUsuario = "INSERT INTO usuarios (id_persona, nombre_usuario, contrasena, rol, activo) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sqlUsuario)) {
            Usuario u = p.getUsuario();
            ps.setInt(1, id);
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getRol().name());
            ps.setBoolean(5, u.isActivo());
            ps.executeUpdate();
        }
        return id;
    }

    /** Actualiza personas y usuarios. Devuelve true si la persona existía. */
    static boolean actualizar(Connection c, Persona p) throws SQLException {
        String sql = "UPDATE personas SET rut = ?, nombres = ?, apellido_paterno = ?, apellido_materno = ?, "
                + "fecha_nacimiento = ?, direccion = ?, nacionalidad = ?, genero = ? WHERE id_persona = ?";
        int filas;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            asignarDatos(ps, p);
            ps.setInt(9, p.getId());
            filas = ps.executeUpdate();
        }
        String sqlUsuario = "UPDATE usuarios SET nombre_usuario = ?, contrasena = ?, rol = ?, activo = ? "
                + "WHERE id_persona = ?";
        try (PreparedStatement ps = c.prepareStatement(sqlUsuario)) {
            Usuario u = p.getUsuario();
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getRol().name());
            ps.setBoolean(4, u.isActivo());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        }
        return filas > 0;
    }

    /** Borra la persona; usuarios y la fila de la subclase se borran en cascada (ON DELETE CASCADE). */
    static boolean eliminar(Connection c, int idPersona) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM personas WHERE id_persona = ?")) {
            ps.setInt(1, idPersona);
            return ps.executeUpdate() > 0;
        }
    }

    private static void asignarDatos(PreparedStatement ps, Persona p) throws SQLException {
        ps.setString(1, p.getRut());
        ps.setString(2, p.getNombres());
        ps.setString(3, p.getApellidoPaterno());
        ps.setString(4, p.getApellidoMaterno());
        ps.setDate(5, Date.valueOf(p.getFechaNacimiento()));
        ps.setString(6, p.getDireccion());
        ps.setString(7, p.getNacionalidad());
        ps.setString(8, p.getGenero().name());
    }
}