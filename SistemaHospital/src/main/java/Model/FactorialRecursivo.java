package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factorial recursivo que registra la traza de apilado y desapilado de la pila
 * de llamadas, para mostrarla en la vista de Recursividad.
 */
public final class FactorialRecursivo {

    /** Mayor n cuyo factorial cabe en un long (20! = 2 432 902 008 176 640 000). */
    public static final int N_MAXIMO = 20;

    public record Resultado(long valor, List<String> traza, int profundidadMaxima, long nanos) {

        public String tiempoFormateado() {
            return String.format(java.util.Locale.US, "%.3f ms", nanos / 1_000_000.0);
        }
    }

    static {
        // Calentamiento: la primera llamada paga la carga de clases y el arranque de la
        // concatenación de cadenas (~30 ms), lo que falsearía el tiempo medido.
        for (int i = 0; i < 50; i++) {
            factorial(N_MAXIMO, 0, new ArrayList<>());
        }
    }

    private FactorialRecursivo() {
    }

    public static Resultado calcular(int n) {
        if (n < 0 || n > N_MAXIMO) {
            throw new IllegalArgumentException("n debe estar entre 0 y " + N_MAXIMO);
        }
        List<String> traza = new ArrayList<>();
        long inicio = System.nanoTime();
        long valor = factorial(n, 0, traza);
        long nanos = System.nanoTime() - inicio;
        int profundidad = Math.max(n, 1);
        return new Resultado(valor, Collections.unmodifiableList(traza), profundidad, nanos);
    }

    private static long factorial(int n, int profundidad, List<String> traza) {
        String sangria = "  ".repeat(profundidad);
        if (n <= 1) {
            traza.add(sangria + "Llamada n=" + n + " -> caso base, retorna 1");
            return 1;
        }
        traza.add(sangria + "Llamada n=" + n + " -> " + n + " * factorial(" + (n - 1) + ")");
        long parcial = factorial(n - 1, profundidad + 1, traza);
        long resultado = n * parcial;
        traza.add(sangria + "Retorna n=" + n + " -> " + n + " * " + parcial + " = " + resultado);
        return resultado;
    }
}
