package model.exceptions;

/** Se lanza cuando el usuario no tiene el permiso que exige la operación. */
public class AccesoDenegadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}