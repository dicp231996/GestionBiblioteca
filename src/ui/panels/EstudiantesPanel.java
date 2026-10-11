package ui.panels;

import model.entities.Estudiante;
import ui.components.BarraEstado;
import ui.components.Estilo;
import ui.components.Mensajes;
import ui.components.Seccion;
import ui.components.TablaModelo;
import ui.components.Tablas;
import ui.controller.EstudianteController.Filtro;
import ui.controller.EstudianteController;
import ui.dialogs.EstudianteDialog;
import ui.dialogs.FichaDialog;
import util.ValidadorRut;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.regex.Pattern;

import static ui.components.TablaModelo.columna;

/** Registro de estudiantes: quién tiene libros prestados y quién no, altas, edición y fichas. */
public class EstudiantesPanel extends Seccion {

    private static final long serialVersionUID = 1L;

    private final EstudianteController controller;
    private final BarraEstado barra;

    private final TablaModelo<Estudiante> modelo = new TablaModelo<>(
            columna("RUT", String.class, e -> ValidadorRut.formatear(e.getRut())),
            columna("Nombre", String.class, Estudiante::getNombreCompleto),
            columna("Curso", String.class, e -> e.getCurso().toString()),
            columna("Edad", Integer.class, Estudiante::getEdad),
            columna("Usuario", String.class, Estudiante::getNombreUsuario),
            columna("Libros prestados", Integer.class, Estudiante::cantidadLibrosPrestados),
            columna("Cuenta", String.class, e -> e.getUsuario().isActivo() ? "Activa" : "Inactiva"));
    private final JTable tabla = Tablas.crear(modelo, e -> !e.getUsuario().isActivo());
    private final TableRowSorter<TablaModelo<Estudiante>> ordenador = new TableRowSorter<>(modelo);
    private final JComboBox<Filtro> filtro = new JComboBox<>(Filtro.values());
    private final JTextField busqueda = new JTextField(18);
    private final JLabel resumen = Estilo.ayuda("");

    public EstudiantesPanel(EstudianteController controller, BarraEstado barra) {
        super("Estudiantes", "Revisa quién tiene libros prestados, registra estudiantes y consulta su historial.");
        this.controller = controller;
        this.barra = barra;
        tabla.setRowSorter(ordenador);
        Tablas.anchos(tabla, 90, 240, 100, 45, 170, 100, 70);

        filtro.addActionListener(e -> refrescar());
        busqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarTexto(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarTexto(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarTexto(); }
        });
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Mostrar:"));
        filtros.add(filtro);
        filtros.add(new JLabel("   Buscar (nombre, RUT o usuario):"));
        filtros.add(busqueda);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(tarjeta(Tablas.conScroll(tabla)), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JButton eliminar = Estilo.boton("Eliminar");
        JButton estadoCuenta = Estilo.boton("Activar / desactivar");
        JButton ficha = Estilo.boton("Ver ficha e historial");
        JButton editar = Estilo.boton("Editar");
        JButton nuevo = Estilo.botonPrincipal("Nuevo estudiante");
        eliminar.addActionListener(e -> eliminar());
        estadoCuenta.addActionListener(e -> cambiarEstado());
        ficha.addActionListener(e -> verFicha());
        editar.addActionListener(e -> editar());
        nuevo.addActionListener(e -> nuevo());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setOpaque(false);
        botones.add(eliminar);
        botones.add(estadoCuenta);
        botones.add(ficha);
        botones.add(editar);
        botones.add(nuevo);
        JPanel acciones = new JPanel(new BorderLayout());
        acciones.setOpaque(false);
        acciones.add(resumen, BorderLayout.WEST);
        acciones.add(botones, BorderLayout.EAST);
        add(acciones, BorderLayout.SOUTH);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) verFicha();
            }
        });
    }

    @Override
    public void refrescar() {
        Mensajes.intentar(this, () -> {
            List<Estudiante> lista = controller.listar((Filtro) filtro.getSelectedItem());
            modelo.setFilas(lista);
            long conLibros = lista.stream().filter(Estudiante::tieneLibrosPrestados).count();
            resumen.setText(lista.size() + " estudiantes · " + conLibros + " con libros prestados");
        });
    }

    private void filtrarTexto() {
        String texto = busqueda.getText().trim();
        ordenador.setRowFilter(texto.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 0, 1, 4));
    }

    private Estudiante seleccionado() {
        return Tablas.seleccion(tabla, modelo).orElseGet(() -> {
            Mensajes.info(this, "Selecciona un estudiante de la tabla.");
            return null;
        });
    }

    private void nuevo() {
        new EstudianteDialog(SwingUtilities.getWindowAncestor(this), "Nuevo estudiante", null,
                controller::registrar, controller::previsualizarUsuario).mostrar().ifPresent(e -> {
            barra.exito("Estudiante registrado con el usuario " + e.getNombreUsuario());
            Mensajes.info(this, "Estudiante registrado.\nUsuario: " + e.getNombreUsuario());
            refrescar();
        });
    }

    private void editar() {
        Estudiante est = seleccionado();
        if (est == null) return;
        new EstudianteDialog(SwingUtilities.getWindowAncestor(this), "Editar estudiante", est,
                e -> { controller.actualizar(e); return e; }, controller::previsualizarUsuario)
                .mostrar().ifPresent(e -> {
                    barra.exito("Datos actualizados: " + e.getNombreCompleto());
                    refrescar();
                });
    }

    private void verFicha() {
        Estudiante est = seleccionado();
        if (est == null) return;
        Mensajes.intentar(this, () ->
                new FichaDialog(SwingUtilities.getWindowAncestor(this), est, controller.historial(est))
                        .setVisible(true));
    }

    private void cambiarEstado() {
        Estudiante est = seleccionado();
        if (est == null) return;
        boolean activar = !est.getUsuario().isActivo();
        String pregunta = (activar ? "¿Activar" : "¿Desactivar") + " la cuenta de " + est.getNombreCompleto() + "?"
                + (activar ? "" : "\nNo podrá iniciar sesión ni pedir libros.");
        if (!Mensajes.confirmar(this, pregunta)) return;
        if (Mensajes.intentar(this, () -> controller.cambiarEstadoCuenta(est, activar))) {
            barra.exito("Cuenta " + (activar ? "activada" : "desactivada") + ": " + est.getNombreCompleto());
            refrescar();
        }
    }

    private void eliminar() {
        Estudiante est = seleccionado();
        if (est == null) return;
        if (!Mensajes.confirmar(this, "¿Eliminar a " + est.getNombreCompleto() + "? Esta acción no se puede deshacer.")) {
            return;
        }
        if (Mensajes.intentar(this, () -> controller.eliminar(est))) {
            barra.exito("Estudiante eliminado: " + est.getNombreCompleto());
            refrescar();
        }
    }
}