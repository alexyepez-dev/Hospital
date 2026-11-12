package Componentes;

import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Barra de progreso delgada y redondeada (0.0 a 1.0). */
public class BarraProgreso extends javax.swing.JComponent {

    private double valor;

    public BarraProgreso() {
        setAlignmentX(LEFT_ALIGNMENT);
    }

    public void setValor(double valor) {
        this.valor = Math.max(0, Math.min(1, valor));
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 8);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int h = 8;
        int y = (getHeight() - h) / 2;
        g2.setColor(Estilo.AZUL_SUAVE);
        g2.fillRoundRect(0, y, getWidth(), h, h, h);
        int w = (int) Math.round(getWidth() * valor);
        if (w > 0) {
            g2.setPaint(new GradientPaint(0, 0, Estilo.AZUL_CLARO, getWidth(), 0, Estilo.AZUL_OSCURO));
            g2.fillRoundRect(0, y, Math.max(w, h), h, h, h);
        }
        g2.dispose();
    }
}
