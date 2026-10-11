package ui.components;

import util.FormatoFecha;

import java.time.LocalDate;

/** Formatos de presentación de la interfaz (delegan en util.FormatoFecha: dd-MM-yyyy). */
public final class Formato {

    private Formato() { }

    public static String fecha(LocalDate fecha) {
        return FormatoFecha.formatear(fecha);
    }
}