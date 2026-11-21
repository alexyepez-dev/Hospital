package Logica;

import Models.PruebaEscritorioModel;

/**
 * Lógica de la Prueba de Escritorio para el módulo Búsqueda
 * (Búsqueda Binaria y Búsqueda Secuencial).
 *
 * @author CRACKS
 */
public class PruebaEscritorioLogica {

    // Arreglo ordenado para Búsqueda Binaria
    private static final int[] HISTORIAS_ORDENADAS = {
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115
    };

    private static final String[] PACIENTES_ORDENADOS = {
        "Carlos Zambrano", "Miguel Intriago", "Luis Mendoza", "Pedro Macías",
        "Diego Bravo", "Jorge Alcívar", "Andrés Torres", "Valeria Ponce",
        "Rosa Moreira", "Ana Mendoza", "Lucía Salazar", "Sofía Cedeño",
        "Daniela Vera", "Elena Chávez", "Gabriela Loor"
    };

    // Arreglo en orden de registro para Búsqueda Secuencial
    private static final int[] HISTORIAS_REGISTRO = {
        108, 103, 112, 101, 115, 106, 110, 104, 113, 102, 109, 105, 114, 107, 111
    };

    private static final String[] PACIENTES_REGISTRO = {
        "Valeria Ponce", "Luis Mendoza", "Sofía Cedeño", "Carlos Zambrano",
        "Gabriela Loor", "Jorge Alcívar", "Ana Mendoza", "Pedro Macías",
        "Daniela Vera", "Miguel Intriago", "Rosa Moreira", "Diego Bravo",
        "Elena Chávez", "Andrés Torres", "Lucía Salazar"
    };

    public PruebaEscritorioModel ejecutarPruebaBinaria(int objetivo) {
        if (objetivo <= 0) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("Prueba de Escritorio - Búsqueda Binaria O(log n)\n");
        sb.append("Buscando historia clínica: H-").append(objetivo).append("\n\n");

        sb.append("Arreglo ordenado (n = ").append(HISTORIAS_ORDENADAS.length).append("):\n");
        for (int i = 0; i < HISTORIAS_ORDENADAS.length; i++) {
            sb.append("  [").append(i).append("] H-").append(HISTORIAS_ORDENADAS[i])
              .append(" - ").append(PACIENTES_ORDENADOS[i]).append("\n");
        }
        sb.append("\n--- Traza de iteraciones ---\n");

        int inicio = 0;
        int fin = HISTORIAS_ORDENADAS.length - 1;
        int paso = 0;

        while (inicio <= fin) {
            paso++;
            int medio = (inicio + fin) / 2;
            int actual = HISTORIAS_ORDENADAS[medio];

            sb.append("\nPaso ").append(paso).append(": ")
              .append("inicio = ").append(inicio)
              .append(", fin = ").append(fin)
              .append("  ->  medio = (").append(inicio).append(" + ").append(fin).append(") / 2 = ").append(medio).append("\n");
            sb.append("  Comparando arreglo[").append(medio).append("] = H-").append(actual)
              .append(" (").append(PACIENTES_ORDENADOS[medio]).append(") con H-").append(objetivo).append("\n");

            if (actual == objetivo) {
                sb.append("  -> Caso encontrado: ").append(actual).append(" == ").append(objetivo).append("\n");
                sb.append("\nResultado: H-").append(objetivo)
                  .append(" encontrado en la posición [").append(medio).append("] en ")
                  .append(paso).append(paso == 1 ? " paso." : " pasos.");
                return new PruebaEscritorioModel(objetivo, medio, sb.toString());
            } else if (actual < objetivo) {
                sb.append("  -> ").append(actual).append(" < ").append(objetivo)
                  .append(" (buscar en mitad derecha: inicio = ").append(medio + 1).append(")\n");
                inicio = medio + 1;
            } else {
                sb.append("  -> ").append(actual).append(" > ").append(objetivo)
                  .append(" (buscar en mitad izquierda: fin = ").append(medio - 1).append(")\n");
                fin = medio - 1;
            }
        }

        sb.append("\nFin del ciclo: inicio (").append(inicio).append(") > fin (").append(fin).append(")\n");
        sb.append("\nResultado: H-").append(objetivo).append(" no se encuentra en el arreglo (").append(paso).append(" pasos).");
        return new PruebaEscritorioModel(objetivo, -1, sb.toString());
    }

    public PruebaEscritorioModel ejecutarPruebaSecuencial(int objetivo) {
        if (objetivo <= 0) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("Prueba de Escritorio - Búsqueda Secuencial O(n)\n");
        sb.append("Buscando historia clínica: H-").append(objetivo).append("\n\n");

        sb.append("Arreglo en orden de registro (n = ").append(HISTORIAS_REGISTRO.length).append("):\n");
        for (int i = 0; i < HISTORIAS_REGISTRO.length; i++) {
            sb.append("  [").append(i).append("] H-").append(HISTORIAS_REGISTRO[i])
              .append(" - ").append(PACIENTES_REGISTRO[i]).append("\n");
        }
        sb.append("\n--- Traza de iteraciones ---\n");

        int paso = 0;
        for (int i = 0; i < HISTORIAS_REGISTRO.length; i++) {
            paso++;
            int actual = HISTORIAS_REGISTRO[i];

            sb.append("\nPaso ").append(paso).append(" (i = ").append(i).append("): ")
              .append("Comparando arreglo[").append(i).append("] = H-").append(actual)
              .append(" (").append(PACIENTES_REGISTRO[i]).append(") con H-").append(objetivo).append("\n");

            if (actual == objetivo) {
                sb.append("  -> Caso encontrado: ").append(actual).append(" == ").append(objetivo).append("\n");
                sb.append("\nResultado: H-").append(objetivo)
                  .append(" encontrado en la posición [").append(i).append("] tras ")
                  .append(paso).append(paso == 1 ? " comparación." : " comparaciones.");
                return new PruebaEscritorioModel(objetivo, i, sb.toString());
            } else {
                sb.append("  -> ").append(actual).append(" != ").append(objetivo)
                  .append(" (no coincide, avanzar a i = ").append(i + 1).append(")\n");
            }
        }

        sb.append("\nFin del recorrido: se revisaron los ").append(HISTORIAS_REGISTRO.length).append(" elementos.\n");
        sb.append("\nResultado: H-").append(objetivo)
          .append(" no se encuentra en el arreglo (").append(paso).append(" comparaciones).");
        return new PruebaEscritorioModel(objetivo, -1, sb.toString());
    }
}
