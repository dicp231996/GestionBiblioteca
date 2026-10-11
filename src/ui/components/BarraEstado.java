package ui.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Barra inferior de la ventana: muestra mensajes y una barra de progreso mientras una
 * operación corre en segundo plano (préstamos/devoluciones con SwingWorker).
 */
public class BarraEstado extends JPanel {

    private static final long serialVersionUID = 1L;
    private final JLabel mensaje = new JLabel("Listo");
    private final JProgressBar progreso = new JProgressBar();

    public BarraEstado() {
        super(new BorderLayout(10, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        progreso.setIndeterminate(true);
        progreso.setPreferredSize(new Dimension(150, 14));
        progreso.setVisible(false);
        add(mensaje, BorderLayout.CENTER);
        add(progreso, BorderLayout.EAST);
    }

    /** Muestra la barra animada: la interfaz sigue respondiendo mientras el hilo trabaja. */
    public void iniciarProceso(String texto) {
        mensaje.setText(texto);
        progreso.setVisible(true);
    }

    public void exito(String texto) {
        progreso.setVisible(false);
        mensaje.setText(texto);
    }

    public void fallo(String texto) {
        progreso.setVisible(false);
        mensaje.setText("Error: " + texto.replace('\n', ' '));
    }
}