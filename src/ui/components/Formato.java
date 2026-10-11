package ui.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Formatos de presentación (fechas al estilo chileno: dd-mm-aaaa). */
public final class Formato {

    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Formato() { }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "—" : fecha.format(FECHA);
    }
}