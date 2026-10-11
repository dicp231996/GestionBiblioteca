package data.dao;

import model.resources.Libro;

import java.util.List;
import java.util.Optional;

public interface LibroDAO extends CrudDAO<Libro> {
    Optional<Libro> buscarPorIsbn(String isbn);
    List<Libro> buscarPorTitulo(String texto);
}