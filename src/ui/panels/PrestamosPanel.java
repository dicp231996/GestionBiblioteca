package ui.panels;

import model.entities.Estudiante;
import model.resources.Libro;
import model.transactions.Prestamo;
import ui.components.BarraEstado;
import ui.components.Estilo;
import ui.components.Formato;
import ui.components.Formulario;
import ui.components.Mensajes;
import ui.components.Seccion;
import ui.components.TablaModelo;
import ui.components.Tablas;
import ui.controller.EstudianteController;
import ui.controller.LibroController;
import ui.controller.PrestamoController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static ui.components.TablaModelo.columna;

/**
 * Préstamos y devoluciones (bibliotecario). Registrar y devolver se ejecutan en segundo plano
 * (SwingWorker en PrestamoController): mientras tanto la ventana sigue respondiendo.
 */
public class PrestamosPanel extends Seccion {

    private static final long serialVersionUID = 1L;

    private final PrestamoController prestamos;
    private final EstudianteController estudiantes;
    private final LibroController libros;
    private final BarraEstado barra;

    private final JComboBox<Estudiante> comboEstudiante = new JComboBox<>();
    private final JComboBox<Libro> comboLibro = new JComboBox<>();
    private final JButton registrar = Estilo.botonPrincipal("Registrar préstamo");
    private final JButton devolver = Estilo.botonPrincipal("Registrar devolución");
    private final JLabel vencimiento = Estilo.ayuda("");

    private final TablaModelo<Prestamo> modeloActivos = modeloPrestamos();
    private final TablaModelo<Prestamo> modeloAtrasados = modeloPrestamos();
    private final JTable tablaActivos = Tablas.crear(modeloActivos, Prestamo::estaAtrasado);
    private final JTable tablaAtrasados = Tablas.crear(modeloAtrasados, Prestamo::estaAtrasado);
    private final JTabbedPane pestanas = new JTabbedPane();

