package Vista;

import Componentes.Estilo;
import Componentes.ItemMenu;
import Componentes.MenuLateral;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.BorderFactory;
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

    public PanelRecursividad(Runnable accionSalir) {
        super(new BorderLayout());
        setBackground(Estilo.FONDO);

        MenuLateral menu = new MenuLateral("Recursividad");
        menu.agregarSeccion("MÓDULOS");
        ItemMenu guardias = menu.agregarItem("n!", "Guardias de doctores", "Método factorial",
                () -> tarjetas.show(contenido, GUARDIAS));
        menu.agregarItem("fib", "Banco de sangre", "Método Fibonacci",
                () -> tarjetas.show(contenido, BANCO));
        menu.agregarSalir("Volver al menú principal", accionSalir);
        add(menu, BorderLayout.WEST);

        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));
        contenido.add(new PanelGuardias(), GUARDIAS);
        contenido.add(new PanelBancoSangre(), BANCO);
        add(contenido, BorderLayout.CENTER);

        menu.seleccionar(guardias);
    }
}
