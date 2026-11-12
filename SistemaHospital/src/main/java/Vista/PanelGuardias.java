package Vista;

import static Componentes.Estilo.*;

import Componentes.BotonRedondeado;
import Componentes.CampoNumero;
import Componentes.PanelRedondeado;
import Componentes.TablaDatos;
import Model.FactorialRecursivo;
import Model.RotacionGuardias;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
 * Guardias de doctores: cuántas rotaciones de guardia son posibles con n
 * doctores (n!, factorial recursivo) y el cronograma con cada opción
 * (permutaciones recursivas) para que el jefe de área elija una.
 */
public class PanelGuardias extends JPanel {

    private static final NumberFormat FORMATO = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-EC"));

    /** Personal disponible para las guardias, en orden de antigüedad. */
    private static final List<String> PERSONAL = List.of(
            "Dra. Zambrano", "Dr. Vera", "Dra. Mendoza", "Dr. Cedeño", "Dra. Moreira", "Dr. Macías",
            "Dra. Alcívar", "Dr. Loor", "Dra. Bravo", "Dr. Intriago", "Dra. Torres", "Dr. Ponce");

    /** Máximo de opciones que se listan: con 8 doctores ya hay 40 320. */
    private static final int LIMITE_OPCIONES = 500;

    private final CampoNumero campoDoctores = new CampoNumero(5, 1, PERSONAL.size(), 1);
    private final JLabel lblRotaciones = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblRotacionesSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblDoctores = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblDoctoresSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblTiempo = etiqueta("-", 28, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblDescripcionTabla = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblSeleccion = etiqueta(" ", 13, Font.BOLD, AZUL_OSCURO);
    private final TablaDatos tabla = new TablaDatos("Opción", "Orden de guardia (turno 1 → turno n)");
    private List<List<String>> opciones = List.of();

    public PanelGuardias() {
        super(new BorderLayout(0, 16));
        setOpaque(false);

        add(encabezado("GUARDIAS DE DOCTORES  ·  MÉTODO FACTORIAL", "Guardias de doctores",
                "Revisa todas las rotaciones posibles del personal de guardia y elige la que se aplicará"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(SEPARACION, SEPARACION));
        cuerpo.setOpaque(false);
        cuerpo.add(crearPanelEntrada(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, SEPARACION));
        centro.setOpaque(false);
        centro.add(crearFilaResultados(), BorderLayout.NORTH);
        tabla.centrar(0);
        tabla.setAnchos(110, 900);
        tabla.getColumnModel().getColumn(0).setMinWidth(100);
        tabla.getColumnModel().getColumn(0).setMaxWidth(130);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccion();
            }
        });
        centro.add(tarjetaTabla("Cronograma de rotaciones", lblDescripcionTabla, franja(lblSeleccion), tabla),
                BorderLayout.CENTER);
        cuerpo.add(centro, BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);

        campoDoctores.addChangeListener(e -> calcular());
        calcular();
    }

    private JComponent crearPanelEntrada() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(292, 0));

        t.add(seccion("PERSONAL DE GUARDIA"));
        t.add(Box.createVerticalStrut(14));
        t.add(izq(etiqueta("Doctores de guardia", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(campoDoctores));
        t.add(Box.createVerticalStrut(5));
        t.add(izq(etiqueta("Entre 1 y " + PERSONAL.size() + " médicos", 11, Font.PLAIN, TEXTO_SEC)));
        t.add(Box.createVerticalStrut(18));

        BotonRedondeado btn = boton("Generar rotaciones");
        btn.addActionListener(e -> calcular());
        t.add(izq(btn));
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
        c.weightx = 1.4;
        c.insets = new Insets(0, 0, 0, SEPARACION);
        fila.add(tarjetaDato("ROTACIONES POSIBLES", alto(lblRotaciones), lblRotacionesSub), c);

        c.gridx = 1;
        c.weightx = 1;
        fila.add(tarjetaDato("DOCTORES ASIGNADOS", alto(lblDoctores), lblDoctoresSub), c);

        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 0);
        fila.add(tarjetaDato("TIEMPO DE CÁLCULO", alto(lblTiempo),
                etiqueta("Cálculo de rotaciones", 12, Font.PLAIN, TEXTO_SEC)), c);
        return fila;
    }

    private void calcular() {
        int n = campoDoctores.getValor();

        // n! (cuántas rotaciones hay) + la lista de rotaciones. Se toma la mejor de varias
        // corridas: la primera paga el arranque de la JVM y falsearía el tiempo.
        long rotaciones = 0;
        long mejor = Long.MAX_VALUE;
        for (int k = 0; k < 5; k++) {
            long inicio = System.nanoTime();
            rotaciones = FactorialRecursivo.calcular(n);
            opciones = RotacionGuardias.generar(PERSONAL.subList(0, n), LIMITE_OPCIONES);
            mejor = Math.min(mejor, System.nanoTime() - inicio);
        }

        String total = FORMATO.format(rotaciones);
        lblRotaciones.setText(total);
        lblRotaciones.setFont(fuente(Font.BOLD, total.length() <= 9 ? 36 : 28));
        lblRotacionesSub.setText(rotaciones == 1 ? "combinación" : "combinaciones");
        lblDoctores.setText(String.valueOf(n));
        lblDoctoresSub.setText(n == 1 ? "médico en turno" : "médicos en turno");
        lblTiempo.setText(String.format(Locale.US, "%.3f ms", mejor / 1_000_000.0));

        List<Object[]> filas = new ArrayList<>(opciones.size());
        for (int i = 0; i < opciones.size(); i++) {
            filas.add(new Object[]{"Opción " + (i + 1), String.join("  →  ", opciones.get(i))});
        }
        tabla.setFilas(filas);
        lblDescripcionTabla.setText(opciones.size() == rotaciones
                ? "·  " + total + (rotaciones == 1 ? " opción" : " opciones")
                : "·  se muestran las primeras " + FORMATO.format(opciones.size()) + " de " + total + " opciones");
        mostrarSeleccion();
    }

    private void mostrarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= opciones.size()) {
            lblSeleccion.setText("Selecciona una opción de la tabla para revisar su orden de guardia");
            lblSeleccion.setToolTipText(null);
            return;
        }
        List<String> orden = opciones.get(fila);
        StringBuilder sb = new StringBuilder("Opción " + (fila + 1) + ":  ");
        for (int i = 0; i < orden.size(); i++) {
            sb.append(i == 0 ? "" : "  ·  ").append("T").append(i + 1).append(" ").append(orden.get(i));
        }
        lblSeleccion.setText(sb.toString());
        lblSeleccion.setToolTipText(String.join(" → ", orden));
    }

    /** Fija la altura del número grande para que las tarjetas no salten al cambiar de tamaño de letra. */
    private static JLabel alto(JLabel l) {
        l.setPreferredSize(new Dimension(10, 50));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return l;
    }
}
