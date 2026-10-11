package ui.components;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Panel de formulario "etiqueta: campo" alineado en dos columnas (GridBagLayout). */
public class Formulario extends JPanel {

    private static final long serialVersionUID = 1L;
    private int fila = 0;

    public Formulario() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    public Formulario campo(String etiqueta, JComponent campo) {
        GridBagConstraints e = new GridBagConstraints();
        e.gridx = 0;
        e.gridy = fila;
        e.anchor = GridBagConstraints.LINE_END;
        e.insets = new Insets(4, 0, 4, 10);
        JLabel label = new JLabel(etiqueta);
        label.setLabelFor(campo);
        add(label, e);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = fila++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(4, 0, 4, 0);
        add(campo, c);
        return this;
    }

    /** Fila que ocupa ambas columnas (para notas o mensajes). */
    public Formulario nota(JComponent componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = fila++;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 2, 0);
        add(componente, c);
        return this;
    }
}