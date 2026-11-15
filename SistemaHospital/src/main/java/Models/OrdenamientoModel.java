package Models;

/**
 * Modelo de resultados del módulo Ordenamiento (Burbuja y Selección).
 * @author CRACKS
 */
public class OrdenamientoModel {

    private final int[] original;
    private final int[] ordenado;
    private final int comparaciones;
    private final int intercambios;
    private final String traza;

    public OrdenamientoModel(int[] original, int[] ordenado, int comparaciones, int intercambios, String traza) {
        this.original = original;
        this.ordenado = ordenado;
        this.comparaciones = comparaciones;
        this.intercambios = intercambios;
        this.traza = traza;
    }

    public int[] getOriginal() {
        return original;
    }

    public int[] getOrdenado() {
        return ordenado;
    }

    public int getComparaciones() {
        return comparaciones;
    }

    public int getIntercambios() {
        return intercambios;
    }

    public String getTraza() {
        return traza;
    }
}
