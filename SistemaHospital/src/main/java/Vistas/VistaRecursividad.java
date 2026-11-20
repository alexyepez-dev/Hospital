package Vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Componentes.Estilo;
import Componentes.PanelRedondeado;
import Componentes.ScrollModerno;
import Componentes.TablaDatos;
import Logica.RecursividadLogica;
import Models.RecursividadModel;

/**
 * Vista del módulo RECURSIVIDAD.
 * @author CRACKS
 */
public class VistaRecursividad extends VistaModulo {

    private final RecursividadLogica logica = new RecursividadLogica();

    public VistaRecursividad(String usuario) {
        super(usuario, "Recursividad", new Color(155, 89, 182));
        initModulo();
    }

    @Override
    protected List<String> getOpcionesMenu() {
        return Arrays.asList("Factorial", "Fibonacci", "Caso de Uso", "Contexto");
    }

    @Override
    protected JPanel crearPanelOpcion(int indice) {
        switch (indice) {
            case 0:
                return panelFactorial();
            case 1:
                return panelFibonacci();
            case 2:
                return panelCasoDeUso();
            default:
                return panelContexto();
        }
    }

    private JPanel panelFactorial() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo("Factorial recursivo"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo("Calcula n! usando recursividad: f(n) = n * f(n-1)"));
        p.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        JTextField txt = crearCampoTexto(8);
        JButton btn = crearBotonPrimario("Calcular");
        fila.add(new JLabel("n ="));
        fila.add(txt);
        fila.add(btn);
        p.add(fila);

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultado.setBackground(new Color(245, 248, 252));
        resultado.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(new JScrollPane(resultado));

