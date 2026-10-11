package util;

/** Valida ISBN-10 e ISBN-13 (acepta guiones o espacios). */
public final class ValidadorIsbn {

    private ValidadorIsbn() { }

    public static String limpiar(String isbn) {
        return isbn == null ? "" : isbn.replaceAll("[\\s-]", "").toUpperCase();
    }

    public static boolean esValido(String isbn) {
        String limpio = limpiar(isbn);
        if (limpio.length() == 13) return esIsbn13(limpio);
        if (limpio.length() == 10) return esIsbn10(limpio);
        return false;
    }

    private static boolean esIsbn13(String isbn) {
        if (!isbn.matches("\\d{13}")) return false;
        int suma = 0;
        for (int i = 0; i < 13; i++) {
            int digito = isbn.charAt(i) - '0';
            suma += (i % 2 == 0) ? digito : digito * 3;
        }
        return suma % 10 == 0;
    }

    private static boolean esIsbn10(String isbn) {
        if (!isbn.matches("\\d{9}[\\dX]")) return false;
        int suma = 0;
        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);
            int valor = (c == 'X') ? 10 : c - '0';
            suma += valor * (10 - i);
        }
        return suma % 11 == 0;
    }
}