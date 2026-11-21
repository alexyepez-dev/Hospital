package Models;

/**
 * Modelo de resultados de la Prueba de Escritorio en Búsqueda.
 * @author CRACKS
 */
public class PruebaEscritorioModel {

    private final int valorBuscado;
    private final int posicion;
    private final String traza;

    public PruebaEscritorioModel(int valorBuscado, int posicion, String traza) {
        this.valorBuscado = valorBuscado;
        this.posicion = posicion;
        this.traza = traza;
    }

    public int getValorBuscado() { return valorBuscado; }
    public int getPosicion() { return posicion; }
    public String getTraza() { return traza; }
}
