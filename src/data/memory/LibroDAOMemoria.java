package data.memory;

import data.dao.LibroDAO;
import model.resources.Libro;
import util.ValidadorIsbn;

import java.util.List;
import java.util.Optional;

public class LibroDAOMemoria extends MemoriaCrudDAO<Libro> implements LibroDAO {

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        String limpio = ValidadorIsbn.limpiar(isbn);
        return buscarPrimero(l -> l.getIsbn().equals(limpio));
    }

    @Override
    public List<Libro> buscarPorTitulo(String texto) {
        String buscado = texto.toLowerCase();
        return filtrar(l -> l.getTitulo().toLowerCase().contains(buscado));
    }

    @Override
    public List<Libro> listarPorCategoria(int idCategoria) {
        return filtrar(l -> l.getCategoria().getId() == idCategoria);
    }
}