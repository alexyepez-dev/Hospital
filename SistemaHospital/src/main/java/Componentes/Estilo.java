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
    public static final Color FONDO = new Color(0xF2F7FD);
    public static final Color BORDE = new Color(0xDCE8F6);
    public static final Color TEXTO = new Color(0x1F2D3D);
    public static final Color TEXTO_SEC = new Color(0x6B7C93);
    public static final String FUENTE = "Segoe UI";

    /** Tamaño de la sombra de las tarjetas (px por lado). */
    public static final int SOMBRA = 6;
    /** Separación entre tarjetas; la sombra ya agrega 2×SOMBRA de espacio visual. */
    public static final int SEPARACION = 6;

    private Estilo() {
    }

    /**
     * Da a la ventana el tamaño deseado sin pasarse del área útil de la pantalla
     * (sin la barra de tareas), así nada queda cortado con el escalado de Windows.
     */
    public static void ajustarAPantalla(java.awt.Window v, int ancho, int alto, int anchoMin, int altoMin) {
        java.awt.Rectangle util = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        v.setMinimumSize(new Dimension(Math.min(anchoMin, util.width), Math.min(altoMin, util.height)));
        v.setSize(Math.min(ancho, util.width), Math.min(alto, util.height));
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
     * Tarjeta de resultado:  TÍTULO ............ [icono]
     *                        valor grande
     *                        texto secundario
     */
    public static PanelRedondeado tarjetaDato(String titulo, String icono, JComponent valor, JComponent sub) {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BorderLayout(0, 4));
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        JLabel lt = etiqueta(titulo, 11, Font.BOLD, TEXTO_SEC);
        cab.add(lt, BorderLayout.WEST);
        if (icono != null) {
            cab.add(Insignia.icono(icono), BorderLayout.EAST);
        }
        t.add(cab, BorderLayout.NORTH);

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
        b.setFont(fuente(Font.BOLD, 14));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setPreferredSize(new Dimension(200, 44));
        return b;
    }

    /** Botón secundario: fondo azul suave con texto azul oscuro. */
    public static BotonRedondeado botonSecundario(String texto) {
        BotonRedondeado b = boton(texto);
        b.setBackground(new Color(0xE3EEFB));
        b.setForeground(AZUL_OSCURO);
        b.setFont(fuente(Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return b;
    }

    public static JComponent separador() {
        JComponent linea = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BORDE);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        linea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        linea.setPreferredSize(new Dimension(10, 1));
        return linea;
    }

    /** Fila horizontal de leyendas (punto de color + texto). */
    public static JComponent filaLeyendas(Object... colorTexto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        p.setOpaque(false);
        for (int i = 0; i < colorTexto.length; i += 2) {
            p.add(leyenda((Color) colorTexto[i], (String) colorTexto[i + 1]));
        }
        return p;
    }

    /** Punto de color + texto, para leyendas. */
    public static JLabel leyenda(Color color, String texto) {
        JLabel l = etiqueta(texto, 12, Font.PLAIN, TEXTO_SEC);
        l.setIconTextGap(8);
        l.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(x, y, 12, 12);
                g2.setColor(new Color(0xC9DAEE));
                g2.drawOval(x, y, 11, 11);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 12;
            }

            @Override
            public int getIconHeight() {
                return 12;
            }
        });
        return l;
    }
}
