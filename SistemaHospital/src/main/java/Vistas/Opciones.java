package Vistas;   // ← ANTES era "package src.sistemahospital.Vistas;"

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.InputStream;

/**
 * Vista de Menú Principal - Universidad de Guayaquil Materia: Estructura de
 * Datos
 *
 * @author CRACKS
 */
public class Opciones extends JFrame {

    // ===== Paleta de colores =====
    private final Color AZUL_OSCURO = new Color(10, 25, 60);
    private final Color AZUL_MEDIO = new Color(20, 60, 130);
    private final Color AZUL_CLARO = new Color(40, 110, 200);
    private final Color AZUL_ACENTO = new Color(0, 180, 255);
    private final Color BLANCO = Color.WHITE;

    // Colores del botón cerrar sesión
    private final Color ROJO_NORMAL = new Color(220, 53, 69);
    private final Color ROJO_HOVER = new Color(200, 35, 51);

    // Colores de las tarjetas
    private final Color TARJETA_ARREGLOS = new Color(52, 152, 219);
    private final Color TARJETA_RECURSIVIDAD = new Color(155, 89, 182);
    private final Color TARJETA_ORDENAMIENTO = new Color(46, 204, 113);
    private final Color TARJETA_BUSQUEDA = new Color(230, 126, 34);

    // Fuentes
    private Font montserratRegular;
    private Font montserratBold;
    private Font montserratItalic;
    private Font montserratBoldItalic;

    // Usuario logueado
    private String usuarioActual;

    public Opciones() {
        this("ADMIN");
    }

    public Opciones(String usuario) {
        this.usuarioActual = usuario;
        cargarFuentes();
        initComponents();
    }

    // ====================== CARGA DE FUENTES ======================
    private void cargarFuentes() {
        montserratRegular = cargarFuente("Montserrat-Regular.ttf", Font.PLAIN);
        montserratBold = cargarFuente("Montserrat-Bold.ttf", Font.BOLD);
        montserratItalic = cargarFuente("Montserrat-Italic.ttf", Font.ITALIC);
        montserratBoldItalic = cargarFuente("Montserrat-BoldItalic.ttf", Font.BOLD | Font.ITALIC);
    }

