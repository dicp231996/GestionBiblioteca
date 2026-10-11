package ui.components;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

/** Colores, fuentes y apariencia común de toda la aplicación. */
public final class Estilo {

    // Paleta: azul tinta + dorado de lomo de libro sobre fondo papel.
    public static final Color PRIMARIO = new Color(0x1F3A5F);
    public static final Color PRIMARIO_CLARO = new Color(0x2E5A8A);
    public static final Color ACENTO = new Color(0xC8963E);
    public static final Color FONDO = new Color(0xF4F1EA);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = new Color(0x1E1E1E);
    public static final Color TEXTO_SUAVE = new Color(0x666666);
    public static final Color BORDE = new Color(0xDDD6C8);
    public static final Color PELIGRO = new Color(0xB3261E);
    public static final Color PELIGRO_FONDO = new Color(0xFBE3E1);
    public static final Color EXITO = new Color(0x2E7D32);

    public static final Font TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    public static final Font SUBTITULO = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    public static final Font NORMAL = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font PEQUENA = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    private Estilo() { }

    /** Nimbus viene incluido en Java: se ve igual en Windows, macOS y Linux. */
    public static void instalarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.put("nimbusBase", PRIMARIO);
            UIManager.put("nimbusBlueGrey", new Color(0xB9C3CF));
            UIManager.put("control", FONDO);
            UIManager.put("nimbusSelectionBackground", PRIMARIO_CLARO);
            UIManager.put("nimbusFocus", ACENTO);
            UIManager.put("Table.alternateRowColor", new Color(0xF8F6F1));
            UIManager.put("defaultFont", NORMAL);
        } catch (UnsupportedLookAndFeelException e) {
            Logger.getLogger(Estilo.class.getName()).warning("Nimbus no disponible; se usa el aspecto por defecto");
        }
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(TITULO);
        l.setForeground(PRIMARIO);
        return l;
    }

    public static JLabel ayuda(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(PEQUENA);
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    /** Botón de la acción principal de una pantalla. */
    public static JButton botonPrincipal(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(PRIMARIO);
        b.setForeground(Color.WHITE);
        b.setFont(SUBTITULO);
        return b;
    }

    public static JButton boton(String texto) {
        JButton b = new JButton(texto);
        b.setFont(NORMAL);
        return b;
    }

    public static javax.swing.border.Border margen(int px) {
        return BorderFactory.createEmptyBorder(px, px, px, px);
    }

    /** Ícono de la ventana dibujado por código (tres lomos de libros), así no depende de archivos. */
    public static Image icono() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(PRIMARIO);
        g.fillRoundRect(0, 0, 64, 64, 14, 14);
        Color[] lomos = {ACENTO, new Color(0xE8DFC9), new Color(0x8FB3D9)};
        int[] alturas = {38, 44, 34};
        for (int i = 0; i < 3; i++) {
            g.setColor(lomos[i]);
            g.fillRoundRect(13 + i * 13, 52 - alturas[i], 10, alturas[i], 3, 3);
        }
        g.setColor(new Color(0xE8DFC9));
        g.setStroke(new BasicStroke(3));
        g.drawLine(9, 54, 55, 54);
        g.dispose();
        return img;
    }
}