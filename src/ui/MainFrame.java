package ui;

import model.core.Persona;
import model.enums.Permiso;
import ui.components.BarraEstado;
import ui.components.Seccion;
import ui.controller.Controladores;
import ui.controller.Sesion;
import ui.panels.CatalogoPanel;
import ui.panels.EstudiantesPanel;
import ui.panels.MisPrestamosPanel;
import ui.panels.PrestamosPanel;
import ui.panels.ReportesPanel;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventana principal: barra de menú, panel de navegación lateral y las pantallas en un
 * CardLayout. Las secciones visibles dependen de los permisos del rol.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final ButtonGroup grupoNavegacion = new ButtonGroup();
    private final Map<String, Seccion> secciones = new LinkedHashMap<>();
    private final Map<String, JToggleButton> botonesNav = new LinkedHashMap<>();
    private final BarraEstado barra = new BarraEstado();
    private final Runnable alCerrarSesion;

    public MainFrame(Controladores c, Runnable alCerrarSesion) {
        super("Biblioteca Escolar");
        this.alCerrarSesion = alCerrarSesion;
        Sesion sesion = c.sesion();
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

        // ---- panel de navegación lateral
        JPanel botones = new JPanel(new GridLayout(0, 1, 0, 4));
        botonesNav.values().forEach(botones::add);
        JPanel navegacion = new JPanel(new BorderLayout());
        navegacion.setBorder(BorderFactory.createTitledBorder("Menú"));
        navegacion.add(botones, BorderLayout.NORTH);
        navegacion.setPreferredSize(new Dimension(170, 0));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(crearEncabezado(sesion.getUsuarioActual()), BorderLayout.NORTH);
        raiz.add(navegacion, BorderLayout.WEST);
        raiz.add(contenido, BorderLayout.CENTER);
        raiz.add(barra, BorderLayout.SOUTH);
        setContentPane(raiz);
        setJMenuBar(crearMenu());

        setSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);
        mostrar(secciones.keySet().iterator().next());
    }

    private void agregar(String nombre, Seccion seccion) {
        secciones.put(nombre, seccion);
        contenido.add(seccion, nombre);
        JToggleButton boton = new JToggleButton(nombre);
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
        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        encabezado.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.LIGHT_GRAY));
        JButton salir = new JButton("Cerrar sesión");
        salir.addActionListener(e -> cerrarSesion());
        encabezado.add(new JLabel("Usuario: " + usuario.getNombreCompleto() + " (" + usuario.getRol() + ")"));
        encabezado.add(salir);
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

        menu.add(sistema);
        return menu;
    }

    private void cerrarSesion() {
        dispose();
        alCerrarSesion.run();
    }
}