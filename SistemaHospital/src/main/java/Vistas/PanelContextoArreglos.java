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
 * Panel independiente de Contexto para el módulo de Arreglos.
 * Se instala dinámicamente justo encima del botón "Salir al menú" para no
 * modificar VistaArreglos.java ni LogicaArreglos.java, restaurando
 * automáticamente el panel original de la tabla al volver a operar.
 *
 * @author CRACKS
 */
public class PanelContextoArreglos extends JPanel {

    private final VistaModulo vista;
    private final Runnable onVolver;

    private PanelContextoArreglos(VistaModulo vista, Runnable onVolver) {
        super(new BorderLayout(0, 14));
        this.vista = vista;
        this.onVolver = onVolver;
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
        if (Boolean.TRUE.equals(vista.panelSidebar.getClientProperty("contextoArreglosInstalado"))) {
            return;
        }
        vista.panelSidebar.putClientProperty("contextoArreglosInstalado", Boolean.TRUE);

        final Component[] panelOriginalRef = new Component[1];
        BotonContextoSidebar btnContexto = new BotonContextoSidebar(vista);

        Runnable restaurarPanelOriginal = () -> {
            btnContexto.setActivo(false);
            if (panelOriginalRef[0] != null) {
                boolean yaVisible = vista.panelContenido.getComponentCount() == 1
                        && vista.panelContenido.getComponent(0) == panelOriginalRef[0];
                if (!yaVisible) {
                    vista.panelContenido.removeAll();
                    vista.panelContenido.add(panelOriginalRef[0], BorderLayout.CENTER);
                    vista.panelContenido.revalidate();
                    vista.panelContenido.repaint();
                }
            }
        };

        Runnable volverConSeleccion = () -> {
            restaurarPanelOriginal.run();
            int idx = Math.max(0, Math.min(vista.opcionSeleccionada, vista.botonesMenu.size() - 1));
            for (int i = 0; i < vista.botonesMenu.size(); i++) {
                actualizarEstadoBoton(vista.botonesMenu.get(i), i == idx);
            }
        };

        btnContexto.addActionListener(e -> {
            if (panelOriginalRef[0] == null && vista.panelContenido.getComponentCount() > 0) {
                Component actual = vista.panelContenido.getComponent(0);
                if (!(actual instanceof PanelContextoArreglos)) {
                    panelOriginalRef[0] = actual;
                }
            }

            // Desactivar botones de opciones superiores
            for (JButton btn : vista.botonesMenu) {
                actualizarEstadoBoton(btn, false);
            }
            btnContexto.setActivo(true);

            // Mostrar el panel de contexto en el área central
            vista.panelContenido.removeAll();
            vista.panelContenido.add(new PanelContextoArreglos(vista, volverConSeleccion), BorderLayout.CENTER);
            vista.panelContenido.revalidate();
            vista.panelContenido.repaint();
        });

        // Restaurar la tabla de Arreglos antes de que se disparen los diálogos de Insertar/Eliminar/Modificar/Buscar
        for (JButton btn : vista.botonesMenu) {
            btn.addActionListener(e -> restaurarPanelOriginal.run());
        }

        // Insertar justo antes del último componente ("← Salir al menú")
        int posSalir = Math.max(0, vista.panelSidebar.getComponentCount() - 1);
        vista.panelSidebar.add(btnContexto, posSalir);
        vista.panelSidebar.add(Box.createRigidArea(new Dimension(0, 10)), posSalir + 1);
        vista.panelSidebar.revalidate();
        vista.panelSidebar.repaint();
    }

    private static void actualizarEstadoBoton(JButton btn, boolean activo) {
        try {
            java.lang.reflect.Method m = btn.getClass().getDeclaredMethod("setActivo", boolean.class);
            m.setAccessible(true);
            m.invoke(btn, activo);
        } catch (Exception ignored) {
        }
    }

