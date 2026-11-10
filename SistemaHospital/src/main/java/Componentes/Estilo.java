package Componentes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

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

    private Estilo() {
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

    public static PanelRedondeado tarjeta() {
        PanelRedondeado p = new PanelRedondeado();
        p.setBackground(Color.WHITE);
        p.setColorBorde(BORDE);
        p.setRadio(14);
        p.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        return p;
    }

    /** Título grande + subtítulo para la parte superior de cada sección. */
    public static JComponent encabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(izq(etiqueta(titulo, 24, Font.BOLD, TEXTO)));
        p.add(Box.createVerticalStrut(4));
        p.add(izq(etiqueta(subtitulo, 13, Font.PLAIN, TEXTO_SEC)));
        return p;
    }

    public static JSpinner spinner(SpinnerNumberModel modelo) {
        JSpinner s = new JSpinner(modelo);
        s.setFont(fuente(Font.PLAIN, 16));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        s.setPreferredSize(new Dimension(200, 40));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) s.getEditor();
        editor.getTextField().setHorizontalAlignment(JLabel.LEFT);
        editor.getTextField().setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        return s;
    }

    public static BotonRedondeado boton(String texto) {
        BotonRedondeado b = new BotonRedondeado();
        b.setText(texto);
        b.setBackground(AZUL);
        b.setFont(fuente(Font.BOLD, 14));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return b;
    }

    public static JComponent separador() {
        JPanel linea = new JPanel();
        linea.setBackground(BORDE);
        linea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        linea.setPreferredSize(new Dimension(10, 1));
        return linea;
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
                g2.setColor(BORDE);
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
