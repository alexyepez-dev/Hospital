/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica;
import Models.OrdenamientoModel;

/**
 *
 * @author Lenovo
 */
public class OrdenamientoExtraLogica {
    


    public OrdenamientoModel ordenarInsercion(int[] numeros) {

        if (numeros == null || numeros.length == 0) {
            return null;
        }

        int[] original = numeros.clone();
        int[] arr = numeros.clone();
        int comparaciones = 0;
        int intercambios = 0;
        StringBuilder sb = new StringBuilder();

        sb.append("========== MÉTODO DE INSERCIÓN ==========\n");
        sb.append("Ordenamiento de tiempos de espera de pacientes\n");
        sb.append("Orden: de menor a mayor\n\n");

        sb.append("Arreglo original: ")
          .append(java.util.Arrays.toString(original))
          .append("\n\n");

        for (int i = 1; i < arr.length; i++) {

            int clave = arr[i];
            int j = i - 1;

            sb.append("---------- PASADA ")
              .append(i)
              .append(" ----------\n");

            sb.append("Elemento que vamos a insertar: ")
              .append(clave)
              .append(" minutos\n");

            while (j >= 0) {

                comparaciones++;

                sb.append("Comparamos ")
                  .append(clave)
                  .append(" con ")
                  .append(arr[j])
                  .append(".\n");

                if (arr[j] <= clave) {

                    sb.append("Como ")
                      .append(clave)
                      .append(" es mayor o igual que ")
                      .append(arr[j])
                      .append(", no necesitamos desplazarlo.\n");

                    break;
                }

                sb.append("Como ")
                  .append(clave)
                  .append(" es menor que ")
                  .append(arr[j])
                  .append(", desplazamos ")
                  .append(arr[j])
                  .append(" una posición a la derecha.\n");

                arr[j + 1] = arr[j];
                intercambios++;
                j--;
            }

            arr[j + 1] = clave;

            sb.append("Insertamos ")
              .append(clave)
              .append(" en la posición ")
              .append(j + 1)
              .append(".\n");

            sb.append("Arreglo después de la pasada: ")
              .append(java.util.Arrays.toString(arr))
              .append("\n\n");
        }

        sb.append("========== RESULTADO FINAL ==========\n");

        sb.append("Arreglo original: ")
          .append(java.util.Arrays.toString(original))
          .append("\n");

        sb.append("Tiempos de espera ordenados: ")
          .append(java.util.Arrays.toString(arr))
          .append("\n\n");

        sb.append("Total de comparaciones: ")
          .append(comparaciones)
          .append("\n");

        sb.append("Total de desplazamientos: ")
          .append(intercambios)
          .append("\n");

        return new OrdenamientoModel(
            original,
            arr,
            comparaciones,
            intercambios,
            sb.toString()
        );
    }


    
    

    public OrdenamientoModel ordenarQuickSort(int[] numeros) {
        if (numeros == null || numeros.length == 0) {
            return null;
        }

        int[] original = numeros.clone();
        int[] arr = numeros.clone();
        int[] comparaciones = {0};
        int[] intercambios = {0};
        StringBuilder sb = new StringBuilder();

        sb.append("========== MÉTODO QUICKSORT ==========\n");
        sb.append("Ordenamiento de números de turnos médicos\n");
        sb.append("Orden: de menor a mayor\n\n");

        sb.append("Arreglo original: ")
          .append(java.util.Arrays.toString(original))
          .append("\n\n");

        quickSortRecursivo(
            arr, 0, arr.length - 1,
            comparaciones, intercambios, sb
        );

        sb.append("========== RESULTADO FINAL ==========\n");

        sb.append("Arreglo original: ")
          .append(java.util.Arrays.toString(original))
          .append("\n");

        sb.append("Turnos médicos ordenados: ")
          .append(java.util.Arrays.toString(arr))
          .append("\n\n");

        sb.append("Total de comparaciones: ")
          .append(comparaciones[0])
          .append("\n");

        sb.append("Total de intercambios: ")
          .append(intercambios[0])
          .append("\n");

        return new OrdenamientoModel(
            original,
            arr,
            comparaciones[0],
            intercambios[0],
            sb.toString()
        );
    }

    private void quickSortRecursivo(
            int[] arr, int inicio, int fin,
            int[] comparaciones, int[] intercambios,
            StringBuilder sb) {

        if (inicio >= fin) {
            return;
        }

        int pivote = arr[fin];
        int i = inicio - 1;

        sb.append("---------- PARTICIÓN ----------\n");
        sb.append("Posiciones a ordenar: ")
          .append(inicio)
          .append(" hasta ")
          .append(fin)
          .append("\n");

        sb.append("Elegimos como pivote el número: ")
          .append(pivote)
          .append("\n");

        for (int j = inicio; j < fin; j++) {

            comparaciones[0]++;

            sb.append("Comparamos ")
              .append(arr[j])
              .append(" con el pivote ")
              .append(pivote)
              .append(".\n");

            if (arr[j] <= pivote) {

                sb.append("Como ")
                  .append(arr[j])
                  .append(" es menor o igual al pivote, ")
                  .append("lo colocamos en la parte izquierda.\n");

                i++;

                if (i != j) {
                    int temporal = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temporal;
                    intercambios[0]++;

                    sb.append("Intercambiamos las posiciones ")
                      .append(i)
                      .append(" y ")
                      .append(j)
                      .append(".\n");
                }

            } else {

                sb.append("Como ")
                  .append(arr[j])
                  .append(" es mayor que el pivote, ")
                  .append("permanece en la parte derecha.\n");
            }
        }

        if (i + 1 != fin) {
            int temporal = arr[i + 1];
            arr[i + 1] = arr[fin];
            arr[fin] = temporal;
            intercambios[0]++;
        }

        int posicionPivote = i + 1;

        sb.append("Colocamos el pivote ")
          .append(pivote)
          .append(" en la posición ")
          .append(posicionPivote)
          .append(".\n");

        sb.append("Arreglo después de la partición: ")
          .append(java.util.Arrays.toString(arr))
          .append("\n\n");

        sb.append("Continuamos ordenando las partes ")
          .append("izquierda y derecha del pivote.\n\n");

        quickSortRecursivo(
            arr, inicio, posicionPivote - 1,
            comparaciones, intercambios, sb
        );

        quickSortRecursivo(
            arr, posicionPivote + 1, fin,
            comparaciones, intercambios, sb
        );
    }

    }

