package data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Patrón Singleton: una única instancia administra la conexión JDBC a MySQL.
 * Los datos de conexión se leen de src/main/resources/db.properties.
 */
public final class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static volatile DatabaseConnection instancia;

    private final String url;
    private final String usuario;
    private final String contrasena;
    private Connection conexion;

    private DatabaseConnection() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                LOGGER.warning("No se encontró db.properties; se usan valores por defecto");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "No se pudo leer db.properties", e);
        }
        this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/biblioteca_escolar");
        this.usuario = props.getProperty("db.usuario", "root");
        this.contrasena = props.getProperty("db.contrasena", "");
    }

    /** Doble verificación para que sea seguro también entre hilos. */
    public static DatabaseConnection getInstance() {
        if (instancia == null) {
            synchronized (DatabaseConnection.class) {
                if (instancia == null) {
                    instancia = new DatabaseConnection();
                }
            }
        }
        return instancia;
    }

    /** Devuelve la conexión, reabriéndola si se cerró. */
    public synchronized Connection getConnection() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
            LOGGER.info("Conexión a MySQL establecida");
        }
        return conexion;
    }

    public synchronized void cerrar() {
        if (conexion != null) {
            try {
                conexion.close();
                LOGGER.info("Conexión a MySQL cerrada");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error al cerrar la conexión", e);
            } finally {
                conexion = null;
            }
        }
    }
}