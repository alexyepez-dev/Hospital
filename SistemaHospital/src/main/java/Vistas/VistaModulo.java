package Vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para las vistas de módulos (Arreglos, Recursividad, etc.)
 * Provee: barra superior, sidebar con opciones, área de contenido y botón salir.
 * @author CRACKS
 */
public abstract class VistaModulo extends JFrame {

    // ===== Paleta de colores =====
    protected final Color AZUL_OSCURO   = new Color(10, 25, 60);
    protected final Color AZUL_MEDIO    = new Color(20, 60, 130);
    protected final Color AZUL_CLARO    = new Color(40, 110, 200);
    protected final Color AZUL_ACENTO   = new Color(0, 180, 255);
    protected final Color BLANCO        = Color.WHITE;
    protected final Color GRIS_TEXTO    = new Color(120, 130, 150);
    protected final Color ROJO_NORMAL   = new Color(220, 53, 69);
    protected final Color ROJO_HOVER    = new Color(200, 35, 51);

    // Fuentes
    protected Font montserratRegular;
    protected Font montserratBold;
    protected Font montserratItalic;
    protected Font montserratBoldItalic;

    // Datos del módulo
    protected String usuarioActual;
    protected String tituloModulo;
    protected Color acentoModulo;
    protected List<String> opciones;
    protected List<JButton> botonesMenu = new ArrayList<>();
    protected int opcionSeleccionada = 0;

    // Panel de contenido dinámico (donde cada vista pinta su UI)
    protected JPanel panelContenido;
    protected JPanel panelSidebar;

    public VistaModulo(String usuario, String titulo, Color acento) {
        this.usuarioActual = usuario;
        this.tituloModulo = titulo;
        this.acentoModulo = acento;
        this.opciones = getOpcionesMenu();

        cargarFuentes();
        initChrome();
    }

    // ====================== MÉTODOS ABSTRACTOS ======================

    /** Cada módulo devuelve las opciones que aparecerán en el sidebar. */
    protected abstract List<String> getOpcionesMenu();

    /** Cada módulo devuelve el panel de contenido para la opción seleccionada. */
    protected abstract JPanel crearPanelOpcion(int indice);

    // ====================== CARGA DE FUENTES ======================
    protected void cargarFuentes() {
        montserratRegular    = cargarFuente("Montserrat-Regular.ttf",    Font.PLAIN);
        montserratBold       = cargarFuente("Montserrat-Bold.ttf",       Font.BOLD);
        montserratItalic     = cargarFuente("Montserrat-Italic.ttf",     Font.ITALIC);
        montserratBoldItalic = cargarFuente("Montserrat-BoldItalic.ttf", Font.BOLD | Font.ITALIC);
    }

