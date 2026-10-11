package model.interfaces;

/** Toda entidad que se guarda en la base de datos tiene un id numérico. */
public interface Identificable {
    int getId();
    void setId(int id);
}