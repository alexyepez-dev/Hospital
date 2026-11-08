package Vistas; 

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Vista del módulo BÚSQUEDA. El contenido de cada opción queda PENDIENTE para
 * que el compañero encargado implemente la lógica (Binaria, Secuencial).
 *
 * @author CRACKS
 */
public class VistaBusqueda extends VistaModulo {

    public VistaBusqueda(String usuario) {
        super(usuario, "Búsqueda", new Color(230, 126, 34));
        initModulo();
    }

    @Override
    protected List<String> getOpcionesMenu() {
        return Arrays.asList("Búsqueda Binaria", "Búsqueda Secuencial");
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
        p.add(crearSubtitulo("Aquí irá la lógica del módulo Búsqueda → " + nombreOpcion));
        p.add(Box.createVerticalGlue());
        return p;
    }
}
