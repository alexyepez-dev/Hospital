package Vista;

import static Componentes.Estilo.*;

import Componentes.BotonRedondeado;
import Componentes.CampoNumero;
import Componentes.CampoTexto;
import Componentes.ItemMenu;
import Componentes.MenuLateral;
import Componentes.PanelRedondeado;
import Componentes.ScrollModerno;
import Componentes.SelectorSegmentos;
import Componentes.TablaPacientes;
import Model.GeneradorPacientes;
import Model.Paciente;
import Servicio.OrdenamientoService;
import Servicio.OrdenamientoService.Algoritmo;
import Servicio.OrdenamientoService.Criterio;
import Servicio.OrdenamientoService.Paso;
import Servicio.OrdenamientoService.Resultado;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * Gestión de pacientes por área hospitalaria. Cada área usa un método de
 * ordenamiento y un criterio fijos:
 *
 *   Censo Hospitalario   -> Burbuja    · por edad
 *   Consulta Externa     -> Selección  · por hora de llegada (tiempo de espera)
 *   Admisión Urgencias   -> Inserción  · inserta un paciente en su puesto según triaje
 *   Triaje de Emergencia -> QuickSort  · por prioridad (triaje)
 *
 * Fluidez de la interfaz:
 *  - SwingWorker: el algoritmo se calcula en un hilo de fondo.
 *  - javax.swing.Timer: reproduce los pasos sobre la tabla, uno por tick, en el EDT.
 *
 * Toda la lógica de ordenamiento vive en Servicio/OrdenamientoService.java.
 */
public class PanelOrdenamiento extends JPanel {

    private static final Logger LOG = Logger.getLogger(PanelOrdenamiento.class.getName());

    /** Milisegundos entre pasos para Lenta / Normal / Rápida. */
    private static final int[] RETARDOS = {420, 150, 35};
    private static final int MAX_PACIENTES = 60;
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Color ROJO = new Color(0xDC2626);
    private static final Color[] COLOR_TRIAJE = {
        new Color(0xDC2626), new Color(0xF97316), new Color(0xCA8A04), new Color(0x16A34A), new Color(0x2563EB)
    };

    /** Área hospitalaria: algoritmo, criterio y textos propios de cada pantalla. */
    private enum Area {
        CENSO("CH", "Censo Hospitalario", Algoritmo.BURBUJA, Criterio.EDAD,
                "Reorganizar Censo por Edad", "Censo reorganizado por edades",
                "Agrupa a los pacientes por edad: niños, adultos y tercera edad"),
        CONSULTA("CE", "Consulta Externa", Algoritmo.SELECCION, Criterio.LLEGADA,
                "Asignar Turnos por Espera", "Turnos asignados por tiempo de espera",
                "Se atiende primero a quien lleva más tiempo esperando"),
        ADMISION("AU", "Admisión Urgencias", Algoritmo.INSERCION, Criterio.PRIORIDAD,
                null, null,
                "Registra ingresos por ambulancia y ubícalos directo en su puesto según el triaje"),
        TRIAJE("TE", "Triaje de Emergencia", Algoritmo.QUICKSORT, Criterio.PRIORIDAD,
                "Priorizar Sala de Choque", "Sala de choque priorizada por triaje",
                "Prioriza la atención: los códigos rojos quedan arriba");

        final String insignia;
        final String titulo;
        final Algoritmo algoritmo;
        final Criterio criterio;
        final String textoBoton;
        final String bitacora;
        final String descripcion;

        Area(String insignia, String titulo, Algoritmo algoritmo, Criterio criterio,
                String textoBoton, String bitacora, String descripcion) {
            this.insignia = insignia;
            this.titulo = titulo;
            this.algoritmo = algoritmo;
            this.criterio = criterio;
            this.textoBoton = textoBoton;
            this.bitacora = bitacora;
            this.descripcion = descripcion;
        }
    }

    private final OrdenamientoService servicio = new OrdenamientoService();
    private final Random azar = new Random();
    /** Pacientes de la sala en su orden actual (compartido entre todas las áreas). */
    private List<Paciente> sala = List.of();
    private int siguienteCodigo = 1;
    private Area area = Area.CENSO;

    // Encabezado
    private final JPanel encabezado = new JPanel(new BorderLayout());