    protected Font cargarFuente(String nombreArchivo, int estilo) {
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("fonts/" + nombreArchivo);
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            }
        } catch (Exception ignored) {}

        try {
            File archivo = new File("fonts/" + nombreArchivo);
            if (archivo.exists()) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, archivo);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            }
        } catch (Exception ignored) {}

        try {
            String[] rutas = {
                System.getProperty("user.dir") + File.separator + "fonts" + File.separator + nombreArchivo,
                System.getProperty("user.dir") + File.separator + "src" + File.separator
                        + "main" + File.separator + "java" + File.separator + "fonts" + File.separator + nombreArchivo,
                System.getProperty("user.dir") + File.separator + "src" + File.separator
                        + "main" + File.separator + "resources" + File.separator + "fonts" + File.separator + nombreArchivo,
                "E:\\BACKUP_AlexJr\\Documentos BACKUP_AlexJr\\Escritorioss BACKUP_AlexJr"
                        + "\\Hospital\\SistemaHospital\\src\\main\\java\\fonts\\" + nombreArchivo
            };
            for (String ruta : rutas) {
                File f = new File(ruta);
                if (f.exists()) {
                    Font fuente = Font.createFont(Font.TRUETYPE_FONT, f);
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuente);
                    return fuente;
                }
            }
        } catch (Exception ignored) {}

        for (String nombre : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (nombre.equalsIgnoreCase("Montserrat")) {
                return new Font("Montserrat", estilo, 14);
            }
        }

        return new Font("Segoe UI", estilo, 14);
    }

    // ====================== CONSTRUCCIÓN DEL CHROME ======================
    private void initChrome() {
        setTitle(tituloModulo + " - Sistema Hospitalario U.G.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1200, 720, 25, 25));

        JPanel fondo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, AZUL_OSCURO,
                        getWidth(), getHeight(), AZUL_MEDIO);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        fondo.add(crearBarraSuperior(), BorderLayout.NORTH);
        fondo.add(crearSidebar(), BorderLayout.WEST);
        fondo.add(crearPanelContenidoWrapper(), BorderLayout.CENTER);

        setContentPane(fondo);

        // Cargar primera opción
//        if (!opciones.isEmpty()) {
//            seleccionarOpcion(0);
//        }
    }
    
    public void initModulo() {
        if (!opciones.isEmpty()) {
            seleccionarOpcion(0);
        }
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(18, 30, 10, 30));

        // Izquierda
        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        marca.setOpaque(false);

        JPanel logoMini = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AZUL_ACENTO, getWidth(), getHeight(), BLANCO);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(AZUL_OSCURO);
                g2.setFont(montserratBold.deriveFont(Font.BOLD, 16f));
                FontMetrics fm = g2.getFontMetrics();
                String t = "UG";
                int x = (getWidth() - fm.stringWidth(t)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(t, x, y);
                g2.dispose();
            }
        };
        logoMini.setOpaque(false);
        logoMini.setPreferredSize(new Dimension(42, 42));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblUni = new JLabel("UNIVERSIDAD DE GUAYAQUIL");
        lblUni.setFont(montserratBold.deriveFont(Font.BOLD, 13f));
        lblUni.setForeground(BLANCO);

        JLabel lblModulo = new JLabel(tituloModulo + " · Estructura de Datos");
        lblModulo.setFont(montserratRegular.deriveFont(Font.PLAIN, 11f));
        lblModulo.setForeground(new Color(180, 220, 255));

        textos.add(lblUni);
        textos.add(lblModulo);

        marca.add(logoMini);
        marca.add(textos);

        // Derecha
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, AZUL_ACENTO,
                        getWidth(), getHeight(), new Color(100, 200, 255));
                g2.setPaint(gp);
                g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));

                g2.setColor(AZUL_OSCURO);
                int cabeza = getWidth() / 2;
                g2.fillOval(cabeza - 6, 7, 12, 12);
                g2.fillArc(-6, 22, getWidth() + 12, getHeight(), 0, 180);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(32, 32));

        JLabel lblUsuario = new JLabel(usuarioActual);
        lblUsuario.setFont(montserratBold.deriveFont(Font.BOLD, 13f));
        lblUsuario.setForeground(BLANCO);

        JButtonX btnCerrar = new JButtonX();
        btnCerrar.addActionListener(e -> cerrarAplicacion());

        derecha.add(avatar);
        derecha.add(lblUsuario);
        derecha.add(btnCerrar);

        barra.add(marca, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearSidebar() {
        panelSidebar = new JPanel();
        panelSidebar.setOpaque(false);
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBorder(new EmptyBorder(30, 30, 30, 20));
        panelSidebar.setPreferredSize(new Dimension(260, 0));

        // Título del sidebar
        JLabel lblSeccion = new JLabel("OPCIONES");
        lblSeccion.setFont(montserratBold.deriveFont(Font.BOLD, 11f));
        lblSeccion.setForeground(new Color(150, 200, 255));
        lblSeccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelSidebar.add(lblSeccion);
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        // Botones de opciones
        for (int i = 0; i < opciones.size(); i++) {
            final int index = i;
            JButton btn = crearBotonMenu(opciones.get(i), i);
            btn.addActionListener(e -> seleccionarOpcion(index));
            botonesMenu.add(btn);
            panelSidebar.add(btn);
            panelSidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        panelSidebar.add(Box.createVerticalGlue());

        // Botón SALIR (regresa al panel de control)
        JButton btnSalir = new JButton("←  Salir al menú") {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hover ? ROJO_HOVER : ROJO_NORMAL;
                Color c2 = hover ? ROJO_NORMAL : new Color(180, 35, 50);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSalir.setFont(montserratBold.deriveFont(Font.BOLD, 13f));
        btnSalir.setForeground(BLANCO);
        btnSalir.setFocusPainted(false);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setOpaque(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnSalir.setPreferredSize(new Dimension(200, 46));
        btnSalir.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalir.addActionListener(e -> regresarAlPanel());

        panelSidebar.add(btnSalir);

        return panelSidebar;
    }

    private JButton crearBotonMenu(String texto, int index) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            private boolean activo = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            public void setActivo(boolean a) { activo = a; repaint(); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (activo) {
                    // Activo: gradiente con acento del módulo
                    Color c1 = acentoModulo;
                    Color c2 = aclarar(acentoModulo, 40);
                    GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                } else if (hover) {
                    g2.setColor(new Color(255, 255, 255, 35));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(montserratBold.deriveFont(Font.BOLD, 13f));
        btn.setForeground(BLANCO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 18, 0, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setPreferredSize(new Dimension(200, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.putClientProperty("index", index);
        return btn;
    }

    private JPanel crearPanelContenidoWrapper() {
        // Contenedor blanco con esquinas redondeadas y sombra
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 22, 22);
                // Fondo blanco
                g2.setColor(BLANCO);
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 22, 22);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 25, 25));

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);
        panelContenido.setBorder(new EmptyBorder(30, 30, 30, 30));

        wrapper.add(panelContenido, BorderLayout.CENTER);
        return wrapper;
    }

    // ====================== CAMBIO DE OPCIÓN ======================
    protected void seleccionarOpcion(int index) {
        opcionSeleccionada = index;

        // Actualizar estado de los botones
        for (int i = 0; i < botonesMenu.size(); i++) {
            JButton btn = botonesMenu.get(i);
            try {
                java.lang.reflect.Method m = btn.getClass().getDeclaredMethod("setActivo", boolean.class);
                m.setAccessible(true);
                m.invoke(btn, i == index);
            } catch (Exception ignored) {}
        }

        // Reemplazar contenido
        panelContenido.removeAll();
        JPanel nuevo = crearPanelOpcion(index);
        if (nuevo != null) {
            panelContenido.add(nuevo, BorderLayout.CENTER);
        }
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ====================== ACCIONES ======================
    private void regresarAlPanel() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Deseas regresar al panel de control?",
                "Salir del módulo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            new Opciones(usuarioActual).setVisible(true);
            this.dispose();
        }
    }

    private void cerrarAplicacion() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Deseas salir del sistema?",
                "Cerrar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ====================== UTILIDADES PARA LAS SUBCLASES ======================

    /** Crea un título grande para el contenido. */
    protected JLabel crearTitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(montserratBold.deriveFont(Font.BOLD, 26f));
        lbl.setForeground(AZUL_OSCURO);
        return lbl;
    }

    /** Crea un subtítulo gris para el contenido. */
    protected JLabel crearSubtitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(montserratRegular.deriveFont(Font.PLAIN, 13f));
        lbl.setForeground(GRIS_TEXTO);
        return lbl;
    }

    /** Crea un botón primario con el acento del módulo. */
    protected JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hover ? aclarar(acentoModulo, 30) : acentoModulo;
                Color c2 = hover ? acentoModulo : oscurecer(acentoModulo, 30);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(montserratBold.deriveFont(Font.BOLD, 13f));
        btn.setForeground(BLANCO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 42));
        return btn;
    }

    /** Crea un campo de texto con estilo. */
    protected JTextField crearCampoTexto(int columnas) {
        JTextField txt = new JTextField(columnas);
        txt.setFont(montserratRegular.deriveFont(Font.PLAIN, 14f));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ACENTO_BORDER, 10),
                new EmptyBorder(8, 12, 8, 12)));
        return txt;
    }

    protected final Color ACENTO_BORDER = new Color(180, 200, 220);

    // ====================== UTILIDADES DE COLOR ======================
    protected Color aclarar(Color c, int delta) {
        return new Color(
                Math.min(255, c.getRed() + delta),
                Math.min(255, c.getGreen() + delta),
                Math.min(255, c.getBlue() + delta));
    }

    protected Color oscurecer(Color c, int delta) {
        return new Color(
                Math.max(0, c.getRed() - delta),
                Math.max(0, c.getGreen() - delta),
                Math.max(0, c.getBlue() - delta));
    }

    // ====================== CLASES AUXILIARES ======================

    protected static class RoundedBorder implements javax.swing.border.Border {
        private final Color color;
        private final int radio;

        public RoundedBorder(Color color, int radio) {
            this.color = color;
            this.radio = radio;
        }

        @Override public Insets getBorderInsets(Component c) { return new Insets(4, 4, 4, 4); }
        @Override public boolean isBorderOpaque() { return false; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radio, radio);
            g2.dispose();
        }
    }

    protected class JButtonX extends JButton {
        private boolean hover = false;

        public JButtonX() {
            setPreferredSize(new Dimension(32, 32));
            setMaximumSize(new Dimension(32, 32));
            setMinimumSize(new Dimension(32, 32));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText("Cerrar");

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            if (hover) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillOval(2, 2, w - 4, h - 4);
            }
            g2.setColor(hover ? ROJO_HOVER : BLANCO);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int m = 10;
            g2.drawLine(m, m, w - m, h - m);
            g2.drawLine(w - m, m, m, h - m);
            g2.dispose();
        }
    }
}