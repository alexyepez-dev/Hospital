package Componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Menú lateral azul de cada módulo:
 *
 *   SISTEMA HOSPITAL
 *   Título del módulo
 *
 *   SECCIÓN
 *   [n!] Opción 1
 *   [fib] Opción 2
 *   ...
 *   ───────────
 *   [⏻] Salir
 */
public class MenuLateral extends JPanel {

    private final List<ItemMenu> items = new ArrayList<>();

    public MenuLateral(String titulo) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createEmptyBorder(28, 16, 32, 16));

        JLabel sistema = Estilo.etiqueta("SISTEMA HOSPITAL", 11, Font.BOLD, new Color(255, 255, 255, 190));
        sistema.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        JLabel lblTitulo = Estilo.etiqueta(titulo, 22, Font.BOLD, Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 0));
        add(Estilo.izq(sistema));
        add(Estilo.izq(lblTitulo));
        add(Box.createVerticalStrut(26));
    }

    public void agregarSeccion(String texto) {
        JLabel seccion = Estilo.etiqueta(texto, 11, Font.BOLD, new Color(255, 255, 255, 170));
        seccion.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 0));
        add(Estilo.izq(seccion));
    }

    /** Agrega una opción seleccionable; al hacer clic queda marcada y ejecuta la acción. */
    public ItemMenu agregarItem(String insignia, String titulo, String descripcion, Runnable accion) {
        ItemMenu item = new ItemMenu(insignia, titulo, descripcion);
        item.setAccion(() -> {
            seleccionar(item);
            accion.run();
        });
        if (!items.isEmpty()) {
            add(Box.createVerticalStrut(4));
        }
        items.add(item);
        add(item);
        return item;
    }

    /** Agrega al fondo del menú una línea separadora y la opción Salir. */
    public void agregarSalir(String descripcion, Runnable accion) {
        add(Box.createVerticalGlue());
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
        add(linea);
        add(Box.createVerticalStrut(12));
        ItemMenu salir = new ItemMenu(null, "Salir", descripcion);
        salir.setAccion(accion);
        add(salir);
    }

    public void seleccionar(ItemMenu elegido) {
        for (ItemMenu i : items) {
            i.setSeleccionado(i == elegido);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, Estilo.AZUL, 0, getHeight(), new Color(0x3A7FD0)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
