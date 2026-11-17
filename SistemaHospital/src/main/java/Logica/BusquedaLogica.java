package Logica;

import Models.BusquedaModel.Expediente;
import Models.BusquedaModel.Hallazgo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Lógica del módulo Búsqueda:
 *  - Búsqueda binaria por número de historia clínica, O(log n).
 *
 * @author CRACKS
 */
public class BusquedaLogica {

    /** Búsqueda binaria sobre la lista ordenada por historia: parte el rango a la mitad en cada paso. */
    public Hallazgo busquedaBinaria(List<Expediente> ordenada, int historia) {
        int inicio = 0;
        int fin = ordenada.size() - 1;
        int revisados = 0;
        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            revisados++;
            int actual = ordenada.get(medio).historia();
            if (actual == historia) {
                return new Hallazgo(List.of(medio), revisados);
            }
            if (actual < historia) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return new Hallazgo(List.of(), revisados);
    }

    /** La búsqueda binaria exige la lista ordenada por historia: si no lo está, se ordena una copia. */
    public List<Expediente> ordenarPorHistoria(List<Expediente> origen) {
        List<Expediente> copia = new ArrayList<>(origen);
        for (int i = 1; i < copia.size(); i++) {
            if (copia.get(i - 1).historia() > copia.get(i).historia()) {
                copia.sort(Comparator.comparingInt(Expediente::historia));
                break;
            }
        }
        return copia;
    }
}
