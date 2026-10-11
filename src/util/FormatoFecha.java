package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Formato de fechas al estilo chileno: dd-MM-yyyy (ej. 25-11-2013). */
public final class FormatoFecha {

    public static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private FormatoFecha() { }

    /** @return la fecha como "dd-MM-yyyy", o "—" si es null. */
    public static String formatear(LocalDate fecha) {
        return fecha == null ? "—" : fecha.format(FORMATO);
    }
}