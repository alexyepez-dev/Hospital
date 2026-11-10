package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;

/**
 * Opción del menú lateral: insignia + título + descripción. Se resalta al pasar
 * el mouse y queda marcada cuando está seleccionada. Si la insignia es null se
 * dibuja el icono de apagado (para "Salir").
 */
public class ItemMenu extends javax.swing.JComponent {

    private final String insignia;
    private final String titulo;
    private final String descripcion;
    private boolean seleccionado;
    private boolean encima;
    private Runnable accion = () -> { };

    public ItemMenu(String insignia, String titulo, String descripcion) {
        this.insignia = insignia;
        this.titulo = titulo;
        this.descripcion = descripcion;
        setOpaque(false);
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(descripcion);
        setAlignmentX(LEFT_ALIGNMENT);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    accion.run();
                }
            }
        });
    }

    public void setAccion(Runnable accion) {
        this.accion = accion;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(220, 58);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 58);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        int w = getWidth();
        int h = getHeight();

        if (seleccionado) {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 2, w, h - 4, 14, 14);
        } else if (encima || isFocusOwner()) {
            g2.setColor(new Color(255, 255, 255, 38));
            g2.fillRoundRect(0, 2, w, h - 4, 14, 14);
        }

        // Insignia
        int d = 34;
        int ix = 12;
        int iy = (h - d) / 2;
        g2.setColor(seleccionado ? Estilo.AZUL_SUAVE : new Color(255, 255, 255, 45));
        g2.fillRoundRect(ix, iy, d, d, 10, 10);
        Color tinta = seleccionado ? Estilo.AZUL_OSCURO : Color.WHITE;
        g2.setColor(tinta);
        if (insignia != null) {
            g2.setFont(Estilo.fuente(Font.BOLD, insignia.length() > 2 ? 11 : 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(insignia, ix + (d - fm.stringWidth(insignia)) / 2,
                    iy + (d - fm.getHeight()) / 2 + fm.getAscent());
        } else {
            pintarApagado(g2, ix + d / 2, iy + d / 2);
        }

        // Textos
        int tx = ix + d + 12;
        g2.setFont(Estilo.fuente(Font.BOLD, 14));
        g2.setColor(seleccionado ? Estilo.TEXTO : Color.WHITE);
        g2.drawString(titulo, tx, h / 2 - 2);
        g2.setFont(Estilo.fuente(Font.PLAIN, 11));
        g2.setColor(seleccionado ? Estilo.TEXTO_SEC : new Color(255, 255, 255, 205));
        g2.drawString(descripcion, tx, h / 2 + 14);
        g2.dispose();
    }

    private static void pintarApagado(Graphics2D g2, int cx, int cy) {
        int r = 8;
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Arc2D.Double(cx - r, cy - r + 1, 2 * r, 2 * r, 125, 290, Arc2D.OPEN));
        g2.drawLine(cx, cy - r - 1, cx, cy - 1);
    }
}
