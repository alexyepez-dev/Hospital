package Componentes;

import Model.Paciente;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Tabla de pacientes con estilo moderno y resaltado de los pasos del
 * ordenamiento: filas comparadas, intercambiadas, pivote y ya ordenadas.
 */
public class TablaPacientes extends JTable {

    public enum Marca { NINGUNA, COMPARAR, INTERCAMBIAR, PIVOTE }

    public static final Color FONDO_COMPARAR = new Color(0xDCEBFC);
    public static final Color FONDO_INTERCAMBIAR = new Color(0xFFEFCC);
    public static final Color FONDO_PIVOTE = new Color(0xEFE7FD);
    public static final Color FONDO_ORDENADO = new Color(0xEAF7EE);

    private static final Color ACENTO_COMPARAR = Estilo.AZUL;
    private static final Color ACENTO_INTERCAMBIAR = new Color(0xE59E0B);
    private static final Color ACENTO_PIVOTE = new Color(0x8B5CF6);
    private static final Color ACENTO_ORDENADO = new Color(0x22A05B);

    private static final Color[] COLOR_TRIAJE = {
        new Color(0xDC2626), new Color(0xF97316), new Color(0xEAB308), new Color(0x16A34A), new Color(0x2563EB)
    };
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] COLUMNAS = {"#", "Código", "Paciente", "Edad", "Triaje", "Llegada"};

    private final Modelo modelo = new Modelo();
    private final BitSet ordenados = new BitSet();
    private Marca marca = Marca.NINGUNA;
    private int filaA = -1;
    private int filaB = -1;
    private int pivote = -1;

    public TablaPacientes() {
        setModel(modelo);
        setRowHeight(36);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFillsViewportHeight(true);
        setBackground(Color.WHITE);
        setRowSelectionAllowed(false);
        setFocusable(false);
        setFont(Estilo.fuente(Font.PLAIN, 13));
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);

        int[] anchos = {44, 80, 220, 70, 170, 80};
        for (int i = 0; i < anchos.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        getColumnModel().getColumn(0).setMaxWidth(56);

        Celda celda = new Celda();
        for (int i = 0; i < COLUMNAS.length; i++) {
            getColumnModel().getColumn(i).setCellRenderer(i == 4 ? new CeldaTriaje() : celda);
        }
        JTableHeader cabecera = getTableHeader();
        cabecera.setDefaultRenderer(new Cabecera());
        cabecera.setPreferredSize(new Dimension(10, 38));
        cabecera.setBackground(Color.WHITE);
    }

    // ------------------------------------------------------------------ API

    public void setPacientes(List<Paciente> pacientes) {
        modelo.filas = new ArrayList<>(pacientes);
        limpiarMarcas();
        modelo.fireTableDataChanged();
    }

    public List<Paciente> getPacientes() {
        return Collections.unmodifiableList(modelo.filas);
    }

    public void limpiarMarcas() {
        marca = Marca.NINGUNA;
        filaA = filaB = pivote = -1;
        ordenados.clear();
        repaint();
    }

    /** Resalta las filas a y b (b puede ser -1). */
    public void marcar(Marca m, int a, int b) {
        marca = m;
        filaA = a;
        filaB = b;
        if (a >= 0) {
            scrollRectToVisible(getCellRect(a, 0, true));
        }
        repaint();
    }

    public void intercambiar(int a, int b) {
        Collections.swap(modelo.filas, a, b);
        modelo.fireTableRowsUpdated(Math.min(a, b), Math.max(a, b));
        marcar(Marca.INTERCAMBIAR, a, b);
    }

    public void setPivote(int i) {
        pivote = i;
        marcar(Marca.PIVOTE, i, -1);
    }

    public void marcarOrdenado(int i) {
        ordenados.set(i);
        if (i == pivote) {
            pivote = -1;
        }
        repaint();
    }

    public void marcarTodoOrdenado() {
        ordenados.set(0, modelo.filas.size());
        marca = Marca.NINGUNA;
        filaA = filaB = pivote = -1;
        repaint();
    }

    // ------------------------------------------------------------- dibujo

    /** Color de fondo y de acento (barra izquierda) de una fila según su estado. */
    private Color[] colores(int fila) {
        boolean marcada = fila == filaA || fila == filaB;
        if (marcada && marca == Marca.INTERCAMBIAR) {
            return new Color[]{FONDO_INTERCAMBIAR, ACENTO_INTERCAMBIAR};
        }
        if (marcada && marca == Marca.COMPARAR) {
            return new Color[]{FONDO_COMPARAR, ACENTO_COMPARAR};
        }
        if (fila == pivote) {
            return new Color[]{FONDO_PIVOTE, ACENTO_PIVOTE};
        }
        if (ordenados.get(fila)) {
            return new Color[]{FONDO_ORDENADO, ACENTO_ORDENADO};
        }
        return new Color[]{fila % 2 == 0 ? Color.WHITE : new Color(0xFAFCFF), null};
    }

    private javax.swing.border.Border borde(int fila, int columna) {
        Color acento = colores(fila)[1];
        int izquierda = columna == 0 ? 14 : 8;
        if (columna == 0 && acento != null) {
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 1, 0, acento),
                    BorderFactory.createEmptyBorder(0, izquierda - 4, 0, 8));
        }
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEEF3F9)),
                BorderFactory.createEmptyBorder(0, izquierda, 0, 8));
    }

    private final class Celda extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco, int fila, int col) {
            super.getTableCellRendererComponent(t, valor, false, false, fila, col);
            Color[] c = colores(fila);
            setBackground(c[0]);
            boolean resaltada = fila == filaA || fila == filaB || fila == pivote;
            setForeground(col == 0 ? Estilo.TEXTO_SEC : Estilo.TEXTO);
            setFont(Estilo.fuente(col == 2 && resaltada ? Font.BOLD : Font.PLAIN, 13));
            setHorizontalAlignment(col == 0 || col == 3 || col == 5 ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(borde(fila, col));
            return this;
        }
    }

    /** Celda de triaje: píldora de color + "3 · Urgente". */
    private final class CeldaTriaje extends JPanel implements TableCellRenderer {

        private int triaje = 1;

        CeldaTriaje() {
            super(new BorderLayout());
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco, int fila, int col) {
            triaje = (Integer) valor;
            setBackground(colores(fila)[0]);
            setBorder(borde(fila, col));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            Color c = COLOR_TRIAJE[triaje - 1];
            String texto = triaje + " · " + Paciente.nombreTriaje(triaje);
            g2.setFont(Estilo.fuente(Font.BOLD, 11));
            int w = g2.getFontMetrics().stringWidth(texto) + 26;
            int h = 22;
            int x = 8;
            int y = (getHeight() - h) / 2;
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 28));
            g2.fillRoundRect(x, y, w, h, h, h);
            g2.setColor(c);
            g2.fillOval(x + 8, y + 7, 8, 8);
            g2.setColor(c.darker());
            g2.drawString(texto, x + 20, y + 15);
            g2.dispose();
        }
    }

    private static final class Cabecera extends JLabel implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco, int fila, int col) {
            setText(valor.toString().toUpperCase());
            setFont(Estilo.fuente(Font.BOLD, 11));
            setForeground(Estilo.TEXTO_SEC);
            setOpaque(true);
            setBackground(Color.WHITE);
            setHorizontalAlignment(col == 0 || col == 3 || col == 5 ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE),
                    BorderFactory.createEmptyBorder(0, col == 0 ? 14 : 8, 0, 8)));
            return this;
        }
    }

    private static final class Modelo extends AbstractTableModel {

        List<Paciente> filas = new ArrayList<>();

        @Override
        public int getRowCount() {
            return filas.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNAS.length;
        }

        @Override
        public String getColumnName(int c) {
            return COLUMNAS[c];
        }

        @Override
        public Object getValueAt(int f, int c) {
            Paciente p = filas.get(f);
            return switch (c) {
                case 0 -> f + 1;
                case 1 -> p.getCodigo();
                case 2 -> p.getNombre();
                case 3 -> p.getEdad();
                case 4 -> p.getTriaje();
                default -> p.getLlegada().format(HORA);
            };
        }
    }

    /** Leyenda de colores de la tabla, para la cabecera de la pestaña. */
    public static JComponent leyenda() {
        return Estilo.filaLeyendas(
                ACENTO_COMPARAR, "Comparando",
                ACENTO_INTERCAMBIAR, "Intercambio",
                ACENTO_PIVOTE, "Pivote / mínimo",
                ACENTO_ORDENADO, "En su lugar");
    }
}
