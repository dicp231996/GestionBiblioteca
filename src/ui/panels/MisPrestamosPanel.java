package ui.panels;

import model.entities.Estudiante;
import model.transactions.Prestamo;
import ui.components.BarraEstado;
import ui.components.Estilo;
import ui.components.Mensajes;
import ui.components.Seccion;
import ui.components.TablaModelo;
import ui.components.Tablas;
import ui.controller.PrestamoController;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;

import static ui.components.TablaModelo.columna;

/** Estudiante: sus préstamos (activos e historial) y la opción de devolver. */
public class MisPrestamosPanel extends Seccion {

    private static final long serialVersionUID = 1L;

    private final PrestamoController prestamos;
    private final BarraEstado barra;

    private final TablaModelo<Prestamo> modelo = new TablaModelo<>(
            columna("Libro", String.class, p -> p.getLibro().getTitulo()),
            columna("Autor", String.class, p -> p.getLibro().getAutor()),
            columna("Prestado", LocalDate.class, Prestamo::getFechaPrestamo),
            columna("Vence", LocalDate.class, Prestamo::getFechaVencimiento),
            columna("Devuelto", LocalDate.class, Prestamo::getFechaDevolucion),
            columna("Estado", String.class, p -> p.estaActivo() && p.estaAtrasado()
                    ? "Atrasado (" + p.diasAtraso() + " días)" : p.getEstado().getEtiqueta()),
            columna("Observación", String.class, Prestamo::getObservacion));
    private final JTable tabla = Tablas.crear(modelo, p -> p.estaActivo() && p.estaAtrasado());
    private final JLabel resumen = new JLabel();
    private final JButton devolver = Estilo.botonPrincipal("Devolver libro");

    public MisPrestamosPanel(PrestamoController prestamos, BarraEstado barra) {
        super("Mis préstamos", "Tus libros prestados aparecen primero. Las filas en rojo están atrasadas.");
        this.prestamos = prestamos;
        this.barra = barra;
        Tablas.anchos(tabla, 240, 150, 80, 80, 80, 140, 200);
        resumen.setFont(Estilo.SUBTITULO);
        resumen.setForeground(Estilo.PRIMARIO);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(resumen, BorderLayout.NORTH);
        centro.add(tarjeta(Tablas.conScroll(tabla)), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        devolver.addActionListener(e -> devolver());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setOpaque(false);
        botones.add(devolver);
        add(botones, BorderLayout.SOUTH);
    }

    @Override
    public void refrescar() {
        Mensajes.intentar(this, () -> {
            List<Prestamo> lista = prestamos.misPrestamos();
            modelo.setFilas(lista);
            long activos = lista.stream().filter(Prestamo::estaActivo).count();
            long atrasados = lista.stream().filter(p -> p.estaActivo() && p.estaAtrasado()).count();
            resumen.setText("Tienes " + activos + " de " + Estudiante.MAX_LIBROS_PRESTADOS + " libros prestados"
                    + (atrasados > 0 ? " · " + atrasados + " atrasado(s)" : ""));
        });
    }

    private void devolver() {
        Prestamo prestamo = Tablas.seleccion(tabla, modelo).orElse(null);
        if (prestamo == null || !prestamo.estaActivo()) {
            Mensajes.info(this, "Selecciona un libro que tengas prestado (estado Activo o Atrasado).");
            return;
        }
        devolver.setEnabled(false);
        barra.iniciarProceso("Registrando devolución en segundo plano...");
        prestamos.registrarDevolucion(prestamo.getId(),
                devuelto -> {
                    devolver.setEnabled(true);
                    barra.exito("Devolviste \"" + devuelto.getLibro().getTitulo() + "\""
                            + (devuelto.estaAtrasado() ? " con " + devuelto.diasAtraso() + " día(s) de atraso" : " a tiempo"));
                    refrescar();
                },
                error -> {
                    devolver.setEnabled(true);
                    barra.fallo(error);
                    Mensajes.error(this, error);
                });
    }
}