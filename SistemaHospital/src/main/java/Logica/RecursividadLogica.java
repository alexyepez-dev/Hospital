package Logica;

import Models.RecursividadModel;

/**
 * Lógica de los algoritmos recursivos.
 * @author CRACKS
 */
public class RecursividadLogica {

    public long factorial(int n) {
        return (n <= 1) ? 1 : n * factorial(n - 1);
    }

    public String trazaFactorial(int n, int nivel) {
        String prefijo = "  ".repeat(nivel);
        if (n <= 1) return prefijo + "f(" + n + ") = 1 (caso base)\n";

        StringBuilder sb = new StringBuilder();
        sb.append(prefijo).append("f(").append(n).append(") = ")
          .append(n).append(" × f(").append(n - 1).append(")\n");
        sb.append(trazaFactorial(n - 1, nivel + 1));
        sb.append(prefijo).append("f(").append(n).append(") = ").append(factorial(n)).append("\n");
        return sb.toString();
    }

    public RecursividadModel calcularFactorial(int n) {
        if (n < 0 || n > 20) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("Calculando factorial(").append(n).append(")\n\n");
        sb.append(trazaFactorial(n, 0));
        sb.append("\n\nResultado: ").append(n).append("! = ").append(factorial(n));
        return new RecursividadModel(n, factorial(n), sb.toString());
    }

    public long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public RecursividadModel calcularFibonacci(int n) {
        if (n < 0 || n > 30) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("Serie de Fibonacci hasta n=").append(n).append(":\n\n");
        for (int i = 0; i <= n; i++) {
            sb.append("f(").append(i).append(") = ").append(fibonacci(i)).append("\n");
        }
        return new RecursividadModel(n, fibonacci(n), sb.toString());
    }
}