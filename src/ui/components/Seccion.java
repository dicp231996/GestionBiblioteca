package ui.components;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/** Panel base de cada pantalla: título arriba, contenido al centro y botones abajo. */
public abstract class Seccion extends JPanel implements Refrescable {

    private static final long serialVersionUID = 1L;

    protected Seccion(String titulo, String descripcion) {
        super(new BorderLayout(0, 12));
        setBackground(Estilo.FONDO);
        setBorder(Estilo.margen(18));
        JPanel encabezado = new JPanel(new BorderLayout(0, 2));
        encabezado.setOpaque(false);
        encabezado.add(Estilo.titulo(titulo), BorderLayout.NORTH);
        encabezado.add(Estilo.ayuda(descripcion), BorderLayout.SOUTH);
        add(encabezado, BorderLayout.NORTH);
    }

    /** Envuelve un componente en una "tarjeta" blanca con borde. */
    protected static JPanel tarjeta(JComponent contenido) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Estilo.SUPERFICIE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        p.add(contenido, BorderLayout.CENTER);
        return p;
    }
}