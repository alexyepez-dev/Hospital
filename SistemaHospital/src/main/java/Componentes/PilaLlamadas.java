package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Dibuja la pila de llamadas de factorial(n) en forma de escalera: cada llamada
 * baja un escalón hasta el caso base y a la derecha se muestra el valor que
 * devuelve al desapilarse.
 */
public class PilaLlamadas extends javax.swing.JPanel implements Scrollable {

    private static final int ALTO_FILA = 46;
    private static final int ANCHO_NODO = 104;
    private static final int ALTO_NODO = 34;
    private static final int SANGRIA = 14;
    private static final int MARGEN = 22;

    private static final NumberFormat FORMATO = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-EC"));

    private int n = -1;

    public PilaLlamadas() {
        setBackground(Color.WHITE);
    }

    public void setN(int n) {
        this.n = n;
        revalidate();
        repaint();
    }

    private int filas() {
        return n < 0 ? 0 : Math.max(n, 1);
    }

    private static long factorial(int k) {
        long r = 1;
        for (int i = 2; i <= k; i++) {
            r *= i;
        }
        return r;
    }

    private static String detalle(int k) {
        return "= " + k + " × " + FORMATO.format(factorial(k - 1)) + "  →  devuelve ";
    }

    @Override
    public Dimension getPreferredSize() {
        int f = filas();
        // Ancho de la fila más ancha (sangría + nodo + texto)
        FontMetrics fd = getFontMetrics(Estilo.fuente(Font.PLAIN, 12));
        FontMetrics fv = getFontMetrics(Estilo.fuente(Font.BOLD, 13));
        int ancho = 0;
        for (int i = 0; i < f; i++) {
            int k = (n == 0) ? 0 : n - i;
            int texto = k <= 1
                    ? fd.stringWidth("caso base  →  devuelve 1")
                    : fd.stringWidth(detalle(k)) + fv.stringWidth(FORMATO.format(factorial(k)));
            ancho = Math.max(ancho, i * SANGRIA + ANCHO_NODO + 16 + texto);
        }
        ancho += MARGEN * 2;
        int alto = MARGEN * 2 + f * ALTO_FILA - (ALTO_FILA - ALTO_NODO);
        return new Dimension(ancho, alto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (n < 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int f = filas();
        Font fNodo = Estilo.fuente(Font.BOLD, 13);
        Font fDetalle = Estilo.fuente(Font.PLAIN, 12);
        Font fValor = Estilo.fuente(Font.BOLD, 13);

        for (int i = 0; i < f; i++) {
            int k = (n == 0) ? 0 : n - i;
            boolean base = k <= 1;
            boolean raiz = i == 0;
            int x = MARGEN + i * SANGRIA;
            int y = MARGEN + i * ALTO_FILA;

            // Conector hacia la llamada siguiente
            if (i < f - 1) {
                g2.setColor(new Color(0xB9D3F0));
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = x + 14;
                g2.drawLine(cx, y + ALTO_NODO, cx, y + ALTO_FILA + ALTO_NODO / 2);
                g2.drawLine(cx, y + ALTO_FILA + ALTO_NODO / 2, x + SANGRIA, y + ALTO_FILA + ALTO_NODO / 2);
            }

            // Nodo
            Color fondo = raiz ? Estilo.AZUL_OSCURO : base ? Estilo.AZUL_SUAVE : Estilo.AZUL;
            Color tinta = base && !raiz ? Estilo.AZUL_OSCURO : Color.WHITE;
            g2.setColor(new Color(47, 111, 184, 22));
            g2.fillRoundRect(x + 1, y + 3, ANCHO_NODO, ALTO_NODO, 14, 14);
            g2.setColor(fondo);
            g2.fillRoundRect(x, y, ANCHO_NODO, ALTO_NODO, 14, 14);
            if (base && !raiz) {
                g2.setColor(new Color(0xB9D3F0));
                g2.drawRoundRect(x, y, ANCHO_NODO - 1, ALTO_NODO - 1, 14, 14);
            }
            String etiqueta = "factorial(" + k + ")";
            g2.setFont(fNodo);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(tinta);
            g2.drawString(etiqueta, x + (ANCHO_NODO - fm.stringWidth(etiqueta)) / 2, y + 22);

            // Detalle de la llamada y valor devuelto
            int tx = x + ANCHO_NODO + 16;
            String detalle = base ? "caso base  →  devuelve 1" : detalle(k);
            g2.setFont(fDetalle);
            g2.setColor(Estilo.TEXTO_SEC);
            g2.drawString(detalle, tx, y + 22);
            if (!base) {
                int vx = tx + g2.getFontMetrics().stringWidth(detalle);
                g2.setFont(fValor);
                g2.setColor(Estilo.AZUL_OSCURO);
                g2.drawString(FORMATO.format(factorial(k)), vx, y + 22);
            }
        }
        g2.dispose();
    }

    // --- Scrollable: ocupa el espacio visible cuando la pila es pequeña ---

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
        return orientation == SwingConstants.HORIZONTAL ? visible.width - 40 : visible.height - 40;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getParent() != null && getParent().getWidth() > getPreferredSize().width;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getParent() != null && getParent().getHeight() > getPreferredSize().height;
    }
}
