package data.mysql;

import data.DatabaseConnection;
import data.PersistenciaException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base de los DAO MySQL: obtiene la conexión única (Singleton) y ofrece un método para
 * ejecutar varias sentencias como una sola transacción (todo o nada).
 */
abstract class ConexionMySQL {

    private static final Logger LOGGER = Logger.getLogger(ConexionMySQL.class.getName());

    protected Connection conexion() {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw error("No se pudo conectar con la base de datos", e);
        }
    }

    protected PersistenciaException error(String mensaje, SQLException e) {
        LOGGER.log(Level.SEVERE, mensaje, e);
        return new PersistenciaException(mensaje + ": " + e.getMessage(), e);
    }

    /** Operación JDBC que puede lanzar SQLException (para usar con lambdas). */
    @FunctionalInterface
    protected interface OperacionSQL<T> {
        T ejecutar(Connection c) throws SQLException;
    }

    /**
     * Ejecuta la operación dentro de una transacción. El bloque synchronized sobre la conexión
     * evita que otro hilo use la misma conexión mientras la transacción está abierta.
     */
    protected <T> T enTransaccion(String descripcion, OperacionSQL<T> operacion) {
        Connection c = conexion();
        synchronized (c) {
            try {
                boolean autoCommitOriginal = c.getAutoCommit();
                c.setAutoCommit(false);
                try {
                    T resultado = operacion.ejecutar(c);
                    c.commit();
                    return resultado;
                } catch (SQLException | RuntimeException e) {
                    c.rollback();
                    throw e;
                } finally {
                    c.setAutoCommit(autoCommitOriginal);
                }
            } catch (SQLException e) {
                throw error(descripcion, e);
            }
        }
    }
}