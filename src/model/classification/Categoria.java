package model.classification;

import model.interfaces.Identificable;
import util.TextoUtil;

/** Categoría del libro (Terror, Fantasía, ...). Es clase y no enum porque vive en su propia tabla. */
public final class Categoria implements Identificable {

    private int id;
    private String nombre;
    private String descripcion;

    public Categoria(String nombre, String descripcion) {
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    public Categoria(int id, String nombre, String descripcion) {
        this(nombre, descripcion);
        this.id = id;
    }

    @Override
    public int getId() { return id; }

    @Override
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        this.nombre = TextoUtil.requerido(nombre, "nombre de categoría");
    }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria)) return false;
        return nombre.equalsIgnoreCase(((Categoria) o).nombre);
    }

    @Override
    public int hashCode() { return nombre.toLowerCase().hashCode(); }

    @Override
    public String toString() { return nombre; }
}