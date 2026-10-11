package model.resources;

import model.classification.Categoria;
import model.enums.EstadoLibro;
import model.exceptions.ReglaNegocioException;
import model.interfaces.Identificable;
import util.TextoUtil;
import util.ValidadorIsbn;

import java.util.Objects;

public final class Libro implements Identificable {

    private int id;
    private String titulo;
    private String autor;
    private String editorial;
    private Categoria categoria;
    private String isbn;
    private int cantidadPaginas;
    private int stock;            // ejemplares disponibles para préstamo
    private EstadoLibro estado;

    public Libro(String titulo, String autor, String editorial, Categoria categoria,
                 String isbn, int cantidadPaginas, int stock) {
        this(0, titulo, autor, editorial, categoria, isbn, cantidadPaginas, stock, EstadoLibro.ACTIVO);
    }

    public Libro(int id, String titulo, String autor, String editorial, Categoria categoria,
                 String isbn, int cantidadPaginas, int stock, EstadoLibro estado) {
        this.id = id;
        setTitulo(titulo);
        setAutor(autor);
        setEditorial(editorial);
        setCategoria(categoria);
        setIsbn(isbn);
        setCantidadPaginas(cantidadPaginas);
        setStock(stock);
        setEstado(estado);
    }

    // ------------------------------------------------------------------ stock

    public boolean estaDisponibleParaPrestamo() {
        return estado == EstadoLibro.ACTIVO && stock > 0;
    }

    /**
     * Resta un ejemplar. OJO: este método NO es synchronized a propósito;
     * la sección crítica la protege ServicioGestion.descontarStock(), que es el
     * único punto del sistema autorizado a modificar el stock.
     */
    public void descontarStock() {
        if (!estaDisponibleParaPrestamo()) {
            throw new ReglaNegocioException("El libro '" + titulo + "' no está disponible (stock: "
                    + stock + ", estado: " + estado + ")");
        }
        stock--;
    }

    public void reponerStock() {
        stock++;
    }

    // ------------------------------------------------------------------ información

    public String datosLibro() {
        StringBuilder sb = new StringBuilder();
        sb.append("Título    : ").append(titulo).append('\n')
                .append("Autor     : ").append(autor).append('\n')
                .append("Editorial : ").append(editorial).append('\n')
                .append("Categoría : ").append(categoria).append('\n')
                .append("ISBN      : ").append(isbn).append('\n')
                .append("Páginas   : ").append(cantidadPaginas).append('\n')
                .append("Stock     : ").append(stock).append('\n')
                .append("Estado    : ").append(estado).append('\n');
        return sb.toString();
    }

    // ------------------------------------------------------------------ get / set

    @Override
    public int getId() { return id; }

    @Override
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = TextoUtil.requerido(titulo, "título"); }

    public String getAutor() { return autor; }

    public void setAutor(String autor) { this.autor = TextoUtil.requerido(autor, "autor"); }

    public String getEditorial() { return editorial; }

    public void setEditorial(String editorial) {
        this.editorial = TextoUtil.requerido(editorial, "editorial");
    }

    public Categoria getCategoria() { return categoria; }

    public void setCategoria(Categoria categoria) {
        this.categoria = Objects.requireNonNull(categoria, "La categoría es obligatoria");
    }

    public String getIsbn() { return isbn; }

    public void setIsbn(String isbn) {
        if (!ValidadorIsbn.esValido(isbn)) {
            throw new IllegalArgumentException("ISBN inválido: " + isbn);
        }
        this.isbn = ValidadorIsbn.limpiar(isbn);
    }

    public int getCantidadPaginas() { return cantidadPaginas; }

    public void setCantidadPaginas(int cantidadPaginas) {
        if (cantidadPaginas <= 0) throw new IllegalArgumentException("Las páginas deben ser mayores a 0");
        this.cantidadPaginas = cantidadPaginas;
    }

    public int getStock() { return stock; }

    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo");
        this.stock = stock;
    }

    public EstadoLibro getEstado() { return estado; }

    public void setEstado(EstadoLibro estado) {
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
    }

    /** El ISBN identifica al libro. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Libro)) return false;
        return isbn.equals(((Libro) o).isbn);
    }

    @Override
    public int hashCode() { return isbn.hashCode(); }

    @Override
    public String toString() { return titulo + " - " + autor; }
}