package Vista;

import static Componentes.Estilo.*;

import Componentes.BotonRedondeado;
import Componentes.CampoNumero;
import Componentes.Insignia;
import Componentes.PanelPestanas;
import Componentes.PanelRedondeado;
import Componentes.PilaLlamadas;
import Componentes.ScrollModerno;
import Componentes.VisorCodigo;
import Model.FactorialRecursivo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Guardias de doctores (factorial recursivo): de cuántas formas distintas se
 * puede ordenar la rotación de guardia de n doctores. n! = n × (n-1)!
 */
public class PanelGuardias extends JPanel {

    private static final NumberFormat FORMATO = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-EC"));

    private static final String CODIGO = """
            // Model/FactorialRecursivo.java  (versión simplificada, sin la traza)
            public static long factorial(int n) {
                if (n <= 1) {                    // caso base: 0! = 1! = 1
                    return 1;
                }
                return n * factorial(n - 1);     // llamada recursiva
            }""";

    private final CampoNumero campoDoctores = new CampoNumero(5, 0, FactorialRecursivo.N_MAXIMO, 1);
    private final JLabel lblFormas = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblFormasSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblLlamadas = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblLlamadasSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblTiempo = etiqueta("-", 28, Font.BOLD, AZUL_OSCURO);
    private final PilaLlamadas pila = new PilaLlamadas();
    private final VisorCodigo traza = new VisorCodigo();

    public PanelGuardias() {
        super(new BorderLayout(0, 16));
        setOpaque(false);

        add(encabezado("FACTORIAL  ·  RECURSIÓN LINEAL", "Guardias de doctores",
                "¿De cuántas formas distintas se puede ordenar la rotación de guardia?   ·   n! = n × (n-1)!"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(SEPARACION, SEPARACION));
        cuerpo.setOpaque(false);
        cuerpo.add(crearPanelEntrada(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, SEPARACION));
        centro.setOpaque(false);
        centro.add(crearFilaResultados(), BorderLayout.NORTH);
        centro.add(crearPestanas(), BorderLayout.CENTER);
        cuerpo.add(centro, BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);

        campoDoctores.addChangeListener(e -> calcular());
        calcular();
    }

    private JComponent crearPanelEntrada() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(292, 0));

        t.add(seccion("DATOS DE ENTRADA"));
        t.add(Box.createVerticalStrut(14));
        t.add(izq(etiqueta("Doctores de guardia", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(campoDoctores));
        t.add(Box.createVerticalStrut(5));
        t.add(izq(etiqueta("Entre 0 y " + FactorialRecursivo.N_MAXIMO + "  ·  usa − / +, escribe o la rueda",
                11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(18));

        BotonRedondeado btn = boton("Calcular rotaciones");
        btn.addActionListener(e -> calcular());
        t.add(izq(btn));

        t.add(Box.createVerticalStrut(20));
        t.add(izq(separador()));
        t.add(Box.createVerticalStrut(18));

        t.add(seccion("¿QUÉ SE CALCULA?"));
        t.add(Box.createVerticalStrut(8));
        t.add(izq(parrafo("Con <b>n</b> doctores, el número de órdenes distintos en que pueden cubrir "
                + "los turnos de guardia es <b>n!</b> (factorial).")));
        t.add(Box.createVerticalStrut(16));
        t.add(seccion("EJEMPLO"));
        t.add(Box.createVerticalStrut(8));
        t.add(izq(parrafo("3 doctores (A, B, C) → 3! = <b>6</b> rotaciones:<br>"
                + "ABC · ACB · BAC · BCA · CAB · CBA")));
        t.add(Box.createVerticalStrut(16));
        t.add(seccion("CASO BASE"));
        t.add(Box.createVerticalStrut(8));
        t.add(izq(parrafo("factorial(0) = factorial(1) = 1")));
        t.add(Box.createVerticalGlue());
        return t;
    }

    private JComponent crearFilaResultados() {
        JPanel fila = new JPanel(new GridBagLayout());
        fila.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;

        c.gridx = 0;
        c.weightx = 1.6;
        c.insets = new Insets(0, 0, 0, SEPARACION);
        fila.add(tarjetaDato("ROTACIONES POSIBLES", "n!", alto(lblFormas), lblFormasSub), c);

        c.gridx = 1;
        c.weightx = 1;
        fila.add(tarjetaDato("LLAMADAS RECURSIVAS", "#", alto(lblLlamadas), lblLlamadasSub), c);

        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 0);
        fila.add(tarjetaDato("TIEMPO DE CÁLCULO", "ms", alto(lblTiempo),
                etiqueta("Medido con System.nanoTime()", 12, Font.PLAIN, TEXTO_SEC)), c);
        return fila;
    }

    private JComponent crearPestanas() {
        PanelPestanas p = new PanelPestanas();
        p.agregar("Pila de llamadas", "Cada llamada espera el valor de la siguiente",
                PanelPestanas.marco(ScrollModerno.envolver(pila)),
                filaLeyendas(AZUL_OSCURO, "Llamada inicial", AZUL, "Recursiva", AZUL_SUAVE, "Caso base"));
        p.agregar("Traza de ejecución", "Orden real de apilado y desapilado",
                PanelPestanas.marco(ScrollModerno.envolver(traza)),
                filaLeyendas(AZUL, "Llamada (apila)", new Color(0x15803D), "Retorna (desapila)",
                        AZUL_OSCURO, "Caso base"));

        VisorCodigo codigo = new VisorCodigo();
        codigo.mostrarCodigo(CODIGO);
        p.agregar("Código Java", "Método recursivo",
                PanelPestanas.marco(ScrollModerno.envolver(codigo)),
                chips("Caso base: n ≤ 1", "Tiempo: O(n)", "Memoria de pila: O(n)"));
        return p;
    }

    private void calcular() {
        int n = campoDoctores.getValor();
        FactorialRecursivo.Resultado r = FactorialRecursivo.calcular(n);
        int llamadas = Math.max(n, 1);

        String formas = FORMATO.format(r.valor());
        lblFormas.setText(formas);
        lblFormas.setFont(fuente(Font.BOLD, formas.length() <= 9 ? 36 : formas.length() <= 15 ? 28 : 21));
        lblFormasSub.setText(n + (n == 1 ? " doctor" : " doctores") + "  →  " + n + "! = " + formas
                + (r.valor() == 1 ? " orden posible" : " órdenes posibles"));

        lblLlamadas.setText(String.valueOf(llamadas));
        lblLlamadasSub.setText("Profundidad de la pila: " + r.profundidadMaxima());
        lblTiempo.setText(r.tiempoFormateado());

        pila.setN(n);
        traza.mostrarTraza(r.traza());
    }

    // ------------------------------------------------------------- utilidades

    /** Fija la altura del número grande para que las tarjetas no salten al cambiar de tamaño de letra. */
    private static JLabel alto(JLabel l) {
        l.setPreferredSize(new Dimension(10, 50));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return l;
    }

    private static JLabel parrafo(String html) {
        return etiqueta("<html><body style='width:185px'>" + html + "</body></html>", 12, Font.PLAIN, TEXTO);
    }

    static JComponent chips(String... textos) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);
        for (String t : textos) {
            p.add(Insignia.chip(t));
        }
        return p;
    }
}
