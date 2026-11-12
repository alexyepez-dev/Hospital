package Vista;

import static Componentes.Estilo.*;

import Componentes.BotonRedondeado;
import Componentes.CampoTexto;
import Componentes.ItemMenu;
import Componentes.MenuLateral;
import Componentes.PanelRedondeado;
import Componentes.TablaDatos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * Búsqueda Clínica: buscador de pacientes en sala (búsqueda secuencial por
 * nombre) y de expedientes en el archivo (búsqueda binaria por número de
 * historia clínica).
 *
 * Está separado de Busqueda.java (que es un formulario de NetBeans) para que
 * el editor visual no pueda borrar este código al regenerar el formulario.
 */
public class PanelBusqueda extends JPanel {

    private static final Color VERDE = new Color(0x16A34A);
    private static final Color ROJO = new Color(0xDC2626);
    private static final Color[] COLOR_TRIAJE = {
        new Color(0xDC2626), new Color(0xF97316), new Color(0xCA8A04), new Color(0x16A34A), new Color(0x2563EB)
    };
    private static final java.util.regex.Pattern TILDES = java.util.regex.Pattern.compile("\\p{M}");
    private static final String[] NOMBRE_TRIAJE ={"Resucitación", "Emergencia", "Urgente", "Menos urgente", "No urgente"};

    /** Expediente clínico de un paciente (solo lo usa esta vista). */
    private record Expediente(int historia, String nombre, int edad, String area, int triaje,
            String estado, String diagnostico) {

        String codigo() {
            return "H-" + historia;
        }

        String triajeTexto() {
            return triaje + " · " + NOMBRE_TRIAJE[triaje - 1];
        }
    }

    /** Resultado de una búsqueda: filas encontradas y cuántos registros se revisaron. */
    private record Hallazgo(List<Integer> filas, int revisados) {
    }

    private enum Modulo {
        SALA("Consulta en Sala", "Método: Búsqueda Secuencial O(n)", "CONSULTA EN SALA",
                "Localización Rápida de Pacientes en Sala",
                "Búsqueda por coincidencia de nombre o apellido  ·  Búsqueda Secuencial O(n)"),
        ARCHIVO("Archivo de Historias", "Método: Búsqueda Binaria O(log n)", "ARCHIVO DE HISTORIAS",
                "Consulta de Archivo Histórico",
                "Localización exacta por número de historia clínica  ·  Búsqueda Binaria O(log n)");

        final String titulo;
        final String metodo;
        final String chip;
        final String cabecera;
        final String subtitulo;

        Modulo(String titulo, String metodo, String chip, String cabecera, String subtitulo) {
            this.titulo = titulo;
            this.metodo = metodo;
            this.chip = chip;
            this.cabecera = cabecera;
            this.subtitulo = subtitulo;
        }
    }

    /** Pacientes en sala, en el orden en que fueron registrados (no por número de historia). */
    private final List<Expediente> pacientes = new ArrayList<>(List.of(
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
            new Expediente(111, "Lucía Salazar", 38, "Observación · O-02", 5, "Alta pendiente", "Cefalea tensional")));

    /** Lo que muestra la tabla: la sala tal cual, o el archivo ordenado por historia. */
    private List<Expediente> lista = pacientes;

    private final JPanel encabezado = new JPanel(new BorderLayout());
    private final JPanel bloqueSala = new JPanel();
    private final JPanel bloqueArchivo = new JPanel();
    private final CampoTexto campoNombre = new CampoTexto("Nombre o apellido");
    private final CampoTexto campoHistoria = new CampoTexto("N.º de historia (ej. 104 o H-104)");

