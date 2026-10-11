package services;

import data.dao.BibliotecarioDAO;
import data.dao.EstudianteDAO;
import model.core.Persona;

import java.util.Optional;

/**
 * Módulo de autenticación: busca el usuario en la "BD" (vía DAO) y verifica la contraseña.
 * Devuelve la Persona concreta (Estudiante o Bibliotecario); la UI habilita opciones según su rol.
 */
public class AutenticacionService {

    private final EstudianteDAO estudianteDAO;
    private final BibliotecarioDAO bibliotecarioDAO;

    public AutenticacionService(EstudianteDAO estudianteDAO, BibliotecarioDAO bibliotecarioDAO) {
        this.estudianteDAO = estudianteDAO;
        this.bibliotecarioDAO = bibliotecarioDAO;
    }

    public Optional<Persona> iniciarSesion(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null) {
            return Optional.empty();
        }
        String usuario = nombreUsuario.trim();

        Optional<? extends Persona> encontrada = bibliotecarioDAO.buscarPorNombreUsuario(usuario);
        if (encontrada.isEmpty()) {
            encontrada = estudianteDAO.buscarPorNombreUsuario(usuario);
        }
        return encontrada
                .filter(p -> p.autenticar(contrasena))   // polimorfismo vía la interfaz Autenticable
                .map(p -> (Persona) p);
    }
}