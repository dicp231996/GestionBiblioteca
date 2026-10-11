package ui.panels;

import model.entities.Estudiante;
import ui.components.BarraEstado;
import ui.components.Estilo;
import ui.components.Mensajes;
import ui.components.Seccion;
import ui.components.TablaModelo;
import ui.components.Tablas;
import ui.controller.EstudianteController;
import ui.controller.EstudianteController.Filtro;
import ui.dialogs.EstudianteDialog;
import ui.dialogs.FichaDialog;
import util.ValidadorRut;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

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
    private final JTable tabla = Tablas.crear(modelo);
    private final JComboBox<Filtro> filtro = new JComboBox<>(Filtro.values());
    private final JLabel resumen = new JLabel();

    public EstudiantesPanel(EstudianteController controller, BarraEstado barra) {
        super("Estudiantes", "Revisa quién tiene libros prestados, registra estudiantes y consulta su historial.");
        this.controller = controller;
        this.barra = barra;
        Tablas.anchos(tabla, 90, 240, 100, 45, 170, 100, 70);

        filtro.addActionListener(e -> refrescar());
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Mostrar:"));
        filtros.add(filtro);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JButton nuevo = Estilo.boton("Nuevo estudiante");
        JButton editar = Estilo.boton("Editar");
        JButton ficha = Estilo.boton("Ver ficha e historial");
        JButton eliminar = Estilo.boton("Eliminar");
        nuevo.addActionListener(e -> nuevo());
        editar.addActionListener(e -> editar());
        ficha.addActionListener(e -> verFicha());
        eliminar.addActionListener(e -> eliminar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(eliminar);
        botones.add(ficha);
        botones.add(editar);
        botones.add(nuevo);
        JPanel acciones = new JPanel(new BorderLayout());
        acciones.add(resumen, BorderLayout.WEST);
        acciones.add(botones, BorderLayout.EAST);
        add(acciones, BorderLayout.SOUTH);
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

    private Estudiante seleccionado() {
        return Tablas.seleccion(tabla, modelo).orElseGet(() -> {
            Mensajes.info(this, "Selecciona un estudiante de la tabla.");
            return null;
        });
    }

    private void nuevo() {
        new EstudianteDialog(SwingUtilities.getWindowAncestor(this), "Nuevo estudiante", null,
                controller::registrar).mostrar().ifPresent(e -> {
            barra.exito("Estudiante registrado con el usuario " + e.getNombreUsuario());
            Mensajes.info(this, "Estudiante registrado.\nUsuario: " + e.getNombreUsuario());
            refrescar();
        });
    }

    private void editar() {
        Estudiante est = seleccionado();
        if (est == null) return;
        new EstudianteDialog(SwingUtilities.getWindowAncestor(this), "Editar estudiante", est,
                e -> { controller.actualizar(e); return e; }).mostrar().ifPresent(e -> {
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