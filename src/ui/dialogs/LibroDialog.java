package ui.dialogs;

import model.classification.Categoria;
import model.enums.EstadoLibro;
import model.resources.Libro;
import ui.components.Estilo;
import ui.components.Formulario;
import ui.components.Mensajes;
import ui.controller.LibroController;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.Optional;

/** Formulario para registrar o editar un libro. */
public class LibroDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final LibroController controller;
    private final Libro existente;          // null = libro nuevo
    private Libro guardado;

    private final JTextField titulo = new JTextField(28);
    private final JTextField autor = new JTextField();
    private final JTextField editorial = new JTextField();
    private final JComboBox<Categoria> categoria = new JComboBox<>();
    private final JTextField isbn = new JTextField();
    private final JSpinner paginas = new JSpinner(new SpinnerNumberModel(100, 1, 5000, 1));
    private final JSpinner stock = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));
    private final JComboBox<EstadoLibro> estado = new JComboBox<>(EstadoLibro.values());

    public LibroDialog(Window padre, LibroController controller, Libro existente) {
        super(padre, existente == null ? "Nuevo libro" : "Editar libro", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.existente = existente;
        controller.categorias().forEach(categoria::addItem);

        Formulario f = new Formulario()
                .campo("Título", titulo)
                .campo("Autor", autor)
                .campo("Editorial", editorial)
                .campo("Categoría", categoria)
                .campo("ISBN", isbn)
                .campo("Páginas", paginas)
                .campo("Stock disponible", stock)
                .campo("Estado", estado)
                .nota(Estilo.ayuda("El ISBN puede ir con o sin guiones (ISBN-10 o ISBN-13)."));

        if (existente != null) cargar(existente);

        JButton cancelar = Estilo.boton("Cancelar");
        JButton guardar = Estilo.boton("Guardar");
        cancelar.addActionListener(e -> dispose());
        guardar.addActionListener(e -> guardar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);
        botones.add(cancelar);
        botones.add(guardar);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBorder(Estilo.margen(18));
        contenido.add(f, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);
        getRootPane().setDefaultButton(guardar);
        pack();
        setLocationRelativeTo(padre);
    }

    /** Muestra el diálogo y devuelve el libro guardado (vacío si se canceló). */
    public Optional<Libro> mostrar() {
        setVisible(true);
        return Optional.ofNullable(guardado);
    }

    private void cargar(Libro l) {
        titulo.setText(l.getTitulo());
        autor.setText(l.getAutor());
        editorial.setText(l.getEditorial());
        categoria.setSelectedItem(l.getCategoria());
        isbn.setText(l.getIsbn());
        paginas.setValue(l.getCantidadPaginas());
        stock.setValue(l.getStock());
        estado.setSelectedItem(l.getEstado());
    }

    private void guardar() {
        if (categoria.getSelectedItem() == null) {
            Mensajes.error(this, "Primero debes crear al menos una categoría.");
            return;
        }
        Mensajes.intentar(this, () -> {
            // Se construye un objeto nuevo: si una validación falla, el original queda intacto.
            Libro libro = new Libro(existente == null ? 0 : existente.getId(), titulo.getText(), autor.getText(),
                    editorial.getText(), (Categoria) categoria.getSelectedItem(), isbn.getText(),
                    (Integer) paginas.getValue(), (Integer) stock.getValue(), (EstadoLibro) estado.getSelectedItem());
            if (existente == null) {
                guardado = controller.registrar(libro);
            } else {
                controller.actualizar(libro);
                guardado = libro;
            }
            dispose();
        });
    }
}