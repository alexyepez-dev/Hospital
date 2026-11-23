package Vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import Logica.RecursividadLogica;
import Models.RecursividadModel;

/**
 * Vista del módulo RECURSIVIDAD aplicada al Sistema Hospitalario.
 *  - Factorial: órdenes de atención posibles en la sala de espera.
 *  - Fibonacci: proyección de un cultivo bacteriano en el laboratorio.
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
        return Arrays.asList("Factorial: Sala de espera", "Fibonacci: Cultivo de laboratorio");
    }

    @Override
    protected JPanel crearPanelOpcion(int indice) {
        return indice == 0 ? panelFactorial() : panelFibonacci();
    }

    // ---------------------------------------------------------
    //  FACTORIAL - Órdenes de atención de pacientes
    // ---------------------------------------------------------
    private JPanel panelFactorial() {
        JPanel p = crearBase(
                "Factorial: órdenes de atención de pacientes",
                "f(n) = n * f(n-1)   |   Caso base: f(0) = f(1) = 1",
                "Caso de uso: Emergencias tiene n pacientes en espera. ¿Cuántos órdenes de atención existen?");

        JTextField txt = crearCampoTexto(8);
        JButton btn = crearBotonPrimario("Calcular");
        p.add(crearFila("Pacientes en espera =", txt, btn));

        JTextArea resultado = crearAreaResultado();
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(new JScrollPane(resultado));

        btn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(txt.getText().trim());
                RecursividadModel r = logica.calcularFactorial(n);
                resultado.setText(r == null
                        ? "⚠ Ingresa un número de pacientes entre 0 y 20"
                        : r.getTraza());
                resultado.setCaretPosition(0);
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });

        return p;
    }

    // ---------------------------------------------------------
    //  FIBONACCI - Crecimiento de cultivo bacteriano
    // ---------------------------------------------------------
    private JPanel panelFibonacci() {
        JPanel p = crearBase(
                "Fibonacci: cultivo bacteriano en laboratorio",
                "f(n) = f(n-1) + f(n-2)   |   Casos base: f(0) = 0, f(1) = 1",
                "Caso de uso: el laboratorio clínico proyecta el crecimiento de un cultivo día a día.");

        JTextField txt = crearCampoTexto(8);
        JButton btn = crearBotonPrimario("Proyectar");
        p.add(crearFila("Días de cultivo =", txt, btn));

        JTextArea resultado = crearAreaResultado();
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(new JScrollPane(resultado));

        btn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(txt.getText().trim());
                RecursividadModel r = logica.calcularFibonacci(n);
                resultado.setText(r == null
                        ? "⚠ Ingresa un número de días entre 0 y 30"
                        : r.getTraza());
                resultado.setCaretPosition(0);
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });

        return p;
    }

    // ---------------------------------------------------------
    //  Auxiliares para no repetir código
    // ---------------------------------------------------------
    private JPanel crearBase(String titulo, String formula, String casoUso) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo(titulo));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo(formula));
        p.add(Box.createRigidArea(new Dimension(0, 4)));
        p.add(crearSubtitulo(casoUso));
        p.add(Box.createRigidArea(new Dimension(0, 25)));
        return p;
    }

    private JPanel crearFila(String etiqueta, JTextField txt, JButton btn) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        fila.add(new JLabel(etiqueta));
        fila.add(txt);
        fila.add(btn);
        return fila;
    }

    private JTextArea crearAreaResultado() {
        JTextArea area = new JTextArea(16, 50);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        area.setBackground(new Color(245, 248, 252));
        area.setBorder(new EmptyBorder(15, 15, 15, 15));
        return area;
    }
}