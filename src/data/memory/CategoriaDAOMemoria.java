package data.memory;

import data.dao.CategoriaDAO;
import model.classification.Categoria;

import java.util.Optional;

public class CategoriaDAOMemoria extends MemoriaCrudDAO<Categoria> implements CategoriaDAO {

    @Override
    public Optional<Categoria> buscarPorNombre(String nombre) {
        return buscarPrimero(c -> c.getNombre().equalsIgnoreCase(nombre.trim()));
    }
}