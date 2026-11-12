package Model;

/**
 * Cantidad de rotaciones de guardia posibles con n doctores: n! (factorial
 * recursivo).
 *
 *   factorial(0) = factorial(1) = 1
 *   factorial(n) = n * factorial(n - 1)
 */
public final class FactorialRecursivo {

    /** Mayor n cuyo factorial cabe en un long (20! = 2 432 902 008 176 640 000). */
    public static final int N_MAXIMO = 20;

    private FactorialRecursivo() {
    }

    public static long calcular(int n) {
        if (n < 0 || n > N_MAXIMO) {
            throw new IllegalArgumentException("n debe estar entre 0 y " + N_MAXIMO);
        }
        return factorial(n);
    }

    private static long factorial(int n) {
        if (n <= 1) {                  // caso base
            return 1;
        }
        return n * factorial(n - 1);   // llamada recursiva
    }
}