    private final JLabel lblIndexados = etiqueta("-", 36, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblIndexadosSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblEstado = etiqueta("-", 28, Font.BOLD, TEXTO_SEC);
    private final JLabel lblEstadoSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblTiempo = etiqueta("-", 28, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblTiempoSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);

    private final JLabel lblAviso = etiqueta(" ", 13, Font.BOLD, AZUL_OSCURO);
    private final PanelRedondeado franjaAviso = franja(lblAviso);
    private final JLabel lblDescripcionTabla = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final TablaDatos tabla = new TablaDatos("Historia", "Paciente", "Edad", "Área / Cama", "Triaje", "Estado");

    // Ficha del paciente
    private final JPanel ficha = new JPanel();
    private final JLabel lblFichaVacia = etiqueta("<html>Busca un paciente o haz clic en una fila para ver su ficha.</html>",
            12, Font.PLAIN, TEXTO_SEC);
    private final JLabel fHistoria = valorFicha();
    private final JLabel fNombre = valorFicha();
    private final JLabel fEdad = valorFicha();
    private final JLabel fArea = valorFicha();
    private final JLabel fDiagnostico = valorFicha();
    private final JLabel fTriaje = valorFicha();
    private final JLabel fEstado = valorFicha();

    public PanelBusqueda(Runnable accionSalir) {
        super(new BorderLayout());
        setBackground(FONDO);
        add(crearMenu(accionSalir), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));
        encabezado.setOpaque(false);
        contenido.add(encabezado, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(SEPARACION, SEPARACION));
        cuerpo.setOpaque(false);
        cuerpo.add(crearPanelIzquierdo(), BorderLayout.WEST);
        JPanel centro = new JPanel(new BorderLayout(0, SEPARACION));
        centro.setOpaque(false);
        centro.add(crearFilaKpis(), BorderLayout.NORTH);
        configurarTabla();
        centro.add(tarjetaTabla("Pacientes", lblDescripcionTabla, franjaAviso, tabla), BorderLayout.CENTER);
        cuerpo.add(centro, BorderLayout.CENTER);
        contenido.add(cuerpo, BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);

        cambiarModulo(Modulo.SALA);
    }

    // ---------------------------------------------------------------- interfaz

    private JComponent crearMenu(Runnable accionSalir) {
        MenuLateral menu = new MenuLateral("Búsqueda Clínica", 280);
        menu.agregarSeccion("MÓDULOS");
        ItemMenu sala = menu.agregarItem("CS", Modulo.SALA.titulo, Modulo.SALA.metodo,
                () -> cambiarModulo(Modulo.SALA));
        menu.agregarItem("AH", Modulo.ARCHIVO.titulo, Modulo.ARCHIVO.metodo,
                () -> cambiarModulo(Modulo.ARCHIVO));
        menu.agregarSalir("Volver al menú principal", accionSalir);
        menu.seleccionar(sala);
        return menu;
    }

