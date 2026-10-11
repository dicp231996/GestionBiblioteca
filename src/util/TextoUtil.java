package util;

import java.text.Normalizer;

public final class TextoUtil {

    private TextoUtil() { }

    /** "Núñez" -> "nunez": sin tildes, sin espacios, en minúscula (para nombres de usuario). */
    public static String normalizar(String texto) {
        if (texto == null) return "";
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /** Valida que un texto obligatorio no venga vacío y lo devuelve recortado. */
    public static String requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio");
        }
        return valor.trim();
    }
}