package app;

import data.DatabaseConnection;
import data.dao.BibliotecarioDAO;
import data.dao.CategoriaDAO;
import data.dao.EstudianteDAO;
import data.dao.LibroDAO;
import data.dao.PrestamoDAO;
import data.mysql.BibliotecarioDAOMySQL;
import data.mysql.CategoriaDAOMySQL;
import data.mysql.EstudianteDAOMySQL;
import data.mysql.LibroDAOMySQL;
import data.mysql.PrestamoDAOMySQL;
import model.services.ServicioDevolucion;
import model.services.ServicioGestion;
import model.services.ServicioPrestamo;
import services.AutenticacionService;
import services.ReporteService;
import ui.LoginFrame;
import ui.MainFrame;
import ui.controller.Controladores;
import ui.controller.EstudianteController;
import ui.controller.LibroController;
import ui.controller.LoginController;
import ui.controller.PerfilController;
import ui.controller.PrestamoController;
import ui.controller.ReporteController;
import ui.controller.Sesion;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.SQLException;

/**
 * Punto de entrada. Arma la aplicación (DAO -> servicios -> controladores -> vistas) y
 * coordina el paso entre la ventana de login y la ventana principal.
 */
public class Biblioteca {

    private final Controladores controladores;
    private LoginFrame login;

    public Biblioteca(LibroDAO libroDAO, CategoriaDAO categoriaDAO, EstudianteDAO estudianteDAO,
                      BibliotecarioDAO bibliotecarioDAO, PrestamoDAO prestamoDAO) {
        // ---- Lógica de negocio
        ServicioGestion gestion = new ServicioGestion(libroDAO, categoriaDAO, estudianteDAO,
                bibliotecarioDAO, prestamoDAO);
        ServicioPrestamo servicioPrestamo = new ServicioPrestamo(gestion, estudianteDAO, prestamoDAO);
        ServicioDevolucion servicioDevolucion = new ServicioDevolucion(gestion, estudianteDAO, prestamoDAO);
        AutenticacionService autenticacion = new AutenticacionService(estudianteDAO, bibliotecarioDAO);
        ReporteService reportes = new ReporteService(prestamoDAO);

        // ---- Controladores (MVC)
        Sesion sesion = new Sesion();
        controladores = new Controladores(sesion,
                new LoginController(autenticacion, gestion, sesion),
                new LibroController(gestion, sesion),
                new EstudianteController(gestion, servicioPrestamo, sesion),
                new PrestamoController(servicioPrestamo, servicioDevolucion, gestion, sesion),
                new ReporteController(reportes, sesion),
                new PerfilController(gestion, sesion));
    }

    public void mostrarLogin() {
        controladores.sesion().cerrar();
        login = new LoginFrame(controladores.login(), persona -> mostrarPrincipal());
        login.setVisible(true);
    }

    private void mostrarPrincipal() {
        login.dispose();
        new MainFrame(controladores, this::mostrarLogin).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection.getInstance().getConnection(); // prueba la conexión al iniciar
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar a MySQL.\n\n" + e.getMessage()
                                + "\n\nRevisa que el servidor esté encendido, que ejecutaste los scripts SQL\n"
                                + "y que el usuario y la contraseña de src/main/resources/db.properties sean correctos.",
                        "Biblioteca Escolar", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> DatabaseConnection.getInstance().cerrar()));

            // ---- Capa de datos: MySQL (para probar sin BD se pueden inyectar los DAO de data.memory)
            LibroDAO libroDAO = new LibroDAOMySQL();
            EstudianteDAO estudianteDAO = new EstudianteDAOMySQL();
            PrestamoDAO prestamoDAO = new PrestamoDAOMySQL(estudianteDAO, libroDAO);
            new Biblioteca(libroDAO, new CategoriaDAOMySQL(), estudianteDAO, new BibliotecarioDAOMySQL(), prestamoDAO)
                    .mostrarLogin();
        });
    }
}