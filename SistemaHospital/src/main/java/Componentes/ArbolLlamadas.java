package Componentes;

import Model.DemandaSangre.Nodo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Dibuja el árbol de llamadas recursivas de DemandaSangre. Las hojas se
 * reparten de izquierda a derecha y cada padre queda centrado sobre sus hijos.
 * Al pasar el mouse sobre un nodo se resalta y muestra el detalle de la llamada.
 */
public class ArbolLlamadas extends javax.swing.JPanel implements Scrollable {

    private static final Color AZUL = new Color(0x4A90E2);
    private static final Color AZUL_OSCURO = new Color(0x2F6FB8);
    private static final Color AZUL_SUAVE = new Color(0xEAF3FC);
    private static final Color LINEA = new Color(0xB9D3F0);
    private static final Color TEXTO_SEC = new Color(0x6B7C93);

    private static final int ANCHO_NODO = 62;
    private static final int ALTO_NODO = 38;
    private static final int SEP_X = 72;
    private static final int SEP_Y = 74;
    private static final int MARGEN = 28;

    private static final Font FUENTE_SEMANA = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FUENTE_VALOR = new Font("Segoe UI", Font.BOLD, 13);

    private Nodo raiz;
    private final Map<Nodo, double[]> posiciones = new IdentityHashMap<>();
    private int hojas;
    private int niveles;
    private int siguienteHoja;
    private Nodo resaltado;

