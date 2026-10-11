package ui.panels;

import model.enums.Permiso;
import model.resources.Libro;
import ui.components.BarraEstado;
import ui.components.Estilo;
import ui.components.Formato;
import ui.components.Mensajes;
import ui.components.Seccion;
import ui.components.TablaModelo;
import ui.components.Tablas;
import ui.controller.LibroController;
import ui.controller.PrestamoController;
import ui.controller.Sesion;
import ui.dialogs.LibroDialog;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import static ui.components.TablaModelo.columna;

/**
 * Libros. El bibliotecario ve el inventario con acciones de gestión (CRUD); el estudiante ve el
 * catálogo y puede solicitar un préstamo. La misma pantalla se adapta según los permisos del rol.
 */
public class CatalogoPanel extends Seccion {

    private static final long serialVersionUID = 1L;

    private final LibroController libros;
    private final PrestamoController prestamos;
    private final BarraEstado barra;
    private final boolean gestiona;

    private final TablaModelo<Libro> modelo = new TablaModelo<>(
            columna("Título", String.class, Libro::getTitulo),
            columna("Autor", String.class, Libro::getAutor),
            columna("Editorial", String.class, Libro::getEditorial),
            columna("Categoría", String.class, l -> l.getCategoria().getNombre()),
            columna("ISBN", String.class, Libro::getIsbn),
            columna("Páginas", Integer.class, Libro::getCantidadPaginas),
            columna("Stock", Integer.class, Libro::getStock),
            columna("Estado", String.class, l -> l.getEstado().getEtiqueta()));
    private final JTable tabla = Tablas.crear(modelo);
    private final JTextField busqueda = new JTextField(22);
    private final JCheckBox soloDisponibles = new JCheckBox("Solo disponibles");
    private final JLabel resumen = Estilo.ayuda("");

