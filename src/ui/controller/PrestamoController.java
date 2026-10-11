package ui.controller;

import model.services.ServicioDevolucion;
import model.services.ServicioGestion;
import model.services.ServicioPrestamo;
import model.transactions.Prestamo;

import javax.swing.SwingWorker;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Préstamos y devoluciones. Registrar un préstamo (validar, descontar stock y guardar el
 * historial) se ejecuta en un hilo en segundo plano con SwingWorker, así la ventana no se
 * congela mientras se habla con la base de datos. El resultado vuelve al hilo de la interfaz
 * (Event Dispatch Thread) en done(), que es el único hilo que puede tocar componentes Swing.
 */
public class PrestamoController {

    private static final Logger LOGGER = Logger.getLogger(PrestamoController.class.getName());

    private final ServicioPrestamo servicioPrestamo;
    private final ServicioDevolucion servicioDevolucion;
    private final ServicioGestion gestion;
    private final Sesion sesion;

    public PrestamoController(ServicioPrestamo servicioPrestamo, ServicioDevolucion servicioDevolucion,
                              ServicioGestion gestion, Sesion sesion) {
        this.servicioPrestamo = servicioPrestamo;
        this.servicioDevolucion = servicioDevolucion;
        this.gestion = gestion;
        this.sesion = sesion;
    }

    // ------------------------------------------------------------------ operaciones en segundo plano

    /** Bibliotecario: registra un préstamo para cualquier estudiante. */
    public void registrarPrestamo(int idEstudiante, int idLibro,
                                  Consumer<Prestamo> alTerminar, Consumer<String> alFallar) {
        enSegundoPlano("registrar préstamo",
                () -> servicioPrestamo.registrarPrestamo(sesion.getUsuarioActual(), idEstudiante, idLibro),
                alTerminar, alFallar);
    }

    /** Estudiante: solicita un préstamo para sí mismo. */
    public void solicitarPrestamo(int idLibro, Consumer<Prestamo> alTerminar, Consumer<String> alFallar) {
        int idPropio = sesion.getUsuarioActual().getId();
        registrarPrestamo(idPropio, idLibro, alTerminar, alFallar);
    }

    public void registrarDevolucion(int idPrestamo, Consumer<Prestamo> alTerminar, Consumer<String> alFallar) {
        enSegundoPlano("registrar devolución",
                () -> servicioDevolucion.registrarDevolucion(sesion.getUsuarioActual(), idPrestamo),
                alTerminar, alFallar);
    }

    private <T> void enSegundoPlano(String operacion, Callable<T> tarea,
                                    Consumer<T> alTerminar, Consumer<String> alFallar) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                LOGGER.info(() -> "Ejecutando '" + operacion + "' en el hilo: " + Thread.currentThread().getName());
                return tarea.call();
            }

            @Override
            protected void done() {               // vuelve al hilo de la interfaz
                try {
                    alTerminar.accept(get());
                } catch (ExecutionException e) {
                    alFallar.accept(Errores.mensaje(e));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    alFallar.accept("La operación fue interrumpida");
                }
            }
        }.execute();
    }

    // ------------------------------------------------------------------ consultas

    public List<Prestamo> activos() {
        return gestion.listarPrestamosActivos(sesion.getUsuarioActual());
    }

    public List<Prestamo> atrasados() {
        return gestion.listarPrestamosAtrasados(sesion.getUsuarioActual());
    }

    /** Estudiante: su historial completo (activos primero). */
    public List<Prestamo> misPrestamos() {
        int idPropio = sesion.getUsuarioActual().getId();
        return servicioPrestamo.historialDe(sesion.getUsuarioActual(), idPropio).stream()
                .sorted((a, b) -> Boolean.compare(b.estaActivo(), a.estaActivo()))
                .collect(Collectors.toList());
    }
}