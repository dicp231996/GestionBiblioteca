package data;

/**
 * Error de acceso a datos. Envuelve la SQLException (que es "checked") para que los
 * servicios y controladores no dependan de JDBC: solo saben que la operación falló.
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}