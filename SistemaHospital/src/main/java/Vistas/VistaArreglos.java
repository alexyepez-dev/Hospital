package Vistas;

import Logica.LogicaArreglos;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * =========================================================================
 * UNIVERSIDAD DE GUAYAQUIL - FACULTAD DE CIENCIAS MATEMÁTICAS Y FÍSICAS
 * Cátedra: Estructura de Datos
 * Módulo: Vista Gráfica de Arreglos Unidimensionales (NetBeans Swing)
 * =========================================================================

 * @version 1.2
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

    // Contenedor central con CardLayout para alternar entre la Tabla y la Prueba de Escritorio
    private CardLayout cardLayout;
    private JPanel panelCentro;
    private JPanel panelTabla;
    private JPanel panelPruebaEscritorio;

    // Componentes de la Prueba de Escritorio
    private DefaultTableModel modeloPruebaEscritorio;
    private JTable tablaPruebaEscritorio;
    private JLabel lblBadgeOperacion;
    private JLabel lblDetalleTraza;

    // Indicadores visuales de capacidad y estado (Métricas superiores)
    private JLabel lblCapacidad;
    private JLabel lblCantidad;
    private JLabel lblDisponibles;
    private JLabel lblEstadoBadge;

    // Barras inferiores discretas: Caso de uso pedagógico y Consola de operaciones
    private JLabel lblCasoDeUso;
    private JLabel lblConsolaOperacion;

    // Bandera para evitar disparar JOptionPane durante la construcción inicial
    private boolean vistaConstruida = false;

    // =========================================================================
    //              DESCRIPCIONES Y CASOS DE USO DE CADA OPCIÓN
    // =========================================================================
    private final String[] CASOS_DE_USO = {
        "Insertar: Agrega un elemento al final si hay espacio libre (cantidadActual < MAX). Caso de uso: registrar un nuevo paciente.",
        "Eliminar: Quita un elemento y desplaza los siguientes a la izquierda para evitar huecos. Caso de uso: dar de baja o retirar un registro.",
        "Modificar: Actualiza el valor de un elemento existente en el arreglo. Caso de uso: corregir datos de un registro.",
        "Buscar: Localiza un elemento en el arreglo (sin importar mayúsculas o tildes) y resalta su fila. Caso de uso: ubicar rápidamente un paciente.",
        "Prueba de Escritorio: Muestra la traza interna de variables y pasos del algoritmo en el mismo recuadro. Caso de uso: verificar cómo cambia el arreglo en cada operación.",
        "Salir: Cierra la vista o regresa del módulo. Caso de uso: finalizar la sesión actual."
    };

    /**
     * Constructor principal de la vista de Arreglos.
     * @param usuario Nombre del usuario logueado en el sistema.
     */
    public VistaArreglos(String usuario) {
        super(usuario, "Arreglos", new Color(52, 152, 219));
        // Inicialización única de la estructura de datos (persistencia en memoria)
        this.logica = new LogicaArreglos();

        // Inicializar componentes gráficos de la tabla, prueba de escritorio y paneles
        inicializarComponentesArreglos();

        // Invoca el inicio del ciclo de vida del módulo base
        initModulo();

        // Configurar ayudas y tooltips en los botones del sidebar
        configurarBotonesSidebar();

        // Generar traza inicial demostrativa para la Prueba de Escritorio
        generarTrazaInicial();

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
        return Arrays.asList("Insertar", "Eliminar", "Modificar", "Buscar", "Prueba de Escritorio", "Salir");
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

        // Actualiza la barra discreta de caso de uso
        mostrarCasoDeUso(index);

        // Despacho del evento correspondiente al botón pulsado
        switch (index) {
            case 0:
                cardLayout.show(panelCentro, "TABLA");
                accionInsertar();
                break;
            case 1:
                cardLayout.show(panelCentro, "TABLA");
                accionEliminar();
                break;
            case 2:
                cardLayout.show(panelCentro, "TABLA");
                accionModificar();
                break;
            case 3:
                cardLayout.show(panelCentro, "TABLA");
                accionBuscar();
                break;
            case 4:
                // Muestra la Prueba de Escritorio en el mismo recuadro central sin ventanas emergentes
                cardLayout.show(panelCentro, "PRUEBA_ESCRITORIO");
                mostrarMensajeConsola("Visualizando Prueba de Escritorio: seguimiento de variables y traza en tiempo real.");
                break;
            case 5:
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

    /**
     * Configura ayudas sutiles y tooltips no invasivos en los botones del sidebar.
     */
    private void configurarBotonesSidebar() {
        for (int i = 0; i < botonesMenu.size() && i < CASOS_DE_USO.length; i++) {
            final int idx = i;
            JButton btn = botonesMenu.get(i);
            btn.setToolTipText("<html><p width='320'>" + CASOS_DE_USO[idx] + "</p></html>");
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mostrarCasoDeUso(idx);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    mostrarCasoDeUso(opcionSeleccionada);
                }
            });
        }
    }

    /**
     * Muestra la descripción breve y caso de uso en la barra discreta inferior.
     */
    private void mostrarCasoDeUso(int index) {
        if (lblCasoDeUso != null && index >= 0 && index < CASOS_DE_USO.length) {
            lblCasoDeUso.setText("💡 " + CASOS_DE_USO[index]);
        }
    }

    // =========================================================================
    //                     INICIALIZACIÓN DE LA VISTA CENTRAL
    // =========================================================================

    private void inicializarComponentesArreglos() {
        panelPrincipal = new JPanel(new BorderLayout(0, 14));
        panelPrincipal.setOpaque(false);

        // 1. Encabezado con título, subtítulo manual y métricas de capacidad (INTACTOS)
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

        // Tarjetas informativas de capacidad (Badges intactos)
        panelNorte.add(crearPanelMetricas());

        // 2. Contenedor Central Dinámico (CardLayout): Tabla actual y Prueba de Escritorio
        cardLayout = new CardLayout();
        panelCentro = new JPanel(cardLayout);
        panelCentro.setOpaque(false);

        panelTabla = crearPanelTabla();
        panelPruebaEscritorio = crearPanelPruebaEscritorio();

        panelCentro.add(panelTabla, "TABLA");
        panelCentro.add(panelPruebaEscritorio, "PRUEBA_ESCRITORIO");

        // 3. Barra inferior limpia: Caso de uso pedagógico y Consola de operaciones
        JPanel panelSur = crearPanelInferior();

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * Construye las tarjetas con las métricas de memoria del arreglo (INTACTAS).
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

        // Estilo del encabezado personalizado por el usuario (INTACTO)
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

    // =========================================================================
    //                 PANEL DE PRUEBA DE ESCRITORIO (CARD 2)
    // =========================================================================

    /**
     * Construye el panel de Prueba de Escritorio para seguimiento paso a paso
     * en tiempo real de las variables (i, j, pos, cantidadActual, desplazamientos, etc.)
     * dentro del mismo recuadro principal sin ventanas emergentes.
     */
    private JPanel crearPanelPruebaEscritorio() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Barra superior de la prueba de escritorio: Selector de acción y badge de operación
        JPanel panelCabeceraTraza = new JPanel(new BorderLayout(10, 0));
        panelCabeceraTraza.setOpaque(false);

        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBotonesAccion.setOpaque(false);

        JButton btnTrazaUltima = crearBotonPildora("⚡ En Vivo", new Color(52, 152, 219), e -> mostrarUltimaTraza());
        JButton btnTrazaInsertar = crearBotonPildora("Insertar", new Color(39, 174, 96), e -> simularTrazaInsercion());
        JButton btnTrazaEliminar = crearBotonPildora("Eliminar", new Color(192, 57, 43), e -> simularTrazaEliminacion());
        JButton btnTrazaModificar = crearBotonPildora("Modificar", new Color(243, 156, 18), e -> simularTrazaModificacion());
        JButton btnTrazaBuscar = crearBotonPildora("Buscar", new Color(142, 68, 173), e -> simularTrazaBusqueda());

        panelBotonesAccion.add(btnTrazaUltima);
        panelBotonesAccion.add(btnTrazaInsertar);
        panelBotonesAccion.add(btnTrazaEliminar);
        panelBotonesAccion.add(btnTrazaModificar);
        panelBotonesAccion.add(btnTrazaBuscar);

        lblBadgeOperacion = new JLabel("OPERACION");
        lblBadgeOperacion.setFont(montserratBold.deriveFont(Font.BOLD, 11f));
        lblBadgeOperacion.setForeground(BLANCO);
        lblBadgeOperacion.setBackground(new Color(52, 152, 219));
        lblBadgeOperacion.setOpaque(true);
        lblBadgeOperacion.setBorder(new EmptyBorder(6, 12, 6, 12));

        panelCabeceraTraza.add(panelBotonesAccion, BorderLayout.WEST);
        panelCabeceraTraza.add(lblBadgeOperacion, BorderLayout.EAST);

        // Tabla de Prueba de Escritorio (Columnas canónicas de Cátedra UG)
        String[] columnasTraza = {
            "Paso", "Instrucción / Algoritmo", "Variables (i, pos, val)", "cantidadActual", "Arreglo en Memoria", "Detalle de la Operación"
        };

        modeloPruebaEscritorio = new DefaultTableModel(columnasTraza, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPruebaEscritorio = new JTable(modeloPruebaEscritorio);
        tablaPruebaEscritorio.setRowHeight(32);
        tablaPruebaEscritorio.setFont(montserratRegular.deriveFont(Font.PLAIN, 12f));
        tablaPruebaEscritorio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPruebaEscritorio.setSelectionBackground(new Color(52, 152, 219));
        tablaPruebaEscritorio.setSelectionForeground(BLANCO);
        tablaPruebaEscritorio.setShowVerticalLines(true);
        tablaPruebaEscritorio.setShowHorizontalLines(true);
        tablaPruebaEscritorio.setGridColor(new Color(230, 235, 245));
        tablaPruebaEscritorio.setFillsViewportHeight(true);

        JTableHeader headerTraza = tablaPruebaEscritorio.getTableHeader();
        headerTraza.setPreferredSize(new Dimension(0, 34));
        headerTraza.setFont(montserratBold.deriveFont(Font.BOLD, 11f));
        headerTraza.setBackground(AZUL_OSCURO);
        headerTraza.setForeground(new Color(0, 0, 0));
        headerTraza.setReorderingAllowed(false);

        // Renderizadores para la tabla de prueba de escritorio
        DefaultTableCellRenderer renderCentro = new DefaultTableCellRenderer();
        renderCentro.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer renderVariables = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(montserratBold.deriveFont(Font.BOLD, 11f));
                if (!isSelected) {
                    c.setForeground(new Color(20, 60, 130));
                }
                return c;
            }
        };

        DefaultTableCellRenderer renderPaso = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(montserratBold.deriveFont(Font.BOLD, 12f));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BLANCO : new Color(248, 250, 253));
                    c.setForeground(new Color(41, 128, 185));
                }
                return c;
            }
        };

        tablaPruebaEscritorio.getColumnModel().getColumn(0).setPreferredWidth(55);
        tablaPruebaEscritorio.getColumnModel().getColumn(0).setMaxWidth(65);
        tablaPruebaEscritorio.getColumnModel().getColumn(0).setCellRenderer(renderPaso);

        tablaPruebaEscritorio.getColumnModel().getColumn(1).setPreferredWidth(190);
        tablaPruebaEscritorio.getColumnModel().getColumn(2).setPreferredWidth(170);
        tablaPruebaEscritorio.getColumnModel().getColumn(2).setCellRenderer(renderVariables);

        tablaPruebaEscritorio.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaPruebaEscritorio.getColumnModel().getColumn(3).setMaxWidth(120);
        tablaPruebaEscritorio.getColumnModel().getColumn(3).setCellRenderer(renderCentro);

        tablaPruebaEscritorio.getColumnModel().getColumn(4).setPreferredWidth(230);
        tablaPruebaEscritorio.getColumnModel().getColumn(5).setPreferredWidth(300);

        JScrollPane scrollTraza = new JScrollPane(tablaPruebaEscritorio);
        scrollTraza.setBorder(new LineBorder(new Color(215, 225, 238), 1, true));
        scrollTraza.getViewport().setBackground(BLANCO);

        // Pie de explicación de la traza
        JPanel panelPieTraza = new JPanel(new BorderLayout(8, 0));
        panelPieTraza.setOpaque(true);
        panelPieTraza.setBackground(new Color(245, 248, 252));
        panelPieTraza.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 225, 242), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        lblDetalleTraza = new JLabel("Traza activa. Cambios en variables se reflejan en tiempo real.");
        lblDetalleTraza.setFont(montserratRegular.deriveFont(Font.PLAIN, 11f));
        lblDetalleTraza.setForeground(new Color(50, 70, 100));

        panelPieTraza.add(lblDetalleTraza, BorderLayout.CENTER);

        panel.add(panelCabeceraTraza, BorderLayout.NORTH);
        panel.add(scrollTraza, BorderLayout.CENTER);
        panel.add(panelPieTraza, BorderLayout.SOUTH);

        return panel;
    }

    private JButton crearBotonPildora(String texto, Color colorBase, java.awt.event.ActionListener accion) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = hover ? aclarar(colorBase, 25) : colorBase;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(montserratBold.deriveFont(Font.BOLD, 11f));
        btn.setForeground(BLANCO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(texto.length() > 16 ? 160 : 110, 28));
        btn.addActionListener(accion);
        return btn;
    }

    // =========================================================================
    //         CONSTRUCCIÓN DE TRAZAS EN TIEMPO REAL (PRUEBA DE ESCRITORIO)
    // =========================================================================

    private void generarTrazaInicial() {
        generarTrazaInsercion("PAC-003 (Carlos Ruiz)", 2, false);
    }

    private void mostrarUltimaTraza() {
        if (modeloPruebaEscritorio.getRowCount() == 0) {
            generarTrazaInicial();
        }
        mostrarMensajeConsola("Visualizando la traza de la última operación en vivo.");
    }

    private void generarTrazaInsercion(String valor, int posInsertada, boolean enVivo) {
        if (modeloPruebaEscritorio == null) return;
        modeloPruebaEscritorio.setRowCount(0);

        lblBadgeOperacion.setText(" OPERACION ");
        lblBadgeOperacion.setBackground(new Color(39, 174, 96));

        int nPrevio = posInsertada;
        int nNuevo = posInsertada + 1;

        modeloPruebaEscritorio.addRow(new Object[]{
            1, "if (cantidadActual >= MAX)", "cantidadActual=" + nPrevio + ", MAX=" + LogicaArreglos.MAX,
            nPrevio, logica.getEstadoMemoria(), "Validación de Overflow: " + nPrevio + " < 10 (Condición cumplida, hay espacio disponible)"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            2, "pos = cantidadActual", "pos=" + posInsertada + ", valor='" + valor + "'",
            nPrevio, logica.getEstadoMemoria(), "Acceso directo en O(1): se determina el índice de memoria para el nuevo registro"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            3, "arreglo[pos] = elemento", "pos=" + posInsertada + ", arreglo[" + posInsertada + "]='" + valor + "'",
            nPrevio, logica.getEstadoMemoria(), "Asignación de memoria: se almacena la referencia del elemento en el slot"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            4, "cantidadActual++", "cantidadActual=" + nNuevo,
            nPrevio + " ➔ " + nNuevo, logica.getEstadoMemoria(), "Incremento de frontera: el arreglo ahora cuenta con " + nNuevo + " elementos válidos"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            5, "actualizarTabla()", "tabla.setRowSelectionInterval(" + posInsertada + ", " + posInsertada + ")",
            nNuevo, logica.getEstadoMemoria(), "Sincronización gráfica en tiempo real: se agrega la fila y se selecciona en JTable"
        });

        lblDetalleTraza.setText("Traza de Inserción: Inserción al final del vector estático en tiempo constante O(1).");
    }

    private void generarTrazaEliminacion(int indice, String elementoEliminado) {
        if (modeloPruebaEscritorio == null) return;
        modeloPruebaEscritorio.setRowCount(0);

        lblBadgeOperacion.setText(" OPERACION");
        lblBadgeOperacion.setBackground(new Color(192, 57, 43));

        int nPrevio = logica.getCantidadActual() + 1;
        int nNuevo = logica.getCantidadActual();

        modeloPruebaEscritorio.addRow(new Object[]{
            1, "if (indice < 0 || indice >= cantidadActual)", "indice=" + indice + ", cantidadActual=" + nPrevio,
            nPrevio, logica.getEstadoMemoria(), "Validación de fronteras: El índice " + indice + " pertenece al rango válido de elementos"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            2, "elementoEliminado = arreglo[indice]", "indice=" + indice + ", valor='" + elementoEliminado + "'",
            nPrevio, logica.getEstadoMemoria(), "Respaldo del registro a dar de baja antes de iniciar el corrimiento"
        });

        // Simulación paso a paso del bucle Shift-Left
        int paso = 3;
        for (int i = indice; i < nPrevio - 1; i++) {
            String elemSiguiente = logica.getElemento(i);
            modeloPruebaEscritorio.addRow(new Object[]{
                paso++, "arreglo[" + i + "] = arreglo[" + (i + 1) + "]",
                "i=" + i + ", i+1=" + (i + 1) + ", valor='" + elemSiguiente + "'",
                nPrevio, logica.getEstadoMemoria(), "Desplazamiento Shift-Left: posición [" + i + "] toma el valor de la posición [" + (i + 1) + "]"
            });
        }

        modeloPruebaEscritorio.addRow(new Object[]{
            paso++, "arreglo[cantidadActual - 1] = null", "posLibre=" + (nPrevio - 1) + ", arreglo[" + (nPrevio - 1) + "]=null",
            nPrevio, logica.getEstadoMemoria(), "Limpieza de memoria: se elimina la referencia duplicada para el Garbage Collector"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            paso++, "cantidadActual--", "cantidadActual=" + nNuevo,
            nPrevio + " ➔ " + nNuevo, logica.getEstadoMemoria(), "Decremento de frontera: el vector queda continuo y sin huecos en memoria"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            paso, "actualizarTabla()", "Filas repobladas de 0 a " + (nNuevo - 1),
            nNuevo, logica.getEstadoMemoria(), "Sincronización gráfica: la tabla JTable se redibuja reflejando el corrimiento"
        });

        lblDetalleTraza.setText("Traza de Eliminación: Desplazamiento a la izquierda (Shift-Left) para mantener la contigüidad O(n).");
    }

    private void generarTrazaModificacion(int indice, String valorAnterior, String nuevoValor) {
        if (modeloPruebaEscritorio == null) return;
        modeloPruebaEscritorio.setRowCount(0);

        lblBadgeOperacion.setText(" OPERACION ");
        lblBadgeOperacion.setBackground(new Color(243, 156, 18));

        int n = logica.getCantidadActual();

        modeloPruebaEscritorio.addRow(new Object[]{
            1, "if (indice < 0 || indice >= cantidadActual)", "indice=" + indice + ", cantidadActual=" + n,
            n, logica.getEstadoMemoria(), "Validación de rango: verificación de que el índice a modificar esté ocupado"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            2, "valorActual = arreglo[indice]", "indice=" + indice + ", valorAnterior='" + valorAnterior + "'",
            n, logica.getEstadoMemoria(), "Lectura en O(1) del dato previo para confirmar o auditar la sustitución"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            3, "arreglo[indice] = nuevoValor.trim()", "indice=" + indice + ", nuevoValor='" + nuevoValor + "'",
            n, logica.getEstadoMemoria(), "Sobreescritura en memoria: reemplazo directo del registro en el índice [" + indice + "]"
        });

        modeloPruebaEscritorio.addRow(new Object[]{
            4, "actualizarTabla()", "tabla.setRowSelectionInterval(" + indice + ", " + indice + ")",
            n, logica.getEstadoMemoria(), "Sincronización gráfica: el modelo refresca la fila y mantiene la selección"
        });

        lblDetalleTraza.setText("Traza de Modificación: Acceso directo por índice de memoria en tiempo constante O(1).");
    }

    private void generarTrazaBusqueda(String criterio, int posEncontrada) {
        if (modeloPruebaEscritorio == null) return;
        modeloPruebaEscritorio.setRowCount(0);

        lblBadgeOperacion.setText(" OPERACION");
        lblBadgeOperacion.setBackground(new Color(142, 68, 173));

        int n = logica.getCantidadActual();
        String criterioNorm = LogicaArreglos.normalizar(criterio);

        modeloPruebaEscritorio.addRow(new Object[]{
            1, "criterioNorm = normalizar(criterio)", "criterio='" + criterio + "', norm='" + criterioNorm + "'",
            n, logica.getEstadoMemoria(), "Normalización NFD: remueve tildes/marcas diacríticas y convierte a minúsculas"
        });

        int paso = 2;
        int limite = (posEncontrada != -1) ? posEncontrada + 1 : n;

        for (int i = 0; i < limite; i++) {
            String elem = logica.getElemento(i);
            String elemNorm = LogicaArreglos.normalizar(elem);
            boolean coincide = elemNorm.contains(criterioNorm);

            modeloPruebaEscritorio.addRow(new Object[]{
                paso++, "for (int i = 0; i < cantidadActual; i++)",
                "i=" + i + ", elem='" + elem + "', norm='" + elemNorm + "'",
                n, logica.getEstadoMemoria(), "Comparación [" + i + "]: '" + elemNorm + "'.contains('" + criterioNorm + "') ➔ " 
                    + (coincide ? "TRUE (¡COINCIDENCIA ENCONTRADA!)" : "FALSE (Continúa búsqueda)")
            });
        }

        if (posEncontrada != -1) {
            modeloPruebaEscritorio.addRow(new Object[]{
                paso, "return pos", "pos=" + posEncontrada,
                n, logica.getEstadoMemoria(), "Éxito: Se localizó en índice [" + posEncontrada + "]. Fila resaltada con tabla.setRowSelectionInterval"
            });
            lblDetalleTraza.setText("Traza de Búsqueda: Coincidencia encontrada en índice [" + posEncontrada + "] tras " + posEncontrada + " iteraciones.");
        } else {
            modeloPruebaEscritorio.addRow(new Object[]{
                paso, "return -1", "pos=-1",
                n, logica.getEstadoMemoria(), "Sin coincidencias: Se recorrieron los " + n + " elementos sin encontrar el criterio '" + criterio + "'"
            });
            lblDetalleTraza.setText("Traza de Búsqueda: Búsqueda lineal finalizada sin coincidencias en O(n).");
        }
    }

    private void simularTrazaInsercion() {
        generarTrazaInsercion("PAC-099 (Simulación Traza)", logica.getCantidadActual(), false);
        mostrarMensajeConsola("Mostrando simulación paso a paso de inserción en el arreglo.");
    }

    private void simularTrazaEliminacion() {
        if (logica.getCantidadActual() > 1) {
            generarTrazaEliminacion(1, logica.getElemento(1));
        } else if (logica.getCantidadActual() > 0) {
            generarTrazaEliminacion(0, logica.getElemento(0));
        } else {
            mostrarMensajeConsola("El arreglo está vacío; no hay elementos para trazar la eliminación.");
        }
    }

    private void simularTrazaModificacion() {
        if (logica.getCantidadActual() > 0) {
            generarTrazaModificacion(0, logica.getElemento(0), logica.getElemento(0) + " (Editado)");
        }
    }

    private void simularTrazaBusqueda() {
        if (logica.getCantidadActual() > 0) {
            generarTrazaBusqueda("maria", logica.buscar("maria"));
        }
    }

    // =========================================================================
    //                 ZONA INFERIOR: CASOS DE USO Y ESTADO
    // =========================================================================

    /**
     * Construye la zona inferior: barra discreta de casos de uso pedagógicos
     * y consola de estado de las operaciones.
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        // Barra discreta con el caso de uso de la opción activa
        JPanel panelCasoDeUso = new JPanel(new BorderLayout(8, 0));
        panelCasoDeUso.setOpaque(true);
        panelCasoDeUso.setBackground(new Color(240, 244, 250));
        panelCasoDeUso.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 218, 238), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        lblCasoDeUso = new JLabel("" + CASOS_DE_USO[0]);
        lblCasoDeUso.setFont(montserratRegular.deriveFont(Font.PLAIN, 12f));
        lblCasoDeUso.setForeground(new Color(25, 55, 95));

        panelCasoDeUso.add(lblCasoDeUso, BorderLayout.CENTER);

        // Consola de traza pedagógica (retroalimentación de algoritmos UG)
        JPanel panelConsola = new JPanel(new BorderLayout(8, 0));
        panelConsola.setOpaque(true);
        panelConsola.setBackground(new Color(245, 248, 252));
        panelConsola.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 225, 242), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        lblConsolaOperacion = new JLabel("Sistema listo. Selecciona una opción del menú lateral.");
        lblConsolaOperacion.setFont(montserratRegular.deriveFont(Font.PLAIN, 12f));
        lblConsolaOperacion.setForeground(new Color(40, 60, 90));

        panelConsola.add(lblConsolaOperacion, BorderLayout.CENTER);

        panel.add(panelCasoDeUso, BorderLayout.NORTH);
        panel.add(panelConsola, BorderLayout.SOUTH);

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
     * almacena en el arreglo global, invoca actualizarTabla() y registra la traza en vivo.
     */
    public void accionInsertar() {
        if (logica.estaLleno()) {
            JOptionPane.showMessageDialog(this,
                    " Desbordamiento de Arreglo (Overflow):\n"
                    + "Se ha alcanzado la capacidad máxima estática (" + logica.getMax() + " elementos).\n"
                    + "Para insertar un nuevo registro, primero debe eliminar uno existente.",
                    "Límite Máximo Alcanzado",
                    JOptionPane.WARNING_MESSAGE);
            mostrarMensajeConsola("  Operación rechazada: El arreglo está lleno (cantidadActual = " + logica.getCantidadActual() + ").");
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
                    " Debe ingresar un valor válido (no se admiten textos vacíos).",
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

            // Registrar traza en tiempo real para la Prueba de Escritorio
            generarTrazaInsercion(nuevoValor, posInsertada, true);

            mostrarMensajeConsola(" Elemento '" + nuevoValor + "' insertado en el índice [" 
                    + posInsertada + "]. Nueva cantidadActual: " + logica.getCantidadActual() + ".");
        }
    }

    /**
     * Operación ELIMINAR:
     * Solicita el índice o elemento, aplica el algoritmo de desplazamiento a la izquierda (shift-left),
     * decrementa cantidadActual, llama a actualizarTabla() y registra la traza en vivo.
     */
    public void accionEliminar() {
        if (logica.estaVacio()) {
            JOptionPane.showMessageDialog(this,
                    " Subdesbordamiento de Arreglo (Underflow):\n"
                    + "El arreglo está completamente vacío. No hay elementos para eliminar.",
                    "Arreglo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            mostrarMensajeConsola("  Operación rechazada: El arreglo está vacío (cantidadActual = 0).");
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
                    " No se encontró ningún elemento coincidente con: \"" + entrada + "\".\n"
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

            // Registrar traza en tiempo real para la Prueba de Escritorio
            generarTrazaEliminacion(indiceAEliminar, eliminado);

            mostrarMensajeConsola("️ Elemento '" + eliminado + "' eliminado del índice [" 
                    + indiceAEliminar + "]. Shift-Left aplicado con éxito. Nueva cantidadActual: " + logica.getCantidadActual() + ".");
        }
    }

    /**
     * Operación MODIFICAR:
     * Permite actualizar el valor del elemento seleccionado en la JTable o del índice indicado.
     * Tras modificar, invoca actualizarTabla() y registra la traza en vivo.
     */
    public void accionModificar() {
        if (logica.estaVacio()) {
            JOptionPane.showMessageDialog(this,
                    " El arreglo se encuentra vacío. No hay registros que modificar.",
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
                JOptionPane.showMessageDialog(this, " Debe ingresar un índice numérico entero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (indiceAModificar < 0 || indiceAModificar >= logica.getCantidadActual()) {
            JOptionPane.showMessageDialog(this, " Índice fuera de rango (0 a " + (logica.getCantidadActual() - 1) + ").", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, " El nuevo valor no puede estar vacío.", "Dato Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean modificado = logica.modificar(indiceAModificar, nuevoValor);
        if (modificado) {
            actualizarTabla();
            tabla.setRowSelectionInterval(indiceAModificar, indiceAModificar);

            // Registrar traza en tiempo real para la Prueba de Escritorio
            generarTrazaModificacion(indiceAModificar, valorActual, nuevoValor);

            mostrarMensajeConsola(" Índice [" + indiceAModificar + "] actualizado: '" 
                    + valorActual + "' ➔ '" + nuevoValor + "'.");
        }
    }

    /**
     * Operación BUSCAR (Insensible a mayúsculas y tildes):
     * Utiliza normalización con java.text.Normalizer y .toLowerCase().
     * Al encontrar el elemento, selecciona automáticamente la fila en la tabla
     * mediante tabla.setRowSelectionInterval(pos, pos) y registra la traza en vivo.
     */
    public void accionBuscar() {
        if (logica.estaVacio()) {
            JOptionPane.showMessageDialog(this,
                    "  El arreglo está vacío. No hay elementos para buscar.",
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

        // Registrar traza en tiempo real para la Prueba de Escritorio
        generarTrazaBusqueda(criterio, pos);

        if (pos != -1) {
            // Requisito: Resaltar / seleccionar automáticamente la fila encontrada en el JTable
            tabla.setRowSelectionInterval(pos, pos);
            tabla.scrollRectToVisible(tabla.getCellRect(pos, 0, true));

            JOptionPane.showMessageDialog(this,
                    " Elemento encontrado con éxito:\n\n"
                    + "Índice en el arreglo: [" + pos + "]\n"
                    + "Valor registrado: " + logica.getElemento(pos) + "\n\n"
                    + "La fila correspondiente ha sido seleccionada en la tabla dinámica.",
                    "Búsqueda Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            mostrarMensajeConsola(" Coincidencia encontrada: '" + criterio + "' en el índice [" 
                    + pos + "] ('" + logica.getElemento(pos) + "').");
        } else {
            JOptionPane.showMessageDialog(this,
                    " El valor \"" + criterio + "\" no se encuentra en el arreglo.",
                    "No Encontrado",
                    JOptionPane.WARNING_MESSAGE);

            mostrarMensajeConsola("    Sin coincidencias para '" + criterio + "'.");
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
