package Logica;

import Models.RecursividadModel;

/**
 * Lógica del módulo RECURSIVIDAD aplicada al Sistema Hospitalario.
 *
 * Algoritmo 1 - Factorial: número de órdenes de atención posibles
 *               para n pacientes en la sala de espera (n!).
 * Algoritmo 2 - Fibonacci: proyección del crecimiento de un cultivo
 *               bacteriano en el laboratorio clínico (modelo simplificado).
 *
 * @author CRACKS
 */
public class RecursividadLogica {

    private static final int MAX_PACIENTES = 20; // 20! es el máximo que cabe en un long
    private static final int MAX_DIAS = 30;

    // Contador de llamadas recursivas (para mostrar el costo de Fibonacci)
    private long llamadasFib;

    // =====================================================
    //  FACTORIAL  ->  órdenes de atención de n pacientes
    // =====================================================

    public RecursividadModel calcularFactorial(int n) {
        if (n < 0 || n > MAX_PACIENTES) {
            return null;
        }

        StringBuilder descenso = new StringBuilder();
        StringBuilder ascenso = new StringBuilder();
        long resultado = factorial(n, 0, descenso, ascenso);

        StringBuilder t = new StringBuilder();
        t.append("=== SALA DE ESPERA - EMERGENCIAS ===\n");
        t.append("Pacientes en espera: ").append(n).append("\n");
        t.append("Pregunta: ¿de cuántas formas distintas se puede ordenar la atención?\n");
        t.append("Se resuelve con n! = n * (n-1)!\n\n");
        t.append("--- FASE DE DESCENSO (llamadas recursivas) ---\n");
        t.append(descenso);
        t.append("\n--- FASE DE ASCENSO (retorno de valores) ---\n");
        t.append(ascenso);
        t.append("\n====================================\n");
        t.append("Resultado: ").append(n).append("! = ").append(resultado).append("\n");
        t.append("Hay ").append(resultado).append(" órdenes posibles de atención para ")
                .append(n).append(n == 1 ? " paciente." : " pacientes.").append("\n");
        t.append("(Por eso el hospital usa TRIAGE: no se puede probar cada orden,\n");
        t.append(" se prioriza por gravedad.)");

        return new RecursividadModel(n, resultado, t.toString());
    }

    /**
     * Factorial recursivo. Registra la traza al bajar y al subir de la pila.
     */
    private long factorial(int n, int nivel, StringBuilder descenso, StringBuilder ascenso) {
        String sangria = "  ".repeat(nivel);

        // Caso base
        if (n <= 1) {
            descenso.append(sangria).append("factorial(").append(n)
                    .append(") -> CASO BASE, retorna 1\n");
            ascenso.append(sangria).append("factorial(").append(n).append(") = 1\n");
            return 1;
        }

        // Caso recursivo
        descenso.append(sangria).append("factorial(").append(n).append(") = ")
                .append(n).append(" * factorial(").append(n - 1).append(")\n");

        long sub = factorial(n - 1, nivel + 1, descenso, ascenso);
        long res = n * sub;

        ascenso.append(sangria).append("factorial(").append(n).append(") = ")
                .append(n).append(" * ").append(sub).append(" = ").append(res).append("\n");
        return res;
    }

    // =====================================================
    //  FIBONACCI  ->  crecimiento de cultivo en laboratorio
    // =====================================================

    public RecursividadModel calcularFibonacci(int n) {
        if (n < 0 || n > MAX_DIAS) {
            return null;
        }

        StringBuilder t = new StringBuilder();
        t.append("=== LABORATORIO CLÍNICO - CULTIVO BACTERIANO ===\n");
        t.append("Días de seguimiento: ").append(n).append("\n");
        t.append("Modelo: f(d) = f(d-1) + f(d-2)   con f(0)=0 y f(1)=1\n");
        t.append("(Modelo simplificado: la colonia de hoy depende de las dos anteriores)\n\n");
        t.append(String.format("%-6s %-18s %-18s%n", "Día", "Colonias f(d)", "Llamadas recursivas"));
        t.append("----------------------------------------------------\n");

        long ultimo = 0;
        long totalLlamadas = 0;
        for (int d = 0; d <= n; d++) {
            llamadasFib = 0;
            ultimo = fibonacci(d);
            totalLlamadas += llamadasFib;
            t.append(String.format("%-6d %-18d %-18d%n", d, ultimo, llamadasFib));
        }

        t.append("----------------------------------------------------\n");
        t.append("Colonias estimadas al día ").append(n).append(": ").append(ultimo).append("\n");
        t.append("Llamadas recursivas totales: ").append(totalLlamadas).append("\n");
        t.append("(Nota: las llamadas crecen exponencialmente porque Fibonacci\n");
        t.append(" recalcula los mismos valores; se mejora con memorización.)");

        return new RecursividadModel(n, ultimo, t.toString());
    }

    /**
     * Fibonacci recursivo (dos llamadas por nivel).
     */
    private long fibonacci(int n) {
        llamadasFib++;
        // Casos base
        if (n == 0) return 0;
        if (n == 1) return 1;
        // Caso recursivo
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}