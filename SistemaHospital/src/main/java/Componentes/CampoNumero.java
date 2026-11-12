package Componentes;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Campo numérico moderno:  [ − ]   valor   [ + ]
 * Se puede escribir el número, usar los botones, las flechas ↑/↓ del teclado o
 * la rueda del mouse. El valor siempre queda dentro del rango del modelo.
 */
public class CampoNumero extends JPanel {

    private final SpinnerNumberModel modelo;
    private final JTextField texto = new JTextField();
    private boolean escribiendo;

    public CampoNumero(int valor, int minimo, int maximo, int paso) {
        super(new BorderLayout(4, 0));
        modelo = new SpinnerNumberModel(valor, minimo, maximo, paso);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        setPreferredSize(new Dimension(200, 44));

        texto.setText(String.valueOf(valor));
        texto.setFont(Estilo.fuente(Font.BOLD, 16));
        texto.setForeground(Estilo.TEXTO);
        texto.setHorizontalAlignment(JTextField.CENTER);
        texto.setBorder(BorderFactory.createEmptyBorder());
        texto.setOpaque(false);
        ((AbstractDocument) texto.getDocument()).setDocumentFilter(new SoloDigitos());

        add(new BotonPaso(false), BorderLayout.WEST);
        add(texto, BorderLayout.CENTER);
        add(new BotonPaso(true), BorderLayout.EAST);

        // Escribir actualiza el valor en vivo si el número es válido
        texto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                desdeTexto();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                desdeTexto();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                desdeTexto();
            }
        });
        modelo.addChangeListener(e -> {
            if (!escribiendo) {
                texto.setText(String.valueOf(getValor()));
            }
        });
        texto.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
                texto.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                texto.setText(String.valueOf(getValor())); // normaliza (vacío o fuera de rango)
                repaint();
            }
        });
        texto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    pasar(true);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    pasar(false);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    texto.setText(String.valueOf(getValor()));
                }
            }
        });
        addMouseWheelListener(e -> pasar(e.getWheelRotation() < 0));
        texto.addMouseWheelListener(e -> pasar(e.getWheelRotation() < 0));
    }

    public int getValor() {
        return ((Number) modelo.getValue()).intValue();
    }

    public void setValor(int valor) {
        modelo.setValue(valor);
    }

    public void addChangeListener(ChangeListener l) {
        modelo.addChangeListener(l);
    }

    private void pasar(boolean subir) {
        Object siguiente = subir ? modelo.getNextValue() : modelo.getPreviousValue();
        if (siguiente != null) {
            modelo.setValue(siguiente);
        }
    }

    private void desdeTexto() {
        String t = texto.getText().trim();
        if (t.isEmpty() || t.length() > 9) {
            return;
        }
        int v = Integer.parseInt(t);
        int min = ((Number) modelo.getMinimum()).intValue();
        int max = ((Number) modelo.getMaximum()).intValue();
        if (v >= min && v <= max && v != getValor()) {
            escribiendo = true;
            try {
                modelo.setValue(v);
            } finally {
                escribiendo = false;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean foco = texto.isFocusOwner();
        int w = getWidth() - 1;
        int h = getHeight() - 1;
        if (foco) {
            g2.setColor(new Color(74, 144, 226, 45)); // anillo de foco
            g2.fillRoundRect(0, 0, w + 1, h + 1, 16, 16);
        }
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(2, 2, w - 4, h - 4, 12, 12);
        g2.setColor(foco ? Estilo.AZUL : Estilo.BORDE);
        g2.setStroke(new BasicStroke(foco ? 1.5f : 1f));
        g2.drawRoundRect(2, 2, w - 4, h - 4, 12, 12);
        g2.dispose();
    }

    /** Botón cuadrado redondeado con un − o un + dibujado. */
    private final class BotonPaso extends JButton {

        private final boolean mas;

        BotonPaso(boolean mas) {
            this.mas = mas;
            setPreferredSize(new Dimension(32, 32));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFocusable(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(mas ? "Aumentar" : "Disminuir");
            addActionListener(e -> pasar(mas));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fondo = Estilo.AZUL_SUAVE;
            if (getModel().isPressed()) {
                fondo = new Color(0xC9DEF6);
            } else if (getModel().isRollover()) {
                fondo = new Color(0xDCEAFA);
            }
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
            g2.setColor(Estilo.AZUL_OSCURO);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            g2.drawLine(cx - 6, cy, cx + 6, cy);
            if (mas) {
                g2.drawLine(cx, cy - 6, cx, cy + 6);
            }
            g2.dispose();
        }
    }

    /** Solo permite escribir dígitos. */
    private static final class SoloDigitos extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
            if (s != null && s.chars().allMatch(Character::isDigit)) {
                super.insertString(fb, offset, s, a);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String s, AttributeSet a) throws BadLocationException {
            if (s == null || s.chars().allMatch(Character::isDigit)) {
                super.replace(fb, offset, length, s, a);
            }
        }
    }
}
