package Componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Etiqueta pequeña con fondo redondeado. Dos usos:
 *  - icono(): cuadrado de 34 px con un símbolo corto (n!, #, ms...) para las tarjetas.
 *  - chip():  píldora con texto, para datos como "Complejidad O(n)".
 */
public class Insignia extends javax.swing.JComponent {

    private final String texto;
    private final boolean cuadrada;
    private final Color fondo;
    private final Color tinta;
    private final Font fuente;

    private Insignia(String texto, boolean cuadrada, Color fondo, Color tinta, Font fuente) {
        this.texto = texto;
        this.cuadrada = cuadrada;
        this.fondo = fondo;
        this.tinta = tinta;
        this.fuente = fuente;
        setAlignmentX(LEFT_ALIGNMENT);
    }

    public static Insignia icono(String simbolo) {
        return new Insignia(simbolo, true, Estilo.AZUL_SUAVE, Estilo.AZUL_OSCURO,
                Estilo.fuente(Font.BOLD, simbolo.length() > 2 ? 11 : 13));
    }

    public static Insignia chip(String texto) {
        return new Insignia(texto, false, Estilo.AZUL_SUAVE, Estilo.AZUL_OSCURO, Estilo.fuente(Font.BOLD, 11));
    }

    @Override
    public Dimension getPreferredSize() {
        if (cuadrada) {
            return new Dimension(34, 34);
        }
        FontMetrics fm = getFontMetrics(fuente);
        return new Dimension(fm.stringWidth(texto) + 22, 26);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        Dimension d = getPreferredSize();
        int arco = cuadrada ? 12 : d.height;
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, d.width, d.height, arco, arco);
        g2.setFont(fuente);
        g2.setColor(tinta);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (d.width - fm.stringWidth(texto)) / 2, (d.height - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
