package model.enums;

/** Acciones del sistema que se pueden habilitar o restringir según el rol. */
public enum Permiso {
    CONSULTAR_CATALOGO,
    SOLICITAR_PRESTAMO,          // préstamo para sí mismo
    DEVOLVER_LIBRO,              // devolución de un préstamo propio
    CONSULTAR_PROPIOS_PRESTAMOS,
    GESTIONAR_LIBROS,            // CRUD de libros y categorías (inventario)
    GESTIONAR_ESTUDIANTES,       // CRUD de estudiantes
    GESTIONAR_PRESTAMOS,         // préstamos/devoluciones de cualquier estudiante
    GESTIONAR_PERSONAL,          // registrar otros bibliotecarios
    VER_REPORTES
}