        btn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(txt.getText().trim());
                RecursividadModel r = logica.calcularFactorial(n);
                if (r == null) {
                    resultado.setText("⚠ Ingresa un número entre 0 y 20");
                } else {
                    resultado.setText(r.getTraza());
                }
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });

        return p;
    }

    private JPanel panelFibonacci() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(crearTitulo("Fibonacci recursivo"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(crearSubtitulo("Serie de Fibonacci: f(n) = f(n-1) + f(n-2)"));
        p.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        JTextField txt = crearCampoTexto(8);
        JButton btn = crearBotonPrimario("Generar");
        fila.add(new JLabel("n ="));
        fila.add(txt);
        fila.add(btn);
        p.add(fila);

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultado.setBackground(new Color(245, 248, 252));
        resultado.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(new JScrollPane(resultado));

        btn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(txt.getText().trim());
                RecursividadModel r = logica.calcularFibonacci(n);
                if (r == null) {
                    resultado.setText("⚠ Ingresa un número entre 0 y 30");
                } else {
                    resultado.setText(r.getTraza());
                    resultado.setCaretPosition(0);
                }
            } catch (NumberFormatException ex) {
                resultado.setText("⚠ Ingresa un número válido");
            }
        });

        return p;
    }

    /**
     * Caso de Uso Hospitalario real aplicando recursividad de Fibonacci:
     * Proyección epidemiológica de ingresos y demanda de camas por ciclo de brote.
     */
    private JPanel panelCasoDeUso() {
        Color colorNormal = new Color(0x16A34A);
        Color colorVigilancia = new Color(0x2563EB);
        Color colorNaranja = new Color(0xEA580C);
        Color colorCritico = new Color(0xDC2626);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);

        // ===== 1. ENCABEZADO + CONTROLES + KPIS =====
        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));

        JLabel titulo = crearTitulo("Proyección Epidemiológica de Camas");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = crearSubtitulo(
                "Caso de uso hospitalario (Fibonacci): f(n) = f(n-1) [activos] + f(n-2) [fin de incubación]");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        norte.add(titulo);
        norte.add(Box.createRigidArea(new Dimension(0, 4)));
        norte.add(subtitulo);
        norte.add(Box.createRigidArea(new Dimension(0, 14)));

        // Barra de controles
        JPanel barraControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        barraControles.setOpaque(false);
        barraControles.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblProtocolo = new JLabel("Protocolo:");
        lblProtocolo.setFont(montserratBold.deriveFont(Font.BOLD, 12f));
        lblProtocolo.setForeground(AZUL_OSCURO);

        JComboBox<String> cmbProtocolo = new JComboBox<>(new String[]{
            "Influenza A (Emergencia)",
            "Dengue Hemorrágico (Med. Interna)",
            "Cepa Nosocomial (UCI)"
        });
        cmbProtocolo.setFont(montserratRegular.deriveFont(Font.PLAIN, 13f));
        cmbProtocolo.setPreferredSize(new Dimension(235, 38));

        JLabel lblCiclos = new JLabel("Ciclo (1-15):");
        lblCiclos.setFont(montserratBold.deriveFont(Font.BOLD, 12f));
        lblCiclos.setForeground(AZUL_OSCURO);

        JTextField txtCiclos = crearCampoTexto(4);
        txtCiclos.setText("6");
        txtCiclos.setPreferredSize(new Dimension(65, 38));

        JButton btnSimular = crearBotonPrimario("Simular Brote");
        btnSimular.setPreferredSize(new Dimension(140, 38));

        JButton btnEjemplo = crearBotonPrimario("Ejemplo (n=9)");
        btnEjemplo.setPreferredSize(new Dimension(135, 38));

        barraControles.add(lblProtocolo);
        barraControles.add(cmbProtocolo);
        barraControles.add(lblCiclos);
        barraControles.add(txtCiclos);
        barraControles.add(btnSimular);
        barraControles.add(btnEjemplo);

        norte.add(barraControles);
        norte.add(Box.createRigidArea(new Dimension(0, 12)));

        // Tarjetas KPI hospitalarias
        JLabel lblKpiNuevos = new JLabel("-");
        lblKpiNuevos.setFont(montserratBold.deriveFont(Font.BOLD, 24f));
        lblKpiNuevos.setForeground(acentoModulo);
        JLabel lblKpiNuevosSub = crearSubtitulo("pacientes en el ciclo n");

        JLabel lblKpiCamas = new JLabel("-");
        lblKpiCamas.setFont(montserratBold.deriveFont(Font.BOLD, 24f));
        lblKpiCamas.setForeground(AZUL_OSCURO);
        JLabel lblKpiCamasSub = crearSubtitulo("suma recursiva acumulada");

        JLabel lblKpiAlerta = new JLabel("-");
        lblKpiAlerta.setFont(montserratBold.deriveFont(Font.BOLD, 20f));
        lblKpiAlerta.setForeground(colorNormal);
        JLabel lblKpiAreaSub = crearSubtitulo("pabellón asignado");

        JPanel filaKpis = new JPanel(new GridLayout(1, 3, 12, 0));
        filaKpis.setOpaque(false);
        filaKpis.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaKpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));

        filaKpis.add(crearTarjetaKpi("NUEVOS INGRESOS f(n)", lblKpiNuevos, lblKpiNuevosSub));
        filaKpis.add(crearTarjetaKpi("CAMAS ACUMULADAS", lblKpiCamas, lblKpiCamasSub));
        filaKpis.add(crearTarjetaKpi("ALERTA HOSPITALARIA", lblKpiAlerta, lblKpiAreaSub));

        norte.add(filaKpis);
        root.add(norte, BorderLayout.NORTH);

        // ===== 2. TABLA CLÍNICA + TRAZA RECURSIVA EN VIVO =====
        TablaDatos tabla = new TablaDatos(
                "Ciclo", "Fórmula Recursiva", "Nuevos f(n)", "Camas Acum.", "Pabellón Asignado", "Nivel de Alerta");
        tabla.centrar(0, 2, 3);
        tabla.setAnchos(70, 155, 95, 100, 175, 145);
        tabla.setPildora(5, valor -> {
            String v = String.valueOf(valor);
            if (v.contains("Normal")) return colorNormal;
            if (v.contains("Vigilancia")) return colorVigilancia;
            if (v.contains("Naranja")) return colorNaranja;
            return colorCritico;
        });

        JComponent contenedorTabla = Estilo.marco(ScrollModerno.envolver(tabla));

        JTextArea txtTraza = new JTextArea(6, 50);
        txtTraza.setEditable(false);
        txtTraza.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtTraza.setBackground(new Color(245, 248, 252));
        txtTraza.setForeground(new Color(30, 41, 59));
        txtTraza.setBorder(new EmptyBorder(10, 14, 10, 14));

        JPanel panelTraza = new JPanel(new BorderLayout(0, 4));
        panelTraza.setOpaque(false);
        JLabel lblTraza = new JLabel("EJECUCIÓN RECURSIVA POR DETRÁS (RecursividadLogica.fibonacci)");
        lblTraza.setFont(montserratBold.deriveFont(Font.BOLD, 11f));
        lblTraza.setForeground(acentoModulo);
        panelTraza.add(lblTraza, BorderLayout.NORTH);
        panelTraza.add(Estilo.marco(ScrollModerno.envolver(txtTraza)), BorderLayout.CENTER);

        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, contenedorTabla, panelTraza);
        divisor.setOpaque(false);
        divisor.setBorder(null);
        divisor.setResizeWeight(0.58);
        divisor.setDividerSize(8);

        root.add(divisor, BorderLayout.CENTER);

        // ===== 3. ACCIÓN DE SIMULACIÓN =====
        Runnable ejecutarSimulacion = () -> {
            try {
                int n = Integer.parseInt(txtCiclos.getText().trim());
                String patologia = String.valueOf(cmbProtocolo.getSelectedItem());
                RecursividadModel resultado = logica.calcularCasoUsoHospital(n, patologia);

                if (resultado == null) {
                    txtTraza.setText("⚠ Ingresa un número de ciclo entre 1 y 15 para la simulación hospitalaria.");
                    return;
                }

                List<Object[]> filas = new ArrayList<>();
                long acumulado = 0;
                for (int i = 0; i <= n; i++) {
                    long nuevos = logica.fibonacci(i);
                    acumulado += nuevos;
                    String formula = (i <= 1)
                            ? "f(" + i + ") = " + i + " (base)"
                            : "f(" + (i - 1) + ") + f(" + (i - 2) + ")";
                    String area = logica.obtenerAreaAsignada(acumulado);
                    String alerta = logica.obtenerNivelAlerta(acumulado);
                    filas.add(new Object[]{
                        "Día " + i,
                        formula,
                        nuevos,
                        acumulado,
                        area,
                        alerta
                    });
                }
                tabla.setFilas(filas);

                long camasTotales = logica.sumaCamasRecursiva(n);
                String alertaFinal = logica.obtenerNivelAlerta(camasTotales);
                String areaFinal = logica.obtenerAreaAsignada(camasTotales);

                lblKpiNuevos.setText(resultado.getResultado() + " pac.");
                lblKpiNuevosSub.setText("en Día " + n + " (" + patologia.split(" \\(")[0] + ")");
                lblKpiCamas.setText(camasTotales + " camas");
                lblKpiCamasSub.setText("acumuladas de Día 0 a Día " + n);
                lblKpiAlerta.setText(alertaFinal);
                lblKpiAreaSub.setText("Área: " + areaFinal);

                if (alertaFinal.contains("Normal")) {
                    lblKpiAlerta.setForeground(colorNormal);
                } else if (alertaFinal.contains("Vigilancia")) {
                    lblKpiAlerta.setForeground(colorVigilancia);
                } else if (alertaFinal.contains("Naranja")) {
                    lblKpiAlerta.setForeground(colorNaranja);
                } else {
                    lblKpiAlerta.setForeground(colorCritico);
                }

                txtTraza.setText(resultado.getTraza());
                txtTraza.setCaretPosition(0);

            } catch (NumberFormatException ex) {
                txtTraza.setText("⚠ Ingresa un número entero válido (entre 1 y 15).");
            }
        };

        btnSimular.addActionListener(e -> ejecutarSimulacion.run());
        txtCiclos.addActionListener(e -> ejecutarSimulacion.run());
        cmbProtocolo.addActionListener(e -> ejecutarSimulacion.run());
        btnEjemplo.addActionListener(e -> {
            cmbProtocolo.setSelectedIndex(2);
            txtCiclos.setText("9");
            ejecutarSimulacion.run();
        });

        // Ejecutar simulación inicial para mostrar la vista hospitalaria activa al abrir
        ejecutarSimulacion.run();

        return root;
    }

    private PanelRedondeado crearTarjetaKpi(String titulo, JLabel valor, JLabel subtitulo) {
        PanelRedondeado card = new PanelRedondeado();
        card.setBackground(new Color(248, 250, 254));
        card.setColorBorde(new Color(220, 230, 242));
        card.setRadio(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(montserratBold.deriveFont(Font.BOLD, 10f));
        lblTit.setForeground(GRIS_TEXTO);
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);

        valor.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTit);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(valor);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(subtitulo);
        return card;
    }

    /**
     * Panel informativo con el marco teórico de la recursividad, explicación de
     * Factorial y Fibonacci, y la justificación del Caso de Uso Hospitalario.
     */
    private JPanel panelContexto() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);

        // Encabezado fijo arriba
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titulo = crearTitulo("Contexto: Recursividad en el Sistema Hospitalario");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = crearSubtitulo(
                "Fundamentos teóricos, funcionamiento de Factorial y Fibonacci, y aplicación en nuestro caso de uso");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(titulo);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(subtitulo);
        root.add(header, BorderLayout.NORTH);

        // Contenedor scrollable que ajusta su ancho al viewport para evitar scroll horizontal
        JPanel listaTarjetas = new PanelScrollVertical();
        listaTarjetas.setOpaque(false);
        listaTarjetas.setLayout(new BoxLayout(listaTarjetas, BoxLayout.Y_AXIS));
        listaTarjetas.setBorder(new EmptyBorder(4, 2, 12, 10));

        // 1. ¿Qué es la Recursividad?
        listaTarjetas.add(crearTarjetaContexto(
                "1. ¿Qué es la Recursividad?",
                new Color(155, 89, 182),
                "La recursividad es una técnica de programación y estructura de datos en la que una función "
                + "se llama a sí misma para dividir un problema grande en subproblemas más pequeños de la misma "
                + "naturaleza, hasta llegar a una solución directa.\n\n"
                + "Todo algoritmo recursivo correcto necesita obligatoriamente dos partes:\n"
                + "• Caso Base: es la condición de parada que devuelve un resultado inmediato sin hacer más "
                + "llamadas recursivas. Sin él, la pila de memoria (Call Stack) se llenaría y ocurriría un StackOverflowError.\n"
                + "• Caso Recursivo: reduce el valor de entrada paso a paso acercándolo al caso base. Cada llamada "
                + "queda pausada en la pila hasta que las llamadas internas retornan su resultado.",
                "Estructura general:  si (esCasoBase) retornar valorBase;  sino retornar combinar(funcion(n - 1));"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2. Factorial Recursivo
        listaTarjetas.add(crearTarjetaContexto(
                "2. Algoritmo 1: Factorial Recursivo  (n!)",
                new Color(41, 128, 185),
                "El factorial de un número entero positivo n (denotado como n!) es el producto de todos los "
                + "números enteros desde 1 hasta n. Es el ejemplo clásico de RECURSIVIDAD LINEAL, porque en cada "
                + "paso la función realiza una única llamada a sí misma.\n\n"
                + "• Cómo funciona por detrás: para calcular factorial(4), la pila desciende apilando "
                + "4 × f(3) → 3 × f(2) → 2 × f(1). Al llegar al caso base f(1) = 1, las llamadas se van "
                + "resolviendo en sentido inverso: 2 × 1 = 2, 3 × 2 = 6 y finalmente 4 × 6 = 24.\n"
                + "• Complejidad temporal: O(n), ya que ejecuta exactamente n llamadas encadenadas.",
                "Caso base: f(0) = 1, f(1) = 1   |   Caso recursivo: f(n) = n × f(n - 1)"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. Fibonacci Recursivo
        listaTarjetas.add(crearTarjetaContexto(
                "3. Algoritmo 2: Serie de Fibonacci Recursiva  (f(n))",
                new Color(39, 174, 96),
                "La sucesión de Fibonacci genera una serie donde cada término es la suma de los dos términos "
                + "inmediatamente anteriores (0, 1, 1, 2, 3, 5, 8, 13, 21...). Es un ejemplo de RECURSIVIDAD "
                + "MÚLTIPLE (o binaria), porque cada llamada se ramifica en dos subllamadas formando un árbol.\n\n"
                + "• Cómo funciona por detrás: para obtener f(5), el algoritmo divide el cálculo en f(4) + f(3); "
                + "a su vez f(4) se divide en f(3) + f(2), y así sucesivamente hasta tocar los casos base f(1) = 1 "
                + "y f(0) = 0. Luego regresa sumando cada rama (por ejemplo: 1 + 0 = 1, 1 + 1 = 2, 2 + 1 = 3, 3 + 2 = 5).\n"
                + "• Complejidad temporal: O(2^n), lo cual ilustra claramente el comportamiento del árbol de llamadas.",
                "Casos base: f(0) = 0, f(1) = 1   |   Caso recursivo: f(n) = f(n - 1) + f(n - 2)"
        ));
        listaTarjetas.add(Box.createRigidArea(new Dimension(0, 12)));

        // 4. Nuestro Caso de Uso Hospitalario
        listaTarjetas.add(crearTarjetaContexto(
                "4. Nuestro Caso de Uso Hospitalario: Proyección Epidemiológica de Camas",
                new Color(230, 126, 34),
                "Para llevar la recursividad a un escenario real dentro del Sistema Hospitalario, implementamos "
                + "un simulador de Vigilancia Epidemiológica basado en FIBONACCI para estimar el crecimiento de "
                + "ingresos de pacientes y la demanda acumulada de camas durante un brote infeccioso (Influenza A, "
                + "Dengue Hemorrágico o Cepa Nosocomial).\n\n"
                + "• Cómo aplica la fórmula al hospital: los nuevos ingresos del ciclo actual f(n) provienen de "
                + "los pacientes activos del ciclo anterior f(n - 1) sumados a los pacientes que concluyen su "
                + "periodo de incubación desde el ciclo f(n - 2). Además, usamos una segunda función recursiva "
                + "(sumaCamasRecursiva) para acumular las camas totales y activar alertas clínicas automáticas "
                + "(Normal → Triaje, Vigilancia → Aislamiento, Alerta Naranja → Medicina Interna, Emergencia → UCI).\n\n"
                + "• ¿Por qué elegimos Fibonacci y no Factorial? Porque Fibonacci produce cantidades clínicas "
                + "realistas (en el día 8 ingresan 21 pacientes y en el día 10 ingresan 55) y cada paso f(0)..f(n) "
                + "representa un día real observable en la tabla hospitalaria. En cambio, Factorial explota demasiado "
                + "rápido (10! = 3,628,800) y solo representa permutaciones abstractas poco prácticas para gestionar camas.",
                "Ingresos día n: fibonacci(n)   |   Camas acumuladas: sumaCamasRecursiva(n) = fibonacci(n) + sumaCamasRecursiva(n - 1)"
        ));

        JScrollPane scroll = ScrollModerno.envolver(listaTarjetas);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scroll, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(0));
        return root;
    }

    private PanelRedondeado crearTarjetaContexto(String tituloSeccion, Color colorAcento, String cuerpo, String formula) {
        PanelRedondeado card = new PanelRedondeado();
        card.setBackground(new Color(248, 250, 254));
        card.setColorBorde(new Color(220, 230, 242));
        card.setRadio(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel cabecera = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cabecera.setOpaque(false);
        cabecera.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel barraColor = new JPanel();
        barraColor.setBackground(colorAcento);
        barraColor.setPreferredSize(new Dimension(5, 20));

        JLabel lblTit = new JLabel(tituloSeccion);
        lblTit.setFont(montserratBold.deriveFont(Font.BOLD, 15f));
        lblTit.setForeground(AZUL_OSCURO);

        cabecera.add(barraColor);
        cabecera.add(lblTit);

        JTextArea txtCuerpo = new JTextArea(cuerpo);
        txtCuerpo.setEditable(false);
        txtCuerpo.setFocusable(false);
        txtCuerpo.setOpaque(false);
        txtCuerpo.setLineWrap(true);
        txtCuerpo.setWrapStyleWord(true);
        txtCuerpo.setFont(montserratRegular.deriveFont(Font.PLAIN, 13f));
        txtCuerpo.setForeground(new Color(40, 52, 70));
        txtCuerpo.setBorder(new EmptyBorder(8, 2, 10, 2));
        txtCuerpo.setAlignmentX(Component.LEFT_ALIGNMENT);

        PanelRedondeado cajaFormula = new PanelRedondeado();
        cajaFormula.setBackground(new Color(
                (colorAcento.getRed() + 255 * 8) / 9,
                (colorAcento.getGreen() + 255 * 8) / 9,
                (colorAcento.getBlue() + 255 * 8) / 9));
        cajaFormula.setColorBorde(new Color(
                (colorAcento.getRed() + 255 * 4) / 5,
                (colorAcento.getGreen() + 255 * 4) / 5,
                (colorAcento.getBlue() + 255 * 4) / 5));
        cajaFormula.setRadio(8);
        cajaFormula.setLayout(new BorderLayout());
        cajaFormula.setBorder(new EmptyBorder(8, 12, 8, 12));
        cajaFormula.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFormula = new JLabel(formula);
        lblFormula.setFont(new Font("Consolas", Font.BOLD, 12));
        lblFormula.setForeground(colorAcento.darker());
        cajaFormula.add(lblFormula, BorderLayout.CENTER);

        card.add(cabecera);
        card.add(txtCuerpo);
        card.add(cajaFormula);
        return card;
    }

    /** Panel auxiliar que fuerza el ajuste de ancho al JViewport para evitar desbordes horizontales. */
    private static class PanelScrollVertical extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 24; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 90; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
