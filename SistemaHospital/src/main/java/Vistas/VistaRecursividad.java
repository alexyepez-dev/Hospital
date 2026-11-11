package Vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import Logica.RecursividadLogica;
import Models.RecursividadModel;

/**
 * Vista del módulo RECURSIVIDAD.
 * @author CRACKS
 */
public class VistaRecursividad extends VistaModulo {

    private final RecursividadLogica logica = new RecursividadLogica();

    public VistaRecursividad(String usuario) {
        super(usuario, "Recursividad", new Color(155, 89, 182));
        initModulo();
    }

    @Override
    protected List<String> getOpcionesMenu() {
        return Arrays.asList("Factorial", "Fibonacci");
    }

    @Override
    protected JPanel crearPanelOpcion(int indice) {
        return indice == 0 ? panelFactorial() : panelFibonacci();
    }

    private JPanel panelFactorial() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo("Factorial recursivo"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo("Calcula n! usando recursividad: f(n) = n * f(n-1)"));
        p.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        JTextField txt = crearCampoTexto(8);
        JButton btn = crearBotonPrimario("Calcular");
        fila.add(new JLabel("n ="));
        fila.add(txt);
        fila.add(btn);
        p.add(fila);

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultado.setBackground(new Color(245, 248, 252));
        resultado.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(new JScrollPane(resultado));

        btn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(txt.getText().trim());
                RecursividadModel r = logica.calcularFactorial(n);
                if (r == null) {
                    resultado.setText("⚠ Ingresa un número entre 0 y 20");
                } else {
                    resultado.setText(r.getTraza());
                }
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });

        return p;
    }

    private JPanel panelFibonacci() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo("Fibonacci recursivo"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo("Serie de Fibonacci: f(n) = f(n-1) + f(n-2)"));
        p.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        JTextField txt = crearCampoTexto(8);
        JButton btn = crearBotonPrimario("Generar");
        fila.add(new JLabel("n ="));
        fila.add(txt);
        fila.add(btn);
        p.add(fila);

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultado.setBackground(new Color(245, 248, 252));
        resultado.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(new JScrollPane(resultado));

        btn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(txt.getText().trim());
                RecursividadModel r = logica.calcularFibonacci(n);
                if (r == null) {
                    resultado.setText("⚠ Ingresa un número entre 0 y 30");
                } else {
                    resultado.setText(r.getTraza());
                }
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });

        return p;
    }
}