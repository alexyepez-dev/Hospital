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
 * Panel independiente de Contexto para el módulo de Búsqueda.
 * Se instala dinámicamente justo encima del botón "Salir al menú" para no
 * modificar VistaBusqueda.java ni afectar las ramas de Búsqueda Binaria o Secuencial.
 *
 * @author CRACKS
 */
public class PanelContextoBusqueda extends JPanel {

    private final VistaModulo vista;

    private PanelContextoBusqueda(VistaModulo vista) {
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
        if (Boolean.TRUE.equals(vista.panelSidebar.getClientProperty("contextoBusquedaInstalado"))) {
            return;
        }
        vista.panelSidebar.putClientProperty("contextoBusquedaInstalado", Boolean.TRUE);

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
            vista.panelContenido.add(new PanelContextoBusqueda(vista), BorderLayout.CENTER);
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

        JLabel titulo = vista.crearTitulo("Contexto: Búsqueda en el Sistema Hospitalario");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = vista.crearSubtitulo(
                "Fundamentos de búsqueda de datos, Búsqueda Secuencial vs. Binaria y nuestro Archivo Histórico");
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

        // 1. ¿Qué son los algoritmos de búsqueda?
        listaTarjetas.add(crearTarjeta(
                "1. ¿Qué son los Algoritmos de Búsqueda?",
                new Color(230, 126, 34),
                "Un algoritmo de búsqueda permite localizar la posición o existencia de un dato específico "
                + "(clave) dentro de una colección de registros almacenados en memoria, como un arreglo o una lista.\n\n"
                + "• Importancia en un Hospital: en una emergencia o consulta médica, el personal necesita ubicar "
                + "en segundos el expediente clínico, diagnóstico, nivel de triaje o cama de un paciente entre "
                + "cientos o miles de registros. Elegir el algoritmo correcto marca la diferencia en velocidad "
                + "y cantidad de registros que el sistema debe revisar.",
                "Entrada: colección de registros + clave buscada   →   Salida: posición encontrada (Hallazgo) o 'No registrado'"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2. Búsqueda Secuencial
        listaTarjetas.add(crearTarjeta(
                "2. Algoritmo 1: Búsqueda Secuencial (o Lineal)",
                new Color(41, 128, 185),
                "La búsqueda secuencial recorre la estructura elemento por elemento, empezando desde el índice 0 "
                + "hasta el final, comparando cada registro con el valor buscado.\n\n"
                + "• Ventajas: no exige que los datos estén previamente ordenados, es muy sencilla de implementar y "
                + "resulta ideal cuando se busca por texto parcial (por ejemplo, nombres de pacientes, áreas o "
                + "diagnósticos) o cuando puede haber múltiples coincidencias.\n"
                + "• Desventaja: en el peor caso (cuando el elemento está al final o no existe) debe revisar "
                + "todos los n registros uno por uno.",
                "Complejidad: Mejor caso O(1)  |  Peor caso O(n)   |   Requisito: funciona en datos desordenados"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. Búsqueda Binaria
        listaTarjetas.add(crearTarjeta(
                "3. Algoritmo 2: Búsqueda Binaria",
                new Color(22, 163, 74),
                "La búsqueda binaria aplica el principio de Divide y Vencerás sobre una colección previamente "
                + "ordenada por su clave. En lugar de revisar registro por registro, salta directamente al punto "
                + "medio del rango actual:\n\n"
                + "1. Calcula el índice central: medio = (inicio + fin) / 2.\n"
                + "2. Si el registro en 'medio' coincide con la clave buscada, termina de inmediato.\n"
                + "3. Si el valor actual es menor que el buscado, descarta toda la mitad izquierda (inicio = medio + 1); "
                + "si es mayor, descarta toda la mitad derecha (fin = medio - 1).\n\n"
                + "• Eficiencia sobresaliente: al dividir el rango a la mitad en cada paso, con 15 expedientes "
                + "necesita como máximo 4 comparaciones, y en un archivo real con 1,000,000 de historias clínicas "
                + "localiza cualquier paciente en a lo sumo 20 pasos.",
                "Complejidad: O(log n)   |   Requisito obligatorio: archivo ordenado por clave (ordenarPorHistoria)"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 4. Caso de Uso Hospitalario: Archivo Histórico con Búsqueda Binaria
        listaTarjetas.add(crearTarjeta(
                "4. Nuestro Caso de Uso: Consulta de Archivo Histórico (Búsqueda Binaria)",
                new Color(155, 89, 182),
                "El caso de uso real implementado en el módulo 'Búsqueda Binaria' simula el Archivo Histórico de "
                + "Expedientes del Hospital, donde cada paciente posee un número único de historia clínica "
                + "(desde H-101 hasta H-115).\n\n"
                + "• Cómo trabaja el sistema por detrás: aunque los expedientes ingresen en desorden, la clase "
                + "BusquedaLogica los indexa ordenados por número de historia mediante ordenarPorHistoria(). Cuando "
                + "el médico o recepcionista ingresa un código (por ejemplo '104' o 'H-104') y pulsa 'Localizar "
                + "Expediente', se ejecuta busquedaBinaria(), devolviendo un objeto Hallazgo con la fila encontrada "
                + "y el conteo exacto de registros revisados.\n"
                + "• Respuesta clínica inmediata: la interfaz resalta automáticamente el expediente en la tabla, "
                + "muestra cuántos registros se revisaron (ej. 2 de 15) y despliega en la Ficha del Paciente su "
                + "nombre, edad, área/cama, diagnóstico, nivel de triaje y estado actual.\n"
                + "• ¿Por qué elegimos Búsqueda Binaria para este caso de uso? Porque las historias clínicas tienen "
                + "códigos numéricos únicos e indexables; buscar por código con O(log n) garantiza consultas "
                + "instantáneas incluso cuando el archivo histórico del hospital crece a miles de expedientes.",
                "Caso real: Historia H-104  →  busquedaBinaria(lista, 104)  →  Pedro Macías (Medicina Interna · M-02)"
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