    // Controles
    private final JPanel bloqueGenerar = new JPanel();
    private final JPanel bloqueAmbulancia = new JPanel();
    private final CampoNumero campoCantidad = new CampoNumero(12, 5, 40, 1);
    private final CampoTexto campoNombre = new CampoTexto("Nombre y apellido");
    private final CampoNumero campoEdad = new CampoNumero(35, 0, 110, 1);
    private final SelectorSegmentos selTriaje = new SelectorSegmentos(0, true, "1", "2", "3", "4", "5");
    private final JLabel lblTriaje = etiqueta(" ", 12, Font.BOLD, TEXTO);
    private final JLabel lblErrorIngreso = etiqueta(" ", 11, Font.BOLD, ROJO);
    private final SelectorSegmentos selVelocidad = new SelectorSegmentos(10, true, "Lenta", "Normal", "Rápida");
    private final BotonRedondeado btnOrdenar = boton(" ");
    private final BotonRedondeado btnAmbulancia = boton("Ingresar Ambulancia");
    private final BotonRedondeado btnPausa = botonSecundario("Pausar");
    private final BotonRedondeado btnFinal = botonSecundario("Saltar al final");
    /** Pausar / Saltar al final: no se muestran en Admisión (un ingreso dura pocos pasos). */
    private final JPanel filaPausa = new JPanel();

