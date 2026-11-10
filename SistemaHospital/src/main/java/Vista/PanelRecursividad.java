package Vista;

import Componentes.Estilo;
import Componentes.ItemMenu;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Contenido de la vista Recursividad: menú lateral + secciones.
 *  - Guardias de doctores  -> factorial recursivo
 *  - Banco de sangre       -> Fibonacci recursivo
 *  - Salir                 -> ejecuta la acción recibida (volver al menú principal)
 *
 * Está separado de Recursividad.java (que es un formulario de NetBeans) para que
 * el editor visual no pueda borrar este código al regenerar el formulario.
 */
public class PanelRecursividad extends JPanel {

    private static final String GUARDIAS = "guardias";
    private static final String BANCO = "banco";

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final ItemMenu itemGuardias = new ItemMenu("n!", "Guardias de doctores", "Método factorial");
    private final ItemMenu itemBanco = new ItemMenu("fib", "Banco de sangre", "Método Fibonacci");
    private final ItemMenu itemSalir = new ItemMenu(null, "Salir", "Volver al menú principal");

    public PanelRecursividad(Runnable accionSalir) {
        super(new BorderLayout());
        setBackground(Estilo.FONDO);
        add(crearMenuLateral(), BorderLayout.WEST);

        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));
        contenido.add(new PanelGuardias(), GUARDIAS);
        contenido.add(new PanelBancoSangre(), BANCO);
        add(contenido, BorderLayout.CENTER);

        itemGuardias.setAccion(() -> mostrar(GUARDIAS));
        itemBanco.setAccion(() -> mostrar(BANCO));
        itemSalir.setAccion(accionSalir);
        mostrar(GUARDIAS);
    }

    private JComponent crearMenuLateral() {
        JPanel menu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Estilo.AZUL, 0, getHeight(), new Color(0x3A7FD0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(250, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(28, 16, 22, 16));

        JLabel sistema = Estilo.etiqueta("SISTEMA HOSPITAL", 11, Font.BOLD, new Color(255, 255, 255, 190));
        sistema.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        JLabel titulo = Estilo.etiqueta("Recursividad", 22, Font.BOLD, Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 0));
        menu.add(Estilo.izq(sistema));
        menu.add(Estilo.izq(titulo));
        menu.add(Box.createVerticalStrut(34));

        JLabel seccion = Estilo.etiqueta("MÓDULOS", 11, Font.BOLD, new Color(255, 255, 255, 170));
        seccion.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 0));
        menu.add(Estilo.izq(seccion));

        menu.add(itemGuardias);
        menu.add(Box.createVerticalStrut(6));
        menu.add(itemBanco);
        menu.add(Box.createVerticalGlue());

        JComponent linea = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 70));
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        linea.setPreferredSize(new Dimension(10, 1));
        linea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        linea.setAlignmentX(LEFT_ALIGNMENT);
        menu.add(linea);
        menu.add(Box.createVerticalStrut(12));
        menu.add(itemSalir);
        return menu;
    }

    private void mostrar(String seccion) {
        tarjetas.show(contenido, seccion);
        itemGuardias.setSeleccionado(GUARDIAS.equals(seccion));
        itemBanco.setSeleccionado(BANCO.equals(seccion));
    }
}
