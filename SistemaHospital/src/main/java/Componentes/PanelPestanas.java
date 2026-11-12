package Componentes;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Tarjeta con pestañas tipo "segmentos":
 *
 *   [ Pila de llamadas | Traza | Código Java ]
 *   descripción de la pestaña activa                (leyenda / chips)
 *   ┌───────────────────────────────────────────────────────────────┐
 *   │ contenido                                                     │
 *   └───────────────────────────────────────────────────────────────┘
 */
public class PanelPestanas extends PanelRedondeado {

    private final SelectorSegmentos selector = new SelectorSegmentos();
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);
    private final JLabel descripcion = Estilo.etiqueta(" ", 12, Font.PLAIN, Estilo.TEXTO_SEC);
    private final JPanel zonaDerecha = new JPanel(new BorderLayout());

    private final List<String> descripciones = new ArrayList<>();
    private final List<JComponent> extras = new ArrayList<>();

    public PanelPestanas() {
        Estilo.comoTarjeta(this);
        setLayout(new BorderLayout(0, 12));

        // Fila 1: pestañas.  Fila 2: descripción a la izquierda + leyenda/chips a la derecha.
        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.add(Estilo.izq(selector));
        JPanel filaDescripcion = new JPanel(new BorderLayout(12, 0));
        filaDescripcion.setOpaque(false);
        filaDescripcion.setBorder(BorderFactory.createEmptyBorder(10, 2, 0, 0));
        filaDescripcion.add(descripcion, BorderLayout.WEST);
        zonaDerecha.setOpaque(false);
        filaDescripcion.add(zonaDerecha, BorderLayout.EAST);
        cabecera.add(Estilo.izq(filaDescripcion));
        add(cabecera, BorderLayout.NORTH);

        contenido.setOpaque(false);
        add(contenido, BorderLayout.CENTER);

        selector.alCambiar(this::mostrar);
    }

    /**
     * @param extra componente opcional a la derecha de la descripción (p. ej. una leyenda); puede ser null
     */
    public void agregar(String titulo, String desc, JComponent panel, JComponent extra) {
        selector.agregarOpcion(titulo);
        descripciones.add(desc);
        extras.add(extra);
        contenido.add(panel, String.valueOf(selector.getCantidad() - 1));
        if (selector.getCantidad() == 1) {
            seleccionar(0);
        }
    }

    /** Cambia la descripción de una pestaña (p. ej. cuando cambia el resultado). */
    public void setDescripcion(int i, String desc) {
        descripciones.set(i, desc);
        if (selector.getSeleccion() == i) {
            descripcion.setText(desc);
        }
    }

    public void seleccionar(int i) {
        selector.setSeleccion(i);
    }

    private void mostrar(int i) {
        cartas.show(contenido, String.valueOf(i));
        descripcion.setText(descripciones.get(i));
        zonaDerecha.removeAll();
        if (extras.get(i) != null) {
            zonaDerecha.add(extras.get(i), BorderLayout.CENTER);
        }
        zonaDerecha.revalidate();
        zonaDerecha.repaint();
    }

    /** Envuelve un contenido con un borde fino redondeado, para el área de la pestaña. */
    public static JComponent marco(JComponent c) {
        PanelRedondeado m = new PanelRedondeado();
        m.setRadio(10);
        m.setBackground(Color.WHITE);
        m.setColorBorde(Estilo.BORDE);
        m.setLayout(new BorderLayout());
        m.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        m.add(c, BorderLayout.CENTER);
        return m;
    }
}
