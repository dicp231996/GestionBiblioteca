package ui.panels;

import model.entities.Estudiante;
import model.resources.Libro;
import model.transactions.Prestamo;
import ui.components.Estilo;
import ui.components.Mensajes;
import ui.components.Seccion;
import ui.components.TablaModelo;
import ui.components.Tablas;
import ui.controller.EstudianteController;
import ui.controller.ReporteController;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ui.components.TablaModelo.columna;

/** Reportes: libros más prestados, libros actualmente en préstamo e historial por estudiante. */
public class ReportesPanel extends Seccion {

    private static final long serialVersionUID = 1L;
    private static final int TOP = 10;

    private final ReporteController reportes;
    private final EstudianteController estudiantes;

    /** Fila del ranking: posición, libro y cantidad de préstamos. */
    private record Ranking(int posicion, Libro libro, long prestamos) { }

    private final TablaModelo<Ranking> modeloRanking = new TablaModelo<>(
            columna("#", Integer.class, Ranking::posicion),
            columna("Título", String.class, r -> r.libro().getTitulo()),
            columna("Autor", String.class, r -> r.libro().getAutor()),
            columna("Préstamos", Long.class, Ranking::prestamos));
    private final TablaModelo<Prestamo> modeloEnPrestamo = new TablaModelo<>(
            columna("Libro", String.class, p -> p.getLibro().getTitulo()),
            columna("Estudiante", String.class, p -> p.getEstudiante().getNombreCompleto()),
            columna("Curso", String.class, p -> p.getEstudiante().getCurso().toString()),
            columna("Vence", LocalDate.class, Prestamo::getFechaVencimiento),
            columna("Días atraso", Long.class, Prestamo::diasAtraso));
    private final TablaModelo<Prestamo> modeloHistorial = new TablaModelo<>(
            columna("Libro", String.class, p -> p.getLibro().getTitulo()),
            columna("Prestado", LocalDate.class, Prestamo::getFechaPrestamo),
            columna("Vence", LocalDate.class, Prestamo::getFechaVencimiento),
            columna("Devuelto", LocalDate.class, Prestamo::getFechaDevolucion),
            columna("Estado", String.class, p -> p.getEstado().getEtiqueta()),
            columna("Observación", String.class, Prestamo::getObservacion));

    private final JComboBox<Estudiante> comboEstudiante = new JComboBox<>();
    private final JLabel totalEnPrestamo = Estilo.ayuda("");
    private long maximoRanking = 1;

    public ReportesPanel(ReporteController reportes, EstudianteController estudiantes) {
        super("Reportes", "Información para la toma de decisiones de la biblioteca.");
        this.reportes = reportes;
        this.estudiantes = estudiantes;

        JTable ranking = Tablas.crear(modeloRanking);
        Tablas.anchos(ranking, 30, 320, 180, 260);
        ranking.getColumnModel().getColumn(3).setCellRenderer(new BarraRenderer());

        JTable enPrestamo = Tablas.crear(modeloEnPrestamo, Prestamo::estaAtrasado);
        Tablas.anchos(enPrestamo, 280, 230, 100, 90, 80);
        JPanel panelEnPrestamo = new JPanel(new BorderLayout(0, 6));
        panelEnPrestamo.add(totalEnPrestamo, BorderLayout.NORTH);
        panelEnPrestamo.add(Tablas.conScroll(enPrestamo), BorderLayout.CENTER);

        JTable historial = Tablas.crear(modeloHistorial, p -> p.estaActivo() && p.estaAtrasado());
        Tablas.anchos(historial, 260, 80, 80, 80, 140, 220);
        comboEstudiante.addActionListener(e -> cargarHistorial());
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        selector.add(new JLabel("Estudiante:"));
        selector.add(comboEstudiante);
        JPanel panelHistorial = new JPanel(new BorderLayout(0, 6));
        panelHistorial.add(selector, BorderLayout.NORTH);
        panelHistorial.add(Tablas.conScroll(historial), BorderLayout.CENTER);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Libros más prestados", Tablas.conScroll(ranking));
        pestanas.addTab("Libros en préstamo", panelEnPrestamo);
        pestanas.addTab("Historial por estudiante", panelHistorial);
        add(pestanas, BorderLayout.CENTER);

        JButton actualizar = Estilo.boton("Actualizar reportes");
        actualizar.addActionListener(e -> refrescar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setOpaque(false);
        botones.add(actualizar);
        add(botones, BorderLayout.SOUTH);
    }

    @Override
    public void refrescar() {
        Mensajes.intentar(this, () -> {
            List<Ranking> filas = new ArrayList<>();
            int posicion = 1;
            for (Map.Entry<Libro, Long> e : reportes.librosMasPrestados(TOP).entrySet()) {
                filas.add(new Ranking(posicion++, e.getKey(), e.getValue()));
            }
            maximoRanking = filas.isEmpty() ? 1 : filas.get(0).prestamos();
            modeloRanking.setFilas(filas);

            List<Prestamo> enPrestamo = reportes.librosEnPrestamo();
            modeloEnPrestamo.setFilas(enPrestamo);
            long atrasados = enPrestamo.stream().filter(Prestamo::estaAtrasado).count();
            totalEnPrestamo.setText(enPrestamo.size() + " libros prestados en este momento · " + atrasados + " atrasados");

            Object elegido = comboEstudiante.getSelectedItem();
            comboEstudiante.removeAllItems();
            estudiantes.listar(EstudianteController.Filtro.TODOS).stream()
                    .sorted(Comparator.comparing(Estudiante::getNombreCompleto))
                    .forEach(comboEstudiante::addItem);
            if (elegido != null) comboEstudiante.setSelectedItem(elegido);
            cargarHistorial();
        });
    }

    private void cargarHistorial() {
        Estudiante est = (Estudiante) comboEstudiante.getSelectedItem();
        if (est == null) {
            modeloHistorial.setFilas(List.of());
            return;
        }
        Mensajes.intentar(this, () -> modeloHistorial.setFilas(reportes.historialPorEstudiante(est)));
    }

    /** Dibuja la cantidad de préstamos como una barra proporcional al libro más prestado. */
    private final class BarraRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private long valor;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int f, int c) {
            super.getTableCellRendererComponent(t, "", sel, foco, f, c);
            valor = v == null ? 0 : (Long) v;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int ancho = (int) ((getWidth() - 50) * (valor / (double) Math.max(1, maximoRanking)));
            g.setColor(Estilo.ACENTO);
            g.fillRoundRect(4, 6, Math.max(2, ancho), getHeight() - 12, 6, 6);
            g.setColor(new Color(0x333333));
            g.drawString(String.valueOf(valor), ancho + 10, getHeight() / 2 + 5);
        }
    }
}