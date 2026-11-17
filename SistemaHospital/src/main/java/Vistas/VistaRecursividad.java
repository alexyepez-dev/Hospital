package Vistas; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Vista del módulo RECURSIVIDAD Opciones: Factorial, Fibonacci
 *
 * @author CRACKS
 */
public class VistaRecursividad extends VistaModulo {

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
                if (n < 0 || n > 20) {
                    resultado.setText("⚠ Ingresa un número entre 0 y 20");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Calculando factorial(").append(n).append(")\n\n");
                sb.append(trazaFactorial(n, 0));
                sb.append("\n\nResultado: ").append(n).append("! = ").append(factorial(n));
                resultado.setText(sb.toString());
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });

        return p;
    }

    private long factorial(int n) {
        return (n <= 1) ? 1 : n * factorial(n - 1);
    }

    private String trazaFactorial(int n, int nivel) {
        String prefijo = "  ".repeat(nivel);
        if (n <= 1) {
            return prefijo + "f(" + n + ") = 1 (caso base)\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(prefijo).append("f(").append(n).append(") = ")
                .append(n).append(" × f(").append(n - 1).append(")\n");
        sb.append(trazaFactorial(n - 1, nivel + 1));
        sb.append(prefijo).append("f(").append(n).append(") = ").append(factorial(n)).append("\n");
        return sb.toString();
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
                if (n < 0 || n > 30) {
                    resultado.setText("⚠ Ingresa un número entre 0 y 30");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Serie de Fibonacci hasta n=").append(n).append(":\n\n");
                for (int i = 0; i <= n; i++) {
                    sb.append("f(").append(i).append(") = ").append(fibonacci(i)).append("\n");
                }
                resultado.setText(sb.toString());
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });

        return p;
    }

    private long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
