package data.dao;

import model.classification.Categoria;

import java.util.Optional;

public interface CategoriaDAO extends CrudDAO<Categoria> {
    Optional<Categoria> buscarPorNombre(String nombre);
}