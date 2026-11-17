package Componentes;

/**
 * JPanel con esquinas redondeadas y sombra suave opcional. Usable desde el
 * editor de formularios de NetBeans: el color de relleno es la propiedad
 * "background"; "radio", "colorBorde" y "sombra" aparecen en la hoja de
 * propiedades.
 *
 * Con sombra > 0 la tarjeta se dibuja "sombra" píxeles hacia adentro por cada
 * lado, para dejar espacio a la sombra; el borde (insets) debe considerarlo.
 */
public class PanelRedondeado extends javax.swing.JPanel {

    private int radio = 16;
    private java.awt.Color colorBorde;
    private int sombra = 0;

    public PanelRedondeado() {
        setOpaque(false);
        setBackground(java.awt.Color.WHITE);
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
        repaint();
    }

    public java.awt.Color getColorBorde() {
        return colorBorde;
    }

    public void setColorBorde(java.awt.Color colorBorde) {
        this.colorBorde = colorBorde;
        repaint();
    }

    public int getSombra() {
        return sombra;
    }

    public void setSombra(int sombra) {
        this.sombra = Math.max(0, sombra);
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        int s = sombra;
        int desplazamiento = s / 2;
        int x = s;
        int y = s - desplazamiento;
        int w = getWidth() - 2 * s - 1;
        int h = getHeight() - 2 * s - 1;
        int arco = radio * 2;

        // Sombra: capas translúcidas que se acumulan cerca de la tarjeta
        for (int i = s; i > 0; i--) {
            g2.setColor(new java.awt.Color(30, 70, 130, 6));
            g2.fillRoundRect(x - i, y - i + desplazamiento + 1, w + 2 * i, h + 2 * i, arco + i * 2, arco + i * 2);
        }

        g2.setColor(getBackground());
        g2.fillRoundRect(x, y, w, h, arco, arco);
        if (colorBorde != null) {
            g2.setColor(colorBorde);
            g2.drawRoundRect(x, y, w, h, arco, arco);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
