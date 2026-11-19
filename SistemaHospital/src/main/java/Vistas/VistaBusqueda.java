package Vistas;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;

/**
 * Vista del módulo BÚSQUEDA:
 *  - Búsqueda Binaria: Archivo de Historias (por número de historia clínica).
 *  - Búsqueda Secuencial: Consulta en Sala (filtra por nombre o por área).
 *
 * @author CRACKS
 */
public class VistaBusqueda extends VistaModulo {

    // Los paneles se reutilizan para no perder la búsqueda al cambiar de opción
    private PanelBusquedaBinaria panelBinaria;
    private PanelBusquedaSecuencial panelSecuencial;

    public VistaBusqueda(String usuario) {
        super(usuario, "Búsqueda", new Color(230, 126, 34));
        Componentes.Estilo.agrandarVentana(this, 1440, 800);
        initModulo();
    }

    @Override
    protected List<String> getOpcionesMenu() {
        return Arrays.asList("Búsqueda Binaria", "Búsqueda Secuencial");
    }

    @Override
    protected JPanel crearPanelOpcion(int indice) {
        if (indice == 0) {
            if (panelBinaria == null) {
                panelBinaria = new PanelBusquedaBinaria();
            }
            return panelBinaria;
        }
        if (panelSecuencial == null) {
            panelSecuencial = new PanelBusquedaSecuencial();
        }
        return panelSecuencial;
    }
}