    // KPIs
    private final JLabel lblSala = etiqueta("0", 30, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblSalaSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblCriticos = etiqueta("0", 30, Font.BOLD, ROJO);
    private final JLabel lblCriticosSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblKpi3Titulo = etiqueta(" ", 11, Font.BOLD, TEXTO_SEC);
    private final JLabel lblKpi3 = etiqueta("-", 30, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblKpi3Sub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    private final JLabel lblTiempo = etiqueta("-", 25, Font.BOLD, AZUL_OSCURO);
    private final JLabel lblTiempoSub = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);

    // Tabla y bitácora
    private final TablaPacientes tabla = new TablaPacientes();
    private final JLabel lblDescripcionTabla = etiqueta(" ", 12, Font.PLAIN, TEXTO_SEC);
    /** Franja sobre la tabla que explica en palabras lo que está pasando. */
    private final JLabel lblPaso = etiqueta(" ", 13, Font.BOLD, AZUL_OSCURO);
    private final JTextArea bitacora = new JTextArea();

    // Animación
    private Resultado resultado;
    private String mensajeAlTerminar;
    private Timer timer;
    private int indicePaso;
    private int turnoOrden;          // descarta resultados de un SwingWorker ya obsoleto
    /** Refresca la "mayor espera" cada 30 s. */
    private final Timer reloj = new Timer(30_000, e -> actualizarKpis());

    public PanelOrdenamiento(Runnable accionSalir) {
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
        cuerpo.add(crearControles(), BorderLayout.WEST);
        JPanel centro = new JPanel(new BorderLayout(0, SEPARACION));
        centro.setOpaque(false);
        centro.add(crearFilaKpis(), BorderLayout.NORTH);
        centro.add(crearPanelTabla(), BorderLayout.CENTER);
        cuerpo.add(centro, BorderLayout.CENTER);
        contenido.add(cuerpo, BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);

        sala = GeneradorPacientes.generar(campoCantidad.getValor(), azar);
        siguienteCodigo = sala.size() + 1;
        registrar("Turno iniciado: " + sala.size() + " pacientes en sala");
        cambiarArea(Area.CENSO);
        reloj.start();
    }

    // ---------------------------------------------------------------- interfaz

    private JComponent crearMenu(Runnable accionSalir) {
        MenuLateral menu = new MenuLateral("Ordenamiento");
        menu.agregarSeccion("ÁREAS");
        ItemMenu primero = null;
        for (Area a : Area.values()) {
            ItemMenu item = menu.agregarItem(a.insignia, a.titulo, "Método " + a.algoritmo.nombre,
                    () -> cambiarArea(a));
            if (primero == null) {
                primero = item;
            }
        }
        menu.agregarSalir("Volver al menú principal", () -> {
            detenerAnimacion();
            reloj.stop();
            accionSalir.run();
        });
        menu.seleccionar(primero);
        return menu;
    }

    private JComponent crearControles() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setPreferredSize(new Dimension(292, 0));

        // Sala simulada (todas las áreas menos Admisión)
        bloqueGenerar.setOpaque(false);
        bloqueGenerar.setLayout(new BoxLayout(bloqueGenerar, BoxLayout.Y_AXIS));
        bloqueGenerar.add(seccion("SALA DE PACIENTES"));
        bloqueGenerar.add(Box.createVerticalStrut(12));
        bloqueGenerar.add(izq(etiqueta("Cantidad de pacientes", 13, Font.BOLD, TEXTO)));
        bloqueGenerar.add(Box.createVerticalStrut(6));
        bloqueGenerar.add(izq(campoCantidad));
        bloqueGenerar.add(Box.createVerticalStrut(8));
        BotonRedondeado btnGenerar = botonSecundario("Generar nuevos pacientes");
        btnGenerar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        bloqueGenerar.add(izq(btnGenerar));
        t.add(izq(bloqueGenerar));

        // Ingreso por ambulancia (solo Admisión)
        bloqueAmbulancia.setOpaque(false);
        bloqueAmbulancia.setLayout(new BoxLayout(bloqueAmbulancia, BoxLayout.Y_AXIS));
        bloqueAmbulancia.add(seccion("INGRESO POR AMBULANCIA"));
        bloqueAmbulancia.add(Box.createVerticalStrut(12));
        bloqueAmbulancia.add(izq(etiqueta("Nombre", 13, Font.BOLD, TEXTO)));
        bloqueAmbulancia.add(Box.createVerticalStrut(6));
        bloqueAmbulancia.add(izq(campoNombre));
        bloqueAmbulancia.add(Box.createVerticalStrut(8));
        bloqueAmbulancia.add(izq(etiqueta("Edad", 13, Font.BOLD, TEXTO)));
        bloqueAmbulancia.add(Box.createVerticalStrut(6));
        bloqueAmbulancia.add(izq(campoEdad));
        bloqueAmbulancia.add(Box.createVerticalStrut(8));
        JLabel lblTituloTriaje = etiqueta("Triaje", 13, Font.BOLD, TEXTO);
        lblTituloTriaje.setToolTipText("1 = más urgente · 5 = menos urgente (escala de Manchester)");
        bloqueAmbulancia.add(izq(lblTituloTriaje));
        bloqueAmbulancia.add(Box.createVerticalStrut(6));
        bloqueAmbulancia.add(izq(selTriaje));
        bloqueAmbulancia.add(Box.createVerticalStrut(5));
        bloqueAmbulancia.add(izq(lblTriaje));
        bloqueAmbulancia.add(Box.createVerticalStrut(10));
        btnAmbulancia.setBackground(ROJO);
        bloqueAmbulancia.add(izq(btnAmbulancia));
        lblErrorIngreso.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        lblErrorIngreso.setVisible(false);
        bloqueAmbulancia.add(izq(lblErrorIngreso));
        t.add(izq(bloqueAmbulancia));

        t.add(Box.createVerticalStrut(16));
        t.add(seccion("VELOCIDAD DE VISUALIZACIÓN"));
        t.add(Box.createVerticalStrut(8));
        selVelocidad.setSeleccion(1);
        t.add(izq(selVelocidad));

        t.add(Box.createVerticalGlue());
        t.add(Box.createVerticalStrut(16));
        t.add(izq(btnOrdenar));
        t.add(Box.createVerticalStrut(8));
        filaPausa.setLayout(new GridLayout(1, 2, 8, 0));
        JPanel fila = filaPausa;
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        fila.add(btnPausa);
        fila.add(btnFinal);
        t.add(izq(fila));

        // Eventos
        campoCantidad.addChangeListener(e -> generar());
        btnGenerar.addActionListener(e -> generar());
        selTriaje.alCambiar(i -> {
            lblTriaje.setText("● " + (i + 1) + " · " + Paciente.nombreTriaje(i + 1));
            lblTriaje.setForeground(COLOR_TRIAJE[i]);
        });
        selTriaje.setSeleccion(2);
        btnAmbulancia.addActionListener(e -> ingresarAmbulancia());
        campoNombre.alPresionarEnter(e -> ingresarAmbulancia());
        selVelocidad.alCambiar(i -> {
            if (timer != null) {
                timer.setDelay(RETARDOS[i]);
            }
        });
        btnOrdenar.addActionListener(e -> ordenar());
        btnPausa.addActionListener(e -> pausarOReanudar());
        btnFinal.addActionListener(e -> saltarAlFinal());
        return t;
    }

