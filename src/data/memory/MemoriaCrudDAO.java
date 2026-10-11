package data.memory;

import data.dao.CrudDAO;
import model.interfaces.Identificable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación TEMPORAL en memoria (sin MySQL) para desarrollar y probar la lógica.
 * Cuando exista la BD, se crean las clases en data.mysql (LibroDAOMySQL, etc.) y basta con
 * cambiar qué implementación se inyecta en app.Main: los servicios no cambian (eso es DAO).
 */
public abstract class MemoriaCrudDAO<T extends Identificable> implements CrudDAO<T> {

    protected final Map<Integer, T> datos = new ConcurrentSkipListMap<>();
    private final AtomicInteger secuencia = new AtomicInteger(0);

    @Override
    public T insertar(T entidad) {
        int id = secuencia.incrementAndGet();   // simula AUTO_INCREMENT
        entidad.setId(id);
        datos.put(id, entidad);
        return entidad;
    }

    @Override
    public boolean actualizar(T entidad) {
        return datos.replace(entidad.getId(), entidad) != null;
    }

    @Override
    public boolean eliminar(int id) {
        return datos.remove(id) != null;
    }

    @Override
    public Optional<T> buscarPorId(int id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(datos.values());
    }

    protected List<T> filtrar(Predicate<T> condicion) {
        return datos.values().stream().filter(condicion).collect(Collectors.toList());
    }

    protected Optional<T> buscarPrimero(Predicate<T> condicion) {
        return datos.values().stream().filter(condicion).findFirst();
    }
}