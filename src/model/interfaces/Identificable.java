package model.interfaces;

import model.entities.Usuario;
import model.enums.Permiso;
import model.enums.Rol;
import model.exceptions.AccesoDenegadoException;

/**
 * Contrato para todo lo que puede iniciar sesión en el sistema.
 * Los métodos default resuelven credenciales y privilegios a partir del Usuario.
 */
public interface Autenticable {

    Usuario getUsuario();

    default String getNombreUsuario() {
        return getUsuario().getNombreUsuario();
    }

    default Rol getRol() {
        return getUsuario().getRol();
    }

    default boolean autenticar(String contrasena) {
        return getUsuario().isActivo() && getUsuario().verificarContrasena(contrasena);
    }

    default boolean tienePermiso(Permiso permiso) {
        return getRol().tienePermiso(permiso);
    }

    /** Lanza AccesoDenegadoException si el rol no tiene el permiso. */
    default void exigirPermiso(Permiso permiso) {
        if (!tienePermiso(permiso)) {
            throw new AccesoDenegadoException(
                    "El rol " + getRol() + " no tiene permiso para: " + permiso);
        }
    }
}