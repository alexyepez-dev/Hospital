package Componentes;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Campo de texto con el mismo aspecto que CampoNumero (borde redondeado y anillo de foco). */
public class CampoTexto extends JPanel {

    private final JTextField texto = new JTextField();
    private final String sugerencia;

    public CampoTexto(String sugerencia) {
        super(new BorderLayout());
        this.sugerencia = sugerencia;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        setPreferredSize(new Dimension(200, 44));

        texto.setFont(Estilo.fuente(Font.PLAIN, 14));
        texto.setForeground(Estilo.TEXTO);
        texto.setBorder(BorderFactory.createEmptyBorder());
        texto.setOpaque(false);
        texto.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
        texto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                repaint();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                repaint();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                repaint();
            }
        });
        add(texto, BorderLayout.CENTER);
    }

    public String getTexto() {
        return texto.getText().trim();
    }

    public void setTexto(String t) {
        texto.setText(t);
    }

    /** Se ejecuta al presionar Enter dentro del campo. */
    public void alPresionarEnter(ActionListener l) {
        texto.addActionListener(l);
    }

    public void enfocar() {
        texto.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        boolean foco = texto.isFocusOwner();
        int w = getWidth() - 1;
        int h = getHeight() - 1;
        if (foco) {
            g2.setColor(new Color(74, 144, 226, 45));
            g2.fillRoundRect(0, 0, w + 1, h + 1, 16, 16);
        }
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(2, 2, w - 4, h - 4, 12, 12);
        g2.setColor(foco ? Estilo.AZUL : Estilo.BORDE);
        g2.setStroke(new BasicStroke(foco ? 1.5f : 1f));
        g2.drawRoundRect(2, 2, w - 4, h - 4, 12, 12);
        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (texto.getText().isEmpty() && !texto.isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setFont(texto.getFont());
            g2.setColor(new Color(0xA0AEC0));
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(sugerencia, getInsets().left + 2, y);
            g2.dispose();
        }
    }
}
