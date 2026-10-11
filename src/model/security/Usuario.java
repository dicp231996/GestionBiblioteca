package model.security;

import model.enums.Rol;
import util.TextoUtil;

import java.util.Objects;

/** Credenciales de acceso: nombre de usuario, contraseña, rol y si la cuenta está activa. */
public class Usuario {

    public static final int LARGO_MINIMO_CONTRASENA = 6;

    private String nombreUsuario;
    private String contrasena;
    private Rol rol;
    private boolean activo;

    private Usuario(String nombreUsuario, String contrasena, Rol rol, boolean activo) {
        this.nombreUsuario = TextoUtil.requerido(nombreUsuario, "nombre de usuario");
        this.contrasena = Objects.requireNonNull(contrasena);
        this.rol = Objects.requireNonNull(rol, "El rol es obligatorio");
        this.activo = activo;
    }

    /** Crea un usuario nuevo (valida el largo mínimo de la contraseña). */
    public static Usuario crear(String nombreUsuario, String contrasena, Rol rol) {
        validarContrasena(contrasena);
        return new Usuario(nombreUsuario, contrasena, rol, true);
    }

    /** Reconstruye un usuario leído de la BD. */
    public static Usuario desdeBaseDeDatos(String nombreUsuario, String contrasena, Rol rol, boolean activo) {
        return new Usuario(nombreUsuario, contrasena, rol, activo);
    }

    public boolean verificarContrasena(String contrasenaIngresada) {
        return contrasena.equals(contrasenaIngresada);
    }

    private static void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < LARGO_MINIMO_CONTRASENA) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + LARGO_MINIMO_CONTRASENA + " caracteres");
        }
    }

    public String getNombreUsuario() { return nombreUsuario; }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = TextoUtil.requerido(nombreUsuario, "nombre de usuario");
    }

    public String getContrasena() { return contrasena; }

    public Rol getRol() { return rol; }

    public void setRol(Rol rol) { this.rol = Objects.requireNonNull(rol); }

    public boolean isActivo() { return activo; }

    public void setActivo(boolean activo) { this.activo = activo; }

    /** No muestra la contraseña. */
    @Override
    public String toString() {
        return nombreUsuario + " [" + rol + (activo ? "" : ", inactivo") + "]";
    }
}