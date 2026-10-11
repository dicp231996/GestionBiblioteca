package ui.controller;

import data.PersistenciaException;
import model.exceptions.AccesoDenegadoException;
import model.exceptions.ReglaNegocioException;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Traduce cualquier excepción a un mensaje entendible para mostrar en la interfaz. */
public final class Errores {

    private static final Logger LOGGER = Logger.getLogger(Errores.class.getName());

    private Errores() { }

    public static String mensaje(Throwable error) {
        Throwable e = (error instanceof ExecutionException && error.getCause() != null) ? error.getCause() : error;
        if (e instanceof ReglaNegocioException || e instanceof AccesoDenegadoException
                || e instanceof IllegalArgumentException) {
            return e.getMessage();
        }
        if (e instanceof PersistenciaException) {
            return "Error de base de datos.\n" + e.getMessage();
        }
        LOGGER.log(Level.SEVERE, "Error inesperado", e);
        return "Ocurrió un error inesperado: " + e;
    }
}