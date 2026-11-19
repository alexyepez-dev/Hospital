package Componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Paleta (azul medio claro + blanco) y fábricas de componentes comunes de la interfaz. */
public final class Estilo {

    public static final Color AZUL = new Color(0x4A90E2);
    public static final Color AZUL_CLARO = new Color(0x7DB4F0);
    public static final Color AZUL_OSCURO = new Color(0x2F6FB8);
    public static final Color AZUL_SUAVE = new Color(0xEAF3FC);
    public static final Color BORDE = new Color(0xDCE8F6);
    public static final Color TEXTO = new Color(0x1F2D3D);
    public static final Color TEXTO_SEC = new Color(0x6B7C93);
    /**
     * Montserrat, la fuente del resto del sistema (VistaModulo la registra al abrir
     * cada módulo); si no está disponible se usa Segoe UI.
     */
    public static final String FUENTE = elegirFuente();

    /** Tamaño de la sombra de las tarjetas (px por lado). */
    public static final int SOMBRA = 6;
    /** Separación entre tarjetas; la sombra ya agrega 2×SOMBRA de espacio visual. */
    public static final int SEPARACION = 6;

    private Estilo() {
    }

    private static String elegirFuente() {
        for (String familia : java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (familia.equalsIgnoreCase("Montserrat")) {
                return familia;
            }
        }
        return "Segoe UI";
    }

    /**
     * Agranda la ventana de un módulo (VistaModulo la crea de 1200×720) sin pasarse
     * del área útil de la pantalla, y vuelve a recortar sus esquinas redondeadas.
     */
    public static void agrandarVentana(javax.swing.JFrame v, int ancho, int alto) {
        java.awt.Rectangle util = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int w = Math.min(ancho, util.width);
        int h = Math.min(alto, util.height);
        v.setSize(w, h);
        v.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 25, 25));
        v.setLocationRelativeTo(null);
    }

    public static Font fuente(int estilo, int tamano) {
        return new Font(FUENTE, estilo, tamano);
    }

    public static JLabel etiqueta(String texto, int tamano, int estilo, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(estilo, tamano));
        l.setForeground(color);
        return l;
    }

    /** Alinea a la izquierda dentro de un BoxLayout vertical. */
    public static <T extends JComponent> T izq(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    /** Da a un PanelRedondeado el aspecto de tarjeta blanca con sombra. */
    public static <T extends PanelRedondeado> T comoTarjeta(T p) {
        p.setBackground(Color.WHITE);
        p.setColorBorde(BORDE);
        p.setRadio(14);
        p.setSombra(SOMBRA);
        int s = SOMBRA;
        p.setBorder(BorderFactory.createEmptyBorder(18 + s - s / 2, 20 + s, 18 + s + s / 2, 20 + s));
        return p;
    }

    public static PanelRedondeado tarjeta() {
        return comoTarjeta(new PanelRedondeado());
    }

    /** Chip + título grande + subtítulo para la parte superior de cada sección. */
    public static JComponent encabezado(String chip, String titulo, String subtitulo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(0, SOMBRA, 0, 0));
        p.add(izq(Insignia.chip(chip)));
        p.add(Box.createVerticalStrut(8));
        p.add(izq(anchoLibre(etiqueta(titulo, 26, Font.BOLD, TEXTO))));
        p.add(Box.createVerticalStrut(2));
        p.add(izq(anchoLibre(etiqueta(subtitulo, 13, Font.PLAIN, TEXTO_SEC))));
        return p;
    }

    /** Deja que BoxLayout le dé todo el ancho disponible (evita que el texto salga cortado con "..."). */
    private static JLabel anchoLibre(JLabel l) {
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, l.getPreferredSize().height));
        return l;
    }

    /**
     * Tarjeta de resultado:  TÍTULO
     *                        valor grande
     *                        texto secundario
     */
    public static PanelRedondeado tarjetaDato(String titulo, JComponent valor, JComponent sub) {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BorderLayout(0, 4));
        t.add(etiqueta(titulo, 11, Font.BOLD, TEXTO_SEC), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(izq(valor));
        cuerpo.add(izq(sub));
        t.add(cuerpo, BorderLayout.CENTER);
        return t;
    }

    /** Etiqueta de sección dentro de una tarjeta (azul, mayúsculas). */
    public static JLabel seccion(String texto) {
        return izq(etiqueta(texto, 11, Font.BOLD, AZUL));
    }

    public static BotonRedondeado boton(String texto) {
        BotonRedondeado b = new BotonRedondeado();
        b.setText(texto);
        b.setBackground(AZUL);
        b.setFont(fuente(Font.BOLD, 13));
        // Margen lateral corto para que los textos largos quepan en el panel izquierdo
        b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setPreferredSize(new Dimension(200, 44));
        return b;
    }

    /** Envuelve un contenido (tabla, lista) con un borde fino redondeado. */
    public static JComponent marco(JComponent c) {
        PanelRedondeado m = new PanelRedondeado();
        m.setRadio(10);
        m.setBackground(Color.WHITE);
        m.setColorBorde(BORDE);
        m.setLayout(new BorderLayout());
        m.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        m.add(c, BorderLayout.CENTER);
        return m;
    }

    /** Franja de aviso con fondo de color suave; el color se puede cambiar con setBackground. */
    public static PanelRedondeado franja(JLabel texto) {
        PanelRedondeado f = new PanelRedondeado();
        f.setBackground(AZUL_SUAVE);
        f.setRadio(10);
        f.setLayout(new BorderLayout());
        f.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        f.add(texto, BorderLayout.CENTER);
        return f;
    }

    /**
     * Tarjeta con título (y descripción opcional) arriba, una franja de aviso y
     * una tabla con scroll ocupando el resto.
     */
    public static PanelRedondeado tarjetaTabla(String titulo, JLabel descripcion, JComponent franja, JComponent tabla) {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BorderLayout(0, 10));
        JPanel cab = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cab.setOpaque(false);
        cab.add(etiqueta(titulo, 16, Font.BOLD, TEXTO));
        if (descripcion != null) {
            cab.add(Box.createHorizontalStrut(10));
            cab.add(descripcion);
        }
        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setOpaque(false);
        norte.add(cab, BorderLayout.NORTH);
        if (franja != null) {
            norte.add(franja, BorderLayout.SOUTH);
        }
        t.add(norte, BorderLayout.NORTH);
        t.add(marco(ScrollModerno.envolver(tabla)), BorderLayout.CENTER);
        return t;
    }

    /**
     * Círculo de color dibujado (Montserrat no trae el carácter "●").
     * @param borde color del contorno, o null para no dibujarlo
     */
    public static Icon punto(Color color, int diametro, Color borde) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(x, y, diametro, diametro);
                if (borde != null) {
                    g2.setColor(borde);
                    g2.drawOval(x, y, diametro - 1, diametro - 1);
                }
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return diametro;
            }

            @Override
            public int getIconHeight() {
                return diametro;
            }
        };
    }
}
