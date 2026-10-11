package app;

import data.dao.BibliotecarioDAO;
import data.dao.CategoriaDAO;
import data.dao.EstudianteDAO;
import data.dao.LibroDAO;
import data.dao.PrestamoDAO;
import data.memory.BibliotecarioDAOMemoria;
import data.memory.CategoriaDAOMemoria;
import data.memory.EstudianteDAOMemoria;
import data.memory.LibroDAOMemoria;
import data.memory.PrestamoDAOMemoria;
import model.services.ServicioDevolucion;
import model.services.ServicioGestion;
import model.services.ServicioPrestamo;
import services.AutenticacionService;
import services.ReporteService;

/**
 * Punto de entrada del sistema. Aquí se arma la aplicación (inyección de dependencias):
 * se crean los DAO, luego los servicios que los usan y, más adelante, la ventana de login.
 * Los datos iniciales vienen de la base de datos (Script_poblado_tablas_biblioteca.sql).
 */
public class Biblioteca {

    public static void main(String[] args) {
        // ---- Capa de datos: hoy en memoria; para MySQL solo se cambian estas 5 líneas.
        LibroDAO libroDAO = new LibroDAOMemoria();
        CategoriaDAO categoriaDAO = new CategoriaDAOMemoria();
        EstudianteDAO estudianteDAO = new EstudianteDAOMemoria();
        BibliotecarioDAO bibliotecarioDAO = new BibliotecarioDAOMemoria();
        PrestamoDAO prestamoDAO = new PrestamoDAOMemoria();

        // ---- Lógica de negocio
        ServicioGestion gestion = new ServicioGestion(libroDAO, categoriaDAO, estudianteDAO,
                bibliotecarioDAO, prestamoDAO);
        ServicioPrestamo servicioPrestamo = new ServicioPrestamo(gestion, estudianteDAO, prestamoDAO);
        ServicioDevolucion servicioDevolucion = new ServicioDevolucion(gestion, estudianteDAO, prestamoDAO);
        AutenticacionService autenticacion = new AutenticacionService(estudianteDAO, bibliotecarioDAO);
        ReporteService reportes = new ReporteService(prestamoDAO);

        // ---- Interfaz gráfica (pendiente): aquí se abrirá la ventana de login,
        //      entregándole a los controladores los servicios creados arriba.
        System.out.println("Sistema de biblioteca inicializado.");
    }
}