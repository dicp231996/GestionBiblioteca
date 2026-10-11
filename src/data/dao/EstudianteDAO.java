package data.dao;

import model.entities.Estudiante;

import java.util.Optional;

public interface EstudianteDAO extends CrudDAO<Estudiante> {
    Optional<Estudiante> buscarPorRut(String rut);
    Optional<Estudiante> buscarPorNombreUsuario(String nombreUsuario);
}