    public CatalogoPanel(LibroController libros, PrestamoController prestamos, Sesion sesion, BarraEstado barra) {
        super(sesion.puede(Permiso.GESTIONAR_LIBROS) ? "Inventario de libros" : "Catálogo",
                sesion.puede(Permiso.GESTIONAR_LIBROS)
                        ? "Registra, edita y da de baja libros. El stock es la cantidad de ejemplares disponibles."
                        : "Busca un libro disponible y solicítalo. Tienes 7 días para devolverlo.");
        this.libros = libros;
        this.prestamos = prestamos;
        this.barra = barra;
        this.gestiona = sesion.puede(Permiso.GESTIONAR_LIBROS);
        Tablas.anchos(tabla, 260, 160, 110, 120, 110, 60, 50, 90);

        JButton buscar = Estilo.boton("Buscar");
        buscar.addActionListener(e -> refrescar());
        busqueda.addActionListener(e -> refrescar());
        soloDisponibles.setOpaque(false);
        soloDisponibles.addActionListener(e -> refrescar());
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Título:"));
        filtros.add(busqueda);
        filtros.add(buscar);
        filtros.add(soloDisponibles);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(tarjeta(Tablas.conScroll(tabla)), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new BorderLayout());
        acciones.setOpaque(false);
        acciones.add(resumen, BorderLayout.WEST);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setOpaque(false);
        if (gestiona) {
            JButton categoria = Estilo.boton("Nueva categoría");
            JButton eliminar = Estilo.boton("Eliminar");
            JButton editar = Estilo.boton("Editar");
            JButton nuevo = Estilo.botonPrincipal("Nuevo libro");
            categoria.addActionListener(e -> nuevaCategoria());
            eliminar.addActionListener(e -> eliminar());
            editar.addActionListener(e -> editar());
            nuevo.addActionListener(e -> nuevo());
            botones.add(categoria);
            botones.add(eliminar);
            botones.add(editar);
            botones.add(nuevo);
        } else {
            JButton detalle = Estilo.boton("Ver detalle");
            JButton solicitar = Estilo.botonPrincipal("Solicitar préstamo");
            detalle.addActionListener(e -> verDetalle());
            solicitar.addActionListener(e -> solicitar(solicitar));
            botones.add(detalle);
            botones.add(solicitar);
        }
        acciones.add(botones, BorderLayout.EAST);
        add(acciones, BorderLayout.SOUTH);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (gestiona) editar(); else verDetalle();
                }
            }
        });
    }

    @Override
    public void refrescar() {
        Mensajes.intentar(this, () -> {
            List<Libro> lista = libros.buscar(busqueda.getText());
            if (soloDisponibles.isSelected()) {
                lista = lista.stream().filter(Libro::estaDisponibleParaPrestamo).collect(Collectors.toList());
            }
            modelo.setFilas(lista);
            long disponibles = lista.stream().filter(Libro::estaDisponibleParaPrestamo).count();
            resumen.setText(lista.size() + " libros · " + disponibles + " disponibles para préstamo");
        });
    }

    private Libro seleccionado() {
        return Tablas.seleccion(tabla, modelo).orElseGet(() -> {
            Mensajes.info(this, "Selecciona un libro de la tabla.");
            return null;
        });
    }

    // ------------------------------------------------------------------ bibliotecario

    private void nuevo() {
        new LibroDialog(SwingUtilities.getWindowAncestor(this), libros, null).mostrar().ifPresent(l -> {
            barra.exito("Libro registrado: " + l.getTitulo());
            refrescar();
        });
    }

    private void editar() {
        Libro libro = seleccionado();
        if (libro == null) return;
        new LibroDialog(SwingUtilities.getWindowAncestor(this), libros, libro).mostrar().ifPresent(l -> {
            barra.exito("Libro actualizado: " + l.getTitulo());
            refrescar();
        });
    }

    private void eliminar() {
        Libro libro = seleccionado();
        if (libro == null) return;
        if (!Mensajes.confirmar(this, "¿Eliminar \"" + libro.getTitulo() + "\"? Esta acción no se puede deshacer.")) {
            return;
        }
        if (Mensajes.intentar(this, () -> libros.eliminar(libro))) {
            barra.exito("Libro eliminado: " + libro.getTitulo());
            refrescar();
        }
    }

    private void nuevaCategoria() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva categoría:", "Nueva categoría",
                JOptionPane.PLAIN_MESSAGE);
        if (nombre == null || nombre.isBlank()) return;
        if (Mensajes.intentar(this, () -> libros.registrarCategoria(nombre, ""))) {
            barra.exito("Categoría creada: " + nombre.trim());
        }
    }

    // ------------------------------------------------------------------ estudiante

    private void verDetalle() {
        Libro libro = seleccionado();
        if (libro == null) return;
        JTextArea texto = new JTextArea(libro.datosLibro());
        texto.setFont(Estilo.MONO);
        texto.setEditable(false);
        JOptionPane.showMessageDialog(this, texto, libro.getTitulo(), JOptionPane.PLAIN_MESSAGE);
    }

    private void solicitar(JButton boton) {
        Libro libro = seleccionado();
        if (libro == null) return;
        boton.setEnabled(false);
        barra.iniciarProceso("Registrando el préstamo de \"" + libro.getTitulo() + "\" en segundo plano...");
        prestamos.solicitarPrestamo(libro.getId(),
                prestamo -> {
                    boton.setEnabled(true);
                    barra.exito("Préstamo registrado. Devuélvelo antes del "
                            + Formato.fecha(prestamo.getFechaVencimiento()));
                    refrescar();
                    Mensajes.info(this, "¡Listo! Te prestamos \"" + libro.getTitulo() + "\".\nFecha de devolución: "
                            + Formato.fecha(prestamo.getFechaVencimiento()));
                },
                error -> {
                    boton.setEnabled(true);
                    barra.fallo(error);
                    Mensajes.error(this, error);
                });
    }
}