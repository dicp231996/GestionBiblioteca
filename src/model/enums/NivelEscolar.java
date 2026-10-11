package model.enums;

/** Niveles del liceo: de 7° Básico a 4° Medio. */
public enum NivelEscolar {
    SEPTIMO_BASICO("7° Básico"),
    OCTAVO_BASICO("8° Básico"),
    PRIMERO_MEDIO("1° Medio"),
    SEGUNDO_MEDIO("2° Medio"),
    TERCERO_MEDIO("3° Medio"),
    CUARTO_MEDIO("4° Medio");

    private final String etiqueta;

    NivelEscolar(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}