package Vistas; 

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Logica.OrdenamientoLogica;
import Models.OrdenamientoModel;

/**
 * Vista del módulo ORDENAMIENTO.
 * Implementados: Método Burbuja y Método Selección.
 * Implementados: Método Inserción y Método Quicksort.
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
        return Arrays.asList("Método Burbuja", "Método Selección", "Método Inserción", "Método Quicksort", "Caso de uso");
    }

    @Override
    protected JPanel crearPanelOpcion(int indice) {
        switch (indice) {
            case 0:
                return panelBurbuja();
            case 1:
                return panelSeleccion();

            case 2:
                return panelInsercion();
            case 3:
                return panelQuickSort();
            case 4:
                return panelCasoUso();
            
            default:
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
        p.add(crearTarjetaConcepto("El método Burbuja compara dos elementos consecutivos y los intercambia cuando están en el orden incorrecto. Repite las comparaciones hasta ordenar toda la lista.", "En el hospital se utiliza para ordenar las edades de los pacientes de menor a mayor."));
        p.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        JTextField txt = crearCampoTexto(20);
        txt.setText("45, 18, 72, 30, 12, 60");
        JButton btnOrdenar = crearBotonPrimario("Ordenar");
        JButton btnEjemplo = crearBotonPrimario("Ejemplo");

        JLabel etiquetaDatos = new JLabel("Edades (separadas por coma):");
        etiquetaDatos.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(etiquetaDatos);
        fila.add(Box.createVerticalStrut(6));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txt.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(txt);
        fila.add(Box.createVerticalStrut(10));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnOrdenar);
        botones.add(btnEjemplo);
        botones.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(botones);
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
        p.add(crearTarjetaConcepto("El método Selección busca el número más pequeño de la parte desordenada y lo coloca en su posición correcta. Repite el procedimiento con los demás elementos.", "En el hospital se utiliza para organizar los números de camas o habitaciones."));
        p.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        JTextField txt = crearCampoTexto(20);
        txt.setText("108, 102, 115, 101, 109, 104");
        JButton btnOrdenar = crearBotonPrimario("Ordenar");
        JButton btnEjemplo = crearBotonPrimario("Ejemplo");

        JLabel etiquetaDatos = new JLabel("N° Camas (separados por coma):");
        etiquetaDatos.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(etiquetaDatos);
        fila.add(Box.createVerticalStrut(6));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txt.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(txt);
        fila.add(Box.createVerticalStrut(10));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnOrdenar);
        botones.add(btnEjemplo);
        botones.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(botones);
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

    
    
    
    private JPanel panelInsercion() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo("Método Inserción"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearTarjetaConcepto("El método Inserción toma cada elemento y lo coloca en su posición correcta dentro de la parte de la lista que ya está ordenada.", "En el hospital se utiliza para organizar los tiempos de espera de los pacientes."));
        p.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);

        JTextField txt = crearCampoTexto(20);
        txt.setText("35, 10, 25, 5, 20");

        JButton btnOrdenar = crearBotonPrimario("Ordenar");
        JButton btnEjemplo = crearBotonPrimario("Ejemplo");

        JLabel etiquetaDatos = new JLabel("Tiempos de espera (separados por coma):");
        etiquetaDatos.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(etiquetaDatos);
        fila.add(Box.createVerticalStrut(6));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txt.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(txt);
        fila.add(Box.createVerticalStrut(10));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnOrdenar);
        botones.add(btnEjemplo);
        botones.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(botones);
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
                resultado.setText("Ingresa entre 2 y 20 números enteros válidos separados por coma.");
            } else {
                Logica.OrdenamientoExtraLogica logicaExtra =
                        new Logica.OrdenamientoExtraLogica();

                OrdenamientoModel r = logicaExtra.ordenarInsercion(datos);

                resultado.setText(r.getTraza());
                resultado.setCaretPosition(0);
            }
        });

        btnEjemplo.addActionListener(e -> {
            txt.setText("40, 15, 30, 5, 25");
            btnOrdenar.doClick();
        });

        return p;
    }
    
    
    
    private JPanel panelQuickSort() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo("Método Quicksort"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearTarjetaConcepto("QuickSort selecciona un pivote, divide los números en grupos y ordena cada partición hasta completar la lista.", "En el hospital se utiliza para organizar los números de turnos médicos."));
        p.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);

        JTextField txt = crearCampoTexto(20);
        txt.setText("108, 103, 115, 101, 110");

        JButton btnOrdenar = crearBotonPrimario("Ordenar");
        JButton btnEjemplo = crearBotonPrimario("Ejemplo");

        JLabel etiquetaDatos = new JLabel("N° Turnos médicos(separados por coma):");
        etiquetaDatos.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(etiquetaDatos);
        fila.add(Box.createVerticalStrut(6));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txt.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(txt);
        fila.add(Box.createVerticalStrut(10));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnOrdenar);
        botones.add(btnEjemplo);
        botones.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        fila.add(botones);
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
                resultado.setText("⚠ Ingresa entre 2 y 20 números enteros válidos separados por coma.");
            } else {
                Logica.OrdenamientoExtraLogica logicaExtra =
                        new Logica.OrdenamientoExtraLogica();

                OrdenamientoModel r = logicaExtra.ordenarQuickSort(datos);

                resultado.setText(r.getTraza());
                resultado.setCaretPosition(0);
            }
        });

        btnEjemplo.addActionListener(e -> {
            txt.setText("120, 105, 118, 101, 112");
            btnOrdenar.doClick();
        });

        return p;
    }


    
    
    
    /** Concepto visible y legible en las cuatro pestañas originales. */
    private JPanel crearTarjetaConcepto(String concepto, String aplicacion) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(new Color(236, 245, 255));
        tarjeta.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(204, 224, 246)),
                new EmptyBorder(12, 16, 12, 16)));
        tarjeta.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        JLabel titulo = new JLabel("¿QUÉ SIGNIFICA ESTE MÉTODO?");
        titulo.setForeground(new Color(30, 94, 174));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        tarjeta.add(titulo, BorderLayout.NORTH);
        JTextArea contenido = new JTextArea(concepto + "\n\nAPLICACIÓN: " + aplicacion);
        contenido.setEditable(false);
        contenido.setFocusable(false);
        contenido.setOpaque(false);
        contenido.setLineWrap(true);
        contenido.setWrapStyleWord(true);
        contenido.setForeground(new Color(19, 43, 83));
        contenido.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tarjeta.add(contenido, BorderLayout.CENTER);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));
        return tarjeta;
    }

    /** Quinta pestaña: caso hospitalario con resultados, sin trazas técnicas. */
    private JPanel panelCasoUso() {
        Color azul = new Color(19, 43, 83);
        Color gris = new Color(105, 126, 155);
        Color borde = new Color(219, 230, 242);
        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setOpaque(false);
        principal.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        JLabel etiqueta = tituloCaso("CASO PRÁCTICO  ·  GESTIÓN HOSPITALARIA", new Color(35, 111, 205));
        encabezado.add(etiqueta);
        encabezado.add(Box.createVerticalStrut(6));
        encabezado.add(crearTitulo("Organización de Turnos Médicos"));
        encabezado.add(Box.createVerticalStrut(3));
        encabezado.add(crearSubtitulo("Organizar los números de turno para facilitar la consulta de registros"));
        encabezado.add(Box.createVerticalStrut(12));
        JPanel escenario = tarjetaCaso(borde);
        escenario.setLayout(new BorderLayout(0, 5));
        escenario.add(tituloCaso("ESCENARIO DEL HOSPITAL", azul), BorderLayout.NORTH);
        escenario.add(textoCaso("En un hospital se registran turnos de pacientes que pueden quedar desordenados. " +
                "Recepción necesita consultar sus números en secuencia. El sistema permite ordenarlos " +
                "con Burbuja, Selección, Inserción o QuickSort. Este orden numérico no sustituye " +
                "la prioridad clínica establecida por triaje.", azul), BorderLayout.CENTER);
        encabezado.add(escenario);
        principal.add(encabezado, BorderLayout.NORTH);

        JPanel columnas = new JPanel(new BorderLayout(12, 0));
        columnas.setOpaque(false);
        JPanel controles = tarjetaCaso(borde);
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.setPreferredSize(new Dimension(285, 400));
        controles.add(tituloCaso("ORDENAR TURNOS", new Color(35, 111, 205)));
        controles.add(Box.createVerticalStrut(10));
        controles.add(tituloCaso("Números de turno (separados por coma)", azul));
        controles.add(Box.createVerticalStrut(6));
        JTextField entrada = crearCampoTexto(18);
        entrada.setText("108, 103, 115, 101, 110");
        entrada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        controles.add(entrada);
        controles.add(Box.createVerticalStrut(12));
        controles.add(tituloCaso("SELECCIONAR MÉTODO", gris));
        controles.add(Box.createVerticalStrut(8));
        javax.swing.JComboBox<String> metodos = new javax.swing.JComboBox<>(
                new String[]{"Burbuja", "Selección", "Inserción", "QuickSort"});
        metodos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        metodos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        controles.add(metodos);
        controles.add(Box.createVerticalStrut(10));
        JTextArea concepto = textoCaso("Burbuja: compara números vecinos e intercambia los desordenados.", gris);
        concepto.setRows(3);
        concepto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        controles.add(concepto);
        controles.add(Box.createVerticalStrut(10));
        JButton ordenar = crearBotonPrimario("Ordenar turnos");
        ordenar.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        ordenar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        controles.add(ordenar);
        controles.add(Box.createVerticalStrut(8));
        JButton ejemplo = crearBotonPrimario("Restablecer ejemplo");
        ejemplo.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        ejemplo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        controles.add(ejemplo);
        columnas.add(controles, BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout(0, 12));
        derecha.setOpaque(false);
        JPanel indicadores = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        indicadores.setOpaque(false);
        JPanel tCantidad = tarjetaCaso(borde);
        tCantidad.setLayout(new BoxLayout(tCantidad, BoxLayout.Y_AXIS));
        tCantidad.add(tituloCaso("TURNOS REGISTRADOS", gris));
        JLabel cantidad = new JLabel("5");
        cantidad.setFont(new Font("SansSerif", Font.BOLD, 28));
        cantidad.setForeground(new Color(35, 111, 205));
        tCantidad.add(cantidad);
        JPanel tEstado = tarjetaCaso(borde);
        tEstado.setLayout(new BoxLayout(tEstado, BoxLayout.Y_AXIS));
        tEstado.add(tituloCaso("ESTADO", gris));
        JLabel estado = new JLabel("En espera");
        estado.setFont(new Font("SansSerif", Font.BOLD, 22));
        estado.setForeground(gris);
        tEstado.add(estado);
        indicadores.add(tCantidad);
        indicadores.add(tEstado);
        indicadores.setPreferredSize(new Dimension(450, 100));
        derecha.add(indicadores, BorderLayout.NORTH);

        JPanel tarjetaTabla = tarjetaCaso(borde);
        tarjetaTabla.setLayout(new BorderLayout(0, 10));
        tarjetaTabla.add(tituloCaso("RESULTADO  ·  TURNOS ORDENADOS", azul), BorderLayout.NORTH);
        javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
                new Object[]{"POSICIÓN", "N° DE TURNO", "ESTADO"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        javax.swing.JTable tabla = new javax.swing.JTable(modelo);
        tabla.setRowHeight(32);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(232, 243, 255));
        tabla.getTableHeader().setForeground(azul);
        tabla.setGridColor(borde);
        tabla.setFillsViewportHeight(true);
        tarjetaTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JLabel mensaje = new JLabel("Selecciona un método y presiona 'Ordenar turnos'.");
        mensaje.setForeground(gris);
        tarjetaTabla.add(mensaje, BorderLayout.SOUTH);
        derecha.add(tarjetaTabla, BorderLayout.CENTER);
        columnas.add(derecha, BorderLayout.CENTER);
        principal.add(columnas, BorderLayout.CENTER);

        metodos.addActionListener(e -> {
            String m = (String) metodos.getSelectedItem();
            switch (m) {
                case "Selección": concepto.setText("Selección: busca el menor número y lo coloca en su posición."); break;
                case "Inserción": concepto.setText("Inserción: inserta cada número en la parte ya ordenada."); break;
                case "QuickSort": concepto.setText("QuickSort: divide los números mediante un pivote y ordena las partes."); break;
                default: concepto.setText("Burbuja: compara números vecinos e intercambia los desordenados.");
            }
        });
        ordenar.addActionListener(e -> {
            int[] datos = logica.parsearArreglo(entrada.getText());
            if (datos == null) {
                estado.setText("Datos inválidos");
                modelo.setRowCount(0);
                mensaje.setText("Ingresa entre 2 y 20 números enteros separados por comas.");
                return;
            }
            String m = (String) metodos.getSelectedItem();
            OrdenamientoModel r;
            switch (m) {
                case "Selección": r = logica.ordenarSeleccion(datos); break;
                case "Inserción": r = new Logica.OrdenamientoExtraLogica().ordenarInsercion(datos); break;
                case "QuickSort": r = new Logica.OrdenamientoExtraLogica().ordenarQuickSort(datos); break;
                default: r = logica.ordenarBurbuja(datos);
            }
            modelo.setRowCount(0);
            int[] ordenados = r.getOrdenado();
            for (int i = 0; i < ordenados.length; i++) {
                modelo.addRow(new Object[]{i + 1, ordenados[i], "Ordenado"});
            }
            cantidad.setText(String.valueOf(ordenados.length));
            estado.setText("Ordenado");
            mensaje.setText("Resultado obtenido con " + m + ". Turnos organizados de menor a mayor.");
        });
        ejemplo.addActionListener(e -> {
            entrada.setText("108, 103, 115, 101, 110");
            metodos.setSelectedIndex(0);
            modelo.setRowCount(0);
            cantidad.setText("5");
            estado.setText("En espera");
            mensaje.setText("Selecciona un método y presiona 'Ordenar turnos'.");
        });
        return principal;
    }

    private JPanel tarjetaCaso(Color borde) {
        JPanel tarjeta = new JPanel();
        tarjeta.setOpaque(true);
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(borde, 1, true),
                new EmptyBorder(13, 16, 13, 16)));
        tarjeta.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        return tarjeta;
    }

    private JLabel tituloCaso(String titulo, Color color) {
        JLabel label = new JLabel(titulo);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(color);
        return label;
    }

    private JTextArea textoCaso(String texto, Color color) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFocusable(false);
        area.setOpaque(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setForeground(color);
        return area;
    }

    //--------------------------------------------------------------------------------------
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
