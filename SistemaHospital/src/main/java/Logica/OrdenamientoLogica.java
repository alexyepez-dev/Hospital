package Logica;

import Models.OrdenamientoModel;
import java.util.Arrays;

/**
 * Lógica de los algoritmos de ordenamiento: Método Burbuja y Método Selección.
 * @author CRACKS
 */
public class OrdenamientoLogica {

    /**
     * Convierte una cadena de números separados por coma o espacio en un arreglo de enteros.
     * Retorna null si el formato es inválido o si hay menos de 2 o más de 20 elementos.
     */
    public int[] parsearArreglo(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        String[] partes = texto.trim().split("[,;\\s]+");
        if (partes.length < 2 || partes.length > 20) {
            return null;
        }
        int[] arreglo = new int[partes.length];
        try {
            for (int i = 0; i < partes.length; i++) {
                arreglo[i] = Integer.parseInt(partes[i].trim());
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return arreglo;
    }

    /**
     * Algoritmo 1: Método Burbuja (Bubble Sort).
     * Ejemplo aplicado: Ordenar edades de pacientes en sala de espera de menor a mayor.
     * Compara pares adyacentes (arr[j] y arr[j+1]) e intercambia si están desordenados.
     */
    public OrdenamientoModel ordenarBurbuja(int[] entrada) {
        if (entrada == null || entrada.length < 2) {
            return null;
        }

        int[] original = Arrays.copyOf(entrada, entrada.length);
        int[] arr = Arrays.copyOf(entrada, entrada.length);
        int n = arr.length;
        int comparaciones = 0;
        int intercambios = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("=== MÉTODO BURBUJA (Edades de Pacientes) ===\n");
        sb.append("Arreglo inicial: ").append(Arrays.toString(original)).append("\n\n");

        for (int i = 0; i < n - 1; i++) {
            sb.append("Pasada ").append(i + 1).append(":\n");
            boolean huboCambio = false;

            for (int j = 0; j < n - 1 - i; j++) {
                comparaciones++;
                int a = arr[j];
                int b = arr[j + 1];

                if (a > b) {
                    arr[j] = b;
                    arr[j + 1] = a;
                    intercambios++;
                    huboCambio = true;
                    sb.append("  • Compara ").append(a).append(" > ").append(b)
                      .append("  → Intercambia: ").append(Arrays.toString(arr)).append("\n");
                } else {
                    sb.append("  • Compara ").append(a).append(" <= ").append(b)
                      .append(" → Sin cambio:  ").append(Arrays.toString(arr)).append("\n");
                }
            }

            if (!huboCambio) {
                sb.append("  (Sin intercambios en esta pasada, el arreglo ya está ordenado)\n");
                sb.append("\n");
                break;
            }
            sb.append("\n");
        }

        sb.append("Resultado final ordenado: ").append(Arrays.toString(arr)).append("\n");
        sb.append("Total de comparaciones: ").append(comparaciones)
          .append("  |  Total de intercambios: ").append(intercambios);

        return new OrdenamientoModel(original, arr, comparaciones, intercambios, sb.toString());
    }

    /**
     * Algoritmo 2: Método Selección (Selection Sort).
     * Ejemplo aplicado: Ordenar números de cama/habitación o prioridades de atención.
     * Busca el elemento mínimo en la parte no ordenada y lo coloca en su posición final.
     */
    public OrdenamientoModel ordenarSeleccion(int[] entrada) {
        if (entrada == null || entrada.length < 2) {
            return null;
        }

        int[] original = Arrays.copyOf(entrada, entrada.length);
        int[] arr = Arrays.copyOf(entrada, entrada.length);
        int n = arr.length;
        int comparaciones = 0;
        int intercambios = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("=== MÉTODO SELECCIÓN (N° de Habitaciones / Camas) ===\n");
        sb.append("Arreglo inicial: ").append(Arrays.toString(original)).append("\n\n");

        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            sb.append("Iteración ").append(i + 1)
              .append(" (buscando el menor desde posición ").append(i).append("):\n");

            for (int j = i + 1; j < n; j++) {
                comparaciones++;
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }

            sb.append("  • Mínimo encontrado: ").append(arr[minIdx])
              .append(" (en posición ").append(minIdx).append(")\n");

            if (minIdx != i) {
                int temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
                intercambios++;
                sb.append("  • Intercambia pos ").append(i).append(" (").append(temp)
                  .append(") ↔ pos ").append(minIdx).append(" (").append(arr[i]).append(") → ")
                  .append(Arrays.toString(arr)).append("\n\n");
            } else {
                sb.append("  • El valor ").append(arr[i])
                  .append(" ya está en su posición correcta → ")
                  .append(Arrays.toString(arr)).append("\n\n");
            }
        }

        sb.append("Resultado final ordenado: ").append(Arrays.toString(arr)).append("\n");
        sb.append("Total de comparaciones: ").append(comparaciones)
          .append("  |  Total de intercambios: ").append(intercambios);

        return new OrdenamientoModel(original, arr, comparaciones, intercambios, sb.toString());
    }
}