    private JComponent crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, SEPARACION, 0));
        fila.setOpaque(false);
        fila.add(tarjetaKpi(etiqueta("PACIENTES EN SALA", 11, Font.BOLD, TEXTO_SEC), lblSala, lblSalaSub));
        fila.add(tarjetaKpi(etiqueta("CASOS CRÍTICOS", 11, Font.BOLD, TEXTO_SEC), lblCriticos, lblCriticosSub));
        fila.add(tarjetaKpi(lblKpi3Titulo, lblKpi3, lblKpi3Sub));
        fila.add(tarjetaKpi(etiqueta("TIEMPO DE RESPUESTA", 11, Font.BOLD, TEXTO_SEC), lblTiempo, lblTiempoSub));
        return fila;
    }

    private static JComponent tarjetaKpi(JLabel titulo, JLabel valor, JLabel sub) {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        int s = SOMBRA;   // relleno lateral menor que el de las tarjetas normales: caben 4 en fila
        t.setBorder(BorderFactory.createEmptyBorder(14 + s - s / 2, 14 + s, 14 + s + s / 2, 10 + s));
        t.add(izq(titulo));
        t.add(Box.createVerticalStrut(4));
        valor.setPreferredSize(new Dimension(10, 42));
        valor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        t.add(izq(valor));
        t.add(izq(sub));
        return t;
    }

    private JComponent crearPanelTabla() {
        PanelRedondeado t = tarjeta();
        t.setLayout(new BorderLayout(0, 10));

        // Cabecera en una sola línea: título + criterio a la izquierda, leyenda a la derecha
        JPanel cab = new JPanel(new BorderLayout(12, 0));
        cab.setOpaque(false);
        JPanel textos = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        textos.setOpaque(false);
        textos.add(etiqueta("Pacientes en sala", 16, Font.BOLD, TEXTO));
        textos.add(Box.createHorizontalStrut(10));
        textos.add(lblDescripcionTabla);
        cab.add(textos, BorderLayout.CENTER);
        cab.add(TablaPacientes.leyenda(), BorderLayout.EAST);

        PanelRedondeado franja = new PanelRedondeado();
        franja.setBackground(AZUL_SUAVE);
        franja.setRadio(10);
        franja.setLayout(new BorderLayout());
        franja.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        franja.add(lblPaso, BorderLayout.CENTER);

        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setOpaque(false);
        norte.add(cab, BorderLayout.NORTH);
        norte.add(franja, BorderLayout.SOUTH);
        t.add(norte, BorderLayout.NORTH);

        t.add(marco(ScrollModerno.envolver(tabla)), BorderLayout.CENTER);

        // Bitácora clínica
        bitacora.setEditable(false);
        bitacora.setFocusable(false);
        bitacora.setFont(fuente(Font.PLAIN, 12));
        bitacora.setForeground(TEXTO);
        bitacora.setBackground(Color.WHITE);
        bitacora.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        JPanel zonaBitacora = new JPanel(new BorderLayout(0, 6));
        zonaBitacora.setOpaque(false);
        zonaBitacora.add(seccion("BITÁCORA CLÍNICA"), BorderLayout.NORTH);
        JComponent marcoBitacora = marco(ScrollModerno.envolver(bitacora));
        marcoBitacora.setPreferredSize(new Dimension(10, 74));
        zonaBitacora.add(marcoBitacora, BorderLayout.CENTER);
        t.add(zonaBitacora, BorderLayout.SOUTH);
        return t;
    }

    // ------------------------------------------------------------------ lógica

    private void cambiarArea(Area nueva) {
        if (animando()) {
            saltarAlFinal();               // no se pierde lo que se estaba ordenando o ingresando
        }
        area = nueva;
        encabezado.removeAll();
        encabezado.add(Componentes.Estilo.encabezado(
                nueva.titulo.toUpperCase(Locale.ROOT) + "  ·  MÉTODO " + nueva.algoritmo.nombre.toUpperCase(Locale.ROOT),
                nueva.titulo, nueva.descripcion), BorderLayout.CENTER);
        encabezado.revalidate();
        encabezado.repaint();

        boolean admision = nueva == Area.ADMISION;
        bloqueGenerar.setVisible(!admision);
        bloqueAmbulancia.setVisible(admision);
        btnOrdenar.setVisible(!admision);
        filaPausa.setVisible(!admision);
        mostrarError(null);
        lblKpi3Titulo.setText(nueva == Area.CENSO ? "EDAD PROMEDIO" : "MAYOR ESPERA");
        lblDescripcionTabla.setText("·  por " + nueva.criterio.titulo.toLowerCase(Locale.ROOT));
        lblDescripcionTabla.setToolTipText(nueva.criterio.descripcion);

        restablecer();

        if (admision) {
            // Admisión necesita la sala ordenada por triaje para insertar cada ingreso en su puesto
            List<Paciente> porTriaje = servicio.ordenar(sala, Algoritmo.INSERCION, Criterio.PRIORIDAD).ordenados();
            if (!porTriaje.equals(sala)) {
                sala = porTriaje;
                tabla.setPacientes(sala);
                registrar("Sala ordenada por triaje para recibir ingresos");
            }
            actualizarKpis();
            campoNombre.enfocar();
        }
    }

    private void generar() {
        sala = GeneradorPacientes.generar(campoCantidad.getValor(), azar);
        siguienteCodigo = sala.size() + 1;
        restablecer();
        registrar("Sala cargada con " + sala.size() + " pacientes");
    }

    /** Muestra la sala en su orden actual, sin animación en curso. */
    private void restablecer() {
        detenerAnimacion();
        turnoOrden++;
        resultado = null;
        indicePaso = 0;
        tabla.setPacientes(sala);
        lblTiempo.setText("-");
        lblTiempoSub.setText(area.algoritmo.nombre + " · pendiente");
        if (area == Area.ADMISION) {
            lblPaso.setText("Registra el ingreso: el paciente se ubicará directo en su puesto según el triaje");
        } else {
            lblPaso.setText("Presiona \"" + area.textoBoton + "\" para ver el proceso paso a paso");
            btnOrdenar.setText(area.textoBoton);
        }
        btnOrdenar.setEnabled(true);
        btnAmbulancia.setEnabled(true);
        btnPausa.setText("Pausar");
        btnPausa.setEnabled(false);
        btnFinal.setEnabled(false);
        actualizarKpis();
    }

    /** Calcula el ordenamiento del área en un hilo de fondo y luego inicia la animación. */
    private void ordenar() {
        restablecer();
        int turno = turnoOrden;
        btnOrdenar.setEnabled(false);
        btnOrdenar.setText("Procesando…");
        lblPaso.setText("Procesando la sala…");
        List<Paciente> datos = List.copyOf(sala);
        Area a = area;

        ejecutarEnFondo(turno, () -> servicio.ordenar(datos, a.algoritmo, a.criterio), a.bitacora);
    }

    /** Admisión: inserta un paciente nuevo directo en su puesto, sin reordenar la sala. */
    private void ingresarAmbulancia() {
        if (animando()) {
            saltarAlFinal();
        }
        String nombre = campoNombre.getTexto();
        if (nombre.isEmpty()) {
            mostrarError("Escribe el nombre del paciente");
            campoNombre.enfocar();
            return;
        }
        if (sala.size() >= MAX_PACIENTES) {
            mostrarError("Sala llena (máximo " + MAX_PACIENTES + " pacientes)");
            return;
        }
        mostrarError(null);
        int triaje = selTriaje.getSeleccion() + 1;
        Paciente nuevo = new Paciente(String.format("P-%03d", siguienteCodigo++), nombre, campoEdad.getValor(),
                triaje, LocalTime.now().withSecond(0).withNano(0));

        restablecer();
        int turno = turnoOrden;
        List<Paciente> conNuevo = new ArrayList<>(sala);
        conNuevo.add(nuevo);
        tabla.setPacientes(conNuevo);                 // entra al final y desde ahí retrocede
        tabla.setPivote(conNuevo.size() - 1);
        actualizarKpis();
        btnAmbulancia.setEnabled(false);
        lblPaso.setText("Ingresando a " + nuevo.getNombre() + " (triaje " + triaje + ")…");
        campoNombre.setTexto("");

        List<Paciente> datos = List.copyOf(sala);
        ejecutarEnFondo(turno, () -> servicio.insertar(datos, nuevo, Criterio.PRIORIDAD), null);
    }

    /** Muestra (o con null oculta) el aviso rojo bajo el botón de ambulancia. */
    private void mostrarError(String texto) {
        lblErrorIngreso.setText(texto == null ? " " : texto);
        lblErrorIngreso.setVisible(texto != null);
        bloqueAmbulancia.revalidate();
    }

    /** Corre el cálculo con SwingWorker y, al volver al EDT, inicia la animación. */
    private void ejecutarEnFondo(int turno, java.util.function.Supplier<Resultado> calculo, String mensaje) {
        new SwingWorker<Resultado, Void>() {
            @Override
            protected Resultado doInBackground() {
                return calculo.get();                      // hilo de fondo: no toca Swing
            }

            @Override
            protected void done() {                        // de vuelta en el hilo de Swing
                if (turno != turnoOrden) {
                    return;                                // el usuario cambió algo mientras tanto
                }
                try {
                    resultado = get();
                    mensajeAlTerminar = mensaje != null ? mensaje : mensajeIngreso(resultado);
                    iniciarAnimacion();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    LOG.log(Level.SEVERE, "Error al procesar la sala", ex);
                    lblPaso.setText("Ocurrió un error: " + ex.getCause());
                    restablecer();
                }
            }
        }.execute();
    }

    /** Texto de bitácora para un ingreso: busca en qué puesto quedó el último paciente creado. */
    private String mensajeIngreso(Resultado r) {
        String codigo = String.format("P-%03d", siguienteCodigo - 1);
        List<Paciente> lista = r.ordenados();
        for (int i = 0; i < lista.size(); i++) {
            Paciente p = lista.get(i);
            if (p.getCodigo().equals(codigo)) {
                return "Ingreso por ambulancia: " + p.getNombre() + " (" + p.getCodigo() + ", triaje "
                        + p.getTriaje() + " · " + Paciente.nombreTriaje(p.getTriaje()) + ") → puesto " + (i + 1);
            }
        }
        return "Ingreso por ambulancia registrado";
    }

    private void iniciarAnimacion() {
        if (area != Area.ADMISION) {
            btnOrdenar.setText("Procesando…");
        }
        btnPausa.setEnabled(true);
        btnFinal.setEnabled(true);
        timer = new Timer(RETARDOS[selVelocidad.getSeleccion()], e -> avanzar());
        timer.start();
    }

    /** Un tick del Timer: aplica pasos hasta mostrar uno visible (comparar, intercambiar o pivote). */
    private void avanzar() {
        List<Paso> pasos = resultado.pasos();
        while (indicePaso < pasos.size()) {
            Paso p = pasos.get(indicePaso++);
            List<Paciente> filas = tabla.getPacientes();
            switch (p.tipo()) {
                case COMPARAR -> {
                    tabla.marcar(TablaPacientes.Marca.COMPARAR, p.i(), p.j());
                    lblPaso.setText("Comparando " + describir(filas, p.i()) + " con " + describir(filas, p.j()));
                }
                case INTERCAMBIAR -> {
                    lblPaso.setText("Intercambiando " + describir(filas, p.i()) + " y " + describir(filas, p.j()));
                    tabla.intercambiar(p.i(), p.j());
                }
                case PIVOTE -> {
                    tabla.setPivote(p.i());
                    String rol = area == Area.TRIAJE ? "Pivote: " : "Menor encontrado hasta ahora: ";
                    lblPaso.setText(rol + describir(filas, p.i()));
                }
                case ORDENADO -> {
                    tabla.marcarOrdenado(p.i());
                    continue;                               // no consume tick: sigue al próximo paso
                }
            }
            break;
        }
        if (indicePaso >= pasos.size()) {
            terminar();
        }
    }

    private String describir(List<Paciente> filas, int i) {
        Paciente p = filas.get(i);
        return "puesto " + (i + 1) + " (" + p.getCodigo() + " · " + area.criterio.valor.apply(p) + ")";
    }

    private void terminar() {
        detenerAnimacion();
        sala = resultado.ordenados();
        tabla.marcarTodoOrdenado();
        indicePaso = resultado.pasos().size();
        lblTiempo.setText(String.format(Locale.US, "%.3f ms", resultado.nanos() / 1_000_000.0));
        lblTiempoSub.setText(resultado.algoritmo().nombre + " · " + resultado.algoritmo().promedio);
        lblPaso.setText("Listo: " + mensajeAlTerminar);
        registrar(mensajeAlTerminar);
        if (area != Area.ADMISION) {
            btnOrdenar.setText(area.textoBoton);
        }
        btnOrdenar.setEnabled(true);
        btnAmbulancia.setEnabled(true);
        btnPausa.setText("Pausar");
        btnPausa.setEnabled(false);
        btnFinal.setEnabled(false);
        actualizarKpis();
    }

    private boolean animando() {
        return resultado != null && indicePaso < resultado.pasos().size();
    }

    private void pausarOReanudar() {
        if (timer == null) {
            return;
        }
        if (timer.isRunning()) {
            timer.stop();
            btnPausa.setText("Reanudar");
        } else {
            timer.start();
            btnPausa.setText("Pausar");
        }
    }

    private void saltarAlFinal() {
        if (resultado == null) {
            return;
        }
        tabla.setPacientes(resultado.ordenados());
        terminar();
    }

    private void detenerAnimacion() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    // -------------------------------------------------------------------- KPIs

    private void actualizarKpis() {
        List<Paciente> filas = tabla.getPacientes();
        int n = filas.size();
        lblSala.setText(String.valueOf(n));
        lblSalaSub.setText("Capacidad " + MAX_PACIENTES + " camillas");

        int resucitacion = 0;
        int emergencia = 0;
        int ninos = 0;
        int adultos = 0;
        int mayores = 0;
        long sumaEdad = 0;
        Paciente masEspera = null;
        for (Paciente p : filas) {
            if (p.getTriaje() == 1) {
                resucitacion++;
            } else if (p.getTriaje() == 2) {
                emergencia++;
            }
            sumaEdad += p.getEdad();
            if (p.getEdad() < 18) {
                ninos++;
            } else if (p.getEdad() < 65) {
                adultos++;
            } else {
                mayores++;
            }
            if (masEspera == null || p.getLlegada().isBefore(masEspera.getLlegada())) {
                masEspera = p;
            }
        }
        int criticos = resucitacion + emergencia;
        lblCriticos.setText(String.valueOf(criticos));
        lblCriticos.setForeground(criticos > 0 ? ROJO : new Color(0x16A34A));
        lblCriticosSub.setText("Rojos " + resucitacion + " · Naranjas " + emergencia);
        lblCriticosSub.setToolTipText(resucitacion + " en resucitación (triaje 1) · "
                + emergencia + " en emergencia (triaje 2)");

        if (area == Area.CENSO) {
            lblKpi3.setText(n == 0 ? "-" : Math.round((double) sumaEdad / n) + " años");
            lblKpi3Sub.setText(ninos + (ninos == 1 ? " niño · " : " niños · ") + adultos + " adul. · " + mayores + " +65");
            lblKpi3Sub.setToolTipText(ninos + " niños (< 18) · " + adultos + " adultos (18-64) · "
                    + mayores + " tercera edad (65+)");
        } else if (masEspera == null) {
            lblKpi3.setText("-");
            lblKpi3Sub.setText(" ");
        } else {
            long minutos = Duration.between(masEspera.getLlegada(), LocalTime.now()).toMinutes();
            if (minutos < 0) {
                minutos += 24 * 60;                        // llegó antes de la medianoche
            }
            lblKpi3.setText(minutos >= 60 ? (minutos / 60) + "h " + (minutos % 60) + "m" : minutos + " min");
            lblKpi3Sub.setText(masEspera.getCodigo() + " · desde " + masEspera.getLlegada().format(HORA));
            lblKpi3Sub.setToolTipText(masEspera.getNombre());
        }
    }

    /** Agrega una entrada con la hora actual al inicio de la bitácora clínica. */
    private void registrar(String texto) {
        bitacora.insert(LocalTime.now().format(HORA) + "  -  " + texto + "\n", 0);
        bitacora.setCaretPosition(0);
    }
}
