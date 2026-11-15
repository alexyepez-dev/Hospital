package Vistas; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import Logica.OrdenamientoLogica;
import Models.OrdenamientoModel;

/**
 * Vista del módulo ORDENAMIENTO.
 * Implementados: Método Burbuja y Método Selección.
 * Pendientes para: Método Inserción y Método Quicksort.
 *
 * @author CRACKS
 */
public class VistaOrdenamiento extends VistaModulo {

    private final OrdenamientoLogica logica = new OrdenamientoLogica();

    public VistaOrdenamiento(String usuario) {
        super(usuario, "Ordenamiento", new Color(46, 204, 113));
        initModulo();
    }

    @Override
    protected List<String> getOpcionesMenu() {
        return Arrays.asList("Método Burbuja", "Método Selección", "Método Inserción", "Método Quicksort");
    }

    @Override
    protected JPanel crearPanelOpcion(int indice) {
        switch (indice) {
            case 0:
                return panelBurbuja();
            case 1:
                return panelSeleccion();
            default:
                // Espacio libre para Método Inserción (2) y Método Quicksort (3)
                String opcion = getOpcionesMenu().get(indice);
                return panelPendiente(opcion);
        }
    }

    private JPanel panelBurbuja() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo("Método Burbuja"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo("Ordena edades de pacientes comparando pares adyacentes e intercambiándolos si están en orden incorrecto"));
        p.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        JTextField txt = crearCampoTexto(20);
        txt.setText("45, 18, 72, 30, 12, 60");
        JButton btnOrdenar = crearBotonPrimario("Ordenar");
        JButton btnEjemplo = crearBotonPrimario("Ejemplo");

        fila.add(new JLabel("Edades (separadas por coma):"));
        fila.add(txt);
        fila.add(btnOrdenar);
        fila.add(btnEjemplo);
        p.add(fila);

        JTextArea resultado = new JTextArea(12, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultado.setBackground(new Color(245, 248, 252));
        resultado.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(new JScrollPane(resultado));

        btnOrdenar.addActionListener(e -> {
            int[] datos = logica.parsearArreglo(txt.getText());
            if (datos == null) {
                resultado.setText("⚠ Ingresa entre 2 y 20 números enteros válidos separados por coma (ej: 45, 18, 72, 30)");
            } else {
                OrdenamientoModel r = logica.ordenarBurbuja(datos);
                resultado.setText(r.getTraza());
                resultado.setCaretPosition(0);
            }
        });

        btnEjemplo.addActionListener(e -> {
            txt.setText("54, 21, 68, 14, 39, 10");
            btnOrdenar.doClick();
        });

        return p;
    }

    private JPanel panelSeleccion() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo("Método Selección"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo("Ordena números de cama/habitación buscando el valor mínimo en cada pasada y colocándolo al inicio"));
        p.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        JTextField txt = crearCampoTexto(20);
        txt.setText("108, 102, 115, 101, 109, 104");
        JButton btnOrdenar = crearBotonPrimario("Ordenar");
        JButton btnEjemplo = crearBotonPrimario("Ejemplo");

        fila.add(new JLabel("N° Camas (separados por coma):"));
        fila.add(txt);
        fila.add(btnOrdenar);
        fila.add(btnEjemplo);
        p.add(fila);

        JTextArea resultado = new JTextArea(12, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultado.setBackground(new Color(245, 248, 252));
        resultado.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(new JScrollPane(resultado));

        btnOrdenar.addActionListener(e -> {
            int[] datos = logica.parsearArreglo(txt.getText());
            if (datos == null) {
                resultado.setText("⚠ Ingresa entre 2 y 20 números enteros válidos separados por coma (ej: 108, 102, 115, 101)");
            } else {
                OrdenamientoModel r = logica.ordenarSeleccion(datos);
                resultado.setText(r.getTraza());
                resultado.setCaretPosition(0);
            }
        });

        btnEjemplo.addActionListener(e -> {
            txt.setText("210, 203, 219, 201, 214, 207");
            btnOrdenar.doClick();
        });

        return p;
    }

    /**
     * Panel vacío con un mensaje de "sección pendiente".
     */
    private JPanel panelPendiente(String nombreOpcion) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo(nombreOpcion));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo("Aquí irá la lógica del módulo Ordenamiento → " + nombreOpcion));
        p.add(Box.createVerticalGlue());
        return p;
    }
}
