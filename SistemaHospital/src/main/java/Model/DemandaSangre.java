package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Proyección recursiva (Fibonacci) de la demanda de sangre por semana.
 *
 * La demanda de una semana es la suma de la demanda de las dos semanas previas:
 *
 *   D(1) = DEMANDA_SEMANA_1
 *   D(2) = DEMANDA_SEMANA_2
 *   D(n) = D(n-1) + D(n-2)      para n >= 3
 */
public final class DemandaSangre {

    public static final int SEMANA_MINIMA = 1;
    public static final int SEMANA_MAXIMA = 12;

    /** Unidades requeridas en las dos primeras semanas. */
    public static final long DEMANDA_SEMANA_1 = 10;
    public static final long DEMANDA_SEMANA_2 = 15;

    /** Reserva de seguridad exigida sobre lo requerido para el estado "Suficiente" (20 %). */
    public static final int RESERVA_PORCENTAJE = 20;

    public enum Estado {
        SUFICIENTE("Suficiente"),
        JUSTO("Sin reserva"),
        DEFICIT("Solicitar donantes");

        public final String titulo;

        Estado(String titulo) {
            this.titulo = titulo;
        }
    }

    /** Una fila de la proyección: demanda de la semana frente al stock disponible. */
    public record Semana(int numero, long demanda, long stock, Estado estado) {

        public long balance() {
            return stock - demanda;
        }
    }

    public record Proyeccion(List<Semana> semanas, long total, long nanos) {
    }

    private DemandaSangre() {
    }

    /** Proyecta la demanda de las semanas 1..semanas y la compara con el stock actual. */
    public static Proyeccion proyectar(int semanas, long stock) {
        if (semanas < SEMANA_MINIMA || semanas > SEMANA_MAXIMA) {
            throw new IllegalArgumentException(
                    "Las semanas deben estar entre " + SEMANA_MINIMA + " y " + SEMANA_MAXIMA);
        }
        List<Semana> filas = new ArrayList<>(semanas);
        long total = 0;
        for (int n = 1; n <= semanas; n++) {
            long demanda = demanda(n);
            total += demanda;
            filas.add(new Semana(n, demanda, stock, evaluarStock(demanda, stock)));
        }
        return new Proyeccion(Collections.unmodifiableList(filas), total, medir(semanas));
    }

    /** Tiempo de calcular la demanda de todas las semanas: mejor de varias corridas. */
    private static long medir(int semanas) {
        long mejor = Long.MAX_VALUE;
        for (int k = 0; k < 7; k++) {
            long inicio = System.nanoTime();
            for (int n = 1; n <= semanas; n++) {
                demanda(n);
            }
            mejor = Math.min(mejor, System.nanoTime() - inicio);
        }
        return mejor;
    }

    /** Fibonacci recursivo: D(n) = D(n-1) + D(n-2). */
    public static long demanda(int semana) {
        if (semana == 1) {
            return DEMANDA_SEMANA_1;
        }
        if (semana == 2) {
            return DEMANDA_SEMANA_2;
        }
        return demanda(semana - 1) + demanda(semana - 2);
    }

    public static Estado evaluarStock(long requeridas, long stock) {
        if (stock * 100 >= requeridas * (100L + RESERVA_PORCENTAJE)) {
            return Estado.SUFICIENTE;
        }
        if (stock >= requeridas) {
            return Estado.JUSTO;
        }
        return Estado.DEFICIT;
    }
}