    private Font cargarFuente(String nombreArchivo, int estilo) {
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("fonts/" + nombreArchivo);
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            }
        } catch (Exception ignored) {
        }

        try {
            File archivo = new File("fonts/" + nombreArchivo);
            if (archivo.exists()) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, archivo);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            }
        } catch (Exception ignored) {
        }

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
        } catch (Exception ignored) {
        }

        for (String nombre : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (nombre.equalsIgnoreCase("Montserrat")) {
                return new Font("Montserrat", estilo, 14);
            }
        }

        return new Font("Segoe UI", estilo, 14);
    }

    // ====================== UI ======================
    private void initComponents() {
        setTitle("Menú Principal - Sistema Hospitalario U.G.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 700, 25, 25));

        // ===== Fondo con gradiente =====
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

        // ===== Barra superior =====
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);
        barraSuperior.setBorder(new EmptyBorder(20, 30, 10, 30));

        // ---- Izquierda: logo + título ----
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

        JPanel textosMarca = new JPanel();
        textosMarca.setOpaque(false);
        textosMarca.setLayout(new BoxLayout(textosMarca, BoxLayout.Y_AXIS));

        JLabel lblUni = new JLabel("UNIVERSIDAD DE GUAYAQUIL");
        lblUni.setFont(montserratBold.deriveFont(Font.BOLD, 14f));
        lblUni.setForeground(BLANCO);

        JLabel lblSub = new JLabel("Estructura de Datos · Sistema Hospitalario");
        lblSub.setFont(montserratRegular.deriveFont(Font.PLAIN, 11f));
        lblSub.setForeground(new Color(180, 220, 255));

        textosMarca.add(lblUni);
        textosMarca.add(lblSub);

        marca.add(logoMini);
        marca.add(textosMarca);

        // ---- Derecha: usuario + botón cerrar sesión + X ----
        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelDerecha.setOpaque(false);

        // Ícono de usuario (avatar dibujado)
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Círculo de fondo
                GradientPaint gp = new GradientPaint(
                        0, 0, AZUL_ACENTO,
                        getWidth(), getHeight(), new Color(100, 200, 255));
                g2.setPaint(gp);
                g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));

                // Silueta de persona (cabeza + hombros)
                g2.setColor(AZUL_OSCURO);

                // Cabeza
                int cabeza = getWidth() / 2;
                g2.fillOval(cabeza - 6, 7, 12, 12);

                // Hombros (arco)
                g2.fillArc(-6, 22, getWidth() + 12, getHeight(), 0, 180);

                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(32, 32));

        JLabel lblUsuario = new JLabel(usuarioActual);
        lblUsuario.setFont(montserratBold.deriveFont(Font.BOLD, 13f));
        lblUsuario.setForeground(BLANCO);

        // Botón cerrar sesión COMPACTO (nueva ubicación: barra superior)
        BotonCerrarSesion btnCerrarSesion = new BotonCerrarSesion();
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        JButtonX btnCerrarVentana = new JButtonX();
        btnCerrarVentana.addActionListener(e -> cerrarAplicacion());

        panelDerecha.add(avatar);
        panelDerecha.add(lblUsuario);
        panelDerecha.add(btnCerrarSesion);
        panelDerecha.add(btnCerrarVentana);

        barraSuperior.add(marca, BorderLayout.WEST);
        barraSuperior.add(panelDerecha, BorderLayout.EAST);

        // ===== Encabezado central =====
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBorder(new EmptyBorder(20, 0, 30, 0));

        JLabel lblTitulo = new JLabel("Panel de Control");
        lblTitulo.setFont(montserratBold.deriveFont(Font.BOLD, 34f));
        lblTitulo.setForeground(BLANCO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBienvenida = new JLabel("Bienvenido, " + usuarioActual + ". Selecciona un módulo para continuar.");
        lblBienvenida.setFont(montserratItalic.deriveFont(Font.ITALIC, 14f));
        lblBienvenida.setForeground(new Color(200, 225, 255));
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createRigidArea(new Dimension(0, 8)));
        encabezado.add(lblBienvenida);

        // ===== Grid de tarjetas (FIX: GridBagLayout para tamaño uniforme) =====
        JPanel contenedorTarjetas = new JPanel(new GridBagLayout());
        contenedorTarjetas.setOpaque(false);
        contenedorTarjetas.setBorder(new EmptyBorder(10, 80, 30, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;        // ← ocupa todo el espacio disponible
        gbc.weightx = 1.0;                         // ← mismo ancho
        gbc.weighty = 1.0;                         // ← MISMA ALTURA para todas
        gbc.insets = new Insets(12, 12, 12, 12);   // ← separación uniforme

        JPanel cardArreglos = crearTarjeta(
                "Arreglos",
                "Vectores, matrices y operaciones",
                TARJETA_ARREGLOS,
                TipoIcono.ARREGLOS,
                e -> abrirModulo("Arreglos"));

        JPanel cardRecursividad = crearTarjeta(
                "Recursividad",
                "Algoritmos y funciones recursivas",
                TARJETA_RECURSIVIDAD,
                TipoIcono.RECURSIVIDAD,
                e -> abrirModulo("Recursividad"));

        JPanel cardOrdenamiento = crearTarjeta(
                "Ordenamiento",
                "Burbuja, quicksort, mergesort y más",
                TARJETA_ORDENAMIENTO,
                TipoIcono.ORDENAMIENTO,
                e -> abrirModulo("Ordenamiento"));

        JPanel cardBusqueda = crearTarjeta(
                "Búsqueda",
                "Búsqueda lineal y binaria",
                TARJETA_BUSQUEDA,
                TipoIcono.BUSQUEDA,
                e -> abrirModulo("Búsqueda"));

        // Fila 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        contenedorTarjetas.add(cardArreglos, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        contenedorTarjetas.add(cardRecursividad, gbc);

        // Fila 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        contenedorTarjetas.add(cardOrdenamiento, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        contenedorTarjetas.add(cardBusqueda, gbc);

        // ===== Ensamblado =====
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(encabezado);
        centro.add(contenedorTarjetas);

        fondo.add(barraSuperior, BorderLayout.NORTH);
        fondo.add(centro, BorderLayout.CENTER);

        setContentPane(fondo);
    }

    // ====================== TARJETAS CON HOVER ======================
    private enum TipoIcono {
        ARREGLOS, RECURSIVIDAD, ORDENAMIENTO, BUSQUEDA
    }

    private JPanel crearTarjeta(String titulo, String subtitulo, Color acento,
            TipoIcono icono, java.awt.event.ActionListener accion) {

        TarjetaPremium tarjeta = new TarjetaPremium(acento, icono);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(24, 24, 24, 24));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel panelIcono = tarjeta.getPanelIcono();
        panelIcono.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(montserratBold.deriveFont(Font.BOLD, 22f));
        lblTitulo.setForeground(BLANCO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(montserratRegular.deriveFont(Font.PLAIN, 13f));
        lblSubtitulo.setForeground(new Color(220, 235, 255));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAbrir = new JLabel("Abrir módulo  →");
        lblAbrir.setFont(montserratBold.deriveFont(Font.BOLD, 12f));
        lblAbrir.setForeground(BLANCO);
        lblAbrir.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(panelIcono);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 18)));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 8)));
        tarjeta.add(lblSubtitulo);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(Box.createRigidArea(new Dimension(0, 18)));
        tarjeta.add(lblAbrir);

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                accion.actionPerformed(null);
            }
        });

        propagarHover(tarjeta);
        return tarjeta;
    }

    private void propagarHover(TarjetaPremium tarjeta) {
        MouseAdapter hoverPropagator = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setHover(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(
                        e.getComponent(), e.getPoint(), tarjeta);
                if (!tarjeta.contains(p)) {
                    tarjeta.setHover(false);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                tarjeta.dispatchEvent(SwingUtilities.convertMouseEvent(
                        e.getComponent(), e, tarjeta));
            }
        };

        for (Component c : tarjeta.getComponents()) {
            if (c instanceof JComponent) {
                ((JComponent) c).addMouseListener(hoverPropagator);
            }
        }
    }

    // ====================== ACCIONES ======================
    private void abrirModulo(String modulo) {
        JFrame vista = null;
        switch (modulo) {
            case "Arreglos":
                vista = new VistaArreglos(usuarioActual);
                break;
            case "Recursividad":
                vista = new VistaRecursividad(usuarioActual);
                break;
            case "Ordenamiento":
                vista = new VistaOrdenamiento(usuarioActual);
                break;
            case "Búsqueda":
                vista = new VistaBusqueda(usuarioActual);
                break;
        }
        if (vista != null) {
            vista.setVisible(true);
            this.dispose();
        }
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            new InicioSistema().setVisible(true);
            this.dispose();
        }
    }

    private void cerrarAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas salir del sistema?",
                "Cerrar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ====================== CLASES INTERNAS ======================
    private class TarjetaPremium extends JPanel {

        private final Color acento;
        private final TipoIcono icono;
        private boolean hover = false;
        private final JPanel panelIcono;

        public TarjetaPremium(Color acento, TipoIcono icono) {
            this.acento = acento;
            this.icono = icono;
            this.panelIcono = crearPanelIcono();

            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setHover(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setHover(false);
                }
            });
        }

        public void setHover(boolean valor) {
            if (this.hover != valor) {
                this.hover = valor;
                repaint();
            }
        }

        public JPanel getPanelIcono() {
            return panelIcono;
        }

        private JPanel crearPanelIcono() {
            JPanel p = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    dibujarIcono(g2, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            p.setOpaque(false);
            p.setPreferredSize(new Dimension(64, 64));
            p.setMaximumSize(new Dimension(64, 64));
            p.setMinimumSize(new Dimension(64, 64));
            return p;
        }

        private void dibujarIcono(Graphics2D g2, int w, int h) {
            g2.setColor(BLANCO);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = w / 2;
            int cy = h / 2;

            switch (icono) {
                case ARREGLOS: {
                    int baseY = cy + 12;
                    g2.fillRoundRect(cx - 18, baseY - 14, 8, 14, 3, 3);
                    g2.fillRoundRect(cx - 4, baseY - 22, 8, 22, 3, 3);
                    g2.fillRoundRect(cx + 10, baseY - 30, 8, 30, 3, 3);
                    break;
                }
                case RECURSIVIDAD: {
                    g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    for (int i = 0; i < 3; i++) {
                        int r = 6 + i * 6;
                        g2.drawArc(cx - r, cy - r, r * 2, r * 2, 0, 270);
                    }
                    break;
                }
                case ORDENAMIENTO: {
                    g2.drawLine(cx - 10, cy - 14, cx - 10, cy + 14);
                    g2.drawLine(cx + 10, cy - 14, cx + 10, cy + 14);
                    g2.drawLine(cx - 10, cy - 14, cx - 16, cy - 8);
                    g2.drawLine(cx - 10, cy - 14, cx - 4, cy - 8);
                    g2.drawLine(cx + 10, cy + 14, cx + 4, cy + 8);
                    g2.drawLine(cx + 10, cy + 14, cx + 16, cy + 8);
                    break;
                }
                case BUSQUEDA: {
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(cx - 14, cy - 14, 20, 20);
                    g2.drawLine(cx + 4, cy + 4, cx + 14, cy + 14);
                    break;
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int offset = hover ? 4 : 0;

            g2.setColor(new Color(0, 0, 0, hover ? 90 : 50));
            g2.fillRoundRect(6, 6 + offset, getWidth() - 12, getHeight() - 12, 25, 25);

            Color c1 = hover ? aclarar(acento, 30) : acento;
            Color c2 = hover ? acento : oscurecer(acento, 25);
            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, offset, getWidth() - 12, getHeight() - 12, 25, 25);

            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(0, offset, getWidth() - 12, 40, 25, 25);

            g2.dispose();
            super.paintComponent(g);
        }

        private Color aclarar(Color c, int delta) {
            return new Color(
                    Math.min(255, c.getRed() + delta),
                    Math.min(255, c.getGreen() + delta),
                    Math.min(255, c.getBlue() + delta));
        }

        private Color oscurecer(Color c, int delta) {
            return new Color(
                    Math.max(0, c.getRed() - delta),
                    Math.max(0, c.getGreen() - delta),
                    Math.max(0, c.getBlue() - delta));
        }
    }

    /**
     * Botón cerrar sesión COMPACTO para la barra superior.
     */
    private class BotonCerrarSesion extends JButton {

        private boolean hover = false;

        public BotonCerrarSesion() {
            setText("Cerrar sesión");
            setFont(montserratBold.deriveFont(Font.BOLD, 12f));
            setForeground(BLANCO);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(140, 34));
            setIcon(new IconoSalida(16, BLANCO));
            setIconTextGap(6);
            setToolTipText("Cerrar sesión");

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fondo1 = hover ? ROJO_HOVER : ROJO_NORMAL;
            Color fondo2 = hover ? ROJO_NORMAL : new Color(180, 35, 50);
            GradientPaint gp = new GradientPaint(0, 0, fondo1, getWidth(), getHeight(), fondo2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 10, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class IconoSalida implements Icon {

        private final int size;
        private final Color color;

        public IconoSalida(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int w = size, h = size;
            // Marco de la puerta
            g2.drawRoundRect(x + 1, y + 2, w - 6, h - 4, 3, 3);
            // Flecha saliendo
            int ax = x + 4;
            int ay = y + h / 2;
            g2.drawLine(ax, ay, ax + 7, ay);
            g2.drawLine(ax + 4, ay - 3, ax + 8, ay);
            g2.drawLine(ax + 4, ay + 3, ax + 8, ay);

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

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
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
