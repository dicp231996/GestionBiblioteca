package ui.controller;

import model.classification.Categoria;
import model.resources.Libro;
import model.services.ServicioGestion;

import java.util.List;
import java.util.stream.Collectors;

/** Catálogo e inventario de libros. */
public class LibroController {

    private final ServicioGestion gestion;
    private final Sesion sesion;

    public LibroController(ServicioGestion gestion, Sesion sesion) {
        this.gestion = gestion;
        this.sesion = sesion;
    }

    public List<Libro> listar() {
        return gestion.listarLibros(sesion.getUsuarioActual());
    }

    public List<Libro> buscar(String texto) {
        if (texto == null || texto.isBlank()) return listar();
        return gestion.buscarLibrosPorTitulo(sesion.getUsuarioActual(), texto);
    }

    /** Libros que hoy se pueden prestar (activos y con stock). */
    public List<Libro> disponibles() {
        return listar().stream().filter(Libro::estaDisponibleParaPrestamo).collect(Collectors.toList());
    }

    public Libro registrar(Libro libro) {
        return gestion.registrarLibro(sesion.getUsuarioActual(), libro);
    }

    public void actualizar(Libro libro) {
        gestion.actualizarLibro(sesion.getUsuarioActual(), libro);
    }

    public void eliminar(Libro libro) {
        gestion.eliminarLibro(sesion.getUsuarioActual(), libro.getId());
    }

    public List<Categoria> categorias() {
        return gestion.listarCategorias();
    }

    public Categoria registrarCategoria(Categoria categoria) {
        return gestion.registrarCategoria(sesion.getUsuarioActual(), categoria);
    }

    public void actualizarCategoria(Categoria categoria) {
        gestion.actualizarCategoria(sesion.getUsuarioActual(), categoria);
    }

    public void eliminarCategoria(Categoria categoria) {
        gestion.eliminarCategoria(sesion.getUsuarioActual(), categoria.getId());
    }
}