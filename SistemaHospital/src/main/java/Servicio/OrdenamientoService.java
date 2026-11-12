package Servicio;

import Model.Paciente;
import java.text.Collator;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Servicio con los 4 métodos de ordenamiento: Burbuja, Selección, Inserción y
 * QuickSort.
 *
 * No depende de Swing: recibe una lista de pacientes y un criterio, devuelve la
 * lista ordenada y el registro de cada paso (comparar, intercambiar, pivote,
 * posición final). La vista usa ese registro para animar la tabla.
 */
public final class OrdenamientoService {

    // ------------------------------------------------------------------ tipos

    public enum Algoritmo {
        BURBUJA("Burbuja", "O(n)", "O(n²)", "O(n²)", true),
        SELECCION("Selección", "O(n²)", "O(n²)", "O(n²)", false),
        INSERCION("Inserción", "O(n)", "O(n²)", "O(n²)", true),
        QUICKSORT("QuickSort", "O(n log n)", "O(n log n)", "O(n²)", false);

        public final String nombre;
        public final String mejorCaso;
        public final String promedio;
        public final String peorCaso;
        /** Estable = si dos pacientes empatan, conservan su orden original. */
        public final boolean estable;

        Algoritmo(String nombre, String mejorCaso, String promedio, String peorCaso, boolean estable) {
            this.nombre = nombre;
            this.mejorCaso = mejorCaso;
            this.promedio = promedio;
            this.peorCaso = peorCaso;
            this.estable = estable;
        }
    }

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Collator ALFABETO = Collator.getInstance(Locale.forLanguageTag("es"));

    public enum Criterio {
        PRIORIDAD("Prioridad (triaje)", "Más urgente primero (empate: llegada)",
                Comparator.comparingInt(Paciente::getTriaje).thenComparing(Paciente::getLlegada),
                p -> "triaje " + p.getTriaje() + ", " + p.getLlegada().format(HORA)),
        EDAD("Edad", "De menor a mayor",
                Comparator.comparingInt(Paciente::getEdad),
                p -> p.getEdad() + " años"),
        NOMBRE("Nombre", "Orden alfabético A → Z",
                Comparator.comparing(Paciente::getNombre, ALFABETO),
                Paciente::getNombre),
        LLEGADA("Hora de llegada", "El que llegó primero va primero",
                Comparator.comparing(Paciente::getLlegada),
                p -> p.getLlegada().format(HORA));

        public final String titulo;
        public final String descripcion;
        /** Siempre desempata por código, para que el orden final sea único. */
        public final Comparator<Paciente> comparador;
        /** Texto del valor que se compara, para explicar cada paso. */
        public final Function<Paciente, String> valor;

