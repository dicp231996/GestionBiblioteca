package data.memory;

import data.dao.BibliotecarioDAO;
import model.entities.Bibliotecario;
import util.ValidadorRut;

import java.util.Optional;

public class BibliotecarioDAOMemoria extends MemoriaCrudDAO<Bibliotecario> implements BibliotecarioDAO {

    @Override
    public Optional<Bibliotecario> buscarPorRut(String rut) {
        if (!ValidadorRut.esValido(rut)) return Optional.empty();
        String normalizado = ValidadorRut.normalizar(rut);
        return buscarPrimero(b -> b.getRut().equals(normalizado));
    }

    @Override
    public Optional<Bibliotecario> buscarPorNombreUsuario(String nombreUsuario) {
        return buscarPrimero(b -> b.getNombreUsuario().equalsIgnoreCase(nombreUsuario));
    }
}