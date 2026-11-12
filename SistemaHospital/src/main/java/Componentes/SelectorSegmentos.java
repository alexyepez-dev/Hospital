package Componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Control segmentado (como el interruptor de iOS):  [ Lenta | Normal | Rápida ]
 * La opción activa se dibuja como una píldora blanca sobre fondo azul suave.
 */
public class SelectorSegmentos extends javax.swing.JComponent {

    private static final int ALTO = 38;
    private static final int MARGEN = 4;

    private final List<String> opciones = new ArrayList<>();
    private final List<IntConsumer> oyentes = new ArrayList<>();
    private final Font fuente = Estilo.fuente(Font.BOLD, 13);
    private final int relleno;
    private final boolean llenarAncho;
    private int seleccion;
    private int encima = -1;

    /**
     * @param llenarAncho true para repartir todo el ancho disponible entre las opciones
     */
    public SelectorSegmentos(int relleno, boolean llenarAncho, String... opciones) {
        this.relleno = relleno;
        this.llenarAncho = llenarAncho;
        this.opciones.addAll(List.of(opciones));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setAlignmentX(LEFT_ALIGNMENT);
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = indiceEn(e.getX());
                if (i >= 0 && isEnabled()) {
                    setSeleccion(i);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int i = indiceEn(e.getX());
                if (i != encima) {
                    encima = i;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = -1;
                repaint();
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    public SelectorSegmentos(String... opciones) {
        this(18, false, opciones);
    }

    public int getSeleccion() {
        return seleccion;
    }

    /** Selecciona la opción i y avisa a los oyentes. */
    public void setSeleccion(int i) {
        seleccion = i;
        repaint();
        for (IntConsumer o : oyentes) {
            o.accept(i);
        }
    }

    public void alCambiar(IntConsumer oyente) {
        oyentes.add(oyente);
    }

    private int anchoNatural(int i) {
        return getFontMetrics(fuente).stringWidth(opciones.get(i)) + 2 * relleno;
    }

    /** Ancho de cada segmento según el modo (natural o repartido). */
    private int[] anchos() {
        int[] w = new int[opciones.size()];
        if (llenarAncho && !opciones.isEmpty()) {
            int total = getWidth() - 2 * MARGEN;
            for (int i = 0; i < w.length; i++) {
                w[i] = total / w.length + (i < total % w.length ? 1 : 0);
            }
        } else {
            for (int i = 0; i < w.length; i++) {
                w[i] = anchoNatural(i);
            }
        }
        return w;
    }

    private int indiceEn(int x) {
        int[] w = anchos();
        int cx = MARGEN;
        for (int i = 0; i < w.length; i++) {
            if (x >= cx && x < cx + w[i]) {
                return i;
            }
            cx += w[i];
        }
        return -1;
    }

    @Override
    public Dimension getPreferredSize() {
        int w = 2 * MARGEN;
        for (int i = 0; i < opciones.size(); i++) {
            w += anchoNatural(i);
        }
        return new Dimension(w, ALTO);
    }

    @Override
    public Dimension getMaximumSize() {
        return llenarAncho ? new Dimension(Integer.MAX_VALUE, ALTO) : getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        int ancho = llenarAncho ? getWidth() : getPreferredSize().width;
        g2.setColor(Estilo.AZUL_SUAVE);
        g2.fillRoundRect(0, 0, ancho, ALTO, 14, 14);

        g2.setFont(fuente);
        FontMetrics fm = g2.getFontMetrics();
        int[] w = anchos();
        int x = MARGEN;
        for (int i = 0; i < w.length; i++) {
            if (i == seleccion) {
                g2.setColor(new Color(30, 70, 130, 22));
                g2.fillRoundRect(x, MARGEN + 1, w[i], ALTO - 2 * MARGEN, 10, 10);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, MARGEN, w[i], ALTO - 2 * MARGEN, 10, 10);
                g2.setColor(Estilo.AZUL_OSCURO);
            } else {
                g2.setColor(i == encima && isEnabled() ? Estilo.AZUL_OSCURO : Estilo.TEXTO_SEC);
            }
            String t = opciones.get(i);
            g2.drawString(t, x + (w[i] - fm.stringWidth(t)) / 2, (ALTO - fm.getHeight()) / 2 + fm.getAscent());
            x += w[i];
        }
        g2.dispose();
    }
}
