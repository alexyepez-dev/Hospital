package src.sistemahospital.Vistas;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Vista del módulo ORDENAMIENTO. El contenido de cada opción queda PENDIENTE
 * para que el compañero encargado implemente la lógica (Burbuja, Selección,
 * Inserción, Quicksort).
 *
 * @author CRACKS
 */
public class VistaOrdenamiento extends VistaModulo {

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
        String opcion = getOpcionesMenu().get(indice);
        return panelPendiente(opcion);
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
