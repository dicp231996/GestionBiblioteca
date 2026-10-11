package model.enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/** Niveles de privilegio: cada rol trae su conjunto de permisos. */
public enum Rol {
    ESTUDIANTE("Estudiante", EnumSet.of(
            Permiso.CONSULTAR_CATALOGO,
            Permiso.SOLICITAR_PRESTAMO,
            Permiso.DEVOLVER_LIBRO,
            Permiso.CONSULTAR_PROPIOS_PRESTAMOS)),

    BIBLIOTECARIO("Bibliotecario", EnumSet.allOf(Permiso.class)); // gestión total

    private final String etiqueta;
    private final Set<Permiso> permisos;

    Rol(String etiqueta, Set<Permiso> permisos) {
        this.etiqueta = etiqueta;
        this.permisos = Collections.unmodifiableSet(permisos);
    }

    public boolean tienePermiso(Permiso permiso) {
        return permisos.contains(permiso);
    }

    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}