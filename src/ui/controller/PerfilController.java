package ui.controller;

import model.core.Persona;
import model.services.ServicioGestion;

/** Datos del usuario conectado, recargados desde la BD (por ejemplo, tras pedir un libro). */
public class PerfilController {

    private final ServicioGestion gestion;
    private final Sesion sesion;

    public PerfilController(ServicioGestion gestion, Sesion sesion) {
        this.gestion = gestion;
        this.sesion = sesion;
    }

    public Persona datosActuales() {
        Persona actual = sesion.getUsuarioActual();
        if (sesion.esEstudiante()) {
            gestion.buscarEstudiantePorId(actual, actual.getId()).ifPresent(sesion::iniciar);
        }
        return sesion.getUsuarioActual();
    }
}