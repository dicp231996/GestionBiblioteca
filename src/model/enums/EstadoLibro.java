package model.enums;

/** Estado administrativo del libro (la disponibilidad real depende además del stock). */
public enum EstadoLibro {
    ACTIVO("Activo"),
    EN_REPARACION("En reparación"),
    DADO_DE_BAJA("Dado de baja");

    private final String etiqueta;

    EstadoLibro(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}