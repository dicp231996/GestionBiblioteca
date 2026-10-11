package ui.dialogs;

import model.core.Persona;
import model.transactions.Prestamo;
import ui.components.Estilo;
import ui.components.TablaModelo;
import ui.components.Tablas;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.util.List;

import static ui.components.TablaModelo.columna;

/** Ficha de una persona (datosPersonales, polimórfico) junto a su historial de préstamos. */
public class FichaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public FichaDialog(Window padre, Persona persona, List<Prestamo> historial) {
        super(padre, "Ficha de " + persona.getNombreCompleto(), ModalityType.APPLICATION_MODAL);

        JTextArea ficha = new JTextArea(persona.datosPersonales()); // Estudiante o Bibliotecario: cada uno arma la suya
        ficha.setFont(Estilo.MONO);
        ficha.setEditable(false);
        ficha.setBorder(Estilo.margen(10));

        TablaModelo<Prestamo> modelo = new TablaModelo<>(
                columna("Libro", String.class, p -> p.getLibro().getTitulo()),
                columna("Prestado", LocalDate.class, Prestamo::getFechaPrestamo),
                columna("Devuelto", LocalDate.class, Prestamo::getFechaDevolucion),
                columna("Estado", String.class, p -> p.getEstado().getEtiqueta()),
                columna("Observación", String.class, Prestamo::getObservacion));
        modelo.setFilas(historial);
        JTable tabla = Tablas.crear(modelo, p -> p.estaActivo() && p.estaAtrasado());
        Tablas.anchos(tabla, 210, 85, 85, 135, 190);

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(ficha), Tablas.conScroll(tabla));
        division.setDividerLocation(450);
        division.setResizeWeight(0.3);

        JButton cerrar = Estilo.boton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(cerrar);

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBorder(Estilo.margen(14));
        contenido.add(division, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);
        setSize(new Dimension(1180, 500));
        setLocationRelativeTo(padre);
    }
}