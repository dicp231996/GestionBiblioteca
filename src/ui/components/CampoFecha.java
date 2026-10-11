package ui.components;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Campo de texto con máscara dd-mm-aaaa que entrega un LocalDate. */
public class CampoFecha extends JFormattedTextField {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    public CampoFecha() {
        super(crearMascara());
        setColumns(10);
        setFocusLostBehavior(JFormattedTextField.COMMIT);
    }

    private static MaskFormatter crearMascara() {
        try {
            MaskFormatter mascara = new MaskFormatter("##-##-####");
            mascara.setPlaceholderCharacter('_');
            return mascara;
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /** @throws IllegalArgumentException si la fecha está incompleta o no existe (ej. 31-02-2010). */
    public LocalDate getFecha() {
        try {
            return LocalDate.parse(getText(), FORMATO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida: use el formato dd-mm-aaaa (ej. 15-03-2011)");
        }
    }

    public void setFecha(LocalDate fecha) {
        setText(fecha == null ? "" : fecha.format(FORMATO));
    }
}