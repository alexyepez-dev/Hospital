package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Semáforo horizontal de tres luces. Estado: 0 = rojo, 1 = amarillo, 2 = verde,
 * -1 = apagado.
 */
public class Semaforo extends javax.swing.JComponent {

    public static final Color ROJO = new Color(0xEF4444);
    public static final Color AMARILLO = new Color(0xF5B700);
    public static final Color VERDE = new Color(0x22C55E);

    private static final Color CARCASA = new Color(0xEEF4FB);
    private static final Color BORDE = new Color(0xD6E4F5);
    private static final Color APAGADO = new Color(0xD5DEEA);

    private int estado = -1;

    public Semaforo() {
        setOpaque(false);
    }

    public void setEstado(int estado) {
        this.estado = estado;
        repaint();
    }

    public int getEstado() {
        return estado;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(132, 46);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getPreferredSize().width;
        int h = getPreferredSize().height;
        int x0 = 0;
        int y0 = (getHeight() - h) / 2;

        g2.setColor(CARCASA);
        g2.fillRoundRect(x0, y0, w - 1, h - 1, h, h);
        g2.setColor(BORDE);
        g2.drawRoundRect(x0, y0, w - 1, h - 1, h, h);

        Color[] colores = {ROJO, AMARILLO, VERDE};
        int d = 28;
        int gap = (w - 3 * d) / 4;
        for (int i = 0; i < 3; i++) {
            int cx = x0 + gap + i * (d + gap);
            int cy = y0 + (h - d) / 2;
            if (i == estado) {
                Color c = colores[i];
                // Halo suave alrededor de la luz encendida
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
                g2.fillOval(cx - 4, cy - 4, d + 8, d + 8);
                g2.setColor(c);
                g2.fillOval(cx, cy, d, d);
                g2.setColor(new Color(255, 255, 255, 110));
                g2.fillOval(cx + 7, cy + 5, d / 3, d / 4);
            } else {
                g2.setColor(APAGADO);
                g2.fillOval(cx, cy, d, d);
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(BORDE);
                g2.drawOval(cx, cy, d, d);
            }
        }
        g2.dispose();
    }
}
