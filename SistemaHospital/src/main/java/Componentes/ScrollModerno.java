package Componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** Barra de desplazamiento delgada, sin flechas y con el "pulgar" redondeado. */
public class ScrollModerno extends BasicScrollBarUI {

    private static final Color PULGAR = new Color(0xC6DAF2);
    private static final Color PULGAR_ACTIVO = new Color(0x9EC2EC);

    /** Crea un JScrollPane sin borde, fondo blanco y barras modernas. */
    public static JScrollPane envolver(JComponent contenido) {
        JScrollPane s = new JScrollPane(contenido);
        s.setBorder(BorderFactory.createEmptyBorder());
        s.setViewportBorder(null);
        s.getViewport().setBackground(Color.WHITE);
        s.setBackground(Color.WHITE);
        for (JScrollBar barra : new JScrollBar[]{s.getVerticalScrollBar(), s.getHorizontalScrollBar()}) {
            barra.setUI(new ScrollModerno());
            barra.setUnitIncrement(24);
            barra.setOpaque(false);
        }
        JPanel esquina = new JPanel();
        esquina.setBackground(Color.WHITE);
        s.setCorner(JScrollPane.LOWER_RIGHT_CORNER, esquina);
        return s;
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        scrollBarWidth = 10;
        minimumThumbSize = new Dimension(30, 30);
        trackColor = Color.WHITE;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return botonVacio();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return botonVacio();
    }

    private static JButton botonVacio() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(Color.WHITE);
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isDragging || isThumbRollover() ? PULGAR_ACTIVO : PULGAR);
        int m = 2;
        int arco = Math.min(r.width, r.height) - 2 * m;
        g2.fillRoundRect(r.x + m, r.y + m, r.width - 2 * m, r.height - 2 * m, arco, arco);
        g2.dispose();
    }
}
