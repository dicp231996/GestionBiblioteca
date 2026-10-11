package ui.components;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/** Pocos valores comunes para que todas las pantallas se vean consistentes (aspecto estándar de Swing). */
public final class Estilo {

    public static final Font TITULO = new Font(Font.DIALOG, Font.BOLD, 18);
    public static final Font NEGRITA = new Font(Font.DIALOG, Font.BOLD, 12);
    public static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    /** Color de fondo para las filas de préstamos atrasados. */
    public static final Color FILA_ATRASADA = new Color(255, 215, 215);

    private Estilo() { }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(TITULO);
        return l;
    }

    public static JLabel ayuda(String texto) {
        return new JLabel(texto);
    }

    public static JLabel negrita(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(NEGRITA);
        return l;
    }

    public static JButton boton(String texto) {
        return new JButton(texto);
    }

    public static Border margen(int px) {
        return BorderFactory.createEmptyBorder(px, px, px, px);
    }
}