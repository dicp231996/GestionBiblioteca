package model.exceptions;

/** Se lanza cuando una operación viola una regla del negocio (sin stock, límite de préstamos, etc.). */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}