package ui;

import model.core.Persona;
import ui.components.Estilo;
import ui.components.Formulario;
import ui.controller.Errores;
import ui.controller.LoginController;
import ui.dialogs.EstudianteDialog;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

/** Pantalla de inicio de sesión (módulo de autenticación) con acceso al autorregistro de estudiantes. */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final LoginController controller;
    private final Consumer<Persona> alIngresar;
    private final JTextField usuario = new JTextField(20);
    private final JPasswordField contrasena = new JPasswordField(20);
    private final JLabel mensaje = new JLabel(" ");

    public LoginFrame(LoginController controller, Consumer<Persona> alIngresar) {
        super("Biblioteca Escolar - Iniciar sesión");
        this.controller = controller;
        this.alIngresar = alIngresar;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton ingresar = new JButton("Ingresar");
        JButton registrarse = new JButton("Registrarse (estudiantes)");
        ingresar.addActionListener(e -> ingresar());
        registrarse.addActionListener(e -> registrarse());

        Formulario form = new Formulario()
                .nota(Estilo.titulo("Sistema de Biblioteca Escolar"))
                .nota(Estilo.ayuda("Ingresa con tu usuario y contraseña."))
                .campo("Usuario:", usuario)
                .campo("Contraseña:", contrasena)
                .nota(mensaje);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(registrarse);
        botones.add(ingresar);

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBorder(Estilo.margen(16));
        contenido.add(form, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);
        getRootPane().setDefaultButton(ingresar);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        mensaje.setForeground(Color.RED);
        char[] clave = contrasena.getPassword();
        try {
            Optional<Persona> persona = controller.iniciarSesion(usuario.getText(), clave);
            if (persona.isPresent()) {
                mensaje.setText(" ");
                contrasena.setText("");
                alIngresar.accept(persona.get());
            } else {
                mensaje.setText("Usuario o contraseña incorrectos, o la cuenta está inactiva.");
                contrasena.selectAll();
                contrasena.requestFocusInWindow();
            }
        } catch (RuntimeException ex) {   // por ejemplo, la base de datos no responde
            mensaje.setText("<html>" + Errores.mensaje(ex).replace("\n", "<br>") + "</html>");
            pack();
        } finally {
            Arrays.fill(clave, '\0');
        }
    }

    private void registrarse() {
        new EstudianteDialog(this, "Registro de estudiante", null, controller::autoregistrar).mostrar().ifPresent(e -> {
            usuario.setText(e.getNombreUsuario());
            contrasena.setText("");
            contrasena.requestFocusInWindow();
            mensaje.setForeground(new Color(0, 120, 0));
            mensaje.setText("Cuenta creada. Tu usuario es " + e.getNombreUsuario());
        });
    }
}