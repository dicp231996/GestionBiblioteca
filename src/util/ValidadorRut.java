package util;

import java.util.regex.Pattern;

/**
 * Valida el FORMATO de un RUT chileno y lo normaliza.
 * Acepta: "12.345.678-5", "12345678-5", "123456785", "1.234.567-K" (la K en mayúscula o minúscula).
 * Solo revisa la forma (7 u 8 dígitos + dígito verificador); no calcula el dígito verificador.
 */
public final class ValidadorRut {

    /** 7 u 8 dígitos (con o sin puntos), guion opcional y un dígito o K al final. */
    private static final Pattern FORMATO =
            Pattern.compile("^(\\d{1,2}\\.?\\d{3}\\.?\\d{3})-?([\\dkK])$");

    private ValidadorRut() { }

    public static boolean esValido(String rut) {
        return rut != null && FORMATO.matcher(rut.trim()).matches();
    }

    /** Devuelve el RUT sin puntos, con guion y DV en mayúscula: "12345678-K". */
    public static String normalizar(String rut) {
        if (!esValido(rut)) {
            throw new IllegalArgumentException("RUT inválido: " + rut + " (use el formato 12.345.678-9)");
        }
        String limpio = rut.trim().replace(".", "").replace("-", "").toUpperCase();
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);
        return cuerpo + "-" + dv;
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
}