package util;

import java.util.regex.Pattern;

/**
 * Valida el FORMATO de un ISBN: 10 dígitos (el último puede ser X) o 13 dígitos.
 * Acepta guiones o espacios (ej. 978-956-12-3456-7). No calcula el dígito de control.
 */
public final class ValidadorIsbn {

    private static final Pattern FORMATO = Pattern.compile("^(\\d{9}[\\dX]|\\d{13})$");

    private ValidadorIsbn() { }

    /** Quita guiones y espacios: "978-956-12-3456-7" -> "9789561234567". */
    public static String limpiar(String isbn) {
        return isbn == null ? "" : isbn.replaceAll("[\\s-]", "").toUpperCase();
    }

    public static boolean esValido(String isbn) {
        return FORMATO.matcher(limpiar(isbn)).matches();
    }
}