package Vistas;

import Logica.LogicaArreglos;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * =========================================================================
 * UNIVERSIDAD DE GUAYAQUIL - FACULTAD DE CIENCIAS MATEMÁTICAS Y FÍSICAS
 * Cátedra: Estructura de Datos
 * Módulo: Vista Gráfica de Arreglos Unidimensionales (NetBeans Swing)
 * =========================================================================
 * 
 * Conecta la lógica de negocio ({@link LogicaArreglos}) con la interfaz gráfica,
 * respetando estrictamente el diseño institucional, paleta de colores y estilos manuales.
 * 
 * Mejoras aplicadas:
 *  1. Persistencia y estado global: Instancia 'logica' declarada a nivel de clase.
 *  2. Limpieza de GUI: Eliminación de botones obsoletos en la zona inferior.
 *  3. Panel lateral con opciones principales: Insertar, Eliminar, Modificar, Buscar, Salir.
 *  4. JTable dinámico ocupando el contenedor central con DefaultTableModel y estilos intactos.
 *  5. Método actualizarTabla() tras cada operación (Insertar, Eliminar, Modificar).
 *  6. Búsqueda flexible insensible a mayúsculas y tildes mediante Normalizer y .toLowerCase().
 *  7. Salir controlado con confirmación.
 * 
 * @author Docente / Desarrollador Senior UG
 * @version 1.1
 */
public class VistaArreglos extends VistaModulo {

    // =========================================================================
    //                        ESTADO GLOBAL Y COMPONENTES
    // =========================================================================
    // Instancia única y persistente a nivel de clase para evitar pérdida de datos
    private final LogicaArreglos logica;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JPanel panelPrincipal;

    // Indicadores visuales de capacidad y estado
    private JLabel lblCapacidad;
    private JLabel lblCantidad;
    private JLabel lblDisponibles;
    private JLabel lblEstadoBadge;
    private JLabel lblConsolaOperacion;

    // Bandera para evitar disparar JOptionPane durante la construcción inicial
    private boolean vistaConstruida = false;

    /**
     * Constructor principal de la vista de Arreglos.
     * @param usuario Nombre del usuario logueado en el sistema.
     */
    public VistaArreglos(String usuario) {
        super(usuario, "Arreglos", new Color(52, 152, 219));
        // Inicialización única de la estructura de datos (persistencia en memoria)
        this.logica = new LogicaArreglos();

        // Inicializar componentes gráficos de la tabla y paneles
        inicializarComponentesArreglos();

        // Invoca el inicio del ciclo de vida del módulo base
        initModulo();

        // Poblamos inicialmente la tabla con los datos existentes
        actualizarTabla();

        // Marcamos la vista como completamente construida
        this.vistaConstruida = true;
    }

    // =========================================================================
    //               CONFIGURACIÓN DEL MENÚ LATERAL (SIDEBAR)
    // =========================================================================

    @Override
    protected List<String> getOpcionesMenu() {
        return Arrays.asList("Insertar", "Eliminar", "Modificar", "Buscar", "Salir");
    }

    /**
     * Devuelve el contenedor central para el índice seleccionado.
     * Mantiene persistente el panel con la JTable para una visualización continua.
     */
    @Override
    protected JPanel crearPanelOpcion(int indice) {
        if (panelPrincipal == null) {
            inicializarComponentesArreglos();
        }
        return panelPrincipal;
    }

    /**
     * Sobrescribe la selección de opciones del sidebar para despachar directamente
     * a las operaciones de la Estructura de Datos requeridas por la cátedra.
     */
    @Override
    protected void seleccionarOpcion(int index) {
        // En la inicialización se monta la estructura visual sin solicitar diálogos
        if (!vistaConstruida) {
            super.seleccionarOpcion(index);
            return;
        }

        // Ilumina el botón activo en el sidebar
        actualizarBotonSidebar(index);

        // Despacho del evento correspondiente al botón pulsado
        switch (index) {
            case 0:
                accionInsertar();
                break;
            case 1:
                accionEliminar();
                break;
            case 2:
                accionModificar();
                break;
            case 3:
                accionBuscar();
                break;
            case 4:
                accionSalir();
                break;
            default:
                break;
        }
    }

    /**
     * Actualiza el resaltado visual de los botones del menú lateral.
     */
    private void actualizarBotonSidebar(int index) {
        this.opcionSeleccionada = index;
        for (int i = 0; i < botonesMenu.size(); i++) {
            JButton btn = botonesMenu.get(i);
            try {
                java.lang.reflect.Method m = btn.getClass().getDeclaredMethod("setActivo", boolean.class);
                m.setAccessible(true);
                m.invoke(btn, i == index);
            } catch (Exception ignored) {}
        }
    }