    private void construirContenido() {
        // Encabezado fijo superior con botón para regresar a la tabla de Arreglos
        JPanel barraHeader = new JPanel(new BorderLayout(12, 0));
        barraHeader.setOpaque(false);

        JPanel textosHeader = new JPanel();
        textosHeader.setOpaque(false);
        textosHeader.setLayout(new BoxLayout(textosHeader, BoxLayout.Y_AXIS));

        JLabel titulo = vista.crearTitulo("Contexto: Arreglos en el Sistema Hospitalario");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = vista.crearSubtitulo(
                "Vectores estáticos, operaciones fundamentales y gestión del registro de pacientes (MAX = 10)");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        textosHeader.add(titulo);
        textosHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        textosHeader.add(subtitulo);

        JButton btnVolver = vista.crearBotonPrimario("← Volver a tabla");
        btnVolver.setPreferredSize(new Dimension(155, 38));
        btnVolver.addActionListener(e -> onVolver.run());

        JPanel panelBtnVolver = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        panelBtnVolver.setOpaque(false);
        panelBtnVolver.add(btnVolver);

        barraHeader.add(textosHeader, BorderLayout.CENTER);
        barraHeader.add(panelBtnVolver, BorderLayout.EAST);
        add(barraHeader, BorderLayout.NORTH);

        // Lista vertical de tarjetas con ajuste automático de ancho
        JPanel listaTarjetas = new PanelScrollVertical();
        listaTarjetas.setOpaque(false);
        listaTarjetas.setLayout(new BoxLayout(listaTarjetas, BoxLayout.Y_AXIS));
        listaTarjetas.setBorder(new EmptyBorder(4, 2, 12, 10));

        // 1. ¿Qué son los arreglos unidimensionales?
        listaTarjetas.add(crearTarjeta(
                "1. ¿Qué son los Arreglos Unidimensionales (Vectores Estáticos)?",
                new Color(52, 152, 219),
                "Un arreglo unidimensional (o vector estático) es una estructura de datos lineal que almacena "
                + "una colección finita y ordenada de elementos del mismo tipo en posiciones contiguas de memoria. "
                + "Cada casilla se identifica de forma única mediante un índice entero que inicia en 0 hasta MAX - 1.\n\n"
                + "• Control de frontera (cantidadActual): al tener una capacidad fija (en nuestro sistema MAX = 10), "
                + "mantenemos un contador 'cantidadActual' que cumple la invariante 0 <= cantidadActual <= MAX. "
                + "Esto permite prevenir dos errores clásicos: el Desbordamiento (Overflow) cuando el arreglo está lleno "
                + "y el Subdesbordamiento (Underflow) cuando se intenta eliminar en un arreglo vacío.",
                "Declaración: String[] arreglo = new String[MAX];   |   Capacidad fija: MAX = 10   |   Índices: [0 .. 9]"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2. Operaciones de acceso directo O(1): Insertar y Modificar
        listaTarjetas.add(crearTarjeta(
                "2. Operaciones de Acceso Directo: Insertar y Modificar",
                new Color(39, 174, 96),
                "Gracias a que los elementos ocupan posiciones contiguas en memoria, el arreglo permite acceder "
                + "inmediatamente a cualquier casilla conociendo su índice:\n\n"
                + "• Insertar al final — O(1): verifica primero que el arreglo no esté lleno (!estaLleno()), "
                + "guarda el nuevo registro directamente en arreglo[cantidadActual] e incrementa cantidadActual en 1. "
                + "(Adicionalmente, LogicaArreglos soporta insertarEn(indice, elemento) desplazando elementos a la "
                + "derecha con Shift-Right en O(n)).\n"
                + "• Modificar por índice — O(1): valida que el índice esté dentro del rango válido "
                + "(0 <= indice < cantidadActual) y sobrescribe directamente arreglo[indice] = nuevoValor sin "
                + "necesidad de recorrer el resto del vector.",
                "Insertar al final: O(1)   |   Modificar por índice: O(1)   |   Actualización inmediata en JTable"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. Operaciones lineales O(n): Eliminar (Shift-Left) y Buscar (Normalizer)
        listaTarjetas.add(crearTarjeta(
                "3. Operaciones Lineales: Eliminar (Shift-Left) y Buscar",
                new Color(155, 89, 182),
                "Otras operaciones requieren recorrer las posiciones del arreglo para mantener la integridad "
                + "de los datos o localizar un registro por su contenido:\n\n"
                + "• Eliminar con desplazamiento a la izquierda (Shift-Left) — O(n): cuando se elimina un elemento "
                + "en la posición 'indice', no puede quedar un espacio vacío en medio del vector. Por ello, se "
                + "recorren todos los elementos posteriores una posición hacia la izquierda (arreglo[i] = arreglo[i + 1]), "
                + "se limpia la última casilla con null y se decrementa cantidadActual.\n"
                + "• Búsqueda secuencial flexible — O(n): recorre desde 0 hasta cantidadActual - 1 comparando el "
                + "texto normalizado con java.text.Normalizer (elimina tildes y pasa a minúsculas). Así, buscar "
                + "'maria' o 'JUAN' encuentra 'PAC-002 (María Gómez)' o 'PAC-001 (Juan Pérez)' y selecciona la fila.",
                "Eliminar (Shift-Left): for (i = idx; i < n-1; i++) arreglo[i] = arreglo[i+1];   |   Buscar: O(n)"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 4. Caso de Uso Hospitalario implementado
        listaTarjetas.add(crearTarjeta(
                "4. Nuestro Caso de Uso: Registro Activo de Pacientes en Sala (Cupo Fijo MAX = 10)",
                new Color(230, 126, 34),
                "El caso de uso real implementado en VistaArreglos y LogicaArreglos gestiona el registro activo "
                + "de pacientes asignados a una sala o unidad de atención con cupo limitado (MAX = 10 espacios), "
                + "iniciando con pacientes registrados como PAC-001 (Juan Pérez), PAC-002 (María Gómez) y "
                + "PAC-003 (Carlos Ruiz).\n\n"
                + "• Cómo funciona en la práctica: la interfaz presenta en tiempo real las métricas de Capacidad "
                + "(MAX), Cantidad Actual, Espacios Libres y una insignia de estado (Disponible / Lleno / Vacío). "
                + "Cada acción del menú lateral (Insertar ingreso, Eliminar alta con Shift-Left, Modificar datos "
                + "del paciente y Buscar paciente) actualiza automáticamente la tabla y la consola inferior.\n"
                + "• ¿Por qué usamos un Arreglo Estático para este caso de uso? Porque en un hospital una sala "
                + "de observación o unidad clínica tiene una capacidad física fija de camas/cupos que no puede "
                + "excederse (control estricto de Overflow). El arreglo estático garantiza acceso instantáneo O(1) "
                + "por número de cupo/índice y visibilidad exacta de cuántos espacios libres quedan disponibles.",
                "Uso hospitalario: [0] PAC-001 (Juan Pérez) · [1] PAC-002 (María Gómez) · [2] PAC-003 (Carlos Ruiz) · 7 libres"
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