    private JComponent crearPanelIzquierdo() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(292, 0));

        // Consulta en sala
        bloqueSala.setOpaque(false);
        bloqueSala.setLayout(new BoxLayout(bloqueSala, BoxLayout.Y_AXIS));
        bloqueSala.add(seccion("BUSCAR EN SALA"));
        bloqueSala.add(Box.createVerticalStrut(12));
        bloqueSala.add(izq(etiqueta("Nombre o apellido", 13, Font.BOLD, TEXTO)));
        bloqueSala.add(Box.createVerticalStrut(6));
        bloqueSala.add(izq(campoNombre));
        bloqueSala.add(Box.createVerticalStrut(12));
        BotonRedondeado btnBuscar = boton("Buscar Paciente");
        bloqueSala.add(izq(btnBuscar));
        t.add(izq(bloqueSala));

        // Archivo de historias
        bloqueArchivo.setOpaque(false);
        bloqueArchivo.setLayout(new BoxLayout(bloqueArchivo, BoxLayout.Y_AXIS));
        bloqueArchivo.add(seccion("BUSCAR EN ARCHIVO"));
        bloqueArchivo.add(Box.createVerticalStrut(12));
        bloqueArchivo.add(izq(etiqueta("Número de historia clínica", 13, Font.BOLD, TEXTO)));
        bloqueArchivo.add(Box.createVerticalStrut(6));
        bloqueArchivo.add(izq(campoHistoria));
        bloqueArchivo.add(Box.createVerticalStrut(12));
        BotonRedondeado btnLocalizar = boton("Localizar Expediente");
        bloqueArchivo.add(izq(btnLocalizar));
        t.add(izq(bloqueArchivo));

        // Ficha del paciente
        t.add(Box.createVerticalStrut(22));
        t.add(seccion("FICHA DEL PACIENTE"));
        t.add(Box.createVerticalStrut(10));
        lblFichaVacia.setPreferredSize(new Dimension(220, 36));
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

        btnBuscar.addActionListener(e -> buscarEnSala());
        campoNombre.alPresionarEnter(e -> buscarEnSala());
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
        JPanel fila = new JPanel(new GridLayout(1, 3, SEPARACION, 0));
        fila.setOpaque(false);
        fila.add(tarjetaDato("EXPEDIENTES INDEXADOS", alto(lblIndexados), lblIndexadosSub));
        fila.add(tarjetaDato("ESTADO DE BÚSQUEDA", alto(lblEstado), lblEstadoSub));
        lblEstado.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                ajustarFuenteEstado();
            }
        });
        fila.add(tarjetaDato("TIEMPO DE LOCALIZACIÓN", alto(lblTiempo), lblTiempoSub));
        return fila;
    }

    private void configurarTabla() {
        // Varias filas resaltadas a la vez: la búsqueda por nombre puede tener más de una coincidencia
        tabla.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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

    private void cambiarModulo(Modulo nuevo) {
        encabezado.removeAll();
        encabezado.add(Componentes.Estilo.encabezado(nuevo.chip, nuevo.cabecera, nuevo.subtitulo), BorderLayout.CENTER);
        encabezado.revalidate();
        encabezado.repaint();

        boolean sala = nuevo == Modulo.SALA;
        bloqueSala.setVisible(sala);
        bloqueArchivo.setVisible(!sala);

        if (sala) {
            lista = pacientes;
            lblDescripcionTabla.setText("·  en sala, por orden de registro");
            lblIndexadosSub.setText("pacientes en sala");
            aviso(AZUL, "Escribe un nombre o apellido y presiona \"Buscar Paciente\"");
            campoNombre.enfocar();
        } else {
            lista = ordenarPorHistoria(pacientes);
            lblDescripcionTabla.setText("·  archivo ordenado por número de historia");
            lblIndexadosSub.setText("historias clínicas en archivo");
            aviso(AZUL, "Ingresa el número de historia exacto y presiona \"Localizar Expediente\"");
            campoHistoria.enfocar();
        }
        lblIndexados.setText(String.valueOf(lista.size()));
        mostrarTabla();
        estado("En espera", TEXTO_SEC, "Sin búsquedas todavía");
        lblTiempo.setText("-");
        lblTiempoSub.setText(" ");
        mostrarFicha(null);
    }

    /** La búsqueda binaria exige la lista ordenada por historia: si no lo está, se ordena una copia. */
    private static List<Expediente> ordenarPorHistoria(List<Expediente> origen) {
        List<Expediente> copia = new ArrayList<>(origen);
        for (int i = 1; i < copia.size(); i++) {
            if (copia.get(i - 1).historia() > copia.get(i).historia()) {
                copia.sort(Comparator.comparingInt(Expediente::historia));
                break;
            }
        }
        return copia;
    }

    private void mostrarTabla() {
        List<Object[]> filas = new ArrayList<>(lista.size());
        for (Expediente e : lista) {
            filas.add(new Object[]{e.codigo(), e.nombre(), e.edad(), e.area(), e.triajeTexto(), e.estado()});
        }
        tabla.setFilas(filas);
    }

    private void buscarEnSala() {
        String texto = campoNombre.getTexto();
        if (texto.isEmpty()) {
            aviso(ROJO, "Escribe el nombre o apellido del paciente");
            campoNombre.enfocar();
            return;
        }
        Hallazgo h = busquedaSecuencial(lista, texto);
        long nanos = medir(() -> busquedaSecuencial(lista, texto));
        mostrarResultado(h, nanos, "\"" + texto + "\"", "Paciente no encontrado en sala");
    }

    private void localizarExpediente() {
        String digitos = campoHistoria.getTexto().replaceAll("[^0-9]", "");
        if (digitos.isEmpty() || digitos.length() > 6) {
            aviso(ROJO, "Ingresa un número de historia válido (ej. 104 o H-104)");
            campoHistoria.enfocar();
            return;
        }
        int historia = Integer.parseInt(digitos);
        Hallazgo h = busquedaBinaria(lista, historia);
        long nanos = medir(() -> busquedaBinaria(lista, historia));
        mostrarResultado(h, nanos, "H-" + historia, "Expediente H-" + historia + " no registrado en el archivo");
    }

    private void mostrarResultado(Hallazgo h, long nanos, String buscado, String mensajeNoEncontrado) {
        lblTiempo.setText(formatoMs(nanos));
        lblTiempoSub.setText("Registros revisados: " + h.revisados() + " de " + lista.size());
        tabla.clearSelection();

        if (h.filas().isEmpty()) {
            estado("No registrado", ROJO, "Sin coincidencias para " + buscado);
            aviso(ROJO, mensajeNoEncontrado);
            mostrarFicha(null);
            return;
        }
        for (int fila : h.filas()) {
            tabla.addRowSelectionInterval(fila, fila);
        }
        int primera = h.filas().get(0);
        tabla.scrollRectToVisible(tabla.getCellRect(primera, 0, true));
        Expediente e = lista.get(primera);
        mostrarFicha(e);

        int n = h.filas().size();
        estado("Encontrado", VERDE, n == 1 ? "Fila " + (primera + 1) + " de la tabla" : n + " coincidencias");
        aviso(VERDE, n == 1
                ? "Encontrado: " + e.nombre() + " (" + e.codigo() + ")  ·  " + e.area() + "  ·  " + e.estado()
                : n + " pacientes coinciden con " + buscado + ". Haz clic en una fila para ver su ficha.");
    }

    // ------------------------------------------------------------- algoritmos

    /**
     * Búsqueda secuencial: recorre la lista uno por uno comparando el texto con
     * el nombre (coincide con el inicio del nombre o de un apellido, sin
     * distinguir mayúsculas ni tildes). Devuelve todas las coincidencias.
     */
    private static Hallazgo busquedaSecuencial(List<Expediente> lista, String texto) {
        String buscado = " " + normalizar(texto);
        List<Integer> filas = new ArrayList<>();
        int revisados = 0;
        for (int i = 0; i < lista.size(); i++) {
            revisados++;
            if ((" " + normalizar(lista.get(i).nombre())).contains(buscado)) {
                filas.add(i);
            }
        }
        return new Hallazgo(filas, revisados);
    }

    /** Búsqueda binaria sobre la lista ordenada por historia: parte el rango a la mitad en cada paso. */
    private static Hallazgo busquedaBinaria(List<Expediente> ordenada, int historia) {
        int inicio = 0;
        int fin = ordenada.size() - 1;
        int revisados = 0;
        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            revisados++;
            int actual = ordenada.get(medio).historia();
            if (actual == historia) {
                return new Hallazgo(List.of(medio), revisados);
            }
            if (actual < historia) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return new Hallazgo(List.of(), revisados);
    }

    /** Mejor de varias corridas medidas con System.nanoTime(): la primera paga el arranque de la JVM. */
    private static long medir(Runnable busqueda) {
        long mejor = Long.MAX_VALUE;
        for (int k = 0; k < 7; k++) {
            long inicio = System.nanoTime();
            busqueda.run();
            mejor = Math.min(mejor, System.nanoTime() - inicio);
        }
        return mejor;
    }

    /** Minúsculas y sin tildes, para que "Cedeno" encuentre a "Cedeño" y "jose" a "José". */
    private static String normalizar(String s) {
        return TILDES.matcher(Normalizer.normalize(s, Normalizer.Form.NFD)).replaceAll("")
                .toLowerCase(Locale.ROOT).trim();
    }

    /** Milisegundos con más decimales cuando el tiempo es muy pequeño (si no, saldría 0.000 ms). */
    private static String formatoMs(long nanos) {
        double ms = nanos / 1_000_000.0;
        return String.format(Locale.US, ms < 0.001 ? "%.5f ms" : "%.3f ms", ms);
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
        fTriaje.setText("● " + e.triajeTexto());
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
