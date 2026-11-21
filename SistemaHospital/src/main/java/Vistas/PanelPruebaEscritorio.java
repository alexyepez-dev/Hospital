package Vistas;

import Logica.PruebaEscritorioLogica;
import Models.PruebaEscritorioModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Panel de la opción Prueba de Escritorio del módulo Búsqueda.
 * Permite seleccionar entre Búsqueda Binaria y Búsqueda Secuencial.
 *
 * @author CRACKS
 */
public class PanelPruebaEscritorio extends JPanel {

    private static final Color AZUL_OSCURO = new Color(10, 25, 60);
    private static final Color GRIS_TEXTO = new Color(120, 130, 150);
    private static final Color ACENTO_MODULO = new Color(230, 126, 34);
    private static final Color ACENTO_BORDER = new Color(180, 200, 220);

    private final PruebaEscritorioLogica logica = new PruebaEscritorioLogica();

    public PanelPruebaEscritorio() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(crearTitulo("Prueba de Escritorio"));
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(crearSubtitulo("Selecciona el tipo de búsqueda y simula paso a paso sus iteraciones"));
        add(Box.createRigidArea(new Dimension(0, 22)));

        // Selección de tipo de búsqueda (ninguna seleccionada por defecto)
        JPanel filaTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaTipo.setOpaque(false);
        filaTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTipo = new JLabel("Método de búsqueda:");
        lblTipo.setFont(new Font("Montserrat", Font.BOLD, 13));
        lblTipo.setForeground(AZUL_OSCURO);

        JRadioButton rbBinaria = crearRadio("Búsqueda Binaria");
        JRadioButton rbSecuencial = crearRadio("Búsqueda Secuencial");
        ButtonGroup grupoBusqueda = new ButtonGroup();
        grupoBusqueda.add(rbBinaria);
        grupoBusqueda.add(rbSecuencial);

        filaTipo.add(lblTipo);
        filaTipo.add(Box.createHorizontalStrut(15));
        filaTipo.add(rbBinaria);
        filaTipo.add(Box.createHorizontalStrut(15));
        filaTipo.add(rbSecuencial);
        add(filaTipo);

        add(Box.createRigidArea(new Dimension(0, 15)));

        // Entrada del número de historia y botón de ejecución
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txt = crearCampoTexto(10);
        JButton btn = crearBotonPrimario("Ejecutar prueba");
        JLabel lblHistoria = new JLabel("N.º Historia (ej. 104) =");
        lblHistoria.setFont(new Font("Montserrat", Font.PLAIN, 13));
        lblHistoria.setForeground(AZUL_OSCURO);

        fila.add(lblHistoria);
        fila.add(Box.createHorizontalStrut(10));
        fila.add(txt);
        fila.add(Box.createHorizontalStrut(10));
        fila.add(btn);
        add(fila);

        JTextArea resultado = new JTextArea(16, 65);
        resultado.setEditable(false);
        resultado.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultado.setBackground(new Color(245, 248, 252));
        resultado.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(Box.createRigidArea(new Dimension(0, 20)));
        JScrollPane scroll = new JScrollPane(resultado);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scroll);

        btn.addActionListener(e -> {
            if (!rbBinaria.isSelected() && !rbSecuencial.isSelected()) {
                resultado.setText("⚠ Debe seleccionar una de las dos opciones: Búsqueda Binaria o Búsqueda Secuencial");
                return;
            }

            try {
                int n = Integer.parseInt(txt.getText().trim());
                PruebaEscritorioModel r = rbBinaria.isSelected()
                        ? logica.ejecutarPruebaBinaria(n)
                        : logica.ejecutarPruebaSecuencial(n);

                if (r == null) {
                    resultado.setText("⚠ Ingresa un número de historia mayor a 0");
                } else {
                    resultado.setText(r.getTraza());
                    resultado.setCaretPosition(0);
                }
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });
    }

    private JRadioButton crearRadio(String texto) {
        JRadioButton rb = new JRadioButton(texto);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setFont(new Font("Montserrat", Font.PLAIN, 13));
        rb.setForeground(AZUL_OSCURO);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return rb;
    }

    private JLabel crearTitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Montserrat", Font.BOLD, 26));
        lbl.setForeground(AZUL_OSCURO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel crearSubtitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Montserrat", Font.PLAIN, 13));
        lbl.setForeground(GRIS_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField crearCampoTexto(int columnas) {
        JTextField txt = new JTextField(columnas);
        txt.setFont(new Font("Montserrat", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new VistaModulo.RoundedBorder(ACENTO_BORDER, 10),
                new EmptyBorder(8, 12, 8, 12)));
        return txt;
    }

    private JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hover ? aclarar(ACENTO_MODULO, 30) : ACENTO_MODULO;
                Color c2 = hover ? ACENTO_MODULO : oscurecer(ACENTO_MODULO, 30);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Montserrat", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 42));
        return btn;
    }

    private static Color aclarar(Color c, int delta) {
        return new Color(
                Math.min(255, c.getRed() + delta),
                Math.min(255, c.getGreen() + delta),
                Math.min(255, c.getBlue() + delta));
    }

    private static Color oscurecer(Color c, int delta) {
        return new Color(
                Math.max(0, c.getRed() - delta),
                Math.max(0, c.getGreen() - delta),
                Math.max(0, c.getBlue() - delta));
    }
}
