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
        sb.append("Serie de Fibonacci hasta n=").append(n)
          .append(" (f(n) = f(n-1) + f(n-2)):\n\n");

        long prev2 = 0;
        long prev1 = 1;
        long resultadoFinal = 0;

        for (int i = 0; i <= n; i++) {
            long actual = fibonacci(i);
            resultadoFinal = actual;
            if (i <= 1) {
                sb.append("f(").append(i).append(") = ").append(actual)
                  .append("  (caso base)\n");
            } else {
                sb.append("f(").append(i).append(") = f(").append(i - 1)
                  .append(") + f(").append(i - 2).append(") = ")
                  .append(prev1).append(" + ").append(prev2)
                  .append(" = ").append(actual).append("\n");
                prev2 = prev1;
                prev1 = actual;
            }
        }

        sb.append("\nResultado: f(").append(n).append(") = ").append(resultadoFinal);

        if (n <= 6) {
            sb.append("\n\n--- Traza de llamadas recursivas para f(").append(n).append(") ---\n");
            sb.append(trazaArbolFibonacci(n, 0));
        }

        return new RecursividadModel(n, resultadoFinal, sb.toString());
    }

    // ==================== CASO DE USO HOSPITALARIO (FIBONACCI) ====================

    /**
     * Calcula recursivamente el total acumulado de camas requeridas desde el ciclo 0 hasta n.
     */
    public long sumaCamasRecursiva(int n) {
        if (n <= 0) return 0;
        return fibonacci(n) + sumaCamasRecursiva(n - 1);
    }

    /**
     * Genera la traza en árbol de las llamadas recursivas f(n) = f(n-1) + f(n-2),
     * mostrando también la suma resuelta al retornar cada llamada.
     */
    public String trazaArbolFibonacci(int n, int nivel) {
        String prefijo = "  ".repeat(nivel);
        if (n <= 1) {
            return prefijo + "f(" + n + ") = " + n + " (caso base)\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(prefijo).append("f(").append(n).append(") = f(")
          .append(n - 1).append(") + f(").append(n - 2).append(")\n");
        sb.append(trazaArbolFibonacci(n - 1, nivel + 1));
        sb.append(trazaArbolFibonacci(n - 2, nivel + 1));
        long a = fibonacci(n - 1);
        long b = fibonacci(n - 2);
        sb.append(prefijo).append("f(").append(n).append(") = ")
          .append(a).append(" + ").append(b).append(" = ").append(a + b).append("\n");
        return sb.toString();
    }

    /**
     * Determina el pabellón/área del hospital asignada según la demanda acumulada de camas.
     */
    public String obtenerAreaAsignada(long camasAcumuladas) {
        if (camasAcumuladas <= 5) return "Triaje / Observación";
        if (camasAcumuladas <= 20) return "Aislamiento General";
        if (camasAcumuladas <= 60) return "Medicina Interna";
        return "UCI / Contingencia";
    }

    /**
     * Determina el nivel de alerta sanitaria del hospital según las camas acumuladas.
     */
    public String obtenerNivelAlerta(long camasAcumuladas) {
        if (camasAcumuladas <= 5) return "Normal";
        if (camasAcumuladas <= 20) return "Vigilancia";
        if (camasAcumuladas <= 60) return "Alerta Naranja";
        return "Emergencia UCI";
    }

    /**
     * Ejecuta la proyección epidemiológica recursiva para el caso de uso hospitalario.
     */
    public RecursividadModel calcularCasoUsoHospital(int n, String patologia) {
        if (n < 1 || n > 15) return null;

        long nuevosCiclo = fibonacci(n);
        long camasTotales = sumaCamasRecursiva(n);

        StringBuilder sb = new StringBuilder();
        sb.append("=== PROYECCIÓN EPIDEMIOLÓGICA RECURSIVA ===\n");
        sb.append("Protocolo: ").append(patologia)
          .append("  |  Ciclo proyectado: n = ").append(n).append("\n");
        sb.append("Fórmula: f(n) = f(n-1) [activos] + f(n-2) [fin incubación]\n\n");

        if (n <= 6) {
            sb.append("Árbol de llamadas recursivas para f(").append(n).append("):\n");
            sb.append(trazaArbolFibonacci(n, 0));
        } else {
            sb.append("Desglose recursivo paso a paso (n > 6, vista resumida):\n");
            for (int i = 0; i <= n; i++) {
                if (i <= 1) {
                    sb.append("  Ciclo ").append(i).append(": f(").append(i)
                      .append(") = ").append(i).append(" (caso base)\n");
                } else {
                    long f1 = fibonacci(i - 1);
                    long f2 = fibonacci(i - 2);
                    sb.append("  Ciclo ").append(i).append(": f(").append(i).append(") = f(")
                      .append(i - 1).append(") + f(").append(i - 2).append(") = ")
                      .append(f1).append(" + ").append(f2).append(" = ").append(f1 + f2).append(" pacientes\n");
                }
            }
        }

        sb.append("\nResumen Clínico → Nuevos ingresos en ciclo ").append(n).append(": ")
          .append(nuevosCiclo).append("  |  Camas acumuladas: ").append(camasTotales)
          .append("  |  Estado: ").append(obtenerNivelAlerta(camasTotales));

        return new RecursividadModel(n, nuevosCiclo, sb.toString());
    }
}
