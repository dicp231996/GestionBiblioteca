package ui.dialogs;

import model.entities.Estudiante;
import model.enums.Genero;
import model.security.Usuario;
import model.valueobjects.Curso;
import ui.components.CampoFecha;
import ui.components.Estilo;
import ui.components.Formulario;
import ui.components.Mensajes;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Formulario de estudiante. Se usa en tres casos: autorregistro desde el login, registro hecho
 * por un bibliotecario y edición. La acción de guardar la entrega quien abre el diálogo
 * (el controlador correspondiente), así el formulario no depende de ningún servicio.
 */
public class EstudianteDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final Estudiante existente;
    private final UnaryOperator<Estudiante> accionGuardar;
    private Estudiante guardado;

    private final JTextField nombres = new JTextField(24);
    private final JTextField apPaterno = new JTextField();
    private final JTextField apMaterno = new JTextField();
    private final JTextField rut = new JTextField();
    private final CampoFecha nacimiento = new CampoFecha();
    private final JComboBox<Genero> genero = new JComboBox<>(Genero.values());
    private final JTextField nacionalidad = new JTextField("Chilena");
    private final JTextField direccion = new JTextField();
    private final JComboBox<Curso> curso = new JComboBox<>(Curso.todos().toArray(new Curso[0]));
    private final JPasswordField contrasena = new JPasswordField();
    private final JPasswordField confirmacion = new JPasswordField();

    public EstudianteDialog(Window padre, String titulo, Estudiante existente,
                            UnaryOperator<Estudiante> accionGuardar) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        this.existente = existente;
        this.accionGuardar = accionGuardar;

        Formulario f = new Formulario()
                .campo("Nombres", nombres)
                .campo("Apellido paterno", apPaterno)
                .campo("Apellido materno", apMaterno)
                .campo("RUT", rut)
                .campo("Fecha de nacimiento", nacimiento)
                .campo("Género", genero)
                .campo("Nacionalidad", nacionalidad)
                .campo("Dirección", direccion)
                .campo("Curso", curso);
        if (existente == null) {
            f.campo("Contraseña", contrasena)
                    .campo("Repetir contraseña", confirmacion)
                    .nota(Estilo.ayuda("RUT con guion (12.345.678-5). El usuario se genera con tus datos al registrarte."));
        } else {
            cargar(existente);
            f.campo("Usuario", new JLabel(existente.getNombreUsuario()));
        }

        JButton cancelar = Estilo.boton("Cancelar");
        JButton guardar = Estilo.boton(existente == null ? "Registrar" : "Guardar cambios");
        cancelar.addActionListener(e -> dispose());
        guardar.addActionListener(e -> guardar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(cancelar);
        botones.add(guardar);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBorder(Estilo.margen(16));
        contenido.add(f, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);
        getRootPane().setDefaultButton(guardar);
        pack();
        setLocationRelativeTo(padre);
    }

    public Optional<Estudiante> mostrar() {
        setVisible(true);
        return Optional.ofNullable(guardado);
    }

    private void cargar(Estudiante e) {
        nombres.setText(e.getNombres());
        apPaterno.setText(e.getApellidoPaterno());
        apMaterno.setText(e.getApellidoMaterno());
        rut.setText(e.getRut());
        nacimiento.setFecha(e.getFechaNacimiento());
        genero.setSelectedItem(e.getGenero());
        nacionalidad.setText(e.getNacionalidad());
        direccion.setText(e.getDireccion());
        curso.setSelectedItem(e.getCurso());
    }

    private void guardar() {
        Mensajes.intentar(this, () -> {
            Estudiante estudiante;
            if (existente == null) {
                char[] clave = contrasena.getPassword();
                char[] repetida = confirmacion.getPassword();
                if (!Arrays.equals(clave, repetida)) throw new IllegalArgumentException("Las contraseñas no coinciden");
                estudiante = new Estudiante(nombres.getText(), apPaterno.getText(), apMaterno.getText(),
                        nacimiento.getFecha(), rut.getText(), direccion.getText(), nacionalidad.getText(),
                        (Genero) genero.getSelectedItem(), (Curso) curso.getSelectedItem(), new String(clave));
            } else {
                // Mismo id y mismo Usuario: el nombre de usuario y la contraseña no cambian al editar.
                Usuario usuario = existente.getUsuario();
                estudiante = new Estudiante(existente.getId(), nombres.getText(), apPaterno.getText(),
                        apMaterno.getText(), nacimiento.getFecha(), rut.getText(), direccion.getText(),
                        nacionalidad.getText(), (Genero) genero.getSelectedItem(), (Curso) curso.getSelectedItem(),
                        usuario);
                estudiante.restaurarLibrosPrestados(existente.getLibrosPrestados());
            }
            guardado = accionGuardar.apply(estudiante);
            dispose();
        });
    }
}