package ui.components;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Modelo de tabla genérico: cada columna se define con un nombre, su tipo y una función que
 * saca el valor desde el objeto de la fila. Así una misma clase sirve para libros, estudiantes,
 * préstamos, etc., sin escribir un TableModel distinto para cada uno.
 */
public class TablaModelo<T> extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    /** Definición de una columna. */
    public static final class Columna<T> {
        private final String nombre;
        private final Class<?> tipo;
        private final Function<T, Object> valor;

        private Columna(String nombre, Class<?> tipo, Function<T, Object> valor) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.valor = valor;
        }
    }

    public static <T> Columna<T> columna(String nombre, Class<?> tipo, Function<T, Object> valor) {
        return new Columna<>(nombre, tipo, valor);
    }

    private final List<Columna<T>> columnas = new ArrayList<>();
    private final List<T> filas = new ArrayList<>();

    @SafeVarargs
    public TablaModelo(Columna<T>... columnas) {
        for (Columna<T> c : columnas) {
            this.columnas.add(c);
        }
    }

    public void setFilas(List<T> nuevas) {
        filas.clear();
        filas.addAll(nuevas);
        fireTableDataChanged();
    }

    public T getFila(int indiceModelo) {
        return filas.get(indiceModelo);
    }

    public List<T> getFilas() {
        return List.copyOf(filas);
    }

    @Override
    public int getRowCount() { return filas.size(); }

    @Override
    public int getColumnCount() { return columnas.size(); }

    @Override
    public String getColumnName(int columna) { return columnas.get(columna).nombre; }

    @Override
    public Class<?> getColumnClass(int columna) { return columnas.get(columna).tipo; }

    @Override
    public Object getValueAt(int fila, int columna) {
        return columnas.get(columna).valor.apply(filas.get(fila));
    }
}