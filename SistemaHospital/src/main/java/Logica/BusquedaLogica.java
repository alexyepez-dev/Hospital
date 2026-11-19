package Logica;

import Models.BusquedaModel.Campo;
import Models.BusquedaModel.Expediente;
import Models.BusquedaModel.Hallazgo;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Lógica del módulo Búsqueda:
 *  - Búsqueda binaria por número de historia clínica, O(log n).
 *  - Búsqueda secuencial por nombre o por área, O(n).
 *
 * @author CRACKS
 */
public class BusquedaLogica {

    private static final Pattern TILDES = Pattern.compile("\\p{M}");

    /**
     * Búsqueda secuencial: recorre la lista uno por uno, desde la primera
     * posición hasta la última, comparando el texto con el campo elegido
     * (nombre del paciente o área del hospital). Coincide con el inicio de
     * cualquier palabra, sin distinguir mayúsculas ni tildes, y devuelve
     * todas las coincidencias.
     */
    public Hallazgo busquedaSecuencial(List<Expediente> lista, String texto, Campo campo) {
        String buscado = " " + normalizar(texto);
        List<Integer> filas = new ArrayList<>();
        int revisados = 0;
        for (int i = 0; i < lista.size(); i++) {
            revisados++;
            String valor = " " + normalizar(valorDe(lista.get(i), campo));
            if (valor.contains(buscado)) {
                filas.add(i);
            }
        }
        return new Hallazgo(filas, revisados);
    }

    /** Texto del expediente que se compara: el nombre, o el área sin la cama ("Emergencia · C-03" → "Emergencia"). */
    private static String valorDe(Expediente e, Campo campo) {
        if (campo == Campo.AREA) {
            int punto = e.area().indexOf('·');
            return punto < 0 ? e.area() : e.area().substring(0, punto);
        }
        return e.nombre();
    }

    /** Minúsculas y sin tildes, para que "cedeno" encuentre a "Cedeño" y "SOFIA" a "Sofía". */
    private static String normalizar(String s) {
        return TILDES.matcher(Normalizer.normalize(s, Normalizer.Form.NFD)).replaceAll("")
                .toLowerCase(Locale.ROOT).trim();
    }

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
