package ui;

import model.core.Persona;
import model.enums.Permiso;
import ui.components.BarraEstado;
import ui.components.Estilo;
import ui.components.Seccion;
import ui.controller.Controladores;
import ui.controller.Sesion;
import ui.panels.CatalogoPanel;
import ui.panels.EstudiantesPanel;
import ui.panels.MisPrestamosPanel;
import ui.panels.PerfilPanel;
import ui.panels.PrestamosPanel;
import ui.panels.ReportesPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventana principal: encabezado con el usuario, menú superior, panel de navegación lateral
 * y las pantallas en un CardLayout. Las secciones visibles dependen de los permisos del rol.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final JPanel navegacion = new JPanel();
    private final ButtonGroup grupoNavegacion = new ButtonGroup();
    private final Map<String, Seccion> secciones = new LinkedHashMap<>();
    private final Map<String, JToggleButton> botonesNav = new LinkedHashMap<>();
    private final BarraEstado barra = new BarraEstado();
    private final Runnable alCerrarSesion;

    public MainFrame(Controladores c, Runnable alCerrarSesion) {
        super("Biblioteca Escolar");
        this.alCerrarSesion = alCerrarSesion;
        Sesion sesion = c.sesion();
        setIconImage(Estilo.icono());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ---- secciones según el rol (niveles de privilegio)
        if (sesion.puede(Permiso.GESTIONAR_LIBROS)) {
            agregar("Libros", new CatalogoPanel(c.libros(), c.prestamos(), sesion, barra));
        } else {
            agregar("Catálogo", new CatalogoPanel(c.libros(), c.prestamos(), sesion, barra));
        }
        if (sesion.puede(Permiso.GESTIONAR_ESTUDIANTES)) {
            agregar("Estudiantes", new EstudiantesPanel(c.estudiantes(), barra));
        }
        if (sesion.puede(Permiso.GESTIONAR_PRESTAMOS)) {
            agregar("Préstamos", new PrestamosPanel(c.prestamos(), c.estudiantes(), c.libros(), barra));
        } else {
            agregar("Mis préstamos", new MisPrestamosPanel(c.prestamos(), barra));
        }
        if (sesion.puede(Permiso.VER_REPORTES)) {
            agregar("Reportes", new ReportesPanel(c.reportes(), c.estudiantes()));
        }
        agregar("Mi perfil", new PerfilPanel(c.perfil()));

        // ---- armado de la ventana
        navegacion.setLayout(new BoxLayout(navegacion, BoxLayout.Y_AXIS));
        navegacion.setBackground(Estilo.SUPERFICIE);
        navegacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Estilo.BORDE),
                BorderFactory.createEmptyBorder(14, 10, 14, 10)));
        navegacion.setPreferredSize(new Dimension(190, 0));
        JLabel menuTitulo = Estilo.ayuda("MENÚ");
        menuTitulo.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 0));
        navegacion.add(menuTitulo);
        botonesNav.values().forEach(b -> {
            navegacion.add(b);
            navegacion.add(Box.createVerticalStrut(4));
        });
        navegacion.add(Box.createVerticalGlue());

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(crearEncabezado(sesion.getUsuarioActual()), BorderLayout.NORTH);
        raiz.add(navegacion, BorderLayout.WEST);
        raiz.add(contenido, BorderLayout.CENTER);
        raiz.add(barra, BorderLayout.SOUTH);
        setContentPane(raiz);
        setJMenuBar(crearMenu());

        setMinimumSize(new Dimension(1100, 680));
        setSize(new Dimension(1240, 760));
        setLocationRelativeTo(null);
        mostrar(secciones.keySet().iterator().next());
    }

    private void agregar(String nombre, Seccion seccion) {
        secciones.put(nombre, seccion);
        contenido.add(seccion, nombre);
        JToggleButton boton = new BotonNavegacion(nombre);
        boton.addActionListener(e -> mostrar(nombre));
        grupoNavegacion.add(boton);
        botonesNav.put(nombre, boton);
    }

    /** Cambia de pantalla y recarga sus datos. */
    public void mostrar(String nombre) {
        tarjetas.show(contenido, nombre);
        botonesNav.get(nombre).setSelected(true);
        secciones.get(nombre).refrescar();
    }

    private JPanel crearEncabezado(Persona usuario) {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Estilo.PRIMARIO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JLabel marca = new JLabel("Biblioteca Escolar");
        marca.setFont(new Font(Font.SERIF, Font.BOLD, 22));
        marca.setForeground(Color.WHITE);
        encabezado.add(marca, BorderLayout.WEST);

        JLabel nombre = new JLabel(usuario.getNombreCompleto());
        nombre.setForeground(Color.WHITE);
        nombre.setFont(Estilo.SUBTITULO);
        JLabel rol = new JLabel(" " + usuario.getRol().getEtiqueta() + " ");
        rol.setOpaque(true);
        rol.setBackground(Estilo.ACENTO);
        rol.setForeground(Estilo.PRIMARIO);
        rol.setFont(Estilo.PEQUENA.deriveFont(Font.BOLD));
        rol.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        JButton salir = Estilo.boton("Cerrar sesión");
        salir.addActionListener(e -> cerrarSesion());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(nombre);
        derecha.add(rol);
        derecha.add(salir);
        encabezado.add(derecha, BorderLayout.EAST);
        return encabezado;
    }

    private JMenuBar crearMenu() {
        JMenuBar menu = new JMenuBar();

        JMenu sistema = new JMenu("Sistema");
        JMenuItem cerrar = new JMenuItem("Cerrar sesión");
        cerrar.addActionListener(e -> cerrarSesion());
        JMenuItem salir = new JMenuItem("Salir");
        salir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        salir.addActionListener(e -> System.exit(0));
        sistema.add(cerrar);
        sistema.addSeparator();
        sistema.add(salir);

        JMenu irA = new JMenu("Ir a");
        int tecla = KeyEvent.VK_1;
        for (String nombre : secciones.keySet()) {
            JMenuItem item = new JMenuItem(nombre);
            item.setAccelerator(KeyStroke.getKeyStroke(tecla++, InputEvent.CTRL_DOWN_MASK));
            item.addActionListener(e -> mostrar(nombre));
            irA.add(item);
        }

        JMenu ayuda = new JMenu("Ayuda");
        JMenuItem acerca = new JMenuItem("Acerca de");
        acerca.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Sistema de Gestión de Biblioteca Escolar\nDesarrollo Orientado a Objetos II\n\n"
                        + "Java Swing · MySQL (JDBC) · MVC · DAO · Singleton",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon(Estilo.icono())));
        ayuda.add(acerca);

        menu.add(sistema);
        menu.add(irA);
        menu.add(ayuda);
        return menu;
    }

    private void cerrarSesion() {
        dispose();
        alCerrarSesion.run();
    }

    /** Botón del menú lateral: la sección activa se pinta en azul con una marca dorada. */
    private static final class BotonNavegacion extends JToggleButton {
        private static final long serialVersionUID = 1L;

        BotonNavegacion(String texto) {
            super(texto);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(Estilo.SUBTITULO);
            setForeground(Estilo.TEXTO);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            getModel().addChangeListener(e -> setForeground(isSelected() ? Color.WHITE : Estilo.TEXTO));
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected()) {
                g2.setColor(Estilo.PRIMARIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Estilo.ACENTO);
                g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(0xECEFF4));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}