    public ArbolLlamadas() {
        setBackground(Color.WHITE);
        setToolTipText("");
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Nodo n = nodoEn(e.getX(), e.getY());
                if (n != resaltado) {
                    resaltado = n;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                resaltado = null;
                repaint();
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    public void setArbol(Nodo raiz) {
        this.raiz = raiz;
        posiciones.clear();
        resaltado = null;
        siguienteHoja = 0;
        niveles = 0;
        if (raiz != null) {
            ubicar(raiz, 0);
        }
        hojas = siguienteHoja;
        revalidate();
        repaint();
        javax.swing.SwingUtilities.invokeLater(this::centrarRaiz);
    }

    /** Desplaza la vista para que la llamada inicial quede centrada arriba. */
    private void centrarRaiz() {
        if (raiz == null || !posiciones.containsKey(raiz)) {
            return;
        }
        Rectangle r = rectangulo(raiz);
        Rectangle visible = getVisibleRect();
        int x = Math.max(0, r.x + r.width / 2 - visible.width / 2);
        scrollRectToVisible(new Rectangle(x, 0, visible.width, visible.height));
    }

    /** Asigna (columna, nivel) a cada nodo y devuelve la columna del nodo. */
    private double ubicar(Nodo n, int nivel) {
        niveles = Math.max(niveles, nivel + 1);
        double x;
        if (n.esCasoBase()) {
            x = siguienteHoja++;
        } else {
            double primero = Double.NaN;
            double ultimo = 0;
            for (Nodo h : n.getHijos()) {
                ultimo = ubicar(h, nivel + 1);
                if (Double.isNaN(primero)) {
                    primero = ultimo;
                }
            }
            x = (primero + ultimo) / 2.0;
        }
        posiciones.put(n, new double[]{x, nivel});
        return x;
    }

    private int anchoContenido() {
        return Math.max(1, hojas) * SEP_X + 2 * MARGEN - (SEP_X - ANCHO_NODO);
    }

    private int altoContenido() {
        return Math.max(1, niveles) * SEP_Y + 2 * MARGEN - (SEP_Y - ALTO_NODO);
    }

    private int desplazamientoX() {
        return Math.max(0, (getWidth() - anchoContenido()) / 2);
    }

    private Rectangle rectangulo(Nodo n) {
        double[] p = posiciones.get(n);
        int x = desplazamientoX() + MARGEN + (int) Math.round(p[0] * SEP_X);
        int y = MARGEN + (int) p[1] * SEP_Y;
        return new Rectangle(x, y, ANCHO_NODO, ALTO_NODO);
    }

    private Nodo nodoEn(int x, int y) {
        for (Nodo n : posiciones.keySet()) {
            if (rectangulo(n).contains(x, y)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Nodo n = nodoEn(e.getX(), e.getY());
        if (n == null) {
            return null;
        }
        String cuerpo;
        if (n.esCasoBase()) {
            cuerpo = "Caso base: D(" + n.getSemana() + ") = " + n.getValor() + " unidades";
        } else {
            Nodo a = n.getHijos().get(0);
            Nodo b = n.getHijos().get(1);
            cuerpo = "D(" + n.getSemana() + ") = D(" + a.getSemana() + ") + D(" + b.getSemana() + ") = "
                    + a.getValor() + " + " + b.getValor() + " = " + n.getValor() + " unidades";
        }
        return "<html><b>Llamada #" + n.getOrden() + "</b><br>" + cuerpo + "</html>";
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(anchoContenido(), altoContenido());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raiz == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Aristas primero, para que queden debajo de los nodos
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(LINEA);
        for (Nodo n : posiciones.keySet()) {
            Rectangle p = rectangulo(n);
            for (Nodo h : n.getHijos()) {
                Rectangle c = rectangulo(h);
                int x1 = p.x + p.width / 2;
                int y1 = p.y + p.height;
                int x2 = c.x + c.width / 2;
                int y2 = c.y;
                int ym = (y1 + y2) / 2;
                java.awt.geom.Path2D curva = new java.awt.geom.Path2D.Double();
                curva.moveTo(x1, y1);
                curva.curveTo(x1, ym, x2, ym, x2, y2);
                g2.draw(curva);
            }
        }

        for (Nodo n : posiciones.keySet()) {
            pintarNodo(g2, n, rectangulo(n));
        }
        g2.dispose();
    }

    private void pintarNodo(Graphics2D g2, Nodo n, Rectangle r) {
        boolean base = n.esCasoBase();
        boolean esRaiz = n == raiz;
        boolean hover = n == resaltado;
        int arco = 14;

        // Sombra ligera
        g2.setColor(new Color(47, 111, 184, hover ? 50 : 22));
        g2.fillRoundRect(r.x + 1, r.y + 3, r.width, r.height, arco, arco);

        Color fondo;
        Color texto;
        Color subtexto;
        if (esRaiz) {
            fondo = AZUL_OSCURO;
            texto = Color.WHITE;
            subtexto = new Color(0xDCEBFB);
        } else if (base) {
            fondo = hover ? AZUL_SUAVE.darker() : AZUL_SUAVE;
            texto = AZUL_OSCURO;
            subtexto = TEXTO_SEC;
        } else {
            fondo = AZUL;
            texto = Color.WHITE;
            subtexto = new Color(0xE3EEFB);
        }
        if (hover && !base) {
            fondo = fondo.darker();
        }
        g2.setColor(fondo);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, arco, arco);
        if (base) {
            g2.setColor(LINEA);
            g2.drawRoundRect(r.x, r.y, r.width - 1, r.height - 1, arco, arco);
        }

        String sup = "D(" + n.getSemana() + ")";
        String inf = n.getValor() + " u";
        g2.setFont(FUENTE_SEMANA);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(subtexto);
        g2.drawString(sup, r.x + (r.width - fm.stringWidth(sup)) / 2, r.y + 15);
        g2.setFont(FUENTE_VALOR);
        fm = g2.getFontMetrics();
        g2.setColor(texto);
        g2.drawString(inf, r.x + (r.width - fm.stringWidth(inf)) / 2, r.y + 31);
    }

    // --- Scrollable: estira el panel al ancho/alto visible cuando el árbol es pequeño ---

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
        return orientation == SwingConstants.HORIZONTAL ? visible.width - 40 : visible.height - 40;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getParent() != null && getParent().getWidth() > anchoContenido();
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getParent() != null && getParent().getHeight() > altoContenido();
    }
}
