package model.interfaces;

import java.util.List;

/**
 * Contrato de registro que implementan Estudiante y Bibliotecario.
 * Cada subclase define sus propias reglas (polimorfismo): un estudiante valida
 * su edad escolar y curso; un bibliotecario, su mayoría de edad y año de contratación.
 * La persistencia NO va aquí: la hace ServicioGestion a través del DAO.
 */
public interface Registrable {

    /** @return lista de errores; vacía si los datos permiten el registro. */
    List<String> validarDatosRegistro();

    default boolean esRegistrable() {
        return validarDatosRegistro().isEmpty();
    }
}