package ui.dialogs;

import model.classification.Categoria;
import ui.components.Estilo;
import ui.components.Formulario;
import ui.components.Mensajes;
import ui.components.TablaModelo;
import ui.components.Tablas;
import ui.controller.LibroController;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

import static ui.components.TablaModelo.columna;

/**
 * CRUD de categorías en una sola ventana:
 * Create = "Nueva", Read = la tabla, Update = "Editar", Delete = "Eliminar".
 */
public class CategoriasDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final LibroController controller;
    private final TablaModelo<Categoria> modelo = new TablaModelo<>(
            columna("Nombre", String.class, Categoria::getNombre),
            columna("Descripción", String.class, Categoria::getDescripcion));
    private final JTable tabla = Tablas.crear(modelo);

    public CategoriasDialog(Window padre, LibroController controller) {
        super(padre, "Categorías", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        Tablas.anchos(tabla, 150, 300);

        JButton nueva = Estilo.boton("Nueva");
        JButton editar = Estilo.boton("Editar");
        JButton eliminar = Estilo.boton("Eliminar");
        JButton cerrar = Estilo.boton("Cerrar");
        nueva.addActionListener(e -> nueva());
        editar.addActionListener(e -> editar());
        eliminar.addActionListener(e -> eliminar());
        cerrar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(nueva);
        botones.add(editar);
        botones.add(eliminar);
        botones.add(cerrar);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(480, 260));
        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setBorder(Estilo.margen(12));
        contenido.add(scroll, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);
        refrescar();
        pack();
        setLocationRelativeTo(padre);
    }

    private void refrescar() {
        Mensajes.intentar(this, () -> modelo.setFilas(controller.categorias()));
    }

    private Categoria seleccionada() {
        return Tablas.seleccion(tabla, modelo).orElseGet(() -> {
            Mensajes.info(this, "Selecciona una categoría de la tabla.");
            return null;
        });
    }

    /** Create */
    private void nueva() {
        JTextField nombre = new JTextField(20);
        JTextField descripcion = new JTextField(20);
        if (pedirDatos("Nueva categoría", nombre, descripcion)
                && Mensajes.intentar(this, () -> controller.registrarCategoria(
                new Categoria(nombre.getText(), descripcion.getText())))) {
            refrescar();
        }
    }

    /** Update */
    private void editar() {
        Categoria c = seleccionada();
        if (c == null) return;
        JTextField nombre = new JTextField(c.getNombre(), 20);
        JTextField descripcion = new JTextField(c.getDescripcion(), 20);
        if (pedirDatos("Editar categoría", nombre, descripcion)
                && Mensajes.intentar(this, () -> controller.actualizarCategoria(
                new Categoria(c.getId(), nombre.getText(), descripcion.getText())))) {
            refrescar();
        }
    }

    /** Delete */
    private void eliminar() {
        Categoria c = seleccionada();
        if (c == null) return;
        if (Mensajes.confirmar(this, "¿Eliminar la categoría \"" + c.getNombre() + "\"?")
                && Mensajes.intentar(this, () -> controller.eliminarCategoria(c))) {
            refrescar();
        }
    }

    private boolean pedirDatos(String titulo, JTextField nombre, JTextField descripcion) {
        Formulario f = new Formulario()
                .campo("Nombre", nombre)
                .campo("Descripción", descripcion);
        return JOptionPane.showConfirmDialog(this, f, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }
}