package Componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 * Gráfico de barras horizontales que compara los algoritmos: por cada uno, una
 * barra de comparaciones y otra de intercambios, con el tiempo a la derecha.
 */
public class BarrasComparacion extends JComponent {

    /** Datos de un algoritmo. */
    public record Fila(String nombre, String complejidad, long comparaciones, long intercambios, String tiempo) {
    }

    private static final Color BARRA_COMP = Estilo.AZUL;
    private static final Color BARRA_INTER = new Color(0xF2B33D);
    private static final int ALTO_BLOQUE = 74;

    private List<Fila> filas = new ArrayList<>();
    private String mensaje = "Calculando…";
    private int destacado = -1;

    public BarrasComparacion() {
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    public void setFilas(List<Fila> filas) {
        this.filas = new ArrayList<>(filas);
        this.mensaje = null;
        revalidate();
        repaint();
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
        repaint();
    }

    /** Índice del algoritmo elegido en el menú, que se dibuja resaltado. */
    public void setDestacado(int i) {
        destacado = i;
        repaint();
    }

    public static JComponent leyenda() {
        return Estilo.filaLeyendas(BARRA_COMP, "Comparaciones", BARRA_INTER, "Intercambios");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 24 + Math.max(1, filas.size()) * ALTO_BLOQUE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (mensaje != null) {
            g2.setFont(Estilo.fuente(Font.PLAIN, 13));
            g2.setColor(Estilo.TEXTO_SEC);
            g2.drawString(mensaje, 24, 36);
            g2.dispose();
            return;
        }

        long max = 1;
        for (Fila f : filas) {
            max = Math.max(max, Math.max(f.comparaciones(), f.intercambios()));
        }
        Font fNombre = Estilo.fuente(Font.BOLD, 14);
        Font fChico = Estilo.fuente(Font.PLAIN, 11);
        Font fNumero = Estilo.fuente(Font.BOLD, 12);
        int x0 = 24;
        int anchoEtiqueta = 150;
        int anchoTiempo = 110;
        int xb = x0 + anchoEtiqueta;
        int anchoBarras = Math.max(60, getWidth() - xb - anchoTiempo - 70);

        for (int i = 0; i < filas.size(); i++) {
            Fila f = filas.get(i);
            int y = 16 + i * ALTO_BLOQUE;
            if (i == destacado) {
                g2.setColor(Estilo.AZUL_SUAVE);
                g2.fillRoundRect(10, y - 4, getWidth() - 20, ALTO_BLOQUE - 8, 14, 14);
            }
            // Nombre y complejidad
            g2.setFont(fNombre);
            g2.setColor(i == destacado ? Estilo.AZUL_OSCURO : Estilo.TEXTO);
            g2.drawString(f.nombre(), x0, y + 24);
            g2.setFont(fChico);
            g2.setColor(Estilo.TEXTO_SEC);
            g2.drawString(f.complejidad(), x0, y + 42);

            // Barras
            barra(g2, xb, y + 10, anchoBarras, f.comparaciones(), max, BARRA_COMP, fNumero);
            barra(g2, xb, y + 34, anchoBarras, f.intercambios(), max, BARRA_INTER, fNumero);

            // Tiempo
            g2.setFont(fNumero);
            g2.setColor(Estilo.TEXTO);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(f.tiempo(), getWidth() - 24 - fm.stringWidth(f.tiempo()), y + 24);
            g2.setFont(fChico);
            g2.setColor(Estilo.TEXTO_SEC);
            fm = g2.getFontMetrics();
            g2.drawString("tiempo real", getWidth() - 24 - fm.stringWidth("tiempo real"), y + 42);
        }
        g2.dispose();
    }

    private static void barra(Graphics2D g2, int x, int y, int ancho, long valor, long max, Color color, Font fuente) {
        int h = 16;
        g2.setColor(new Color(0xF1F5FA));
        g2.fillRoundRect(x, y, ancho, h, h, h);
        int w = (int) Math.round((double) valor / max * ancho);
        if (valor > 0) {
            g2.setColor(color);
            g2.fillRoundRect(x, y, Math.max(w, h), h, h, h);
        }
        g2.setFont(fuente);
        g2.setColor(Estilo.TEXTO);
        g2.drawString(String.valueOf(valor), x + Math.max(w, h) + 8, y + 13);
    }
}
