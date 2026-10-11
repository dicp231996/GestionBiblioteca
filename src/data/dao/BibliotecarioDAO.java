package data.dao;

import model.entities.Bibliotecario;

import java.util.Optional;

public interface BibliotecarioDAO extends CrudDAO<Bibliotecario> {
    Optional<Bibliotecario> buscarPorRut(String rut);
    Optional<Bibliotecario> buscarPorNombreUsuario(String nombreUsuario);
}