    // =========================================================================
    //                     INICIALIZACIÓN DE LA VISTA CENTRAL
    // =========================================================================

    private void inicializarComponentesArreglos() {
        panelPrincipal = new JPanel(new BorderLayout(0, 16));
        panelPrincipal.setOpaque(false);

        // 1. Encabezado con título, subtítulo manual y métricas de capacidad
        JPanel panelNorte = new JPanel();
        panelNorte.setOpaque(false);
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));

        JLabel titulo = crearTitulo("Estructura de Datos: Arreglos Unidimensionales");
        JLabel subtitulo = crearSubtitulo(
                "Gestión de vectores en memoria estática MAX = " + LogicaArreglos.MAX );

        panelNorte.add(titulo);
        panelNorte.add(Box.createRigidArea(new Dimension(0, 6)));
        panelNorte.add(subtitulo);
        panelNorte.add(Box.createRigidArea(new Dimension(0, 12)));

        // Tarjetas informativas de capacidad (Badges)
        panelNorte.add(crearPanelMetricas());

        // 2. Componente de Tabla Dinámica (JTable + DefaultTableModel) que ocupa el espacio central
        JPanel panelCentro = crearPanelTabla();

        // 3. Barra inferior limpia (únicamente con la consola/estado, sin botones redundantes)
        JPanel panelSur = crearPanelInferior();

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * Construye las tarjetas con las métricas de memoria del arreglo.
     */
    private JPanel crearPanelMetricas() {
        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        contenedor.setOpaque(false);

        lblCapacidad = crearBadge("Capacidad (MAX)", String.valueOf(LogicaArreglos.MAX), new Color(41, 128, 185));
        lblCantidad = crearBadge("Cantidad Actual", "0", new Color(39, 174, 96));
        lblDisponibles = crearBadge("Espacios Libres", String.valueOf(LogicaArreglos.MAX), new Color(142, 68, 173));

        lblEstadoBadge = new JLabel(" DISPONIBLE ");
        lblEstadoBadge.setFont(montserratBold.deriveFont(Font.BOLD, 11f));
        lblEstadoBadge.setForeground(BLANCO);
        lblEstadoBadge.setBackground(new Color(39, 174, 96));
        lblEstadoBadge.setOpaque(true);
        lblEstadoBadge.setBorder(new EmptyBorder(6, 12, 6, 12));

        contenedor.add(lblCapacidad);
        contenedor.add(lblCantidad);
        contenedor.add(lblDisponibles);
        contenedor.add(lblEstadoBadge);

        return contenedor;
    }

    private JLabel crearBadge(String etiqueta, String valor, Color colorFondo) {
        JLabel badge = new JLabel(etiqueta + ": " + valor);
        badge.setFont(montserratBold.deriveFont(Font.BOLD, 11f));
        badge.setForeground(BLANCO);
        badge.setBackground(colorFondo);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));
        return badge;
    }

    /**
     * Configura el componente JTable con el DefaultTableModel y diseño personalizado intacto.
     */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        // Modelo de tabla con columnas [Indice, Valor / Elemento] respetando la personalización manual
        String[] columnas = {"Indice", "Valor / Elemento"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Estructura de solo lectura directa para salvaguardar la lógica
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(34);
        tabla.setFont(montserratRegular.deriveFont(Font.PLAIN, 13f));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(new Color(52, 152, 219));
        tabla.setSelectionForeground(BLANCO);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(230, 235, 245));
        tabla.setFillsViewportHeight(true);

        // Estilo del encabezado personalizado por el usuario
        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        header.setFont(montserratBold.deriveFont(Font.BOLD, 12f));
        header.setBackground(AZUL_OSCURO);
        header.setForeground(new Color(0, 0, 0)); // Texto en negro según ajuste manual
        header.setReorderingAllowed(false);

        // Renderizador de celdas centrado y con colores alternos
        DefaultTableCellRenderer renderizadorIndice = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(montserratBold.deriveFont(Font.BOLD, 13f));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BLANCO : new Color(248, 250, 253));
                    c.setForeground(AZUL_MEDIO);
                }
                return c;
            }
        };

        DefaultTableCellRenderer renderizadorValor = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BLANCO : new Color(248, 250, 253));
                    c.setForeground(new Color(30, 40, 60));
                }
                return c;
            }
        };

        // Ajustar anchos y renderizadores
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(0).setMaxWidth(130);
        tabla.getColumnModel().getColumn(0).setCellRenderer(renderizadorIndice);

        tabla.getColumnModel().getColumn(1).setPreferredWidth(450);
        tabla.getColumnModel().getColumn(1).setCellRenderer(renderizadorValor);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(new Color(215, 225, 238), 1, true));
        scroll.getViewport().setBackground(BLANCO);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Construye la zona inferior limpia: se removieron los botones obsoletos redundantes
     * manteniendo únicamente el panel de estado/consola informativa.
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Consola de traza pedagógica (retroalimentación de algoritmos UG)
        JPanel panelConsola = new JPanel(new BorderLayout(8, 0));
        panelConsola.setOpaque(true);
        panelConsola.setBackground(new Color(245, 248, 252));
        panelConsola.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 225, 242), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        lblConsolaOperacion = new JLabel("Sistema listo. Selecciona una opción del menú lateral.");
        lblConsolaOperacion.setFont(montserratRegular.deriveFont(Font.PLAIN, 12f));
        lblConsolaOperacion.setForeground(new Color(40, 60, 90));

        panelConsola.add(lblConsolaOperacion, BorderLayout.CENTER);
        panel.add(panelConsola, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    //                 MÉTODO AUXILIAR ACTUALIZARTABLA()
    // =========================================================================

    /**
     * Actualiza la representación visual de la estructura de datos:
     *  1. Limpia por completo el DefaultTableModel con setRowCount(0).
     *  2. Recorre el arreglo de 0 hasta cantidadActual - 1 poblando las filas.
     *  3. Actualiza los indicadores numéricos de capacidad y estado de memoria.
     *  4. Se invoca tras cada operación (Insertar, Eliminar, Modificar) para reflejar cambios en tiempo real.
     */
    public void actualizarTabla() {
        if (modeloTabla == null || logica == null) {
            return;
        }

        // 1. Limpieza de filas previas
        modeloTabla.setRowCount(0);

        // 2. Repoblación secuencial según cantidadActual
        for (int i = 0; i < logica.getCantidadActual(); i++) {
            modeloTabla.addRow(new Object[]{
                i,                      // Columna 0: Indice
                logica.getElemento(i)   // Columna 1: Valor / Elemento
            });
        }

        // 3. Sincronización de badges y estados de capacidad
        actualizarIndicadores();
    }

    private void actualizarIndicadores() {
        if (lblCantidad == null || lblDisponibles == null || lblEstadoBadge == null) {
            return;
        }

        int actual = logica.getCantidadActual();
        int max = logica.getMax();
        int libres = logica.getEspaciosDisponibles();

        lblCantidad.setText("Cantidad Actual: " + actual);
        lblDisponibles.setText("Espacios Libres: " + libres);

        if (logica.estaLleno()) {
            lblEstadoBadge.setText(" ARREGLO LLENO (MAX) ");
            lblEstadoBadge.setBackground(new Color(231, 76, 60)); // Rojo alerta
        } else if (logica.estaVacio()) {
            lblEstadoBadge.setText(" ARREGLO VACÍO ");
            lblEstadoBadge.setBackground(new Color(230, 126, 34)); // Naranja advertencia
        } else {
            lblEstadoBadge.setText(" DISPONIBLE (" + libres + " LIBRES) ");
            lblEstadoBadge.setBackground(new Color(39, 174, 96)); // Verde óptimo
        }
    }

    // =========================================================================
    //                     EVENTOS DEL MENÚ LATERAL Y ACCIONES
    // =========================================================================

    /**
     * Operación INSERTAR:
     * Valida límites de memoria (overflow), solicita el nuevo dato mediante JOptionPane,
     * almacena en el arreglo global e invoca actualizarTabla().
     */
    public void accionInsertar() {
        if (logica.estaLleno()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ Desbordamiento de Arreglo (Overflow):\n"
                    + "Se ha alcanzado la capacidad máxima estática (" + logica.getMax() + " elementos).\n"
                    + "Para insertar un nuevo registro, primero debe eliminar uno existente.",
                    "Límite Máximo Alcanzado",
                    JOptionPane.WARNING_MESSAGE);
            mostrarMensajeConsola("⚠ Operación rechazada: El arreglo está lleno (cantidadActual = " + logica.getCantidadActual() + ").");
            return;
        }

        String nuevoValor = JOptionPane.showInputDialog(this,
                "Ingrese el nuevo valor o registro para el arreglo:\n"
                + "(Se almacenará en el índice [" + logica.getCantidadActual() + "])",
                "Insertar Elemento",
                JOptionPane.QUESTION_MESSAGE);

        if (nuevoValor == null) {
            // Usuario canceló la operación
            return;
        }

        nuevoValor = nuevoValor.trim();
        if (nuevoValor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ Debe ingresar un valor válido (no se admiten textos vacíos).",
                    "Dato Inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int posInsertada = logica.getCantidadActual();
        boolean exito = logica.insertar(nuevoValor);

        if (exito) {
            actualizarTabla();
            // Seleccionar y enfocar la nueva fila agregada
            tabla.setRowSelectionInterval(posInsertada, posInsertada);
            tabla.scrollRectToVisible(tabla.getCellRect(posInsertada, 0, true));

            mostrarMensajeConsola("✅ Elemento '" + nuevoValor + "' insertado en el índice [" 
                    + posInsertada + "]. Nueva cantidadActual: " + logica.getCantidadActual() + ".");
        }
    }

    /**
     * Operación ELIMINAR:
     * Solicita el índice o elemento, aplica el algoritmo de desplazamiento a la izquierda (shift-left),
     * decrementa cantidadActual y llama a actualizarTabla().
     */
    public void accionEliminar() {
        if (logica.estaVacio()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ Subdesbordamiento de Arreglo (Underflow):\n"
                    + "El arreglo está completamente vacío. No hay elementos para eliminar.",
                    "Arreglo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            mostrarMensajeConsola("⚠ Operación rechazada: El arreglo está vacío (cantidadActual = 0).");
            return;
        }

        int filaSeleccionada = tabla.getSelectedRow();
        String valorPorDefecto = (filaSeleccionada != -1) ? String.valueOf(filaSeleccionada) : "0";

        String entrada = JOptionPane.showInputDialog(this,
                "Ingrese el ÍNDICE (0 a " + (logica.getCantidadActual() - 1) + ") o el VALOR del elemento a eliminar:",
                valorPorDefecto);

        if (entrada == null) {
            return; // Cancelado por el usuario
        }

        entrada = entrada.trim();
        if (entrada.isEmpty()) {
            return;
        }

        int indiceAEliminar = -1;

        // Verificar si la entrada es un número entero (índice directo)
        try {
            int num = Integer.parseInt(entrada);
            if (num >= 0 && num < logica.getCantidadActual()) {
                indiceAEliminar = num;
            }
        } catch (NumberFormatException ignored) {}

        // Si no fue un índice numérico válido, buscar por coincidencia insensible a tildes/mayúsculas
        if (indiceAEliminar == -1) {
            indiceAEliminar = logica.buscar(entrada);
        }

        if (indiceAEliminar < 0 || indiceAEliminar >= logica.getCantidadActual()) {
            JOptionPane.showMessageDialog(this,
                    "❌ No se encontró ningún elemento coincidente con: \"" + entrada + "\".\n"
                    + "Asegúrese de ingresar un índice válido entre [0 y " + (logica.getCantidadActual() - 1) + "].",
                    "Elemento No Encontrado",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String elementoBorrado = logica.getElemento(indiceAEliminar);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Confirmas eliminar el elemento '" + elementoBorrado + "' del índice [" + indiceAEliminar + "]?\n"
                + "Se aplicará el algoritmo de desplazamiento hacia la izquierda (Shift-Left).",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String eliminado = logica.eliminar(indiceAEliminar);
            actualizarTabla();

            // Mantener selección contextual si aún quedan elementos
            if (logica.getCantidadActual() > 0) {
                int nuevoIndexSeleccion = Math.min(indiceAEliminar, logica.getCantidadActual() - 1);
                tabla.setRowSelectionInterval(nuevoIndexSeleccion, nuevoIndexSeleccion);
            }

            mostrarMensajeConsola("🗑️ Elemento '" + eliminado + "' eliminado del índice [" 
                    + indiceAEliminar + "]. Shift-Left aplicado con éxito. Nueva cantidadActual: " + logica.getCantidadActual() + ".");
        }
    }

    /**
     * Operación MODIFICAR:
     * Permite actualizar el valor del elemento seleccionado en la JTable o del índice indicado.
     * Tras modificar, invoca actualizarTabla().
     */
    public void accionModificar() {
        if (logica.estaVacio()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ El arreglo se encuentra vacío. No hay registros que modificar.",
                    "Arreglo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaSeleccionada = tabla.getSelectedRow();
        int indiceAModificar = -1;

        if (filaSeleccionada != -1) {
            indiceAModificar = filaSeleccionada;
        } else {
            String inputIndice = JOptionPane.showInputDialog(this,
                    "Ingrese el índice del elemento que desea modificar (0 a " + (logica.getCantidadActual() - 1) + "):",
                    "0");
            if (inputIndice == null) return;
            try {
                indiceAModificar = Integer.parseInt(inputIndice.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "⚠ Debe ingresar un índice numérico entero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (indiceAModificar < 0 || indiceAModificar >= logica.getCantidadActual()) {
            JOptionPane.showMessageDialog(this, "⚠ Índice fuera de rango (0 a " + (logica.getCantidadActual() - 1) + ").", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String valorActual = logica.getElemento(indiceAModificar);
        String nuevoValor = JOptionPane.showInputDialog(this,
                "Modificando el índice [" + indiceAModificar + "]:\n"
                + "Valor actual: " + valorActual + "\n\nIngrese el nuevo valor:",
                valorActual);

        if (nuevoValor == null) return;
        nuevoValor = nuevoValor.trim();
        if (nuevoValor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ El nuevo valor no puede estar vacío.", "Dato Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean modificado = logica.modificar(indiceAModificar, nuevoValor);
        if (modificado) {
            actualizarTabla();
            tabla.setRowSelectionInterval(indiceAModificar, indiceAModificar);
            mostrarMensajeConsola("✎ Índice [" + indiceAModificar + "] actualizado: '" 
                    + valorActual + "' ➔ '" + nuevoValor + "'.");
        }
    }

    /**
     * Operación BUSCAR (Insensible a mayúsculas y tildes):
     * Utiliza normalización con java.text.Normalizer y .toLowerCase().
     * Al encontrar el elemento, selecciona automáticamente la fila en la tabla
     * mediante tabla.setRowSelectionInterval(pos, pos).
     */
    public void accionBuscar() {
        if (logica.estaVacio()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ El arreglo está vacío. No hay elementos para buscar.",
                    "Arreglo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String criterio = JOptionPane.showInputDialog(this,
                "Ingrese el valor o texto a buscar en el arreglo:\n(Insensible a mayúsculas y tildes, ej. 'maria' o 'JUAN')",
                "Búsqueda Secuencial",
                JOptionPane.QUESTION_MESSAGE);

        if (criterio == null) return;
        criterio = criterio.trim();
        if (criterio.isEmpty()) return;

        int pos = logica.buscar(criterio);

        if (pos != -1) {
            // Requisito: Resaltar / seleccionar automáticamente la fila encontrada en el JTable
            tabla.setRowSelectionInterval(pos, pos);
            tabla.scrollRectToVisible(tabla.getCellRect(pos, 0, true));

            JOptionPane.showMessageDialog(this,
                    "✅ Elemento encontrado con éxito:\n\n"
                    + "Índice en el arreglo: [" + pos + "]\n"
                    + "Valor registrado: " + logica.getElemento(pos) + "\n\n"
                    + "La fila correspondiente ha sido seleccionada en la tabla dinámica.",
                    "Búsqueda Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            mostrarMensajeConsola("🔍 Coincidencia encontrada: '" + criterio + "' en el índice [" 
                    + pos + "] ('" + logica.getElemento(pos) + "').");
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ El valor \"" + criterio + "\" no se encuentra en el arreglo.",
                    "No Encontrado",
                    JOptionPane.WARNING_MESSAGE);

            mostrarMensajeConsola("🔍 Sin coincidencias para '" + criterio + "'.");
        }
    }

    /**
     * Operación SALIR:
     * Cierra el módulo actual con confirmación institucional y retorna a la vista principal.
     */
    public void accionSalir() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Deseas finalizar y cerrar el módulo de Arreglos?",
                "Salir del Módulo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirmacion == JOptionPane.YES_OPTION) {
            new Opciones(usuarioActual).setVisible(true);
            this.dispose();
        }
    }

    /**
     * Muestra un mensaje en la barra inferior de consola pedagógica.
     */
    private void mostrarMensajeConsola(String mensaje) {
        if (lblConsolaOperacion != null) {
            lblConsolaOperacion.setText(mensaje);
        }
    }

    // =========================================================================
    //                            EJECUCIÓN AUTÓNOMA
    // =========================================================================

    /**
     * Método principal para probar el módulo de manera independiente en NetBeans.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VistaArreglos("Docente UG").setVisible(true);
        });
    }
}
