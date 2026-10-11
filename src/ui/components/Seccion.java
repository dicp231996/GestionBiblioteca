package ui.components;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/** Panel base de cada pantalla: título arriba, contenido al centro y botones abajo. */
public abstract class Seccion extends JPanel implements Refrescable {

    private static final long serialVersionUID = 1L;

    protected Seccion(String titulo, String descripcion) {
        super(new BorderLayout(0, 10));
        setBorder(Estilo.margen(12));
        JPanel encabezado = new JPanel(new BorderLayout(0, 2));
        encabezado.add(Estilo.titulo(titulo), BorderLayout.NORTH);
        encabezado.add(Estilo.ayuda(descripcion), BorderLayout.SOUTH);
        add(encabezado, BorderLayout.NORTH);
    }
}