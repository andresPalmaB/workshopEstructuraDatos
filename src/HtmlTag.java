

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;



public class HtmlTag {

    protected final String element;
    protected final boolean openTag;  //¿Es una etiqueta de apertura o de cierre?


    public HtmlTag(String element, boolean isOpenTag)  // Recibe al elemento y si es de cierre o no
    {
        this.element = element.toLowerCase(); // Lo convierte en minúsculas y lo guarda en elemento

        openTag = isOpenTag;
    }
    
    public String getElement() {
        return element;
    }// Devuelve el nombre de la etiqueta


    public boolean isOpenTag() {
   	return openTag && !isSelfClosing();
    }//¿La etiqueta es de apertura y no es de autocierre? = Asegura emparejamiento


    public boolean matches(HtmlTag other)// ¿Todas las etiquetas de apertura tienen su cierre?
    {
        return other != null //Se asegura de que no es un parámetro nulo; si es así, devuelve falso

                && element.equalsIgnoreCase(other.element) //// Compara una etiqueta con otra; ignora si están escritas en mayúsculas o minúsculas

                && openTag != other.openTag;//// Retorna verdadero si openTag es diferente en ambas etiquetas; una de las etiquetas es de apertura y la otra es de cierre

    }
    // En la sintaxis de HTML hay etiquetas de autocierre, es decir, no requieren una etiqueta de cierre separada.
    public boolean isSelfClosing() {
        return SELF_CLOSING_TAGS.contains(element);
    }
    //¿Se trata de una etiqueta de autocierre?
    //// Se tiene un listado en donde están las etiquetas de autocierre y se verifica si la etiqueta escrita se encuentra en ella
    public boolean equals(Object obj) {
        //Compara las etiquetas para saber si tienen el mismo nombre de elemento y tipo (apertura/cierre)

        if (obj instanceof HtmlTag) {
            HtmlTag other = (HtmlTag) obj;
            return element.equals(other.element)
            	&& openTag == other.openTag;
        }
        return false;
    }
    
    public String toString() {
        return "<" + (openTag ? "" : "/")
        	+ (element.equals("!--") ? "!-- --" : element) + ">";
        // Si es true, se añade una cadena vacía "" (indicando que es una etiqueta de apertura). Si es false, se añade un "/" (indicando que es una etiqueta de cierre).
        // Agrega a la cadena el nombre de la etiqueta o señala si es un comentario

    }
    
    /**
     * The remaining fields and functions are related to HTML file parsing.
     */
    // Lista de etiquetas de autocierre:

     protected static final Set<String> SELF_CLOSING_TAGS = new HashSet<String>(
            Arrays.asList("!doctype", "!--", "?xml", "xml", "area", "base",
                          "basefont", "br", "col", "frame", "hr", "img",
                          "input", "link", "meta", "param"));


    // Representa los "Espacios en blanco", útil para eliminar atributos y otros caracteres no deseados al extraer y procesar las etiquetas HTML.
    protected static final String WHITESPACE = " \f\n\r\t";
    // f: Carácter de avance de página; n: Carácter de nueva línea; r: Carácter de retorno de carro; t: Carácter de tabulación (tab)

    public static Queue<HtmlTag> tokenize(String text) {// Devuelve una cola de objetos HtmlTag

        StringBuffer buf = new StringBuffer(text); // StringBuffer es una clase que permite modificar la cadena de texto de manera eficiente, ya que es mutable

        Queue<HtmlTag> queue = new LinkedList<HtmlTag>();// Inicializa la cola

        HtmlTag nextTag = nextTag(buf);// Llama método para extraer etiqueta

        //Mientras haya etiquetas HTML (nextTag no sea null), se ejecuta el bucle
        while (nextTag != null) {
            queue.add(nextTag); // Se agrega la etiqueta a la cola

            nextTag = nextTag(buf);// Se vuelve a llamar

        }
        return queue; // Se devuelve la cola que contiene todas las etiquetas HTML extraídas del texto

    }
    // Buscadores de < y >

    protected static HtmlTag nextTag(StringBuffer buf) {
        int openBracket = buf.indexOf("<");
        int closeBracket = buf.indexOf(">");
        if (openBracket >= 0 && closeBracket > openBracket) {
            // Asegura que la etiqueta de cierre esté después de la de apertura
            //manejo de comentarios
            int commentIndex = openBracket + 4;
            if (commentIndex <= buf.length()
            		&& buf.substring(openBracket + 1, commentIndex).equals("!--")) {
                // look for closing comment tag -->
                closeBracket = buf.indexOf("-->", commentIndex);
                if (closeBracket < 0) {
                    return null;
                } else {
                    buf.insert(commentIndex, " ");
                    closeBracket += 3;    // advance to the closing bracket >
                }
            }

            String element = buf.substring(openBracket + 1, closeBracket).trim();
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
                element = element.substring(1);
            }
            // Elimina caracteres no permitidos

            element = element.replaceAll("[^a-zA-Z0-9!-]+", "");
            
            buf.delete(0, closeBracket + 1);
            return new HtmlTag(element, isOpenTag);
        } else {
            return null;
        }
    }    
}