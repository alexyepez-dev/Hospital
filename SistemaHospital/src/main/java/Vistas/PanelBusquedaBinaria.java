package Vistas;

import static Componentes.Estilo.*;

import Componentes.BotonRedondeado;
import Componentes.CampoTexto;
import Componentes.PanelRedondeado;
import Componentes.TablaDatos;
import Logica.BusquedaLogica;
import Models.BusquedaModel.Expediente;
import Models.BusquedaModel.Hallazgo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Archivo de Historias: localiza un expediente por su número de historia
 * clínica con búsqueda binaria. El algoritmo vive en
 * Logica/BusquedaLogica.java; los datos de prueba están aquí mismo.
 */
public class PanelBusquedaBinaria extends JPanel {

    private static final Color VERDE = new Color(0x16A34A);
    private static final Color ROJO = new Color(0xDC2626);
    private static final Color[] COLOR_TRIAJE = {
        new Color(0xDC2626), new Color(0xF97316), new Color(0xCA8A04), new Color(0x16A34A), new Color(0x2563EB)
    };

    /** Expedientes en el orden en que fueron registrados (no por número de historia). */
    private static final List<Expediente> EXPEDIENTES = List.of(
            new Expediente(108, "Valeria Ponce", 34, "Emergencia · C-03", 2, "En atención", "Dolor torácico"),
            new Expediente(103, "Luis Mendoza", 58, "Medicina Interna · M-08", 3, "Hospitalizado", "Hipertensión descompensada"),
            new Expediente(112, "Sofía Cedeño", 6, "Pediatría · P-12", 3, "En observación", "Crisis asmática"),
            new Expediente(101, "Carlos Zambrano", 71, "UCI · U-02", 1, "En atención", "Infarto agudo de miocardio"),
            new Expediente(115, "Gabriela Loor", 27, "Emergencia · C-07", 4, "En espera", "Herida cortante en mano"),
            new Expediente(106, "Jorge Alcívar", 45, "Traumatología · T-05", 3, "Hospitalizado", "Fractura de radio"),
            new Expediente(110, "Ana Mendoza", 19, "Observación · O-01", 4, "Alta pendiente", "Gastroenteritis"),
            new Expediente(104, "Pedro Macías", 63, "Medicina Interna · M-02", 2, "En atención", "Neumonía"),
            new Expediente(113, "Daniela Vera", 41, "Cirugía · Q-04", 2, "Hospitalizado", "Apendicitis aguda"),
            new Expediente(102, "Miguel Intriago", 82, "Observación · O-03", 3, "En observación", "Deshidratación"),
            new Expediente(109, "Rosa Moreira", 55, "Emergencia · C-01", 3, "En espera", "Cólico renal"),
            new Expediente(105, "Diego Bravo", 3, "Pediatría · P-05", 3, "En observación", "Bronquiolitis"),
            new Expediente(114, "Elena Chávez", 67, "Medicina Interna · M-11", 3, "Hospitalizado", "Diabetes descompensada"),
            new Expediente(107, "Andrés Torres", 22, "Emergencia · C-05", 4, "En espera", "Trauma craneal leve"),
            new Expediente(111, "Lucía Salazar", 38, "Observación · O-02", 5, "Alta pendiente", "Cefalea tensional"));

    private final BusquedaLogica logica = new BusquedaLogica();
    /** El archivo ordenado por número de historia (requisito de la búsqueda binaria). */
    private final List<Expediente> lista = logica.ordenarPorHistoria(EXPEDIENTES);

    private final CampoTexto campoHistoria = new CampoTexto("N.º de historia (ej. 104 o H-104)");

