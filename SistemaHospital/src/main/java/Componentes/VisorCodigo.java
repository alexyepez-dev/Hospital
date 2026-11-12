package Componentes;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Área de texto de solo lectura con colores: muestra código Java resaltado o la
 * traza de ejecución (llamadas en azul, retornos en verde, casos base en negrita).
 */
public class VisorCodigo extends JTextPane {

    private static final Color PALABRA_CLAVE = new Color(0x2F6FB8);
    private static final Color COMENTARIO = new Color(0x8A9AB0);
    private static final Color NUMERO = new Color(0xC2410C);
    private static final Color METODO = new Color(0x7C3AED);
    private static final Color NUM_LINEA = new Color(0xB6C4D6);
    private static final Color RETORNO = new Color(0x15803D);

    private static final Set<String> CLAVES = Set.of(
            "public", "private", "static", "final", "long", "int", "boolean", "if", "else", "for", "while",
            "return", "break", "new", "void", "class", "true", "false");
    private static final Pattern TOKEN = Pattern.compile("//.*|\\b[A-Za-z_]\\w*\\b|\\b\\d+\\b|.", Pattern.DOTALL);

    public VisorCodigo() {
        setEditable(false);
        setFont(new Font("Consolas", Font.PLAIN, 13));
        setForeground(Estilo.TEXTO);
        setBackground(new Color(0xFAFCFF));
        setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 12));
    }

    /** Muestra código Java con resaltado de sintaxis y números de línea. */
    public void mostrarCodigo(String codigo) {
        setText("");
        StyledDocument doc = getStyledDocument();
        String[] lineas = codigo.split("\n", -1);
        for (int n = 0; n < lineas.length; n++) {
            agregar(doc, String.format("%3d   ", n + 1), estilo(NUM_LINEA, false, false));
            Matcher m = TOKEN.matcher(lineas[n]);
            while (m.find()) {
                String t = m.group();
                SimpleAttributeSet a;
                if (t.startsWith("//")) {
                    a = estilo(COMENTARIO, false, true);
                } else if (CLAVES.contains(t)) {
                    a = estilo(PALABRA_CLAVE, true, false);
                } else if (t.matches("\\d+")) {
                    a = estilo(NUMERO, false, false);
                } else if (Character.isLetter(t.charAt(0)) && m.end() < lineas[n].length()
                        && lineas[n].charAt(m.end()) == '(') {
                    // Cualquier identificador seguido de "(" es una llamada a método
                    a = estilo(METODO, true, false);
                } else {
                    a = estilo(Estilo.TEXTO, false, false);
                }
                agregar(doc, t, a);
            }
            if (n < lineas.length - 1) {
                agregar(doc, "\n", estilo(Estilo.TEXTO, false, false));
            }
        }
        setCaretPosition(0);
    }

    /** Muestra la traza: "Llamada" en azul, "Retorna" en verde y el caso base en negrita. */
    public void mostrarTraza(List<String> lineas) {
        setText("");
        StyledDocument doc = getStyledDocument();
        for (int i = 0; i < lineas.size(); i++) {
            String l = lineas.get(i);
            String limpio = l.trim();
            SimpleAttributeSet a;
            if (limpio.contains("caso base")) {
                a = estilo(Estilo.AZUL_OSCURO, true, false);
            } else if (limpio.startsWith("Retorna")) {
                a = estilo(RETORNO, false, false);
            } else {
                a = estilo(Estilo.AZUL, false, false);
            }
            agregar(doc, String.format("%3d   ", i + 1), estilo(NUM_LINEA, false, false));
            agregar(doc, l + (i < lineas.size() - 1 ? "\n" : ""), a);
        }
        setCaretPosition(0);
    }

    private static SimpleAttributeSet estilo(Color c, boolean negrita, boolean cursiva) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setForeground(a, c);
        StyleConstants.setBold(a, negrita);
        StyleConstants.setItalic(a, cursiva);
        return a;
    }

    private static void agregar(StyledDocument doc, String texto, SimpleAttributeSet a) {
        try {
            doc.insertString(doc.getLength(), texto, a);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
    }

    // Sin ajuste de línea: las líneas largas se desplazan horizontalmente
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getParent() == null || getUI().getPreferredSize(this).width <= getParent().getWidth();
    }
}
