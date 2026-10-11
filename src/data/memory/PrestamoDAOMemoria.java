package data.memory;

import data.dao.PrestamoDAO;
import model.transactions.Prestamo;

import java.util.List;

public class PrestamoDAOMemoria extends MemoriaCrudDAO<Prestamo> implements PrestamoDAO {

    @Override
    public List<Prestamo> listarActivos() {
        return filtrar(Prestamo::estaActivo);
    }

    @Override
    public List<Prestamo> listarPorEstudiante(int idEstudiante) {
        return filtrar(p -> p.getEstudiante().getId() == idEstudiante);
    }

    @Override
    public List<Prestamo> listarActivosPorLibro(int idLibro) {
        return filtrar(p -> p.estaActivo() && p.getLibro().getId() == idLibro);
    }
}