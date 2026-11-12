package Vista;

import static Componentes.Estilo.*;

import Componentes.BotonRedondeado;
import Componentes.CampoNumero;
import Componentes.PanelRedondeado;
import Componentes.TablaDatos;
import Model.DemandaSangre;
import Model.DemandaSangre.Estado;
import Model.DemandaSangre.Semana;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Banco de sangre: proyección semanal de la demanda (Fibonacci recursivo,
 * D(n) = D(n-1) + D(n-2)) frente al stock actual, con alerta cuando hay que
 * solicitar donantes.
 */
public class PanelBancoSangre extends JPanel {

    private static final NumberFormat FORMATO = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-EC"));

    private static final Color VERDE = new Color(0x16A34A);
    private static final Color AMARILLO = new Color(0xCA8A04);
    private static final Color ROJO = new Color(0xDC2626);

    private final CampoNumero campoSemanas = new CampoNumero(7, DemandaSangre.SEMANA_MINIMA, DemandaSangre.SEMANA_MAXIMA, 1);
    private final CampoNumero campoStock = new CampoNumero(200, 0, 100_000, 10);
    private final JLabel lblPeriodo = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblPeriodoSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblUnidades = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblUnidadesSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblTiempo = etiqueta("-", 28, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblAlerta = etiqueta(" ", 13, Font.BOLD, AZUL_OSCURO);
    private final PanelRedondeado franjaAlerta = franja(lblAlerta);
    private final TablaDatos tabla = new TablaDatos("Semana", "Demanda", "Stock", "Balance", "Estado");

    public PanelBancoSangre() {
        super(new BorderLayout(0, 16));
        setOpaque(false);

        add(encabezado("BANCO DE SANGRE  ·  MÉTODO FIBONACCI", "Banco de sangre",
                "Proyección semanal de las unidades requeridas frente al stock disponible"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(SEPARACION, SEPARACION));
        cuerpo.setOpaque(false);
        cuerpo.add(crearPanelEntrada(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, SEPARACION));
        centro.setOpaque(false);
        centro.add(crearFilaResultados(), BorderLayout.NORTH);
        tabla.centrar(0, 1, 2, 3);
        tabla.setAnchos(100, 160, 140, 110, 170);
        tabla.setPildora(4, v -> switch (estadoDe(v)) {
            case SUFICIENTE -> VERDE;
            case JUSTO -> AMARILLO;
            case DEFICIT -> ROJO;
        });
        JLabel reserva = etiqueta("·  reserva de seguridad: " + DemandaSangre.RESERVA_PORCENTAJE + " %",
                12, Font.PLAIN, TEXTO_SEC);
        centro.add(tarjetaTabla("Proyección de demanda", reserva, franjaAlerta, tabla), BorderLayout.CENTER);
        cuerpo.add(centro, BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);

        // Recalcula en vivo al cambiar cualquier valor
        campoSemanas.addChangeListener(e -> calcular());
        campoStock.addChangeListener(e -> calcular());
        calcular();
    }

    private JComponent crearPanelEntrada() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(292, 0));

        t.add(seccion("PROYECCIÓN"));
        t.add(Box.createVerticalStrut(14));
        t.add(izq(etiqueta("Semanas a proyectar", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(campoSemanas));
        t.add(Box.createVerticalStrut(5));
        t.add(izq(etiqueta("Entre " + DemandaSangre.SEMANA_MINIMA + " y " + DemandaSangre.SEMANA_MAXIMA + " semanas",
                11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(14));

        t.add(izq(etiqueta("Stock actual en el banco", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(campoStock));
        t.add(Box.createVerticalStrut(5));
        t.add(izq(etiqueta("Unidades disponibles", 11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(18));

        BotonRedondeado btn = boton("Proyectar demanda");
        btn.addActionListener(e -> calcular());
        t.add(izq(btn));
        t.add(Box.createVerticalGlue());
        return t;
    }

    private JComponent crearFilaResultados() {
        JPanel fila = new JPanel(new GridLayout(1, 3, SEPARACION, 0));
        fila.setOpaque(false);
        fila.add(tarjetaDato("PERÍODO PROYECTADO", alto(lblPeriodo), lblPeriodoSub));
        fila.add(tarjetaDato("UNIDADES REQUERIDAS", alto(lblUnidades), lblUnidadesSub));
        fila.add(tarjetaDato("TIEMPO DE CÁLCULO", alto(lblTiempo),
                etiqueta("Proyección de demanda", 12, Font.PLAIN, TEXTO_SEC)));
        return fila;
    }

    private void calcular() {
        int semanas = campoSemanas.getValor();
        long stock = campoStock.getValor();
        DemandaSangre.Proyeccion p = DemandaSangre.proyectar(semanas, stock);

        lblPeriodo.setText(String.valueOf(semanas));
        lblPeriodoSub.setText(semanas == 1 ? "semana" : "semanas  ·  de la 1 a la " + semanas);
        lblUnidades.setText(FORMATO.format(p.total()) + " u");
        Semana pico = p.semanas().get(p.semanas().size() - 1);   // la demanda crece cada semana
        lblUnidadesSub.setText("Pico: " + FORMATO.format(pico.demanda()) + " u en la semana " + pico.numero());
        lblTiempo.setText(String.format(Locale.US, "%.3f ms", p.nanos() / 1_000_000.0));

        List<Object[]> filas = new ArrayList<>(semanas);
        Semana primeraJusta = null;
        Semana primerDeficit = null;
        for (Semana s : p.semanas()) {
            long b = s.balance();
            filas.add(new Object[]{"Semana " + s.numero(), FORMATO.format(s.demanda()) + " u",
                FORMATO.format(s.stock()) + " u", (b >= 0 ? "+" : "−") + FORMATO.format(Math.abs(b)) + " u",
                s.estado().titulo});
            if (s.estado() == Estado.JUSTO && primeraJusta == null) {
                primeraJusta = s;
            }
            if (s.estado() == Estado.DEFICIT && primerDeficit == null) {
                primerDeficit = s;
            }
        }
        tabla.setFilas(filas);
        mostrarAlerta(primeraJusta, primerDeficit);
    }

    /** Franja sobre la tabla: verde si todo se cubre, amarilla sin reserva, roja si faltan unidades. */
    private void mostrarAlerta(Semana primeraJusta, Semana primerDeficit) {
        Color color;
        if (primerDeficit != null) {
            color = ROJO;
            lblAlerta.setText("⚠  Solicitar donantes: desde la semana " + primerDeficit.numero()
                    + " el stock no cubre la demanda (faltan " + FORMATO.format(-primerDeficit.balance()) + " u)");
        } else if (primeraJusta != null) {
            color = AMARILLO;
            lblAlerta.setText("Atención: desde la semana " + primeraJusta.numero() + " se cubre la demanda sin la reserva del "
                    + DemandaSangre.RESERVA_PORCENTAJE + " %");
        } else {
            color = VERDE;
            lblAlerta.setText("✔  El stock actual cubre todo el período con reserva de seguridad");
        }
        lblAlerta.setForeground(color.darker());
        franjaAlerta.setBackground(new Color(
                (color.getRed() + 255 * 9) / 10, (color.getGreen() + 255 * 9) / 10, (color.getBlue() + 255 * 9) / 10));
    }

    private static Estado estadoDe(Object titulo) {
        for (Estado e : Estado.values()) {
            if (e.titulo.equals(titulo)) {
                return e;
            }
        }
        return Estado.SUFICIENTE;
    }

    private static JLabel alto(JLabel l) {
        l.setPreferredSize(new Dimension(10, 50));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return l;
    }
}
