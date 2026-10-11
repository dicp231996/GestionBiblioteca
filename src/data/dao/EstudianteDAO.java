package data.dao;

import model.entities.Estudiante;
import model.valueobjects.Curso;

import java.util.List;
import java.util.Optional;

public interface EstudianteDAO extends CrudDAO<Estudiante> {
    Optional<Estudiante> buscarPorRut(String rut);
    Optional<Estudiante> buscarPorNombreUsuario(String nombreUsuario);
    List<Estudiante> listarPorCurso(Curso curso);
}