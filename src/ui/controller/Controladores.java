package ui.controller;

/** Agrupa la sesión y todos los controladores para entregarlos juntos a las vistas. */
public record Controladores(Sesion sesion,
                            LoginController login,
                            LibroController libros,
                            EstudianteController estudiantes,
                            PrestamoController prestamos,
                            ReporteController reportes) {
}