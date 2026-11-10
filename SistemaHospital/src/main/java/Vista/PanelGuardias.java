package Vista;

import static Componentes.Estilo.*;

import Componentes.BotonRedondeado;
import Componentes.PanelRedondeado;
import Componentes.PilaLlamadas;
import Model.FactorialRecursivo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

/**
 * Guardias de doctores (factorial recursivo): de cuántas formas distintas se
 * puede ordenar la rotación de guardia de n doctores. n! = n × (n-1)!
 */
public class PanelGuardias extends JPanel {

    private static final NumberFormat FORMATO = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-EC"));

    private final JSpinner spDoctores;
    private final JLabel lblFormas = etiqueta("-", 38, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblFormasSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblLlamadas = etiqueta("-", 38, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblLlamadasSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblTiempo = etiqueta("-", 28, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblPilaSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final PilaLlamadas pila = new PilaLlamadas();
    private final JTextArea traza = new JTextArea();

    public PanelGuardias() {
        super(new BorderLayout(0, 20));
        setOpaque(false);
        spDoctores = spinner(new SpinnerNumberModel(5, 0, FactorialRecursivo.N_MAXIMO, 1));

        add(encabezado("Guardias de doctores",
                "¿De cuántas formas distintas se puede ordenar la rotación de guardia?   ·   n! = n × (n-1)!"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(18, 18));
        cuerpo.setOpaque(false);
        cuerpo.add(crearPanelEntrada(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 18));
        centro.setOpaque(false);
        centro.add(crearFilaResultados(), BorderLayout.NORTH);
        centro.add(crearZonaPila(), BorderLayout.CENTER);
        cuerpo.add(centro, BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);

        spDoctores.addChangeListener(e -> calcular());
        calcular();
    }

    private JComponent crearPanelEntrada() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(280, 0));

        t.add(izq(etiqueta("DATOS DE ENTRADA", 11, Font.BOLD, AZUL)));
        t.add(Box.createVerticalStrut(14));
        t.add(izq(etiqueta("Doctores de guardia", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(spDoctores));
        t.add(Box.createVerticalStrut(4));
        t.add(izq(etiqueta("Entre 0 y " + FactorialRecursivo.N_MAXIMO + " doctores", 11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(20));

        BotonRedondeado btn = boton("Calcular rotaciones");
        btn.addActionListener(e -> calcular());
        t.add(izq(btn));

        t.add(Box.createVerticalStrut(22));
        t.add(izq(separador()));
        t.add(Box.createVerticalStrut(18));

        t.add(izq(etiqueta("¿QUÉ SE CALCULA?", 11, Font.BOLD, AZUL)));
        t.add(Box.createVerticalStrut(8));
        t.add(izq(parrafo("Con <b>n</b> doctores, el número de órdenes distintos en que pueden cubrir "
                + "los turnos de guardia es <b>n!</b> (factorial).")));
        t.add(Box.createVerticalStrut(16));
        t.add(izq(etiqueta("EJEMPLO", 11, Font.BOLD, AZUL)));
        t.add(Box.createVerticalStrut(8));
        t.add(izq(parrafo("3 doctores (A, B, C) → 3! = <b>6</b> rotaciones:<br>"
                + "ABC · ACB · BAC · BCA · CAB · CBA")));
        t.add(Box.createVerticalStrut(16));
        t.add(izq(etiqueta("CASO BASE", 11, Font.BOLD, AZUL)));
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
        c.insets = new Insets(0, 0, 0, 18);
        fila.add(tarjetaDato("ROTACIONES POSIBLES", lblFormas, lblFormasSub), c);

        c.gridx = 1;
        c.weightx = 1;
        fila.add(tarjetaDato("LLAMADAS RECURSIVAS", lblLlamadas, lblLlamadasSub), c);

        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 0);
        fila.add(tarjetaDato("TIEMPO DE CÁLCULO", lblTiempo,
                etiqueta("Medido con System.nanoTime()", 12, Font.PLAIN, TEXTO_SEC)), c);
        return fila;
    }

    private JComponent crearZonaPila() {
        JPanel zona = new JPanel(new BorderLayout(18, 0));
        zona.setOpaque(false);

        // Pila de llamadas (gráfico)
        PanelRedondeado cPila = tarjeta();
        cPila.setLayout(new BorderLayout(0, 12));
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(izq(etiqueta("Pila de llamadas recursivas", 16, Font.BOLD, TEXTO)));
        lblPilaSub.setBorder(BorderFactory.createEmptyBorder(3, 0, 6, 0));
        textos.add(izq(lblPilaSub));
        JPanel ley = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        ley.setOpaque(false);
        ley.add(leyenda(AZUL_OSCURO, "Llamada inicial"));
        ley.add(Box.createHorizontalStrut(14));
        ley.add(leyenda(AZUL, "Recursiva"));
        ley.add(Box.createHorizontalStrut(14));
        ley.add(leyenda(AZUL_SUAVE, "Caso base"));
        textos.add(izq(ley));
        cab.add(textos, BorderLayout.WEST);
        cPila.add(cab, BorderLayout.NORTH);
        cPila.add(scroll(pila), BorderLayout.CENTER);
        zona.add(cPila, BorderLayout.CENTER);

        // Traza de ejecución (texto)
        PanelRedondeado cTraza = tarjeta();
        cTraza.setLayout(new BorderLayout(0, 12));
        cTraza.setPreferredSize(new Dimension(290, 0));
        JPanel cabT = new JPanel();
        cabT.setOpaque(false);
        cabT.setLayout(new BoxLayout(cabT, BoxLayout.Y_AXIS));
        cabT.add(izq(etiqueta("Traza de ejecución", 16, Font.BOLD, TEXTO)));
        JLabel subT = etiqueta("Apilado y desapilado, en orden", 12, Font.PLAIN, TEXTO_SEC);
        subT.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        cabT.add(izq(subT));
        cTraza.add(cabT, BorderLayout.NORTH);
        traza.setEditable(false);
        traza.setFont(new Font("Consolas", Font.PLAIN, 11));
        traza.setForeground(TEXTO);
        traza.setBackground(Color.WHITE);
        traza.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        cTraza.add(scroll(traza), BorderLayout.CENTER);
        zona.add(cTraza, BorderLayout.EAST);
        return zona;
    }

    private void calcular() {
        int n = (Integer) spDoctores.getValue();
        FactorialRecursivo.Resultado r = FactorialRecursivo.calcular(n);
        int llamadas = Math.max(n, 1);

        String formas = FORMATO.format(r.valor());
        lblFormas.setText(formas);
        lblFormas.setFont(fuente(Font.BOLD, formas.length() <= 9 ? 38 : formas.length() <= 15 ? 28 : 21));
        lblFormasSub.setText(n + (n == 1 ? " doctor" : " doctores") + "  →  " + n + "! = " + formas
                + (r.valor() == 1 ? " orden posible" : " órdenes posibles"));

        lblLlamadas.setText(String.valueOf(llamadas));
        lblLlamadasSub.setText("Profundidad de la pila: " + r.profundidadMaxima());
        lblTiempo.setText(r.tiempoFormateado());

        lblPilaSub.setText("Cada llamada espera a que la siguiente devuelva su valor");
        pila.setN(n);
        traza.setText(String.join("\n", r.traza()));
        traza.setCaretPosition(0);
    }

    // ------------------------------------------------------------- utilidades

    private static JComponent tarjetaDato(String titulo, JLabel valor, JLabel sub) {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(izq(etiqueta(titulo, 11, Font.BOLD, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(6));
        valor.setPreferredSize(new Dimension(10, 52));
        valor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        t.add(izq(valor));
        t.add(izq(sub));
        return t;
    }

    private static JLabel parrafo(String html) {
        JLabel l = etiqueta("<html><body style='width:185px'>" + html + "</body></html>", 12, Font.PLAIN, TEXTO);
        return l;
    }

    private static JScrollPane scroll(JComponent c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(BorderFactory.createLineBorder(BORDE));
        s.getViewport().setBackground(Color.WHITE);
        s.getHorizontalScrollBar().setUnitIncrement(24);
        s.getVerticalScrollBar().setUnitIncrement(24);
        return s;
    }
}
