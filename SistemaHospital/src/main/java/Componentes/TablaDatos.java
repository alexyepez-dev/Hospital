package Componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Tabla de solo lectura con el mismo estilo que TablaPacientes: filas
 * alternadas, cabecera en mayúsculas y columnas opcionales con "píldora" de color
 * (por ejemplo, el estado del stock).
 */
public class TablaDatos extends JTable {

    private static final Color FILA_PAR = Color.WHITE;
    private static final Color FILA_IMPAR = new Color(0xFAFCFF);
    private static final Color LINEA = new Color(0xEEF3F9);

    private final DefaultTableModel modelo;
    private final Set<Integer> centradas = new HashSet<>();
    private final Map<Integer, Function<Object, Color>> pildoras = new HashMap<>();

    public TablaDatos(String... columnas) {
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        setModel(modelo);
        setRowHeight(34);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFillsViewportHeight(true);
        setBackground(Color.WHITE);
        setFont(Estilo.fuente(Font.PLAIN, 13));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFocusable(false);

        JTableHeader cabecera = getTableHeader();
        cabecera.setReorderingAllowed(false);
        cabecera.setDefaultRenderer(new Cabecera());
        cabecera.setPreferredSize(new Dimension(10, 38));
        cabecera.setBackground(Color.WHITE);

        setDefaultRenderer(Object.class, new Celda());
    }

    /** Reemplaza todas las filas. */
    public void setFilas(List<Object[]> filas) {
        modelo.setRowCount(0);
        for (Object[] f : filas) {
            modelo.addRow(f);
        }
    }

    public void centrar(int... columnas) {
        for (int c : columnas) {
            centradas.add(c);
        }
    }

    public void setAnchos(int... anchos) {
        for (int i = 0; i < anchos.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    /** Dibuja la columna como una píldora del color que devuelve la función para cada valor. */
    public void setPildora(int columna, Function<Object, Color> color) {
        pildoras.put(columna, color);
    }

    private Color fondo(int fila) {
        if (isRowSelected(fila)) {
            return Estilo.AZUL_SUAVE;
        }
        return fila % 2 == 0 ? FILA_PAR : FILA_IMPAR;
    }

    private javax.swing.border.Border borde(int fila, int columna) {
        int izquierda = columna == 0 ? 14 : 8;
        if (columna == 0 && isRowSelected(fila)) {
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 1, 0, Estilo.AZUL),
                    BorderFactory.createEmptyBorder(0, izquierda - 4, 0, 8));
        }
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LINEA),
                BorderFactory.createEmptyBorder(0, izquierda, 0, 8));
    }

    private final class Celda extends DefaultTableCellRenderer {

        private final Pildora pildora = new Pildora();

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco, int fila, int col) {
            if (pildoras.containsKey(col)) {
                pildora.texto = String.valueOf(valor);
                pildora.color = pildoras.get(col).apply(valor);
                pildora.setBackground(fondo(fila));
                pildora.setBorder(borde(fila, col));
                return pildora;
            }
            super.getTableCellRendererComponent(t, valor, false, false, fila, col);
            setBackground(fondo(fila));
            setForeground(Estilo.TEXTO);
            setFont(Estilo.fuente(isRowSelected(fila) ? Font.BOLD : Font.PLAIN, 13));
            setHorizontalAlignment(centradas.contains(col) ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(borde(fila, col));
            setToolTipText(valor == null ? null : valor.toString());
            return this;
        }
    }

    /** Píldora de color + texto, como la del triaje en TablaPacientes. */
    private static final class Pildora extends JPanel {

        String texto = "";
        Color color = Estilo.AZUL;

        Pildora() {
            super(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setFont(Estilo.fuente(Font.BOLD, 11));
            int w = g2.getFontMetrics().stringWidth(texto) + 26;
            int h = 22;
            int x = 8;
            int y = (getHeight() - h) / 2;
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
            g2.fillRoundRect(x, y, w, h, h, h);
            g2.setColor(color);
            g2.fillOval(x + 8, y + 7, 8, 8);
            g2.setColor(color.darker());
            g2.drawString(texto, x + 20, y + 15);
            g2.dispose();
        }
    }

    private final class Cabecera extends JLabel implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco, int fila, int col) {
            setText(valor.toString().toUpperCase());
            setFont(Estilo.fuente(Font.BOLD, 11));
            setForeground(Estilo.TEXTO_SEC);
            setOpaque(true);
            setBackground(Color.WHITE);
            setHorizontalAlignment(centradas.contains(col) ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE),
                    BorderFactory.createEmptyBorder(0, col == 0 ? 14 : 8, 0, 8)));
            return this;
        }
    }
}
