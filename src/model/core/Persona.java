package model.core;

import model.enums.Genero;
import model.interfaces.Identificable;
import model.security.Usuario;
import util.TextoUtil;
import util.ValidadorRut;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Superclase abstracta con los datos comunes de Estudiante y Bibliotecario.
 * No se puede instanciar: en el sistema solo existen estudiantes o bibliotecarios.
 */
public abstract class Persona implements Identificable, Autenticable {

    private int id;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;
    private String rut;
    private String direccion;
    private String nacionalidad;
    private Genero genero;
    private Usuario usuario;

    /** Constructor para personas nuevas (la subclase crea luego su Usuario). */
    protected Persona(String nombres, String apellidoPaterno, String apellidoMaterno,
                      LocalDate fechaNacimiento, String rut, String direccion,
                      String nacionalidad, Genero genero) {
        setNombres(nombres);
        setApellidoPaterno(apellidoPaterno);
        setApellidoMaterno(apellidoMaterno);
        setFechaNacimiento(fechaNacimiento);
        setRut(rut);
        setDireccion(direccion);
        setNacionalidad(nacionalidad);
        setGenero(genero);
    }

    /** Constructor sobrecargado para reconstruir una persona leída desde la BD. */
    protected Persona(int id, String nombres, String apellidoPaterno, String apellidoMaterno,
                      LocalDate fechaNacimiento, String rut, String direccion,
                      String nacionalidad, Genero genero, Usuario usuario) {
        this(nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, rut,
                direccion, nacionalidad, genero);
        this.id = id;
        setUsuario(usuario);
    }

    // ------------------------------------------------------------------ usuario

    /**
     * Cada rol arma su nombre de usuario a su manera (método abstracto -> polimorfismo).
     * Se construye a partir de los atributos recibidos en el constructor.
     */
    protected abstract String generarNombreUsuario();

    /** Base común: inicial del primer nombre + apellido paterno + inicial del materno. Ej: "jperezg". */
    protected final String baseNombreUsuario() {
        String primerNombre = nombres.split("\\s+")[0];
        return TextoUtil.normalizar(primerNombre).charAt(0)
                + TextoUtil.normalizar(apellidoPaterno)
                + TextoUtil.normalizar(apellidoMaterno).charAt(0);
    }

    // ------------------------------------------------------------------ datos

    /** Edad calculada dinámicamente, redondeada hacia abajo (Math.floor). */
    public int getEdad() {
        long meses = ChronoUnit.MONTHS.between(fechaNacimiento, LocalDate.now());
        return (int) Math.floor(meses / 12.0);
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidoPaterno + " " + apellidoMaterno;
    }

    /**
     * Ficha con los datos comunes. Las subclases la sobrescriben (llamando a super)
     * para agregar lo propio de su rol.
     */
    public String datosPersonales() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre completo : ").append(getNombreCompleto()).append('\n')
                .append("RUT             : ").append(ValidadorRut.formatear(rut)).append('\n')
                .append("Fecha nacimiento: ").append(fechaNacimiento).append('\n')
                .append("Edad            : ").append(getEdad()).append(" años").append('\n')
                .append("Género          : ").append(genero).append('\n')
                .append("Nacionalidad    : ").append(nacionalidad).append('\n')
                .append("Dirección       : ").append(direccion).append('\n')
                .append("Usuario         : ").append(usuario != null ? usuario.getNombreUsuario() : "-")
                .append(" (").append(getRolTexto()).append(")").append('\n');
        return sb.toString();
    }

    private String getRolTexto() {
        return usuario != null ? usuario.getRol().getEtiqueta() : "sin rol";
    }

    // ------------------------------------------------------------------ get / set

    @Override
    public int getId() { return id; }

    @Override
    public void setId(int id) { this.id = id; }

    public String getNombres() { return nombres; }

    public final void setNombres(String nombres) {
        this.nombres = TextoUtil.requerido(nombres, "nombres");
    }

    public String getApellidoPaterno() { return apellidoPaterno; }

    public final void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = TextoUtil.requerido(apellidoPaterno, "apellido paterno");
    }

    public String getApellidoMaterno() { return apellidoMaterno; }

    public final void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = TextoUtil.requerido(apellidoMaterno, "apellido materno");
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }

    public final void setFechaNacimiento(LocalDate fechaNacimiento) {
        Objects.requireNonNull(fechaNacimiento, "La fecha de nacimiento es obligatoria");
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
        this.fechaNacimiento = fechaNacimiento;
    }

    /** RUT normalizado sin puntos: "12345678-K". */
    public String getRut() { return rut; }

    public final void setRut(String rut) {
        this.rut = ValidadorRut.normalizar(rut); // lanza IllegalArgumentException si es inválido
    }

    public String getDireccion() { return direccion; }

    public final void setDireccion(String direccion) {
        this.direccion = TextoUtil.requerido(direccion, "dirección");
    }

    public String getNacionalidad() { return nacionalidad; }

    public final void setNacionalidad(String nacionalidad) {
        this.nacionalidad = TextoUtil.requerido(nacionalidad, "nacionalidad");
    }

    public Genero getGenero() { return genero; }

    public final void setGenero(Genero genero) {
        this.genero = Objects.requireNonNull(genero, "El género es obligatorio");
    }

    @Override
    public Usuario getUsuario() { return usuario; }

    protected final void setUsuario(Usuario usuario) {
        this.usuario = Objects.requireNonNull(usuario, "El usuario es obligatorio");
    }

    // ------------------------------------------------------------------ Object

    /** Dos personas son la misma si tienen el mismo RUT. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona)) return false;
        return rut.equals(((Persona) o).rut);
    }

    @Override
    public int hashCode() { return rut.hashCode(); }

    /** Texto corto, útil para JComboBox/JList. La ficha completa es datosPersonales(). */
    @Override
    public String toString() {
        return getNombreCompleto() + " (" + ValidadorRut.formatear(rut) + ")";
    }
}