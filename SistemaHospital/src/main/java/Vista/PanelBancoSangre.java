package Vista;

import static Componentes.Estilo.*;

import Componentes.ArbolLlamadas;
import Componentes.BotonRedondeado;
import Componentes.PanelRedondeado;
import Componentes.Semaforo;
import Model.DemandaSangre;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Banco de sangre (Fibonacci recursivo): la demanda de una semana es la suma de
 * la demanda cubierta en las dos semanas previas. D(n) = D(n-1) + D(n-2)
 */
public class PanelBancoSangre extends JPanel {

    private final JSpinner spSemana;
    private final JSpinner spStock;
    private final JLabel lblUnidades = etiqueta("-", 38, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblUnidadesSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final Semaforo semaforo = new Semaforo();
    private final JLabel lblSemaforoTitulo = etiqueta("-", 17, Font.BOLD, TEXTO);
    private final JLabel lblSemaforoDetalle = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblLlamadas = etiqueta("-", 38, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblLlamadasSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblFormula = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final ArbolLlamadas arbol = new ArbolLlamadas();

    public PanelBancoSangre() {
        super(new BorderLayout(0, 20));
        setOpaque(false);
        spSemana = spinner(new SpinnerNumberModel(7, DemandaSangre.SEMANA_MINIMA, DemandaSangre.SEMANA_MAXIMA, 1));
        spStock = spinner(new SpinnerNumberModel(200, 0, 100_000, 10));

        add(encabezado("Banco de sangre",
                "La demanda de cada semana es la suma de la demanda cubierta en las dos semanas previas"
                + "   ·   D(n) = D(n-1) + D(n-2)  (Fibonacci)"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(18, 18));
        cuerpo.setOpaque(false);
        cuerpo.add(crearPanelEntrada(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 18));
        centro.setOpaque(false);
        centro.add(crearFilaResultados(), BorderLayout.NORTH);
        centro.add(crearPanelArbol(), BorderLayout.CENTER);
        cuerpo.add(centro, BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);

        // Recalcula en vivo al cambiar cualquier valor
        spSemana.addChangeListener(e -> calcular());
        spStock.addChangeListener(e -> calcular());
        calcular();
    }

    private JComponent crearPanelEntrada() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(280, 0));

        t.add(izq(etiqueta("DATOS DE ENTRADA", 11, Font.BOLD, AZUL)));
        t.add(Box.createVerticalStrut(14));
        t.add(izq(etiqueta("Semana de campaña proyectada", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(spSemana));
        t.add(Box.createVerticalStrut(4));
        t.add(izq(etiqueta("Entre " + DemandaSangre.SEMANA_MINIMA + " y " + DemandaSangre.SEMANA_MAXIMA,
                11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(16));

        t.add(izq(etiqueta("Stock actual en el banco", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(spStock));
        t.add(Box.createVerticalStrut(4));
        t.add(izq(etiqueta("Unidades disponibles", 11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(20));

        BotonRedondeado btn = boton("Calcular proyección");
        btn.addActionListener(e -> calcular());
        t.add(izq(btn));

        t.add(Box.createVerticalStrut(22));
        t.add(izq(separador()));
        t.add(Box.createVerticalStrut(18));

        t.add(izq(etiqueta("CASOS BASE", 11, Font.BOLD, AZUL)));
        t.add(Box.createVerticalStrut(8));
        t.add(izq(etiqueta("Semana 1:  " + DemandaSangre.DEMANDA_SEMANA_1 + " unidades", 12, Font.PLAIN, TEXTO)));
        t.add(Box.createVerticalStrut(4));
        t.add(izq(etiqueta("Semana 2:  " + DemandaSangre.DEMANDA_SEMANA_2 + " unidades", 12, Font.PLAIN, TEXTO)));

        t.add(Box.createVerticalStrut(18));
        t.add(izq(etiqueta("SEMÁFORO DE RESERVA", 11, Font.BOLD, AZUL)));
        t.add(Box.createVerticalStrut(8));
        t.add(izq(leyenda(Semaforo.VERDE, "Stock ≥ requerido + " + DemandaSangre.RESERVA_PORCENTAJE + " %")));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(leyenda(Semaforo.AMARILLO, "Cubre, pero sin reserva")));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(leyenda(Semaforo.ROJO, "Stock menor a lo requerido")));
        t.add(Box.createVerticalGlue());
        return t;
    }

    private JComponent crearFilaResultados() {
        JPanel fila = new JPanel(new GridLayout(1, 3, 18, 0));
        fila.setOpaque(false);

        PanelRedondeado cUnidades = tarjeta();
        cUnidades.setLayout(new BoxLayout(cUnidades, BoxLayout.Y_AXIS));
        cUnidades.add(izq(etiqueta("UNIDADES REQUERIDAS", 11, Font.BOLD, TEXTO_SEC)));
        cUnidades.add(Box.createVerticalStrut(6));
        cUnidades.add(izq(lblUnidades));
        cUnidades.add(izq(lblUnidadesSub));
        fila.add(cUnidades);

        PanelRedondeado cSemaforo = tarjeta();
        cSemaforo.setLayout(new BoxLayout(cSemaforo, BoxLayout.Y_AXIS));
        cSemaforo.add(izq(etiqueta("SEMÁFORO DE STOCK", 11, Font.BOLD, TEXTO_SEC)));
        cSemaforo.add(Box.createVerticalStrut(8));
        semaforo.setMaximumSize(semaforo.getPreferredSize());
        cSemaforo.add(izq(semaforo));
        cSemaforo.add(Box.createVerticalStrut(6));
        cSemaforo.add(izq(lblSemaforoTitulo));
        cSemaforo.add(izq(lblSemaforoDetalle));
        fila.add(cSemaforo);

        PanelRedondeado cLlamadas = tarjeta();
        cLlamadas.setLayout(new BoxLayout(cLlamadas, BoxLayout.Y_AXIS));
        cLlamadas.add(izq(etiqueta("LLAMADAS RECURSIVAS", 11, Font.BOLD, TEXTO_SEC)));
        cLlamadas.add(Box.createVerticalStrut(6));
        cLlamadas.add(izq(lblLlamadas));
        cLlamadas.add(izq(lblLlamadasSub));
        fila.add(cLlamadas);
        return fila;
    }

    private JComponent crearPanelArbol() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BorderLayout(0, 12));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(izq(etiqueta("Árbol de llamadas recursivas", 16, Font.BOLD, TEXTO)));
        lblFormula.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        textos.add(izq(lblFormula));
        cabecera.add(textos, BorderLayout.WEST);

        JPanel ley = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        ley.setOpaque(false);
        ley.add(leyenda(AZUL_OSCURO, "Llamada inicial"));
        ley.add(leyenda(AZUL, "Recursiva"));
        ley.add(leyenda(AZUL_SUAVE, "Caso base"));
        cabecera.add(ley, BorderLayout.EAST);
        t.add(cabecera, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(arbol);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getHorizontalScrollBar().setUnitIncrement(24);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        t.add(scroll, BorderLayout.CENTER);
        return t;
    }

    private void calcular() {
        int semana = (Integer) spSemana.getValue();
        long stock = ((Number) spStock.getValue()).longValue();

        DemandaSangre.Resultado r = DemandaSangre.calcular(semana);
        DemandaSangre.Semaforo estado = DemandaSangre.evaluarStock(r.unidades(), stock);

        lblUnidades.setText(r.unidades() + " u");
        lblUnidadesSub.setText("Sangre para la semana " + semana);

        long diferencia = stock - r.unidades();
        semaforo.setEstado(switch (estado) {
            case ROJO -> 0;
            case AMARILLO -> 1;
            case VERDE -> 2;
        });
        lblSemaforoTitulo.setText(estado.titulo);
        lblSemaforoTitulo.setForeground(switch (estado) {
            case ROJO -> Semaforo.ROJO.darker();
            case AMARILLO -> new Color(0xB98900);
            case VERDE -> new Color(0x16A34A);
        });
        lblSemaforoDetalle.setText("Stock " + stock + " u  ·  "
                + (diferencia >= 0 ? "sobran " + diferencia : "faltan " + (-diferencia)) + " u");
        lblSemaforoTitulo.setToolTipText(estado.detalle);
        semaforo.setToolTipText(estado.detalle);

        lblLlamadas.setText(String.valueOf(r.llamadas()));
        lblLlamadasSub.setText(r.casosBase() + " casos base · profundidad " + r.profundidad());

        lblFormula.setText(formula(r.arbol()) + "      (pasa el mouse sobre un nodo para ver el detalle)");
        arbol.setArbol(r.arbol());
    }

    private static String formula(DemandaSangre.Nodo n) {
        if (n.esCasoBase()) {
            return "D(" + n.getSemana() + ") = " + n.getValor() + " unidades (caso base)";
        }
        DemandaSangre.Nodo a = n.getHijos().get(0);
        DemandaSangre.Nodo b = n.getHijos().get(1);
        return "D(" + n.getSemana() + ") = D(" + a.getSemana() + ") + D(" + b.getSemana() + ") = "
                + a.getValor() + " + " + b.getValor() + " = " + n.getValor() + " unidades";
    }
}
