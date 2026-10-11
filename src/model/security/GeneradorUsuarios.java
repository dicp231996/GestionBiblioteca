package model.security;

import model.core.Persona;
import util.TextoUtil;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Genera el nombre de usuario a partir de los datos personales:
 *   2 primeras letras del primer nombre
 * + apellido paterno completo
 * + primera letra del apellido materno
 * + 4 primeros dígitos del RUT
 * + mes de nacimiento (2 dígitos) + año de nacimiento (4 dígitos)
 *
 * Ej: Juan Pablo Pérez González, RUT 21.345.678-4, nacido el 02/06/2011 -> "juperezg2134062011"
 * Todo en minúscula, sin tildes ni espacios.
 */
public final class GeneradorUsuario {

    private static final int LETRAS_NOMBRE = 2;
    private static final int DIGITOS_RUT = 4;

    private GeneradorUsuario() { }

    /** Genera el usuario a partir de una persona ya construida. */
    public static String generar(Persona persona) {
        Objects.requireNonNull(persona);
        return generar(persona.getNombres(), persona.getApellidoPaterno(),
                persona.getApellidoMaterno(), persona.getRut(), persona.getFechaNacimiento());
    }

    /** Sobrecarga con los datos sueltos (útil para previsualizar el usuario en un formulario). */
    public static String generar(String nombres, String apellidoPaterno, String apellidoMaterno,
                                 String rut, LocalDate fechaNacimiento) {
        String primerNombre = TextoUtil.normalizar(TextoUtil.requerido(nombres, "nombres").split("\\s+")[0]);
        String paterno = TextoUtil.normalizar(TextoUtil.requerido(apellidoPaterno, "apellido paterno"));
        String materno = TextoUtil.normalizar(TextoUtil.requerido(apellidoMaterno, "apellido materno"));
        String cuerpoRut = TextoUtil.requerido(rut, "RUT").replace(".", "").split("-")[0];
        Objects.requireNonNull(fechaNacimiento, "La fecha de nacimiento es obligatoria");

        StringBuilder sb = new StringBuilder();
        sb.append(primerNombre, 0, Math.min(LETRAS_NOMBRE, primerNombre.length()))
                .append(paterno)
                .append(materno.charAt(0))
                .append(cuerpoRut, 0, Math.min(DIGITOS_RUT, cuerpoRut.length()))
                .append(String.format("%02d", fechaNacimiento.getMonthValue()))
                .append(fechaNacimiento.getYear());
        return sb.toString();
    }
}