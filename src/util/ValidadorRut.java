package util;

import java.util.regex.Pattern;

/**
 * Valida y normaliza RUT chilenos (formato + dígito verificador, módulo 11).
 * Formatos aceptados: "12.345.678-5" o "12345678-5" (DV puede ser K/k).
 */
public final class ValidadorRut {

    private static final Pattern FORMATO =
            Pattern.compile("^(\\d{1,2}\\.\\d{3}\\.\\d{3}|\\d{7,8})-[\\dkK]$");

    private ValidadorRut() { }

    public static boolean esValido(String rut) {
        if (rut == null) return false;
        String limpio = rut.trim();
        if (!FORMATO.matcher(limpio).matches()) return false;

        String[] partes = limpio.replace(".", "").split("-");
        int cuerpo = Integer.parseInt(partes[0]);
        char dvIngresado = Character.toUpperCase(partes[1].charAt(0));
        return calcularDv(cuerpo) == dvIngresado;
    }

    /** Devuelve el RUT sin puntos y con DV en mayúscula: "12345678-K". */
    public static String normalizar(String rut) {
        if (!esValido(rut)) {
            throw new IllegalArgumentException("RUT inválido: " + rut);
        }
        return rut.trim().replace(".", "").toUpperCase();
    }

    /** Formato de presentación: "12.345.678-K". */
    public static String formatear(String rut) {
        String normalizado = normalizar(rut);
        String[] partes = normalizado.split("-");
        StringBuilder cuerpo = new StringBuilder(partes[0]);
        for (int i = cuerpo.length() - 3; i > 0; i -= 3) {
            cuerpo.insert(i, '.');
        }
        return cuerpo.append('-').append(partes[1]).toString();
    }

    public static char calcularDv(int cuerpo) {
        int suma = 0;
        int multiplicador = 2;
        while (cuerpo > 0) {
            suma += (cuerpo % 10) * multiplicador;
            cuerpo /= 10;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }
        int resto = 11 - (suma % 11);
        if (resto == 11) return '0';
        if (resto == 10) return 'K';
        return (char) ('0' + resto);
    }
}