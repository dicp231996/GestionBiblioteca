package model.transactions;

import model.entities.Estudiante;
import model.enums.EstadoPrestamo;
import model.exceptions.ReglaNegocioException;
import model.interfaces.Identificable;
import model.resources.Libro;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Prestamo implements Identificable {

    public static final int DIAS_PRESTAMO = 7;

    private int id;
    private Estudiante estudiante;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion;   // null mientras esté activo
    private EstadoPrestamo estado;
    private String observacion;          // constancia de atraso u otras notas

    /** Préstamo nuevo: la fecha de vencimiento se calcula automáticamente. */
    public Prestamo(Estudiante estudiante, Libro libro, LocalDate fechaPrestamo) {
        this.estudiante = Objects.requireNonNull(estudiante);
        this.libro = Objects.requireNonNull(libro);
        this.fechaPrestamo = Objects.requireNonNull(fechaPrestamo);
        this.fechaVencimiento = fechaPrestamo.plusDays(DIAS_PRESTAMO);
        this.estado = EstadoPrestamo.ACTIVO;
        this.observacion = "";
    }

    /** Préstamo leído desde la BD. */
    public Prestamo(int id, Estudiante estudiante, Libro libro, LocalDate fechaPrestamo,
                    LocalDate fechaVencimiento, LocalDate fechaDevolucion,
                    EstadoPrestamo estado, String observacion) {
        this.id = id;
        this.estudiante = Objects.requireNonNull(estudiante);
        this.libro = Objects.requireNonNull(libro);
        this.fechaPrestamo = Objects.requireNonNull(fechaPrestamo);
        this.fechaVencimiento = Objects.requireNonNull(fechaVencimiento);
        this.fechaDevolucion = fechaDevolucion;
        this.estado = Objects.requireNonNull(estado);
        this.observacion = observacion == null ? "" : observacion;
    }

    // ------------------------------------------------------------------ lógica

    public boolean estaActivo() {
        return estado == EstadoPrestamo.ACTIVO;
    }

    /** Días de atraso a la fecha de devolución (o a hoy, si sigue activo). */
    public long diasAtraso() {
        LocalDate referencia = (fechaDevolucion != null) ? fechaDevolucion : LocalDate.now();
        return Math.max(0, ChronoUnit.DAYS.between(fechaVencimiento, referencia));
    }

    public boolean estaAtrasado() {
        return diasAtraso() > 0;
    }

    /** Cierra el préstamo y deja constancia si hubo atraso. */
    public void registrarDevolucion(LocalDate fecha) {
        if (!estaActivo()) {
            throw new ReglaNegocioException("El préstamo #" + id + " ya fue devuelto");
        }
        if (fecha.isBefore(fechaPrestamo)) {
            throw new IllegalArgumentException("La devolución no puede ser anterior al préstamo");
        }
        this.fechaDevolucion = fecha;
        long atraso = diasAtraso();
        if (atraso > 0) {
            this.estado = EstadoPrestamo.DEVUELTO_CON_ATRASO;
            this.observacion = "Devuelto con " + atraso + " día(s) de atraso";
        } else {
            this.estado = EstadoPrestamo.DEVUELTO;
        }
    }

    public String datosPrestamo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Préstamo #").append(id).append(" [").append(estado).append("]\n")
                .append("Estudiante : ").append(estudiante.getNombreCompleto())
                .append(" - ").append(estudiante.getCurso()).append('\n')
                .append("Libro      : ").append(libro.getTitulo()).append('\n')
                .append("Prestado   : ").append(fechaPrestamo).append('\n')
                .append("Vence      : ").append(fechaVencimiento).append('\n')
                .append("Devuelto   : ").append(fechaDevolucion != null ? fechaDevolucion : "-").append('\n');
        if (estaAtrasado()) {
            sb.append("Atraso     : ").append(diasAtraso()).append(" día(s)\n");
        }
        if (!observacion.isEmpty()) {
            sb.append("Observación: ").append(observacion).append('\n');
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------ get / set

    @Override
    public int getId() { return id; }

    @Override
    public void setId(int id) { this.id = id; }

    public Estudiante getEstudiante() { return estudiante; }

    public Libro getLibro() { return libro; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }

    public EstadoPrestamo getEstado() { return estado; }

    public String getObservacion() { return observacion; }

    public void setObservacion(String observacion) {
        this.observacion = observacion == null ? "" : observacion.trim();
    }

    @Override
    public String toString() {
        return "#" + id + " " + libro.getTitulo() + " -> " + estudiante.getNombreCompleto();
    }
}