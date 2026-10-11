package ui.controller;

import model.core.Persona;
import model.entities.Estudiante;
import model.enums.Permiso;

import java.util.Objects;

/** Guarda quién inició sesión. Las vistas la consultan para mostrar u ocultar opciones según el rol. */
public class Sesion {

    private Persona usuarioActual;

    public void iniciar(Persona persona) {
        this.usuarioActual = Objects.requireNonNull(persona);
    }

    public void cerrar() {
        this.usuarioActual = null;
    }

    public boolean estaIniciada() {
        return usuarioActual != null;
    }

    public Persona getUsuarioActual() {
        if (usuarioActual == null) throw new IllegalStateException("No hay una sesión iniciada");
        return usuarioActual;
    }

    public boolean puede(Permiso permiso) {
        return usuarioActual != null && usuarioActual.tienePermiso(permiso);
    }

    public boolean esEstudiante() {
        return usuarioActual instanceof Estudiante;
    }
}