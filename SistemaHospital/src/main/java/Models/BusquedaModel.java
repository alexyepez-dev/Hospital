package Models;

import java.util.List;

/**
 * Datos del módulo Búsqueda: el expediente clínico de un paciente y el
 * resultado de una búsqueda.
 *
 * @author CRACKS
 */
public final class BusquedaModel {

    private static final String[] NOMBRE_TRIAJE = {"Resucitación", "Emergencia", "Urgente", "Menos urgente", "No urgente"};

    private BusquedaModel() {
    }

    /** Expediente clínico de un paciente; se identifica por su número de historia. */
    public record Expediente(int historia, String nombre, int edad, String area, int triaje,
            String estado, String diagnostico) {

        public String codigo() {
            return "H-" + historia;
        }

        public String triajeTexto() {
            return triaje + " · " + NOMBRE_TRIAJE[triaje - 1];
        }
    }

    /** Resultado de una búsqueda: filas encontradas y cuántos registros se revisaron. */
    public record Hallazgo(List<Integer> filas, int revisados) {
    }
}
