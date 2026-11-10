package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Proyección recursiva (Fibonacci) de la demanda de sangre para una campaña.
 *
 * La demanda de una semana es la suma de la demanda cubierta en las dos
 * semanas previas:
 *
 *   D(1) = DEMANDA_SEMANA_1
 *   D(2) = DEMANDA_SEMANA_2
 *   D(n) = D(n-1) + D(n-2)      para n >= 3
 *
 * Además del valor, construye el árbol de llamadas recursivas para dibujarlo.
 */
public final class DemandaSangre {

    public static final int SEMANA_MINIMA = 1;
    /** Con 12 semanas el árbol ya tiene 287 llamadas; más allá deja de ser legible. */
    public static final int SEMANA_MAXIMA = 12;

    /** Unidades cubiertas en las dos primeras semanas (casos base). */
    public static final long DEMANDA_SEMANA_1 = 10;
    public static final long DEMANDA_SEMANA_2 = 15;

    /** Reserva de seguridad exigida sobre lo requerido para el semáforo verde (20 %). */
    public static final int RESERVA_PORCENTAJE = 20;

    public enum Semaforo {
        VERDE("Stock suficiente", "El banco cubre la demanda con reserva de seguridad."),
        AMARILLO("Stock justo", "Se cubre la demanda, pero sin la reserva del " + RESERVA_PORCENTAJE + " %."),
        ROJO("Stock insuficiente", "Faltan unidades: hay que convocar donantes.");

        public final String titulo;
        public final String detalle;

        Semaforo(String titulo, String detalle) {
            this.titulo = titulo;
            this.detalle = detalle;
        }
    }

    /** Un nodo del árbol de llamadas: una invocación de fibonacci(semana). */
    public static final class Nodo {

        private final int semana;
        private final int orden;
        private final List<Nodo> hijos = new ArrayList<>(2);
        private long valor;

        private Nodo(int semana, int orden) {
            this.semana = semana;
            this.orden = orden;
        }

        public int getSemana() {
            return semana;
        }

        /** Número de la llamada en el orden en que se ejecutó (empieza en 1). */
        public int getOrden() {
            return orden;
        }

        public long getValor() {
            return valor;
        }

        public List<Nodo> getHijos() {
            return Collections.unmodifiableList(hijos);
        }

        public boolean esCasoBase() {
            return hijos.isEmpty();
        }
    }

    public record Resultado(int semana, long unidades, Nodo arbol, int llamadas, int casosBase, int profundidad) {
    }

    private DemandaSangre() {
    }

    public static Resultado calcular(int semana) {
        if (semana < SEMANA_MINIMA || semana > SEMANA_MAXIMA) {
            throw new IllegalArgumentException(
                    "La semana debe estar entre " + SEMANA_MINIMA + " y " + SEMANA_MAXIMA);
        }
        Contador contador = new Contador();
        Nodo raiz = new Nodo(semana, ++contador.llamadas);
        long unidades = fibonacci(raiz, 1, contador);
        return new Resultado(semana, unidades, raiz, contador.llamadas, contador.casosBase, contador.profundidad);
    }

    /** Fibonacci recursivo: D(n) = D(n-1) + D(n-2), registrando cada llamada en el árbol. */
    private static long fibonacci(Nodo nodo, int nivel, Contador contador) {
        contador.profundidad = Math.max(contador.profundidad, nivel);
        int n = nodo.semana;
        if (n == 1 || n == 2) {
            contador.casosBase++;
            nodo.valor = (n == 1) ? DEMANDA_SEMANA_1 : DEMANDA_SEMANA_2;
            return nodo.valor;
        }
        Nodo previa = new Nodo(n - 1, ++contador.llamadas);
        nodo.hijos.add(previa);
        long d1 = fibonacci(previa, nivel + 1, contador);

        Nodo antepenultima = new Nodo(n - 2, ++contador.llamadas);
        nodo.hijos.add(antepenultima);
        long d2 = fibonacci(antepenultima, nivel + 1, contador);

        nodo.valor = d1 + d2;
        return nodo.valor;
    }

    public static Semaforo evaluarStock(long requeridas, long stock) {
        if (stock * 100 >= requeridas * (100L + RESERVA_PORCENTAJE)) {
            return Semaforo.VERDE;
        }
        if (stock >= requeridas) {
            return Semaforo.AMARILLO;
        }
        return Semaforo.ROJO;
    }

    private static final class Contador {
        int llamadas;
        int casosBase;
        int profundidad;
    }
}
