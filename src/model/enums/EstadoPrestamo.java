package model.enums;

public enum EstadoPrestamo {
    ACTIVO("Activo"),
    DEVUELTO("Devuelto"),
    DEVUELTO_CON_ATRASO("Devuelto con atraso");

    private final String etiqueta;

    EstadoPrestamo(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}