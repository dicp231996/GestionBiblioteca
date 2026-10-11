package model.enums;

public enum Genero {
    FEMENINO("Femenino"),
    MASCULINO("Masculino");

    private final String etiqueta;

    Genero(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}