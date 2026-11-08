package Vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.InputStream;

/**
 * Vista de Inicio de Sesión - Universidad de Guayaquil
 * Materia: Estructura de Datos
 * Fuente: Montserrat (carga multi-nivel)
 * @author CRACKS
 */
public class InicioSistema extends JFrame {

    // Paleta de colores azules
    private final Color AZUL_OSCURO   = new Color(10, 25, 60);
    private final Color AZUL_MEDIO    = new Color(20, 60, 130);
    private final Color AZUL_CLARO    = new Color(40, 110, 200);
    private final Color AZUL_ACENTO   = new Color(0, 180, 255);
    private final Color BLANCO        = Color.WHITE;

    // Colores para el botón cerrar
    private final Color GRIS_ICONO    = new Color(150, 160, 180);
    private final Color ROJO_HOVER    = new Color(220, 53, 69);
    private final Color ROJO_FONDO    = new Color(255, 235, 238);

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JCheckBox chkMostrar;

    // Credenciales válidas
    private final String USUARIO_VALIDO = "ADMIN";
    private final String CLAVE_VALIDA   = "123456";

    // Fuentes Montserrat cargadas
    private Font montserratRegular;
    private Font montserratBold;
    private Font montserratItalic;
    private Font montserratBoldItalic;

    public InicioSistema() {
        cargarFuentes();
        initComponents();
    }

    private void cargarFuentes() {
        montserratRegular    = cargarFuente("Montserrat-Regular.ttf",    Font.PLAIN);
        montserratBold       = cargarFuente("Montserrat-Bold.ttf",       Font.BOLD);
        montserratItalic     = cargarFuente("Montserrat-Italic.ttf",     Font.ITALIC);
        montserratBoldItalic = cargarFuente("Montserrat-BoldItalic.ttf", Font.BOLD | Font.ITALIC);
    }

