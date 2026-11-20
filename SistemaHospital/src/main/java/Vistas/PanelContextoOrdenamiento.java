package Vistas;

import Componentes.PanelRedondeado;
import Componentes.ScrollModerno;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Panel independiente de Contexto para el módulo de Ordenamiento.
 * Se instala dinámicamente justo encima del botón "Salir al menú" para no
 * modificar VistaOrdenamiento.java ni generar conflictos con el botón "Caso de uso".
 *
 * @author CRACKS
 */
public class PanelContextoOrdenamiento extends JPanel {

    private final VistaModulo vista;

    private PanelContextoOrdenamiento(VistaModulo vista) {
        super(new BorderLayout(0, 14));
        this.vista = vista;
        setOpaque(false);
        construirContenido();
    }

    /**
     * Inserta el botón "Contexto" justo encima del botón "← Salir al menú" en el sidebar.
     */
    public static void instalarEn(VistaModulo vista) {
        if (vista == null || vista.panelSidebar == null) {
            return;
        }
        if (Boolean.TRUE.equals(vista.panelSidebar.getClientProperty("contextoOrdenamientoInstalado"))) {
            return;
        }
        vista.panelSidebar.putClientProperty("contextoOrdenamientoInstalado", Boolean.TRUE);

        BotonContextoSidebar btnContexto = new BotonContextoSidebar(vista);

        btnContexto.addActionListener(e -> {
            // Desactivar botones de opciones superiores
            for (JButton btn : vista.botonesMenu) {
                try {
                    java.lang.reflect.Method m = btn.getClass().getDeclaredMethod("setActivo", boolean.class);
                    m.setAccessible(true);
                    m.invoke(btn, false);
                } catch (Exception ignored) {
                }
            }
            btnContexto.setActivo(true);

            // Mostrar el panel de contexto en el área central
            vista.panelContenido.removeAll();
            vista.panelContenido.add(new PanelContextoOrdenamiento(vista), BorderLayout.CENTER);
            vista.panelContenido.revalidate();
            vista.panelContenido.repaint();
        });

        // Desactivar "Contexto" cuando se selecciona cualquiera de las opciones del menú
        for (JButton btn : vista.botonesMenu) {
            btn.addActionListener(e -> btnContexto.setActivo(false));
        }

        // Insertar justo antes del último componente ("← Salir al menú")
        int posSalir = Math.max(0, vista.panelSidebar.getComponentCount() - 1);
        vista.panelSidebar.add(btnContexto, posSalir);
        vista.panelSidebar.add(Box.createRigidArea(new Dimension(0, 10)), posSalir + 1);
        vista.panelSidebar.revalidate();
        vista.panelSidebar.repaint();
    }

