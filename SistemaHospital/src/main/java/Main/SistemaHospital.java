package Main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import Vistas.InicioSistema;  // ✅ ruta nueva

/**
 *
 * @author CRACKS
 */
public class SistemaHospital {

    public static void main(String[] args) {
        // Aplicar Look and Feel del sistema para mejor apariencia
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo cargar el Look and Feel: " + e.getMessage());
        }

        // Iniciar la vista de login en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            InicioSistema login = new InicioSistema();
            login.setVisible(true);
        });
    }
}