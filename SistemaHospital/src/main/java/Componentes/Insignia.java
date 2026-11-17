package Componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Píldora pequeña con texto (chip), usada en la parte superior de cada sección. */
public class Insignia extends javax.swing.JComponent {

    private final String texto;
    private final Color fondo;
    private final Color tinta;
    private final Font fuente;

    private Insignia(String texto, Color fondo, Color tinta, Font fuente) {
        this.texto = texto;
        this.fondo = fondo;
        this.tinta = tinta;
        this.fuente = fuente;
        setAlignmentX(LEFT_ALIGNMENT);
    }

    public static Insignia chip(String texto) {
        return new Insignia(texto, Estilo.AZUL_SUAVE, Estilo.AZUL_OSCURO, Estilo.fuente(Font.BOLD, 11));
    }

    @Override
    public Dimension getPreferredSize() {
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
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, d.width, d.height, d.height, d.height);
        g2.setFont(fuente);
        g2.setColor(tinta);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (d.width - fm.stringWidth(texto)) / 2, (d.height - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
