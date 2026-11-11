package Models;

/**
 * Modelo de resultados del módulo Recursividad.
 * @author CRACKS
 */
public class RecursividadModel {

    private final int n;
    private final long resultado;
    private final String traza;

    public RecursividadModel(int n, long resultado, String traza) {
        this.n = n;
        this.resultado = resultado;
        this.traza = traza;
    }

    public int getN() { return n; }
    public long getResultado() { return resultado; }
    public String getTraza() { return traza; }
}