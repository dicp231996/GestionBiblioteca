package model.security;

import model.enums.Rol;
import util.TextoUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Credenciales de acceso. La contraseña nunca se guarda en texto plano:
 * se almacena su hash SHA-256 (como sugiere la guía de la Experiencia 3).
 */
public class Usuario {

    public static final int LARGO_MINIMO_CONTRASENA = 6;

    private String nombreUsuario;
    private String hashContrasena;
    private Rol rol;
    private boolean activo;

    private Usuario(String nombreUsuario, String hashContrasena, Rol rol, boolean activo) {
        this.nombreUsuario = TextoUtil.requerido(nombreUsuario, "nombre de usuario");
        this.hashContrasena = Objects.requireNonNull(hashContrasena);
        this.rol = Objects.requireNonNull(rol, "El rol es obligatorio");
        this.activo = activo;
    }

    /** Crea un usuario nuevo a partir de la contraseña en texto plano. */
    public static Usuario crear(String nombreUsuario, String contrasenaPlano, Rol rol) {
        validarContrasena(contrasenaPlano);
        return new Usuario(nombreUsuario, hashear(contrasenaPlano), rol, true);
    }

    /** Reconstruye un usuario leído de la BD (ya trae el hash). */
    public static Usuario desdeBaseDeDatos(String nombreUsuario, String hashContrasena,
                                           Rol rol, boolean activo) {
        return new Usuario(nombreUsuario, hashContrasena, rol, activo);
    }

    public boolean verificarContrasena(String contrasenaPlano) {
        return contrasenaPlano != null && hashear(contrasenaPlano).equals(hashContrasena);
    }

    public void cambiarContrasena(String actual, String nueva) {
        if (!verificarContrasena(actual)) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }
        validarContrasena(nueva);
        this.hashContrasena = hashear(nueva);
    }

    private static void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < LARGO_MINIMO_CONTRASENA) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + LARGO_MINIMO_CONTRASENA + " caracteres");
        }
    }

    public static String hashear(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public String getNombreUsuario() { return nombreUsuario; }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = TextoUtil.requerido(nombreUsuario, "nombre de usuario");
    }

    public String getHashContrasena() { return hashContrasena; }

    public Rol getRol() { return rol; }

    public void setRol(Rol rol) { this.rol = Objects.requireNonNull(rol); }

    public boolean isActivo() { return activo; }

    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombreUsuario + " [" + rol + (activo ? "" : ", inactivo") + "]";
    }
}