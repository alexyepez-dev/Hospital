package Main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import Vistas.InicioSistema;

/**
 *
 * @author CRACKS
 */
public class SistemaHospital {

    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo cargar el Look and Feel: " + e.getMessage());
        }

        
        SwingUtilities.invokeLater(() -> {
            InicioSistema login = new InicioSistema();
            login.setVisible(true);
        });
    }
}