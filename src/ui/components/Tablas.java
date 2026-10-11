package ui.components;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

/** Fábrica de tablas con el mismo aspecto en toda la aplicación. */
public final class Tablas {

    private Tablas() { }

    public static <T> JTable crear(TablaModelo<T> modelo) {
        return crear(modelo, null);
    }

    /** @param resaltar filas que se marcan con fondo rojo claro (por ejemplo, préstamos atrasados). */
    public static <T> JTable crear(TablaModelo<T> modelo, Predicate<T> resaltar) {
        JTable tabla = new JTable(modelo) {
            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int fila, int columna) {
                Component c = super.prepareRenderer(renderer, fila, columna);
                if (resaltar != null && !isRowSelected(fila)
                        && resaltar.test(modelo.getFila(convertRowIndexToModel(fila)))) {
                    c.setBackground(Estilo.FILA_ATRASADA);
                }
                return c;
            }
        };
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(22);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setDefaultRenderer(LocalDate.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void setValue(Object valor) {
                setText(Formato.fecha((LocalDate) valor));
            }
        });
        return tabla;
    }

    public static JScrollPane conScroll(JTable tabla) {
        return new JScrollPane(tabla);
    }

    /** Fila seleccionada (considerando el orden aplicado por el usuario al hacer clic en el encabezado). */
    public static <T> Optional<T> seleccion(JTable tabla, TablaModelo<T> modelo) {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? Optional.empty() : Optional.of(modelo.getFila(tabla.convertRowIndexToModel(fila)));
    }

    public static void anchos(JTable tabla, int... anchos) {
        for (int i = 0; i < anchos.length && i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }
}