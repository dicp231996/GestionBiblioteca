package ui;

import model.core.Persona;
import ui.components.Estilo;
import ui.components.Formulario;
import ui.controller.Errores;
import ui.controller.LoginController;
import ui.dialogs.EstudianteDialog;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
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
    private final JLabel error = new JLabel(" ");

    public LoginFrame(LoginController controller, Consumer<Persona> alIngresar) {
        super("Biblioteca Escolar · Iniciar sesión");
        this.controller = controller;
        this.alIngresar = alIngresar;
        setIconImage(Estilo.icono());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel raiz = new JPanel(new GridLayout(1, 2));
        raiz.add(new PortadaPanel());
        raiz.add(crearFormulario());
        setContentPane(raiz);
        setSize(new Dimension(860, 500));
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private JPanel crearFormulario() {
        JLabel titulo = Estilo.titulo("Iniciar sesión");
        JLabel subtitulo = Estilo.ayuda("Ingresa con tu usuario de la biblioteca.");
        error.setForeground(Estilo.PELIGRO);
        error.setFont(Estilo.PEQUENA);

        JButton ingresar = Estilo.botonPrincipal("Ingresar");
        ingresar.addActionListener(e -> ingresar());
        JButton registrarse = new JButton("¿Eres estudiante y no tienes cuenta? Regístrate");
        registrarse.setBorderPainted(false);
        registrarse.setContentAreaFilled(false);
        registrarse.setForeground(Estilo.PRIMARIO_CLARO);
        registrarse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registrarse.addActionListener(e -> registrarse());

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        Formulario form = new Formulario()
                .nota(encabezado)
                .campo("Usuario", usuario)
                .campo("Contraseña", contrasena)
                .nota(error)
                .nota(ingresar)
                .nota(registrarse);

        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(Estilo.SUPERFICIE);
        contenedor.setBorder(Estilo.margen(30));
        contenedor.add(form);
        getRootPane().setDefaultButton(ingresar);
        return contenedor;
    }

    private void ingresar() {
        error.setForeground(Estilo.PELIGRO);
        char[] clave = contrasena.getPassword();
        try {
            Optional<Persona> persona = controller.iniciarSesion(usuario.getText(), clave);
            if (persona.isPresent()) {
                error.setText(" ");
                contrasena.setText("");
                alIngresar.accept(persona.get());
            } else {
                error.setText("Usuario o contraseña incorrectos, o la cuenta está inactiva.");
                contrasena.selectAll();
                contrasena.requestFocusInWindow();
            }
        } catch (RuntimeException ex) {   // por ejemplo, la base de datos no responde
            error.setText("<html>" + Errores.mensaje(ex).replace("\n", "<br>") + "</html>");
        } finally {
            Arrays.fill(clave, '\0');
        }
    }

    private void registrarse() {
        new EstudianteDialog(this, "Registro de estudiante", null, controller::autoregistrar,
                controller::previsualizarUsuario).mostrar().ifPresent(e -> {
            usuario.setText(e.getNombreUsuario());
            contrasena.setText("");
            contrasena.requestFocusInWindow();
            error.setForeground(Estilo.EXITO);
            error.setText("Cuenta creada. Tu usuario es " + e.getNombreUsuario());
        });
    }

    /** Lado izquierdo decorativo: nombre del sistema y una repisa de libros dibujada. */
    private static final class PortadaPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        PortadaPanel() {
            super(new BorderLayout());
            setBackground(Estilo.PRIMARIO);
            setBorder(BorderFactory.createEmptyBorder(40, 36, 40, 36));
            JLabel nombre = new JLabel("<html>Biblioteca<br>Escolar</html>");
            nombre.setFont(new Font(Font.SERIF, Font.BOLD, 40));
            nombre.setForeground(Color.WHITE);
            JLabel lema = new JLabel("Sistema de gestión de préstamos, inventario y estudiantes");
            lema.setFont(Estilo.PEQUENA);
            lema.setForeground(new Color(0xC9D6E6));
            lema.setHorizontalAlignment(SwingConstants.LEFT);
            JPanel textos = new JPanel(new BorderLayout(0, 10));
            textos.setOpaque(false);
            textos.add(nombre, BorderLayout.NORTH);
            textos.add(lema, BorderLayout.CENTER);
            add(textos, BorderLayout.NORTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int base = getHeight() - 70;
            Color[] colores = {Estilo.ACENTO, new Color(0xE8DFC9), new Color(0x8FB3D9), new Color(0xB5523B),
                    new Color(0xE8DFC9), Estilo.ACENTO, new Color(0x6D8FB3), new Color(0xD9C8A5)};
            int[] alturas = {110, 130, 96, 120, 140, 104, 124, 90};
            int[] anchos = {26, 22, 30, 24, 20, 28, 22, 26};
            int x = 40;
            for (int i = 0; i < colores.length; i++) {
                g2.setColor(colores[i]);
                if (i == 3) {   // un libro inclinado
                    g2.rotate(Math.toRadians(-12), x, base);
                    g2.fillRoundRect(x, base - alturas[i], anchos[i], alturas[i], 4, 4);
                    g2.rotate(Math.toRadians(12), x, base);
                    x += anchos[i] + 14;
                    continue;
                }
                g2.fillRoundRect(x, base - alturas[i], anchos[i], alturas[i], 4, 4);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRect(x + 4, base - alturas[i] + 14, anchos[i] - 8, 3);
                x += anchos[i] + 6;
            }
            g2.setColor(new Color(0xE8DFC9));
            g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(30, base + 4, getWidth() - 30, base + 4);
            g2.dispose();
        }
    }
}