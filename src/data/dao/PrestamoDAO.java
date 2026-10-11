package data.dao;

import model.transactions.Prestamo;

import java.util.List;

public interface PrestamoDAO extends CrudDAO<Prestamo> {
    List<Prestamo> listarActivos();
    List<Prestamo> listarPorEstudiante(int idEstudiante);
    List<Prestamo> listarActivosPorLibro(int idLibro);
}