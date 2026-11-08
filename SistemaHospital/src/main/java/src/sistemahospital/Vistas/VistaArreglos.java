package src.sistemahospital.Vistas;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Vista del módulo ARREGLOS.
 * El contenido de cada opción queda PENDIENTE para que el compañero
 * encargado implemente la lógica (Insertar, Eliminar, Modificar, Buscar).
 * @author CRACKS
 */
public class VistaArreglos extends VistaModulo {

    public VistaArreglos(String usuario) {
        super(usuario, "Arreglos", new Color(52, 152, 219));
        initModulo();
    }

    @Override
    protected List<String> getOpcionesMenu() {
        return Arrays.asList("Insertar", "Eliminar", "Modificar", "Buscar");
    }

    @Override
    protected JPanel crearPanelOpcion(int indice) {
        String opcion = getOpcionesMenu().get(indice);
        return panelPendiente(opcion);
    }

    /** Panel vacío con un mensaje de "sección pendiente". */
    private JPanel panelPendiente(String nombreOpcion) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo(nombreOpcion));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo("Aquí irá la lógica del módulo Arreglos → " + nombreOpcion));
        p.add(Box.createVerticalGlue());
        return p;
    }
}