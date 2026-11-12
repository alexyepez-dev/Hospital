package Componentes;

/**
 * Botón con esquinas redondeadas y degradado suave. Usable desde el editor de
 * formularios de NetBeans: el color de relleno es la propiedad "background";
 * se oscurece al pasar el mouse y un poco más al presionarlo.
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
        java.awt.Color base = getBackground();
        if (!isEnabled()) {
            base = mezclar(base, java.awt.Color.WHITE, 0.45f);
        } else if (getModel().isPressed()) {
            base = mezclar(base, java.awt.Color.BLACK, 0.18f);
        } else if (getModel().isRollover()) {
            base = mezclar(base, java.awt.Color.BLACK, 0.08f);
        }
        java.awt.Color arriba = mezclar(base, java.awt.Color.WHITE, 0.14f);
        g2.setPaint(new java.awt.GradientPaint(0, 0, arriba, 0, getHeight(), base));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio * 2, radio * 2);
        // Brillo sutil en el borde superior
        g2.setColor(new java.awt.Color(255, 255, 255, 40));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio * 2, radio * 2);
        g2.dispose();
        super.paintComponent(g);
    }

    private static java.awt.Color mezclar(java.awt.Color a, java.awt.Color b, float t) {
        return new java.awt.Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
