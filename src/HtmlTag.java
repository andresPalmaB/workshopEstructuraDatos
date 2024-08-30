import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class HtmlTag {

    protected final String element;
    protected final boolean openTag; // ¿Es una etiqueta de apertura o de cierre?

    public HtmlTag(String element, boolean isOpenTag) { // Recibe al elemento y si es de cierre o no
        this.element = element.toLowerCase(); // Lo convierte en minúsculas y lo guarda en elemento
        openTag = isOpenTag;
    }

    public String getElement() {
        return element; // Devuelve el nombre de la etiqueta
    }

    public boolean isOpenTag() {
        return openTag && !isSelfClosing(); // ¿La etiqueta es de apertura y no es de autocierre? = Asegura emparejamiento
    }

    public boolean matches(HtmlTag other) { // ¿Todas las etiquetas de apertura tienen su cierre?
        return other != null // Se asegura de que no es un parámetro nulo; si es así, devuelve falso
                && element.equalsIgnoreCase(other.element) // Compara una etiqueta con otra; ignora si están escritas en mayúsculas o minúsculas
                && openTag != other.openTag; // Retorna verdadero si openTag es diferente en ambas etiquetas; una de las etiquetas es de apertura y la otra es de cierre
    }

    // En la sintaxis de HTML hay etiquetas de autocierre, es decir, no requieren una etiqueta de cierre separada.
    public boolean isSelfClosing() { // ¿Se trata de una etiqueta de autocierre?
        return SELF_CLOSING_TAGS.contains(element); // Se tiene un listado en donde están las etiquetas de autocierre y se verifica si la etiqueta escrita se encuentra en ella
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HtmlTag) { // Compara las etiquetas para saber si tienen el mismo nombre de elemento y tipo (apertura/cierre)
            HtmlTag other = (HtmlTag) obj;
            return element.equals(other.element)
                    && openTag == other.openTag;
        }
        return false;
    }

    @Override
    public String toString() {
        return "<" + (openTag ? "" : "/")
                + (element.equals("!--") ? "!-- --" : element) + ">";
        // Si es true, se añade una cadena vacía "" (indicando que es una etiqueta de apertura). Si es false, se añade un "/" (indicando que es una etiqueta de cierre).
        // Agrega a la cadena el nombre de la etiqueta o señala si es un comentario
    }

    // Lista de etiquetas de autocierre:
    protected static final Set<String> SELF_CLOSING_TAGS = new HashSet<>(Arrays.asList(
            "?xml", "xml", "area", "base", "basefont", "br", "col", "frame", "hr", "img",
            "input", "link", "meta", "param"));

    // Representa los "Espacios en blanco", útil para eliminar atributos y otros caracteres no deseados al extraer y procesar las etiquetas HTML.
    protected static final String WHITESPACE = " \f\n\r\t"; // f: Carácter de avance de página; n: Carácter de nueva línea; r: Carácter de retorno de carro; t: Carácter de tabulación (tab)

    public static Queue<HtmlTag> tokenize(String text) { // Devuelve una cola de objetos HtmlTag
        StringBuffer buf = new StringBuffer(text); // StringBuffer es una clase que permite modificar la cadena de texto de manera eficiente, ya que es mutable
        Queue<HtmlTag> queue = new LinkedList<>(); // Inicializa la cola

        HtmlTag nextTag = nextTag(buf); // Llama método para extraer etiqueta
        while (nextTag != null) { // Mientras haya etiquetas HTML (nextTag no sea null), se ejecuta el bucle
            queue.add(nextTag); // Se agrega la etiqueta a la cola
            nextTag = nextTag(buf); // Se vuelve a llamar
        }
        return queue; // Se devuelve la cola que contiene todas las etiquetas HTML extraídas del texto
    }

    protected static HtmlTag nextTag(StringBuffer buf) {
        // Buscadores de < y >
        int openBracket = buf.indexOf("<");
        int closeBracket = buf.indexOf(">");

        if (openBracket >= 0 && closeBracket > openBracket) { // Asegura que la etiqueta de cierre esté después de la de apertura
            // Check for HTML comments: <!-- -->
            if (buf.substring(openBracket + 1, Math.min(openBracket + 4, buf.length())).equals("!--")) {
                closeBracket = buf.indexOf("-->", closeBracket + 1);
                if (closeBracket < 0) {
                    // If closing comment tag not found, remove remaining part
                    buf.delete(openBracket, buf.length());
                    return null;
                }
                // Elimina el comentario del buffer
                buf.delete(openBracket, closeBracket + 1);
                // Llama nextTag para hacer la siguiente etiqueta
                return nextTag(buf);
            }

            // Para manejar las declaraciones Doctype:
            if (buf.substring(openBracket + 1, Math.min(openBracket + 9, buf.length())).startsWith("!doctype")) { // Comprueba si la etiqueta es una declaración DOCTYPE
                closeBracket = buf.indexOf(">", openBracket); // Busca el cierre
                if (closeBracket < 0) { // Si no se encuentra el final, elimina el contenido desde la etiqueta de apertura hasta el final del buffer y devuelve null
                    buf.delete(openBracket, buf.length());
                    return null;
                }
                // Elimina la declaración DOCTYPE del buffer y llama recursivamente a nextTag para procesar la siguiente etiqueta
                buf.delete(openBracket, closeBracket + 1);
                // Llama la siguiente etiqueta
                return nextTag(buf);
            }

            // Extrae el contenido de la etiqueta
            String element = buf.substring(openBracket + 1, closeBracket).trim(); // Obtiene el contenido de la etiqueta sin los caracteres < y >
            //.trim() elimina los espacios en blanco al inicio y al final del contenido extraído

            // Luego, elimina atributos y caracteres extraños
            for (int i = 0; i < WHITESPACE.length(); i++) {
                int attributeIndex = element.indexOf(WHITESPACE.charAt(i));
                if (attributeIndex >= 0) {
                    element = element.substring(0, attributeIndex);
                }
            }

            // Determina si la etiqueta es de apertura o cierre
            boolean isOpenTag = true;
            int checkForClosing = element.indexOf("/");
            if (checkForClosing == 0) {
                isOpenTag = false;
                element = element.substring(1).trim(); // Se eliminan los caracteres de cierre y se recorta el espacio extra
            }

            // Elimina caracteres no permitidos
            element = element.replaceAll("[^a-zA-Z0-9!-]+", "").trim(); // Elimina caracteres no permitidos y recorta el espacio extra

            buf.delete(0, closeBracket + 1);
            return new HtmlTag(element, isOpenTag);
        } else {
            return null;
        }
    }
}


