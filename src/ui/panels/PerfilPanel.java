package ui.panels;

import ui.components.Estilo;
import ui.components.Seccion;
import ui.controller.Errores;
import ui.controller.PerfilController;

import javax.swing.JTextArea;

/** Muestra la ficha del usuario conectado usando datosPersonales() (sobrescrito en cada rol). */
public class PerfilPanel extends Seccion {

    private static final long serialVersionUID = 1L;

    private final PerfilController perfil;
    private final JTextArea ficha = new JTextArea();

    public PerfilPanel(PerfilController perfil) {
        super("Mi perfil", "Tus datos registrados en la biblioteca.");
        this.perfil = perfil;
        ficha.setFont(Estilo.MONO);
        ficha.setEditable(false);
        ficha.setBorder(Estilo.margen(12));
        add(tarjeta(ficha), java.awt.BorderLayout.CENTER);
    }

    @Override
    public void refrescar() {
        // Polimorfismo: la misma llamada arma la ficha de estudiante o la de bibliotecario.
        try {
            ficha.setText(perfil.datosActuales().datosPersonales());
        } catch (RuntimeException e) {
            ficha.setText(Errores.mensaje(e));
        }
    }
}