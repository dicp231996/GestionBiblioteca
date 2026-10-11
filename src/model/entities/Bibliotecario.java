package model.entities;

import model.core.Persona;
import model.enums.Genero;
import model.enums.Rol;
import model.interfaces.Registrable;
import model.security.Usuario;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Personal de biblioteca. Sus privilegios (edición y consulta de préstamos,
 * inventario y estudiantes) vienen de Rol.BIBLIOTECARIO y se ejercen a través
 * de ServicioGestion / ServicioPrestamo / ServicioDevolucion.
 */
public class Bibliotecario extends Persona implements Registrable {

    public static final int EDAD_MINIMA = 18;

    private int anioContratacion;

    public Bibliotecario(String nombres, String apellidoPaterno, String apellidoMaterno,
                         LocalDate fechaNacimiento, String rut, String direccion,
                         String nacionalidad, Genero genero, int anioContratacion,
                         String contrasena) {
        super(nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, rut,
                direccion, nacionalidad, genero);
        setAnioContratacion(anioContratacion);
        setUsuario(Usuario.crear(generarNombreUsuario(), contrasena, Rol.BIBLIOTECARIO));
    }

    public Bibliotecario(int id, String nombres, String apellidoPaterno, String apellidoMaterno,
                         LocalDate fechaNacimiento, String rut, String direccion,
                         String nacionalidad, Genero genero, int anioContratacion,
                         Usuario usuario) {
        super(id, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, rut,
                direccion, nacionalidad, genero, usuario);
        setAnioContratacion(anioContratacion);
    }

    /** Usuario de bibliotecario: prefijo "bib." + base. Ej: "bib.jperezg". */
    @Override
    protected String generarNombreUsuario() {
        return "bib." + baseNombreUsuario();
    }

    public int getAniosServicio() {
        return Year.now().getValue() - anioContratacion;
    }

    // ------------------------------------------------------------------ registro

    @Override
    public List<String> validarDatosRegistro() {
        List<String> errores = new ArrayList<>();
        if (getEdad() < EDAD_MINIMA) {
            errores.add("Un bibliotecario debe ser mayor de edad");
        }
        int anioMayoriaEdad = getFechaNacimiento().getYear() + EDAD_MINIMA;
        if (anioContratacion < anioMayoriaEdad) {
            errores.add("El año de contratación (" + anioContratacion
                    + ") es anterior a su mayoría de edad");
        }
        return errores;
    }

    // ------------------------------------------------------------------ información

    /** Solo la parte laboral de la ficha. */
    public String datosProfesionales() {
        StringBuilder sb = new StringBuilder();
        sb.append("Año contratación: ").append(anioContratacion).append('\n')
                .append("Años de servicio: ").append(getAniosServicio()).append('\n');
        return sb.toString();
    }

    @Override
    public String datosPersonales() {
        return new StringBuilder("===== FICHA BIBLIOTECARIO =====\n")
                .append(super.datosPersonales())
                .append(datosProfesionales())
                .toString();
    }

    // ------------------------------------------------------------------ get / set

    public int getAnioContratacion() { return anioContratacion; }

    public void setAnioContratacion(int anioContratacion) {
        int actual = Year.now().getValue();
        if (anioContratacion < 1950 || anioContratacion > actual) {
            throw new IllegalArgumentException("Año de contratación fuera de rango: " + anioContratacion);
        }
        this.anioContratacion = anioContratacion;
    }
}