    private Font cargarFuente(String nombreArchivo, int estilo) {
        // 1) classpath
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("fonts/" + nombreArchivo);
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                System.out.println("✅ [classpath] Cargada: " + nombreArchivo);
                return f;
            }
        } catch (Exception e) {
            System.err.println("⚠ Falló classpath: " + e.getMessage());
        }

        // 2) ./fonts
        try {
            File archivo = new File("fonts/" + nombreArchivo);
            if (archivo.exists()) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, archivo);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                System.out.println("✅ [./fonts] Cargada: " + nombreArchivo);
                return f;
            }
        } catch (Exception e) {
            System.err.println("⚠ Falló ./fonts: " + e.getMessage());
        }

        // 3) rutas absolutas
        try {
            String[] basesPosibles = {
                System.getProperty("user.dir") + File.separator + "fonts" + File.separator + nombreArchivo,
                System.getProperty("user.dir") + File.separator + "src" + File.separator
                        + "main" + File.separator + "java" + File.separator + "fonts" + File.separator + nombreArchivo,
                System.getProperty("user.dir") + File.separator + "src" + File.separator
                        + "main" + File.separator + "resources" + File.separator + "fonts" + File.separator + nombreArchivo,
                "E:\\BACKUP_AlexJr\\Documentos BACKUP_AlexJr\\Escritorioss BACKUP_AlexJr"
                        + "\\Hospital\\SistemaHospital\\src\\main\\java\\fonts\\" + nombreArchivo
            };
            for (String ruta : basesPosibles) {
                File archivo = new File(ruta);
                if (archivo.exists()) {
                    Font fuente = Font.createFont(Font.TRUETYPE_FONT, archivo);
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuente);
                    System.out.println("✅ [absoluta] Cargada: " + archivo.getAbsolutePath());
                    return fuente;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠ Falló ruta absoluta: " + e.getMessage());
        }

        // 4) sistema
        for (String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (f.equalsIgnoreCase("Montserrat")) {
                System.out.println("✅ [sistema] Usando Montserrat instalada.");
                return new Font("Montserrat", estilo, 14);
            }
        }

        System.err.println("⚠ No se encontró Montserrat. Usando Segoe UI.");
        return new Font("Segoe UI", estilo, 14);
    }

    private void initComponents() {
        setTitle("Inicio de Sesión - Sistema Hospitalario U.G.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 600, 25, 25));

        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, AZUL_OSCURO,
                        getWidth(), getHeight(), AZUL_MEDIO);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // ============ LADO IZQUIERDO ============
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setOpaque(false);
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBorder(new EmptyBorder(60, 40, 60, 40));

        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AZUL_ACENTO, getWidth(), getHeight(), BLANCO);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(AZUL_OSCURO);
                g2.setFont(montserratBold.deriveFont(Font.BOLD, 36f));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "UG";
                int x = (getWidth() - fm.stringWidth(txt)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(txt, x, y);
                g2.dispose();
            }
        };
        logo.setOpaque(false);
        logo.setPreferredSize(new Dimension(120, 120));
        logo.setMaximumSize(new Dimension(120, 120));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUniversidad = new JLabel("UNIVERSIDAD DE GUAYAQUIL");
        lblUniversidad.setFont(montserratBold.deriveFont(Font.BOLD, 20f));
        lblUniversidad.setForeground(BLANCO);
        lblUniversidad.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMateria = new JLabel("Estructura de Datos");
        lblMateria.setFont(montserratItalic.deriveFont(Font.ITALIC, 16f));
        lblMateria.setForeground(new Color(180, 220, 255));
        lblMateria.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(200, 2));
        sep.setForeground(AZUL_ACENTO);
        sep.setBackground(AZUL_ACENTO);

        JLabel lblSistema = new JLabel("Sistema Hospitalario");
        lblSistema.setFont(montserratRegular.deriveFont(Font.PLAIN, 15f));
        lblSistema.setForeground(new Color(220, 235, 255));
        lblSistema.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBienvenido = new JLabel("Bienvenido de vuelta");
        lblBienvenido.setFont(montserratBold.deriveFont(Font.BOLD, 22f));
        lblBienvenido.setForeground(BLANCO);
        lblBienvenido.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelIzquierdo.add(Box.createVerticalGlue());
        panelIzquierdo.add(logo);
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 20)));
        panelIzquierdo.add(lblUniversidad);
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 8)));
        panelIzquierdo.add(lblMateria);
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 25)));
        panelIzquierdo.add(sep);
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 25)));
        panelIzquierdo.add(lblSistema);
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 40)));
        panelIzquierdo.add(lblBienvenido);
        panelIzquierdo.add(Box.createVerticalGlue());

        // ============ LADO DERECHO ============
        JPanel contenedorDerecho = new JPanel(new GridBagLayout());
        contenedorDerecho.setOpaque(false);
        contenedorDerecho.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 30, 30);
                g2.setColor(BLANCO);
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 30, 30);
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(35, 35, 35, 35));

        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(montserratBold.deriveFont(Font.BOLD, 24f));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Ingresa tus credenciales para continuar");
        lblSubtitulo.setFont(montserratRegular.deriveFont(Font.PLAIN, 12f));
        lblSubtitulo.setForeground(new Color(120, 130, 150));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(montserratBold.deriveFont(Font.BOLD, 12f));
        lblUsuario.setForeground(AZUL_MEDIO);
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsuario = new JTextField();
        txtUsuario.setFont(montserratRegular.deriveFont(Font.PLAIN, 14f));
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsuario.setPreferredSize(new Dimension(300, 40));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AZUL_CLARO, 12),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(montserratBold.deriveFont(Font.BOLD, 12f));
        lblPassword.setForeground(AZUL_MEDIO);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(montserratRegular.deriveFont(Font.PLAIN, 14f));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AZUL_CLARO, 12),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtPassword.setEchoChar('•');

        chkMostrar = new JCheckBox("Mostrar contraseña");
        chkMostrar.setFont(montserratRegular.deriveFont(Font.PLAIN, 12f));
        chkMostrar.setForeground(new Color(90, 100, 120));
        chkMostrar.setOpaque(false);
        chkMostrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkMostrar.setFocusPainted(false);
        chkMostrar.addActionListener(e ->
                txtPassword.setEchoChar(chkMostrar.isSelected() ? (char) 0 : '•')
        );

        btnIngresar = new JButton("INGRESAR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, AZUL_CLARO,
                        getWidth(), getHeight(), AZUL_ACENTO);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnIngresar.setFont(montserratBold.deriveFont(Font.BOLD, 14f));
        btnIngresar.setForeground(BLANCO);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setContentAreaFilled(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setOpaque(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnIngresar.setPreferredSize(new Dimension(300, 45));
        btnIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIngresar.addActionListener(e -> validarIngreso());

        txtPassword.addActionListener(e -> validarIngreso());
        txtUsuario.addActionListener(e -> txtPassword.requestFocusInWindow());

        JButtonX btnCerrar = new JButtonX();
        btnCerrar.addActionListener(e -> cerrarAplicacion());

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        panelSuperior.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        panelSuperior.add(btnCerrar, BorderLayout.EAST);

        tarjeta.add(panelSuperior);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 5)));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 5)));
        tarjeta.add(lblSubtitulo);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 25)));
        tarjeta.add(lblUsuario);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 6)));
        tarjeta.add(txtUsuario);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 15)));
        tarjeta.add(lblPassword);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 6)));
        tarjeta.add(txtPassword);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 8)));
        tarjeta.add(chkMostrar);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 20)));
        tarjeta.add(btnIngresar);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblPista = new JLabel("Usuario: ADMIN  |  Clave: 123456");
        lblPista.setFont(montserratItalic.deriveFont(Font.ITALIC, 11f));
        lblPista.setForeground(new Color(150, 160, 180));
        lblPista.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(lblPista);

        contenedorDerecho.add(tarjeta);

        panelPrincipal.add(panelIzquierdo);
        panelPrincipal.add(contenedorDerecho);
        setContentPane(panelPrincipal);
    }

    private void cerrarAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas salir del sistema?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void validarIngreso() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos.",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (usuario.equals(USUARIO_VALIDO) && password.equals(CLAVE_VALIDA)) {
            // ✅ Abre el menú principal
            new Opciones(usuario).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos.\nIntente nuevamente.",
                    "Acceso Denegado",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final Color color;
        private final int radio;

        RoundedBorder(Color color, int radio) {
            this.color = color;
            this.radio = radio;
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(4, 4, 4, 4); }

        @Override
        public boolean isBorderOpaque() { return false; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radio, radio);
            g2.dispose();
        }
    }

    private class JButtonX extends JButton {
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

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e)  { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();

            if (hover) {
                g2.setColor(ROJO_FONDO);
                g2.fillOval(2, 2, w - 4, h - 4);
            }

            g2.setColor(hover ? ROJO_HOVER : GRIS_ICONO);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int margen = 10;
            g2.drawLine(margen, margen, w - margen, h - margen);
            g2.drawLine(w - margen, margen, margen, h - margen);

            g2.dispose();
        }
    }
}