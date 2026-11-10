package Componentes;

/**
 * Botón plano con esquinas redondeadas. Usable desde el editor de formularios
 * de NetBeans: el color de relleno es la propiedad "background" y se oscurece
 * al pasar el mouse.
 */
public class BotonRedondeado extends javax.swing.JButton {

    private int radio = 10;

    public BotonRedondeado() {
        setBackground(new java.awt.Color(0x0E7C86));
        setForeground(java.awt.Color.WHITE);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 22, 10, 22));
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        java.awt.Color c = getBackground();
        g2.setColor(getModel().isRollover() || getModel().isPressed() ? c.darker() : c);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio * 2, radio * 2);
        g2.dispose();
        super.paintComponent(g);
    }
}