    public PrestamosPanel(PrestamoController prestamos, EstudianteController estudiantes,
                          LibroController libros, BarraEstado barra) {
        super("Préstamos y devoluciones",
                "Registra préstamos (vencen en 7 días) y devoluciones. Las filas en rojo están atrasadas.");
        this.prestamos = prestamos;
        this.estudiantes = estudiantes;
        this.libros = libros;
        this.barra = barra;

        // ---- formulario de nuevo préstamo
        vencimiento.setText("Si se presta hoy, vence el " + Formato.fecha(LocalDate.now().plusDays(Prestamo.DIAS_PRESTAMO)));
        registrar.addActionListener(e -> registrarPrestamo());
        Formulario form = new Formulario()
                .campo("Estudiante", comboEstudiante)
                .campo("Libro disponible", comboLibro);
        JPanel filaBoton = new JPanel(new BorderLayout());
        filaBoton.setOpaque(false);
        filaBoton.add(vencimiento, BorderLayout.WEST);
        filaBoton.add(registrar, BorderLayout.EAST);
        form.nota(filaBoton);
        JPanel nuevo = tarjeta(form);
        nuevo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.BORDE), "Nuevo préstamo"),
                Estilo.margen(8)));

        // ---- tablas de préstamos
        for (JTable t : new JTable[]{tablaActivos, tablaAtrasados}) {
            Tablas.anchos(t, 40, 250, 220, 100, 85, 85, 80);
        }
        pestanas.addTab("Préstamos activos", Tablas.conScroll(tablaActivos));
        pestanas.addTab("Atrasados", Tablas.conScroll(tablaAtrasados));

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.add(nuevo, BorderLayout.NORTH);
        centro.add(pestanas, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        devolver.addActionListener(e -> registrarDevolucion());
        JButton actualizar = Estilo.boton("Actualizar");
        actualizar.addActionListener(e -> refrescar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setOpaque(false);
        botones.add(actualizar);
        botones.add(devolver);
        add(botones, BorderLayout.SOUTH);
    }

    private static TablaModelo<Prestamo> modeloPrestamos() {
        return new TablaModelo<>(
                columna("N°", Integer.class, Prestamo::getId),
                columna("Libro", String.class, p -> p.getLibro().getTitulo()),
                columna("Estudiante", String.class, p -> p.getEstudiante().getNombreCompleto()),
                columna("Curso", String.class, p -> p.getEstudiante().getCurso().toString()),
                columna("Prestado", LocalDate.class, Prestamo::getFechaPrestamo),
                columna("Vence", LocalDate.class, Prestamo::getFechaVencimiento),
                columna("Días atraso", Long.class, Prestamo::diasAtraso));
    }

    @Override
    public void refrescar() {
        Mensajes.intentar(this, () -> {
            Object estudianteElegido = comboEstudiante.getSelectedItem();
            comboEstudiante.removeAllItems();
            estudiantes.listar(EstudianteController.Filtro.TODOS).stream()
                    .filter(e -> e.getUsuario().isActivo())
                    .sorted(Comparator.comparing(Estudiante::getNombreCompleto))
                    .forEach(comboEstudiante::addItem);
            if (estudianteElegido != null) comboEstudiante.setSelectedItem(estudianteElegido);

            comboLibro.removeAllItems();
            libros.disponibles().forEach(comboLibro::addItem);

            List<Prestamo> activos = prestamos.activos();
            modeloActivos.setFilas(activos);
            List<Prestamo> atrasados = activos.stream().filter(Prestamo::estaAtrasado).toList();
            modeloAtrasados.setFilas(atrasados);
            pestanas.setTitleAt(0, "Préstamos activos (" + activos.size() + ")");
            pestanas.setTitleAt(1, "Atrasados (" + atrasados.size() + ")");
        });
    }

    private void registrarPrestamo() {
        Estudiante est = (Estudiante) comboEstudiante.getSelectedItem();
        Libro libro = (Libro) comboLibro.getSelectedItem();
        if (est == null || libro == null) {
            Mensajes.info(this, "Elige un estudiante y un libro disponible.");
            return;
        }
        registrar.setEnabled(false);
        barra.iniciarProceso("Registrando préstamo en segundo plano: validando, descontando stock y guardando historial...");
        prestamos.registrarPrestamo(est.getId(), libro.getId(),
                prestamo -> {
                    registrar.setEnabled(true);
                    barra.exito("Préstamo #" + prestamo.getId() + " registrado: \"" + libro.getTitulo() + "\" para "
                            + est.getNombreCompleto() + ". Vence el " + Formato.fecha(prestamo.getFechaVencimiento()));
                    refrescar();
                },
                error -> {
                    registrar.setEnabled(true);
                    barra.fallo(error);
                    Mensajes.error(this, error);
                    refrescar();
                });
    }

    private void registrarDevolucion() {
        boolean enAtrasados = pestanas.getSelectedIndex() == 1;
        JTable tabla = enAtrasados ? tablaAtrasados : tablaActivos;
        TablaModelo<Prestamo> modelo = enAtrasados ? modeloAtrasados : modeloActivos;
        Prestamo prestamo = Tablas.seleccion(tabla, modelo).orElse(null);
        if (prestamo == null) {
            Mensajes.info(this, "Selecciona el préstamo que se está devolviendo.");
            return;
        }
        devolver.setEnabled(false);
        barra.iniciarProceso("Registrando devolución en segundo plano...");
        prestamos.registrarDevolucion(prestamo.getId(),
                devuelto -> {
                    devolver.setEnabled(true);
                    String detalle = devuelto.getObservacion().isEmpty() ? "a tiempo" : devuelto.getObservacion();
                    barra.exito("Devolución registrada (" + detalle + "): " + devuelto.getLibro().getTitulo());
                    refrescar();
                    if (devuelto.estaAtrasado()) {
                        Mensajes.info(this, "Devolución registrada con atraso.\n\n" + devuelto.datosPrestamo());
                    }
                },
                error -> {
                    devolver.setEnabled(true);
                    barra.fallo(error);
                    Mensajes.error(this, error);
                });
    }
}