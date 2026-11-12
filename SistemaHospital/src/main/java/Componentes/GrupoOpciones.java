package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Lista vertical de opciones excluyentes (como radio buttons), cada una con
 * título y descripción:
 *
 *   (●) Prioridad (triaje)
 *       Más urgente primero
 *   ( ) Edad
 *       De menor a mayor
 */
public class GrupoOpciones extends javax.swing.JComponent {

    private static final int ALTO_FILA = 42;

    private final List<String[]> opciones = new ArrayList<>();
    private final List<IntConsumer> oyentes = new ArrayList<>();
    private int seleccion;
    private int encima = -1;

    public GrupoOpciones() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setAlignmentX(LEFT_ALIGNMENT);
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = e.getY() / ALTO_FILA;
                if (i >= 0 && i < opciones.size() && isEnabled()) {
                    setSeleccion(i);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int i = e.getY() / ALTO_FILA;
                if (i != encima) {
                    encima = i;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = -1;
                repaint();
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    public void agregar(String titulo, String descripcion) {
        opciones.add(new String[]{titulo, descripcion});
        revalidate();
    }

    public int getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(int i) {
        seleccion = i;
        repaint();
        for (IntConsumer o : oyentes) {
            o.accept(i);
        }
    }

    public void alCambiar(IntConsumer oyente) {
        oyentes.add(oyente);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(220, opciones.size() * ALTO_FILA);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        Font fTitulo = Estilo.fuente(Font.BOLD, 13);
        Font fDesc = Estilo.fuente(Font.PLAIN, 11);

        for (int i = 0; i < opciones.size(); i++) {
            int y = i * ALTO_FILA;
            boolean activa = i == seleccion;
            if (activa) {
                g2.setColor(Estilo.AZUL_SUAVE);
                g2.fillRoundRect(0, y + 1, getWidth(), ALTO_FILA - 2, 12, 12);
            } else if (i == encima && isEnabled()) {
                g2.setColor(new Color(0xF5F9FE));
                g2.fillRoundRect(0, y + 1, getWidth(), ALTO_FILA - 2, 12, 12);
            }
            // Círculo tipo radio
            int d = 16;
            int cx = 10;
            int cy = y + (ALTO_FILA - d) / 2;
            g2.setStroke(new BasicStroke(activa ? 2f : 1.4f));
            g2.setColor(Color.WHITE);
            g2.fillOval(cx, cy, d, d);
            g2.setColor(activa ? Estilo.AZUL : new Color(0xB9C9DC));
            g2.drawOval(cx, cy, d, d);
            if (activa) {
                g2.fillOval(cx + 4, cy + 4, d - 8, d - 8);
            }
            // Textos
            g2.setFont(fTitulo);
            g2.setColor(activa ? Estilo.AZUL_OSCURO : Estilo.TEXTO);
            g2.drawString(opciones.get(i)[0], 36, y + 18);
            g2.setFont(fDesc);
            g2.setColor(Estilo.TEXTO_SEC);
            g2.drawString(opciones.get(i)[1], 36, y + 33);
        }
        g2.dispose();
    }
}
