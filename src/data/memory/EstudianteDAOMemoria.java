package data.memory;

import data.dao.EstudianteDAO;
import model.entities.Estudiante;
import model.valueobjects.Curso;
import util.ValidadorRut;

import java.util.List;
import java.util.Optional;

public class EstudianteDAOMemoria extends MemoriaCrudDAO<Estudiante> implements EstudianteDAO {

    @Override
    public Optional<Estudiante> buscarPorRut(String rut) {
        if (!ValidadorRut.esValido(rut)) return Optional.empty();
        String normalizado = ValidadorRut.normalizar(rut);
        return buscarPrimero(e -> e.getRut().equals(normalizado));
    }

    @Override
    public Optional<Estudiante> buscarPorNombreUsuario(String nombreUsuario) {
        return buscarPrimero(e -> e.getNombreUsuario().equalsIgnoreCase(nombreUsuario));
    }

    @Override
    public List<Estudiante> listarPorCurso(Curso curso) {
        return filtrar(e -> e.getCurso().equals(curso));
    }
}