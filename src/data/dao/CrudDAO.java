package data.dao;

import java.util.List;
import java.util.Optional;

/** Operaciones CRUD comunes a todas las entidades. Cada DAO concreto agrega sus consultas propias. */
public interface CrudDAO<T> {

    /** Inserta y devuelve la entidad con el id generado. */
    T insertar(T entidad);

    boolean actualizar(T entidad);

    boolean eliminar(int id);

    Optional<T> buscarPorId(int id);

    List<T> listarTodos();
}