    private void construirContenido() {
        // Encabezado fijo superior
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titulo = vista.crearTitulo("Contexto: Ordenamiento en el Sistema Hospitalario");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = vista.crearSubtitulo(
                "Fundamentos de ordenamiento, algoritmos implementados y caso de uso de turnos médicos con Quicksort");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(titulo);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(subtitulo);
        add(header, BorderLayout.NORTH);

        // Lista vertical de tarjetas con ajuste automático de ancho
        JPanel listaTarjetas = new PanelScrollVertical();
        listaTarjetas.setOpaque(false);
        listaTarjetas.setLayout(new BoxLayout(listaTarjetas, BoxLayout.Y_AXIS));
        listaTarjetas.setBorder(new EmptyBorder(4, 2, 12, 10));

        // 1. ¿Qué es el ordenamiento?
        listaTarjetas.add(crearTarjeta(
                "1. ¿Qué son los Algoritmos de Ordenamiento?",
                new Color(46, 204, 113),
                "El ordenamiento es el proceso de reorganizar los elementos de un arreglo o lista bajo un "
                + "criterio definido (normalmente de menor a mayor o de mayor a menor). En Estructuras de Datos "
                + "es una operación fundamental porque transforma datos dispersos en información rápida de consultar.\n\n"
                + "• Importancia en un Hospital: permite organizar edades, números de habitación, tiempos de "
                + "espera y turnos médicos en secuencia, agilizando el trabajo de recepción y habilitando "
                + "algoritmos de búsqueda mucho más veloces como la Búsqueda Binaria (que exige datos ordenados).",
                "Objetivo: transformar un arreglo desordenado A[0..n-1] en una secuencia ordenada A[0] <= A[1] <= ... <= A[n-1]"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2. Algoritmos iterativos clásicos
        listaTarjetas.add(crearTarjeta(
                "2. Métodos Iterativos: Burbuja, Selección e Inserción",
                new Color(41, 128, 185),
                "Estos tres métodos recorren el arreglo mediante bucles anidados y son ideales para comprender "
                + "cómo se comparan e intercambian posiciones paso a paso:\n\n"
                + "• Método Burbuja (Bubble Sort): compara parejas de elementos vecinos (arr[j] y arr[j+1]) e "
                + "intercambia si están al revés, haciendo que el mayor 'flote' hacia el final en cada pasada. "
                + "En el sistema se aplica a edades de pacientes.\n"
                + "• Método Selección (Selection Sort): recorre la parte desordenada buscando el elemento mínimo "
                + "y realiza un único intercambio por iteración para ubicarlo al inicio. Realiza menos intercambios "
                + "que Burbuja. En el sistema se aplica a números de camas/habitaciones.\n"
                + "• Método Inserción (Insertion Sort): toma cada elemento y lo desplaza hacia la izquierda hasta "
                + "insertarlo en su posición correcta (similar a ordenar cartas). Es muy eficiente cuando los datos "
                + "ya están casi ordenados. En el sistema se aplica a tiempos de espera.",
                "Complejidad promedio: Burbuja O(n²)  |  Selección O(n²)  |  Inserción O(n²) [O(n) en mejor caso]"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. Quicksort
        listaTarjetas.add(crearTarjeta(
                "3. Algoritmo Avanzado: Método Quicksort (Divide y Vencerás)",
                new Color(155, 89, 182),
                "Quicksort es uno de los algoritmos de ordenamiento más rápidos y utilizados en sistemas reales. "
                + "Trabaja con la estrategia Divide y Vencerás apoyándose en la recursividad:\n\n"
                + "1. Selección de pivote: elige un elemento del arreglo como punto de referencia (pivote).\n"
                + "2. Partición: reordena los elementos de modo que todos los menores al pivote queden a su "
                + "izquierda y todos los mayores queden a su derecha.\n"
                + "3. Recursividad: aplica exactamente el mismo proceso sobre la sublista izquierda y la sublista "
                + "derecha hasta que cada segmento tenga 0 o 1 elemento.\n\n"
                + "• Ventaja clave: en lugar de comparar todos contra todos como los métodos cuadráticos, reduce "
                + "el problema por mitades, logrando un rendimiento muy superior cuando hay muchos registros.",
                "Complejidad promedio: O(n log n)   |   Estrategia: Pivote + Partición + Llamadas Recursivas"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 4. Caso de Uso Hospitalario
        listaTarjetas.add(crearTarjeta(
                "4. Nuestro Caso de Uso: Organización de Turnos Médicos (Quicksort)",
                new Color(230, 126, 34),
                "En la recepción de un hospital, los números de turno de los pacientes pueden quedar registrados "
                + "en desorden (por ejemplo: 108, 103, 115, 101, 110). El personal administrativo necesita consultar "
                + "esos turnos en secuencia ascendente para ubicar rápidamente cada registro (aclarando que el orden "
                + "numérico facilita la consulta administrativa y no sustituye la prioridad clínica del triaje).\n\n"
                + "• Cómo funciona en la vista 'Caso de uso': el usuario ingresa los números de turno, selecciona "
                + "el algoritmo (Burbuja, Selección, Inserción o QuickSort) y visualiza inmediatamente la tabla de "
                + "turnos organizados de menor a mayor junto con el estado del proceso.\n"
                + "• ¿Por qué destaca Quicksort en este caso de uso? Porque en una jornada real un hospital atiende "
                + "cientos o miles de turnos; mientras Burbuja, Selección e Inserción crecen de forma cuadrática O(n²), "
                + "Quicksort mantiene un tiempo promedio O(n log n), ordenando los turnos de manera ágil y escalable.",
                "Escenario real: Turnos [108, 103, 115, 101, 110]  →  QuickSort  →  [101, 103, 108, 110, 115]"
        ));

        JScrollPane scroll = ScrollModerno.envolver(listaTarjetas);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(0));
    }

    private PanelRedondeado crearTarjeta(String tituloSeccion, Color colorAcento, String cuerpo, String formula) {
        PanelRedondeado card = new PanelRedondeado();
        card.setBackground(new Color(248, 250, 254));
        card.setColorBorde(new Color(220, 230, 242));
        card.setRadio(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel cabecera = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cabecera.setOpaque(false);
        cabecera.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel barraColor = new JPanel();
        barraColor.setBackground(colorAcento);
        barraColor.setPreferredSize(new Dimension(5, 20));

        JLabel lblTit = new JLabel(tituloSeccion);
        lblTit.setFont(vista.montserratBold.deriveFont(Font.BOLD, 15f));
        lblTit.setForeground(vista.AZUL_OSCURO);

        cabecera.add(barraColor);
        cabecera.add(lblTit);

        JTextArea txtCuerpo = new JTextArea(cuerpo);
        txtCuerpo.setEditable(false);
        txtCuerpo.setFocusable(false);
        txtCuerpo.setOpaque(false);
        txtCuerpo.setLineWrap(true);
        txtCuerpo.setWrapStyleWord(true);
        txtCuerpo.setFont(vista.montserratRegular.deriveFont(Font.PLAIN, 13f));
        txtCuerpo.setForeground(new Color(40, 52, 70));
        txtCuerpo.setBorder(new EmptyBorder(8, 2, 10, 2));
        txtCuerpo.setAlignmentX(Component.LEFT_ALIGNMENT);

        PanelRedondeado cajaFormula = new PanelRedondeado();
        cajaFormula.setBackground(new Color(
                (colorAcento.getRed() + 255 * 8) / 9,
                (colorAcento.getGreen() + 255 * 8) / 9,
                (colorAcento.getBlue() + 255 * 8) / 9));
        cajaFormula.setColorBorde(new Color(
                (colorAcento.getRed() + 255 * 4) / 5,
                (colorAcento.getGreen() + 255 * 4) / 5,
                (colorAcento.getBlue() + 255 * 4) / 5));
        cajaFormula.setRadio(8);
        cajaFormula.setLayout(new BorderLayout());
        cajaFormula.setBorder(new EmptyBorder(8, 12, 8, 12));
        cajaFormula.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFormula = new JLabel(formula);
        lblFormula.setFont(new Font("Consolas", Font.BOLD, 12));
        lblFormula.setForeground(colorAcento.darker());
        cajaFormula.add(lblFormula, BorderLayout.CENTER);

        card.add(cabecera);
        card.add(txtCuerpo);
        card.add(cajaFormula);
        return card;
    }

    /** Botón "Contexto" estilizado a juego con el sidebar de VistaModulo. */
    private static class BotonContextoSidebar extends JButton {

        private final VistaModulo vista;
        private boolean hover = false;
        private boolean activo = false;

        BotonContextoSidebar(VistaModulo vista) {
            super("Contexto");
            this.vista = vista;
            setFont(vista.montserratBold.deriveFont(Font.BOLD, 13f));
            setForeground(vista.BLANCO);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, 18, 0, 18));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setPreferredSize(new Dimension(200, 46));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
        }

        void setActivo(boolean valor) {
            if (this.activo != valor) {
                this.activo = valor;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (activo) {
                Color c1 = vista.acentoModulo;
                Color c2 = vista.aclarar(vista.acentoModulo, 40);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
            } else if (hover) {
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            } else {
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Panel auxiliar que ajusta su ancho al viewport para evitar desbordes horizontales. */
    private static class PanelScrollVertical extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 24; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 90; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
