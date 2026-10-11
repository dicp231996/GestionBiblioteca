package ui.controller;

import model.core.Persona;
import model.entities.Estudiante;
import model.services.ServicioGestion;
import services.AutenticacionService;

import java.util.Optional;

/** Inicio de sesión y autorregistro de estudiantes (pantalla de login). */
public class LoginController {

    private final AutenticacionService autenticacion;
    private final ServicioGestion gestion;
    private final Sesion sesion;

    public LoginController(AutenticacionService autenticacion, ServicioGestion gestion, Sesion sesion) {
        this.autenticacion = autenticacion;
        this.gestion = gestion;
        this.sesion = sesion;
    }

    /** @return la persona autenticada (y deja la sesión iniciada), o vacío si las credenciales no son válidas. */
    public Optional<Persona> iniciarSesion(String usuario, char[] contrasena) {
        Optional<Persona> persona = autenticacion.iniciarSesion(usuario, new String(contrasena));
        persona.ifPresent(sesion::iniciar);
        return persona;
    }

    public Estudiante autoregistrar(Estudiante estudiante) {
        return gestion.autoregistrarEstudiante(estudiante);
    }
}