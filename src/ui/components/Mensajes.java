package ui.components;

import ui.controller.Errores;

import javax.swing.JOptionPane;
import java.awt.Component;

/** Cuadros de diálogo estándar y ejecución segura de acciones de la interfaz. */
public final class Mensajes {

    private Mensajes() { }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Biblioteca Escolar", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "No se pudo completar la acción", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String pregunta) {
        return JOptionPane.showConfirmDialog(padre, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Ejecuta una acción y, si falla (regla de negocio, permiso, validación o BD), muestra el
     * mensaje en pantalla en vez de dejar que la excepción se pierda en la consola.
     * @return true si la acción terminó sin errores.
     */
    public static boolean intentar(Component padre, Runnable accion) {
        try {
            accion.run();
            return true;
        } catch (RuntimeException e) {
            error(padre, Errores.mensaje(e));
            return false;
        }
    }
}