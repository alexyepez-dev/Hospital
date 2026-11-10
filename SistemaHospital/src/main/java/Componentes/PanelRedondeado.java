package Componentes;

/**
 * JPanel con esquinas redondeadas. Usable desde el editor de formularios de
 * NetBeans: el color de relleno es la propiedad "background"; "radio" y
 * "colorBorde" aparecen en la hoja de propiedades.
 */
public class PanelRedondeado extends javax.swing.JPanel {

    private int radio = 16;
    private java.awt.Color colorBorde;

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

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio * 2, radio * 2);
        if (colorBorde != null) {
            g2.setColor(colorBorde);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio * 2, radio * 2);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
