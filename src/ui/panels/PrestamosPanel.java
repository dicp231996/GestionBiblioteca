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
import javax.swing.JScrollPane;
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
    private final JButton registrar = Estilo.boton("Registrar préstamo");
    private final JButton devolver = Estilo.boton("Registrar devolución");
    private final JLabel resumen = new JLabel();

    private final TablaModelo<Prestamo> modelo = new TablaModelo<>(
            columna("N°", Integer.class, Prestamo::getId),
            columna("Libro", String.class, p -> p.getLibro().getTitulo()),
            columna("Estudiante", String.class, p -> p.getEstudiante().getNombreCompleto()),
            columna("Curso", String.class, p -> p.getEstudiante().getCurso().toString()),
            columna("Prestado", LocalDate.class, Prestamo::getFechaPrestamo),
            columna("Vence", LocalDate.class, Prestamo::getFechaVencimiento),
            columna("Días atraso", Long.class, Prestamo::diasAtraso));
    private final JTable tabla = Tablas.crear(modelo, Prestamo::estaAtrasado);

    public PrestamosPanel(PrestamoController prestamos, EstudianteController estudiantes,
                          LibroController libros, BarraEstado barra) {
        super("Préstamos y devoluciones",
                "Registra préstamos (vencen en 7 días) y devoluciones. Las filas en rojo están atrasadas.");
        this.prestamos = prestamos;
        this.estudiantes = estudiantes;
        this.libros = libros;
        this.barra = barra;
        Tablas.anchos(tabla, 40, 250, 220, 100, 85, 85, 80);

        // ---- formulario de nuevo préstamo
        registrar.addActionListener(e -> registrarPrestamo());
        JPanel filaBoton = new JPanel(new BorderLayout());
        filaBoton.add(Estilo.ayuda("Si se presta hoy, vence el "
                + Formato.fecha(LocalDate.now().plusDays(Prestamo.DIAS_PRESTAMO))), BorderLayout.WEST);
        filaBoton.add(registrar, BorderLayout.EAST);
        Formulario form = new Formulario()
                .campo("Estudiante", comboEstudiante)
                .campo("Libro disponible", comboLibro)
                .nota(filaBoton);
        JPanel nuevo = new JPanel(new BorderLayout());
        nuevo.add(form, BorderLayout.CENTER);
        nuevo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Nuevo préstamo"), Estilo.margen(6)));

        // ---- préstamos activos
        JPanel activos = new JPanel(new BorderLayout(0, 4));
        activos.add(resumen, BorderLayout.NORTH);
        activos.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.add(nuevo, BorderLayout.NORTH);
        centro.add(activos, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        devolver.addActionListener(e -> registrarDevolucion());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(devolver);
        add(botones, BorderLayout.SOUTH);
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

            List<Prestamo> lista = prestamos.activos();
            modelo.setFilas(lista);
            long atrasados = lista.stream().filter(Prestamo::estaAtrasado).count();
            resumen.setText("Préstamos activos: " + lista.size() + "  ·  atrasados: " + atrasados);
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