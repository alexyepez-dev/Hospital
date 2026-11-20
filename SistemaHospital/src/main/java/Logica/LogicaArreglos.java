package Logica;

import java.text.Normalizer;

/**
 * =========================================================================
 * UNIVERSIDAD DE GUAYAQUIL - FACULTAD DE CIENCIAS MATEMÁTICAS Y FÍSICAS
 * Carrera de Ingeniería en Sistemas / Software
 * Cátedra: Estructura de Datos
 * Módulo: Lógica de Arreglos Unidimensionales (Vectores Estáticos)
 * =========================================================================
 * 
 * Clase encargada de la gestión de un arreglo unidimensional con límite estático.
 * Aplica los conceptos fundamentales de la materia:
 *  - Dimensión estática (capacidad máxima MAX).
 *  - Control de frontera con contador 'cantidadActual'.
 *  - Inserción secuencial al final: O(1).
 *  - Eliminación por índice con corrimiento a la izquierda (Shift Left): O(n).
 *  - Modificación por acceso directo: O(1).
 *  - Búsqueda secuencial lineal insensible a mayúsculas y tildes (Normalizer): O(n).
 * 
 * @author Docente / Desarrollador Senior UG
 * @version 1.1
 */
public class LogicaArreglos {

    /**
     * Capacidad estática máxima del arreglo unidimensional (MAX).
     */
    public static final int MAX = 10;

    /**
     * Arreglo estático de elementos (representa registros hospitalarios / valores).
     */
    private final String[] arreglo;

    /**
     * Contador de elementos válidos actualmente almacenados en el arreglo.
     * Invariante de estructura: 0 <= cantidadActual <= MAX.
     */
    private int cantidadActual;

    /**
     * Constructor por defecto.
     * Inicializa el arreglo con capacidad MAX y cantidadActual en 0.
     */
    public LogicaArreglos() {
        this.arreglo = new String[MAX];
        this.cantidadActual = 0;
        cargarDatosIniciales();
    }

    /**
     * Carga datos iniciales de prueba representativos del Sistema Hospitalario UG.
     */
    private void cargarDatosIniciales() {
        insertar("PAC-001 (Juan Pérez)");
        insertar("PAC-002 (María Gómez)");
        insertar("PAC-003 (Carlos Ruiz)");
    }

    // =========================================================================
    //                            OPERACIONES BÁSICAS
    // =========================================================================

    /**
     * Inserta un nuevo elemento en la siguiente posición disponible del arreglo.
     * Validación de límites: Verifica que cantidadActual < MAX antes de insertar.
     * Complejidad: O(1)
     * 
     * @param elemento Valor o registro a insertar.
     * @return true si se insertó con éxito; false si el arreglo está lleno.
     */
    public boolean insertar(String elemento) {
        if (estaLleno()) {
            return false; // Desbordamiento (Overflow)
        }
        if (elemento == null || elemento.trim().isEmpty()) {
            return false;
        }

        arreglo[cantidadActual] = elemento.trim();
        cantidadActual++;
        return true;
    }

    /**
     * Inserta un elemento en una posición específica, desplazando los elementos
     * existentes hacia la derecha (Shift Right).
     * Complejidad: O(n)
     * 
     * @param indice Posición de inserción (0 <= indice <= cantidadActual).
     * @param elemento Valor a insertar.
     * @return true si la inserción fue exitosa; false en caso contrario.
     */
    public boolean insertarEn(int indice, String elemento) {
        if (estaLleno() || indice < 0 || indice > cantidadActual) {
            return false;
        }
        if (elemento == null || elemento.trim().isEmpty()) {
            return false;
        }

        // Desplazamiento a la derecha desde cantidadActual hasta indice
        for (int i = cantidadActual; i > indice; i--) {
            arreglo[i] = arreglo[i - 1];
        }

        arreglo[indice] = elemento.trim();
        cantidadActual++;
        return true;
    }

    /**
     * Elimina el elemento ubicado en el índice especificado aplicando
     * el algoritmo de desplazamiento hacia la izquierda (Shift Left).
     * Complejidad: O(n)
     * 
     * Algoritmo académico UG:
     * Para i desde indice hasta cantidadActual - 2:
     *     arreglo[i] = arreglo[i + 1]
     * arreglo[cantidadActual - 1] = null
     * cantidadActual--
     * 
     * @param indice Índice del elemento a eliminar (0 <= indice < cantidadActual).
     * @return El elemento que fue eliminado, o null si el índice es inválido.
     */
    public String eliminar(int indice) {
        if (indice < 0 || indice >= cantidadActual) {
            return null; // Subdesbordamiento o índice fuera de rango
        }

        String elementoEliminado = arreglo[indice];

        // Desplazamiento a la izquierda (Shift Left)
        for (int i = indice; i < cantidadActual - 1; i++) {
            arreglo[i] = arreglo[i + 1];
        }

        // Limpiar la última posición liberada para permitir recolección de basura
        arreglo[cantidadActual - 1] = null;

        // Decrementar el contador de elementos válidos
        cantidadActual--;

        return elementoEliminado;
    }