    private final JLabel lblIndexados = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblIndexadosSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblEstado = etiqueta("-", 28, Font.BOLD, TEXTO_SEC);
    private final JLabel lblEstadoSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);

    private final JLabel lblAviso = etiqueta(" ", 13, Font.BOLD, AZUL_OSCURO);
    private final PanelRedondeado franjaAviso = franja(lblAviso);
    private final JLabel lblDescripcionTabla = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final TablaDatos tabla = new TablaDatos("Historia", "Paciente", "Edad", "Área / Cama", "Triaje", "Estado");

    // Ficha del paciente
    private final JPanel ficha = new JPanel();
    private final JLabel lblFichaVacia = etiqueta("<html>Busca un expediente o haz clic en una fila para ver su ficha.</html>",
            12, Font.PLAIN, TEXTO_SEC);
    private final JLabel fHistoria = valorFicha();
    private final JLabel fNombre = valorFicha();
    private final JLabel fEdad = valorFicha();
    private final JLabel fArea = valorFicha();
    private final JLabel fDiagnostico = valorFicha();
    private final JLabel fTriaje = valorFicha();
    private final JLabel fEstado = valorFicha();

    /** El menú de módulos y el botón Salir los pone VistaBusqueda (plantilla VistaModulo). */
    public PanelBusquedaBinaria() {
        super(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setOpaque(false);
        contenido.add(encabezado("ARCHIVO DE HISTORIAS", "Consulta de Archivo Histórico",
                "Localización exacta por número de historia clínica  ·  Búsqueda Binaria O(log n)"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(SEPARACION, SEPARACION));
        cuerpo.setOpaque(false);
        cuerpo.add(crearPanelIzquierdo(), BorderLayout.WEST);
        JPanel centro = new JPanel(new BorderLayout(0, SEPARACION));
        centro.setOpaque(false);
        centro.add(crearFilaKpis(), BorderLayout.NORTH);
        configurarTabla();
        centro.add(tarjetaTabla("Expedientes", lblDescripcionTabla, franjaAviso, tabla), BorderLayout.CENTER);
        cuerpo.add(centro, BorderLayout.CENTER);
        contenido.add(cuerpo, BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);

        lblDescripcionTabla.setText("·  archivo ordenado por número de historia");
        lblIndexados.setText(String.valueOf(lista.size()));
        lblIndexadosSub.setText("historias clínicas en archivo");
        aviso(AZUL, "Ingresa el número de historia exacto y presiona \"Localizar Expediente\"");
        mostrarTabla();
        estado("En espera", TEXTO_SEC, "Sin búsquedas todavía");
        mostrarFicha(null);
        campoHistoria.enfocar();
    }

    // ---------------------------------------------------------------- interfaz

    private JComponent crearPanelIzquierdo() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(292, 0));

        t.add(seccion("BUSCAR EN ARCHIVO"));
        t.add(Box.createVerticalStrut(12));
        t.add(izq(etiqueta("Número de historia clínica", 13, Font.BOLD, TEXTO)));
        t.add(Box.createVerticalStrut(6));
        t.add(izq(campoHistoria));
        t.add(Box.createVerticalStrut(12));
        BotonRedondeado btnLocalizar = boton("Localizar Expediente");
        t.add(izq(btnLocalizar));

        // Ficha del paciente
        t.add(Box.createVerticalStrut(22));
        t.add(seccion("FICHA DEL PACIENTE"));
        t.add(Box.createVerticalStrut(10));
        lblFichaVacia.setPreferredSize(new Dimension(220, 46));   // 2 líneas con Montserrat
        t.add(izq(lblFichaVacia));
        ficha.setOpaque(false);
        ficha.setLayout(new BoxLayout(ficha, BoxLayout.Y_AXIS));
        filaFicha("Historia", fHistoria);
        filaFicha("Paciente", fNombre);
        filaFicha("Edad", fEdad);
        filaFicha("Área / Cama", fArea);
        filaFicha("Diagnóstico", fDiagnostico);
        filaFicha("Triaje", fTriaje);
        filaFicha("Estado", fEstado);
        t.add(izq(ficha));
        t.add(Box.createVerticalGlue());

        btnLocalizar.addActionListener(e -> localizarExpediente());
        campoHistoria.alPresionarEnter(e -> localizarExpediente());
        return t;
    }

    private void filaFicha(String titulo, JLabel valor) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                BorderFactory.createEmptyBorder(6, 0, 6, 0)));
        fila.add(etiqueta(titulo, 12, Font.PLAIN, TEXTO_SEC), BorderLayout.WEST);
        valor.setHorizontalAlignment(JLabel.RIGHT);
        fila.add(valor, BorderLayout.CENTER);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        ficha.add(izq(fila));
    }

    private static JLabel valorFicha() {
        return etiqueta("-", 12, Font.BOLD, TEXTO);
    }

    private JComponent crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 2, SEPARACION, 0));
        fila.setOpaque(false);
        fila.add(tarjetaDato("EXPEDIENTES INDEXADOS", alto(lblIndexados), lblIndexadosSub));
        fila.add(tarjetaDato("ESTADO DE BÚSQUEDA", alto(lblEstado), lblEstadoSub));
        lblEstado.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                ajustarFuenteEstado();
            }
        });
        return fila;
    }

    private void configurarTabla() {
        tabla.centrar(0, 2);
        tabla.setAnchos(90, 190, 60, 200, 150, 130);
        tabla.getColumnModel().getColumn(0).setMinWidth(80);
        tabla.getColumnModel().getColumn(2).setMinWidth(56);
        tabla.getColumnModel().getColumn(4).setMinWidth(140);
        tabla.setPildora(4, v -> COLOR_TRIAJE[Character.getNumericValue(v.toString().charAt(0)) - 1]);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                if (fila >= 0) {
                    mostrarFicha(lista.get(fila));
                }
            }
        });
    }

    // ------------------------------------------------------------------ lógica

    private void mostrarTabla() {
        List<Object[]> filas = new ArrayList<>(lista.size());
        for (Expediente e : lista) {
            filas.add(new Object[]{e.codigo(), e.nombre(), e.edad(), e.area(), e.triajeTexto(), e.estado()});
        }
        tabla.setFilas(filas);
    }

    private void localizarExpediente() {
        String digitos = campoHistoria.getTexto().replaceAll("[^0-9]", "");
        if (digitos.isEmpty() || digitos.length() > 6) {
            aviso(ROJO, "Ingresa un número de historia válido (ej. 104 o H-104)");
            campoHistoria.enfocar();
            return;
        }
        int historia = Integer.parseInt(digitos);
        Hallazgo h = logica.busquedaBinaria(lista, historia);
        String revisados = h.revisados() + " de " + lista.size() + " registros revisados";
        tabla.clearSelection();

        if (h.filas().isEmpty()) {
            estado("No registrado", ROJO, "H-" + historia + "  ·  " + revisados);
            aviso(ROJO, "Expediente H-" + historia + " no registrado en el archivo");
            mostrarFicha(null);
            return;
        }
        int fila = h.filas().get(0);
        tabla.setRowSelectionInterval(fila, fila);
        tabla.scrollRectToVisible(tabla.getCellRect(fila, 0, true));
        Expediente e = lista.get(fila);
        mostrarFicha(e);
        estado("Encontrado", VERDE, "Fila " + (fila + 1) + "  ·  " + revisados);
        aviso(VERDE, "Encontrado: " + e.nombre() + " (" + e.codigo() + ")  ·  " + e.area() + "  ·  " + e.estado());
    }

    // -------------------------------------------------------------- utilidades

    private void mostrarFicha(Expediente e) {
        lblFichaVacia.setVisible(e == null);
        ficha.setVisible(e != null);
        if (e == null) {
            return;
        }
        fHistoria.setText(e.codigo());
        fNombre.setText(e.nombre());
        fEdad.setText(e.edad() + (e.edad() == 1 ? " año" : " años"));
        fArea.setText(e.area());
        fDiagnostico.setText(e.diagnostico());
        fDiagnostico.setToolTipText(e.diagnostico());
        fTriaje.setText(e.triajeTexto());
        fTriaje.setIcon(punto(COLOR_TRIAJE[e.triaje() - 1], 9, null));
        fTriaje.setForeground(COLOR_TRIAJE[e.triaje() - 1]);
        fEstado.setText(e.estado());
    }

    private void estado(String texto, Color color, String detalle) {
        lblEstado.setText(texto);
        lblEstado.setForeground(color);
        lblEstadoSub.setText(detalle);
        ajustarFuenteEstado();
    }

    /** Achica el texto del estado si no cabe en su tarjeta (pantallas pequeñas). */
    private void ajustarFuenteEstado() {
        int ancho = lblEstado.getWidth();
        int tamano = 28;
        while (ancho > 0 && tamano > 18
                && lblEstado.getFontMetrics(fuente(Font.BOLD, tamano)).stringWidth(lblEstado.getText()) > ancho) {
            tamano--;
        }
        lblEstado.setFont(fuente(Font.BOLD, tamano));
    }

    /** Franja sobre la tabla con fondo suave del color indicado. */
    private void aviso(Color color, String texto) {
        lblAviso.setText(texto);
        lblAviso.setForeground(color == AZUL ? AZUL_OSCURO : color.darker());
        franjaAviso.setBackground(color == AZUL ? AZUL_SUAVE : new Color(
                (color.getRed() + 255 * 9) / 10, (color.getGreen() + 255 * 9) / 10, (color.getBlue() + 255 * 9) / 10));
    }

    private static JLabel alto(JLabel l) {
        l.setPreferredSize(new Dimension(10, 50));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return l;
    }
}