        Criterio(String titulo, String descripcion, Comparator<Paciente> comparador, Function<Paciente, String> valor) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.comparador = comparador.thenComparing(Paciente::getCodigo);
            this.valor = valor;
        }
    }

    public enum TipoPaso { COMPARAR, INTERCAMBIAR, PIVOTE, ORDENADO }

    /** Un paso del algoritmo sobre las posiciones i y j de la lista (j = -1 si no aplica). */
    public record Paso(TipoPaso tipo, int i, int j) {
    }

    public record Resultado(Algoritmo algoritmo, List<Paciente> ordenados, List<Paso> pasos,
            long comparaciones, long intercambios, long nanos) {

        /** Microsegundos (µs) si dura menos de 1 ms, para que los tiempos pequeños se puedan comparar. */
        public String tiempoFormateado() {
            if (nanos < 1_000_000) {
                return String.format(Locale.US, "%.1f µs", nanos / 1_000.0);
            }
            return String.format(Locale.US, "%.2f ms", nanos / 1_000_000.0);
        }
    }

    // ------------------------------------------------------------------ API

    /**
     * Ordena una copia de la lista registrando cada paso. El tiempo se mide en
     * una segunda ejecución sin registro, para que no incluya el costo de
     * guardar los pasos.
     */
    public Resultado ordenar(List<Paciente> datos, Algoritmo algoritmo, Criterio criterio) {
        List<Paciente> lista = new ArrayList<>(datos);
        Registro registro = new Registro(true);
        ejecutar(algoritmo, lista, criterio.comparador, registro);
        long nanos = medir(datos, algoritmo, criterio);
        return new Resultado(algoritmo, Collections.unmodifiableList(lista),
                Collections.unmodifiableList(registro.pasos),
                registro.comparaciones, registro.intercambios, nanos);
    }

    /**
     * Inserción de un solo paciente en una lista que ya está ordenada: se agrega
     * al final y retrocede (como el bucle interno de Inserción) hasta su puesto.
     * No reordena el resto de la lista.
     */
    public Resultado insertar(List<Paciente> ordenada, Paciente nuevo, Criterio criterio) {
        List<Paciente> lista = new ArrayList<>(ordenada);
        lista.add(nuevo);
        Registro registro = new Registro(true);
        retroceder(lista, lista.size() - 1, criterio.comparador, registro);

        long mejor = Long.MAX_VALUE;
        for (int k = 0; k < 7; k++) {
            List<Paciente> copia = new ArrayList<>(ordenada);
            copia.add(nuevo);
            long inicio = System.nanoTime();
            retroceder(copia, copia.size() - 1, criterio.comparador, new Registro(false));
            mejor = Math.min(mejor, System.nanoTime() - inicio);
        }
        return new Resultado(Algoritmo.INSERCION, Collections.unmodifiableList(lista),
                Collections.unmodifiableList(registro.pasos),
                registro.comparaciones, registro.intercambios, mejor);
    }

    /** Hace retroceder el elemento de la posición j hasta su lugar y lo marca como ubicado. */
    private static void retroceder(List<Paciente> a, int j, Comparator<Paciente> cmp, Registro r) {
        while (j > 0 && r.comparar(a, j - 1, j, cmp) > 0) {
            r.intercambiar(a, j - 1, j);
            j--;
        }
        r.ordenado(j);
    }

    /** Mejor de varias corridas: la primera paga el arranque de la JVM y falsea el tiempo. */
    private long medir(List<Paciente> datos, Algoritmo algoritmo, Criterio criterio) {
        long mejor = Long.MAX_VALUE;
        for (int k = 0; k < 7; k++) {
            List<Paciente> lista = new ArrayList<>(datos);
            Registro sinPasos = new Registro(false);
            long inicio = System.nanoTime();
            ejecutar(algoritmo, lista, criterio.comparador, sinPasos);
            mejor = Math.min(mejor, System.nanoTime() - inicio);
        }
        return mejor;
    }

    private static void ejecutar(Algoritmo a, List<Paciente> lista, Comparator<Paciente> cmp, Registro r) {
        switch (a) {
            case BURBUJA -> burbuja(lista, cmp, r);
            case SELECCION -> seleccion(lista, cmp, r);
            case INSERCION -> insercion(lista, cmp, r);
            case QUICKSORT -> quickSort(lista, cmp, r);
        }
    }

    // ------------------------------------------------------------ algoritmos

    /** Burbuja: compara vecinos y los intercambia si están al revés; el mayor "sube" al final. */
    static void burbuja(List<Paciente> a, Comparator<Paciente> cmp, Registro r) {
        int n = a.size();
        for (int i = 0; i < n - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (r.comparar(a, j, j + 1, cmp) > 0) {
                    r.intercambiar(a, j, j + 1);
                    huboCambio = true;
                }
            }
            r.ordenado(n - 1 - i);
            if (!huboCambio) {          // ya estaba ordenada: termina antes
                for (int k = 0; k < n - 1 - i; k++) {
                    r.ordenado(k);
                }
                return;
            }
        }
        if (n > 0) {
            r.ordenado(0);
        }
    }

    /** Selección: busca el menor de la parte desordenada y lo coloca al inicio. */
    static void seleccion(List<Paciente> a, Comparator<Paciente> cmp, Registro r) {
        int n = a.size();
        for (int i = 0; i < n - 1; i++) {
            int menor = i;
            r.pivote(menor);
            for (int j = i + 1; j < n; j++) {
                if (r.comparar(a, j, menor, cmp) < 0) {
                    menor = j;
                    r.pivote(menor);
                }
            }
            if (menor != i) {
                r.intercambiar(a, i, menor);
            }
            r.ordenado(i);
        }
        if (n > 0) {
            r.ordenado(n - 1);
        }
    }

    /**
     * Inserción: toma cada elemento y lo hace retroceder (intercambiando con su
     * vecino) hasta su lugar dentro de la parte ya ordenada.
     */
    static void insercion(List<Paciente> a, Comparator<Paciente> cmp, Registro r) {
        int n = a.size();
        for (int i = 1; i < n; i++) {
            int j = i;
            while (j > 0 && r.comparar(a, j - 1, j, cmp) > 0) {
                r.intercambiar(a, j - 1, j);
                j--;
            }
        }
        for (int k = 0; k < n; k++) {
            r.ordenado(k);
        }
    }

    /** QuickSort (partición de Lomuto): el último elemento es el pivote. */
    static void quickSort(List<Paciente> a, Comparator<Paciente> cmp, Registro r) {
        quickSort(a, 0, a.size() - 1, cmp, r);
    }

    private static void quickSort(List<Paciente> a, int inicio, int fin, Comparator<Paciente> cmp, Registro r) {
        if (inicio < fin) {
            int p = particion(a, inicio, fin, cmp, r);
            quickSort(a, inicio, p - 1, cmp, r);
            quickSort(a, p + 1, fin, cmp, r);
        } else if (inicio == fin) {
            r.ordenado(inicio);
        }
    }

    private static int particion(List<Paciente> a, int inicio, int fin, Comparator<Paciente> cmp, Registro r) {
        r.pivote(fin);
        int i = inicio;
        for (int j = inicio; j < fin; j++) {
            if (r.comparar(a, j, fin, cmp) < 0) {
                if (i != j) {
                    r.intercambiar(a, i, j);
                }
                i++;
            }
        }
        if (i != fin) {
            r.intercambiar(a, i, fin);
        }
        r.ordenado(i);
        return i;
    }

    // ---------------------------------------------------------------- registro

    /** Cuenta comparaciones e intercambios y, si se pide, guarda cada paso. */
    static final class Registro {

        final List<Paso> pasos = new ArrayList<>();
        private final boolean guardar;
        long comparaciones;
        long intercambios;

        Registro(boolean guardar) {
            this.guardar = guardar;
        }

        int comparar(List<Paciente> a, int i, int j, Comparator<Paciente> cmp) {
            comparaciones++;
            if (guardar) {
                pasos.add(new Paso(TipoPaso.COMPARAR, i, j));
            }
            return cmp.compare(a.get(i), a.get(j));
        }

        void intercambiar(List<Paciente> a, int i, int j) {
            intercambios++;
            Collections.swap(a, i, j);
            if (guardar) {
                pasos.add(new Paso(TipoPaso.INTERCAMBIAR, i, j));
            }
        }

        void pivote(int i) {
            if (guardar) {
                pasos.add(new Paso(TipoPaso.PIVOTE, i, -1));
            }
        }

        void ordenado(int i) {
            if (guardar) {
                pasos.add(new Paso(TipoPaso.ORDENADO, i, -1));
            }
        }
    }
}