    /**
     * Busca un elemento por valor y, si existe, lo elimina aplicando Shift Left.
     * 
     * @param valor Elemento a eliminar.
     * @return Índice donde se encontraba el elemento eliminado, o -1 si no existe.
     */
    public int eliminarPorValor(String valor) {
        int pos = buscar(valor);
        if (pos != -1) {
            eliminar(pos);
            return pos;
        }
        return -1;
    }

    /**
     * Modifica el valor del elemento ubicado en el índice especificado.
     * Complejidad: O(1)
     * 
     * @param indice Índice a modificar (0 <= indice < cantidadActual).
     * @param nuevoValor Nuevo dato para esa posición.
     * @return true si la modificación se realizó; false si el índice no es válido.
     */
    public boolean modificar(int indice, String nuevoValor) {
        if (indice < 0 || indice >= cantidadActual) {
            return false;
        }
        if (nuevoValor == null || nuevoValor.trim().isEmpty()) {
            return false;
        }

        arreglo[indice] = nuevoValor.trim();
        return true;
    }

    /**
     * Normaliza una cadena de texto eliminando tildes/marcas diacríticas y
     * convirtiéndola a minúsculas mediante java.text.Normalizer.
     * 
     * Permite que "María" -> "maria", "JUAN" -> "juan", "PÉREZ" -> "perez".
     * 
     * @param texto Cadena original a normalizar.
     * @return Cadena en minúsculas y sin acentos ni diacríticos.
     */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        // Descomposición canónica (NFD): separa las letras de sus marcas de acento
        String descompuesto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        // Remueve todos los signos diacríticos (categoría regex \p{M})
        String sinTildes = descompuesto.replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().trim();
    }

    /**
     * Realiza una búsqueda secuencial (lineal) flexible sobre el arreglo.
     * Es totalmente insensible a mayúsculas, minúsculas y tildes mediante
     * la normalización con java.text.Normalizer.
     * Complejidad: O(n)
     * 
     * @param valor Dato o criterio a buscar.
     * @return Índice de la primera coincidencia (0 .. cantidadActual - 1) o -1 si no existe.
     */
    public int buscar(String valor) {
        if (valor == null || estaVacio()) {
            return -1;
        }

        String criterioNormalizado = normalizar(valor);
        if (criterioNormalizado.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < cantidadActual; i++) {
            if (arreglo[i] != null) {
                String elementoNormalizado = normalizar(arreglo[i]);
                if (elementoNormalizado.contains(criterioNormalizado)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Obtiene el elemento ubicado en la posición indicada.
     * Complejidad: O(1)
     * 
     * @param indice Posición requerida.
     * @return El valor en el arreglo, o null si está fuera de rango.
     */
    public String getElemento(int indice) {
        if (indice < 0 || indice >= cantidadActual) {
            return null;
        }
        return arreglo[indice];
    }

    /**
     * Limpia por completo la estructura de datos.
     */
    public void vaciar() {
        for (int i = 0; i < cantidadActual; i++) {
            arreglo[i] = null;
        }
        cantidadActual = 0;
    }

    // =========================================================================
    //                        CONSULTAS DE ESTADO Y LÍMITES
    // =========================================================================

    /**
     * Determina si el arreglo ha alcanzado su límite estático máximo.
     * @return true si cantidadActual == MAX.
     */
    public boolean estaLleno() {
        return cantidadActual >= MAX;
    }

    /**
     * Determina si el arreglo no contiene elementos válidos.
     * @return true si cantidadActual == 0.
     */
    public boolean estaVacio() {
        return cantidadActual == 0;
    }

    /**
     * Retorna la cantidad de elementos almacenados actualmente.
     * @return Valor de cantidadActual.
     */
    public int getCantidadActual() {
        return cantidadActual;
    }

    /**
     * Retorna la capacidad estática máxima del arreglo.
     * @return Constante MAX.
     */
    public int getMax() {
        return MAX;
    }

    /**
     * Retorna los espacios disponibles restantes en el arreglo.
     * @return MAX - cantidadActual.
     */
    public int getEspaciosDisponibles() {
        return MAX - cantidadActual;
    }

    /**
     * Retorna una representación en texto del estado actual del arreglo en memoria.
     * Ejemplo: "[PAC-001, PAC-002, PAC-003, null, null, ...]"
     * @return Cadena que representa el vector estático completo.
     */
    public String getEstadoMemoria() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < MAX; i++) {
            if (i < cantidadActual && arreglo[i] != null) {
                sb.append(arreglo[i]);
            } else {
                sb.append("null");
            }
            if (i < MAX - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Retorna una copia de todos los slots del arreglo (tamaño estático MAX).
     * @return Vector de tamaño MAX con elementos y posiciones null.
     */
    public String[] getArregloCopiaCompleta() {
        String[] copia = new String[MAX];
        System.arraycopy(arreglo, 0, copia, 0, MAX);
        return copia;
    }
}
