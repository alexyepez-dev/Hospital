package Vista;

import static Componentes.Estilo.*;

import Componentes.ArbolLlamadas;
import Componentes.BotonRedondeado;
import Componentes.CampoNumero;
import Componentes.PanelPestanas;
import Componentes.PanelRedondeado;
import Componentes.ScrollModerno;
import Componentes.Semaforo;
import Componentes.VisorCodigo;
import Model.DemandaSangre;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Banco de sangre (Fibonacci recursivo): la demanda de una semana es la suma de
 * la demanda cubierta en las dos semanas previas. D(n) = D(n-1) + D(n-2)
 */
public class PanelBancoSangre extends JPanel {

    private static final String CODIGO = """
            // Model/DemandaSangre.java  (versión simplificada, sin el árbol)
            public static long fibonacci(int semana) {
                if (semana == 1) {               // caso base: semana 1
                    return 10;
                }
                if (semana == 2) {               // caso base: semana 2
                    return 15;
                }
                // demanda = semana anterior + semana antepasada
                return fibonacci(semana - 1) + fibonacci(semana - 2);
            }""";

    private final CampoNumero campoSemana = new CampoNumero(7, DemandaSangre.SEMANA_MINIMA, DemandaSangre.SEMANA_MAXIMA, 1);
    private final CampoNumero campoStock = new CampoNumero(200, 0, 100_000, 10);
    private final JLabel lblUnidades = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblUnidadesSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final Semaforo semaforo = new Semaforo();
    private final JLabel lblSemaforoTitulo = etiqueta("-", 16, Font.BOLD, TEXTO);
    private final JLabel lblSemaforoDetalle = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblLlamadas = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblLlamadasSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final ArbolLlamadas arbol = new ArbolLlamadas();
    private PanelPestanas pestanas;

    public PanelBancoSangre() {
        super(new BorderLayout(0, 16));
        setOpaque(false);

        add(encabezado("FIBONACCI  ·  RECURSIÓN DOBLE", "Banco de sangre",
                "La demanda de cada semana es la suma de la demanda cubierta en las dos semanas previas"
                + "   ·   D(n) = D(n-1) + D(n-2)"),
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

        // Recalcula en vivo al cambiar cualquier valor
        campoSemana.addChangeListener(e -> calcular());
        campoStock.addChangeListener(e -> calcular());
        calcular();
    }

    private JComponent crearPanelEntrada() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(292, 0));

        t.add(seccion("DATOS DE ENTRADA"));
        t.add(Box.createVerticalStrut(14));
        t.add(izq(etiqueta("Semana de campaña proyectada", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(campoSemana));
        t.add(Box.createVerticalStrut(5));
        t.add(izq(etiqueta("Entre " + DemandaSangre.SEMANA_MINIMA + " y " + DemandaSangre.SEMANA_MAXIMA,
                11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(14));

        t.add(izq(etiqueta("Stock actual en el banco", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(campoStock));
        t.add(Box.createVerticalStrut(5));
        t.add(izq(etiqueta("Unidades disponibles (de 10 en 10)", 11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(18));

        BotonRedondeado btn = boton("Calcular proyección");
        btn.addActionListener(e -> calcular());
        t.add(izq(btn));

        t.add(Box.createVerticalStrut(20));
        t.add(izq(separador()));
        t.add(Box.createVerticalStrut(16));

        t.add(seccion("CASOS BASE"));
        t.add(Box.createVerticalStrut(8));
        t.add(izq(etiqueta("Semana 1:  " + DemandaSangre.DEMANDA_SEMANA_1 + " unidades", 12, Font.PLAIN, TEXTO)));
        t.add(Box.createVerticalStrut(4));
        t.add(izq(etiqueta("Semana 2:  " + DemandaSangre.DEMANDA_SEMANA_2 + " unidades", 12, Font.PLAIN, TEXTO)));

        t.add(Box.createVerticalStrut(16));
        t.add(seccion("SEMÁFORO DE RESERVA"));
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
        JPanel fila = new JPanel(new GridLayout(1, 3, SEPARACION, 0));
        fila.setOpaque(false);

        fila.add(tarjetaDato("UNIDADES REQUERIDAS", "u", alto(lblUnidades), lblUnidadesSub));

        JPanel estado = new JPanel();
        estado.setOpaque(false);
        estado.setLayout(new BoxLayout(estado, BoxLayout.Y_AXIS));
        estado.add(izq(lblSemaforoTitulo));
        estado.add(izq(lblSemaforoDetalle));
        semaforo.setMaximumSize(semaforo.getPreferredSize());
        JPanel luces = new JPanel(new BorderLayout());
        luces.setOpaque(false);
        luces.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 4, 0));
        luces.add(semaforo, BorderLayout.WEST);
        luces.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        fila.add(tarjetaDato("SEMÁFORO DE STOCK", null, luces, estado));

        fila.add(tarjetaDato("LLAMADAS RECURSIVAS", "#", alto(lblLlamadas), lblLlamadasSub));
        return fila;
    }

    private JComponent crearPestanas() {
        pestanas = new PanelPestanas();
        pestanas.agregar("Árbol de llamadas", " ",
                PanelPestanas.marco(ScrollModerno.envolver(arbol)),
                filaLeyendas(AZUL_OSCURO, "Llamada inicial", AZUL, "Recursiva", AZUL_SUAVE, "Caso base"));

        VisorCodigo codigo = new VisorCodigo();
        codigo.mostrarCodigo(CODIGO);
        pestanas.agregar("Código Java", "Método recursivo",
                PanelPestanas.marco(ScrollModerno.envolver(codigo)),
                PanelGuardias.chips("Casos base: semana 1 y 2", "Tiempo: O(2^n)", "Memoria de pila: O(n)"));
        return pestanas;
    }

    private void calcular() {
        int semana = campoSemana.getValor();
        long stock = campoStock.getValor();

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

        pestanas.setDescripcion(0, formula(r.arbol()));
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

    private static JLabel alto(JLabel l) {
        l.setPreferredSize(new Dimension(10, 50));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return l;
    }
}
