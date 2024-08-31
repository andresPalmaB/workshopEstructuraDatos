import java.io.IOException;
import java.util.Queue;
import java.util.Stack;
import java.util.stream.Collectors;

public class HtmlValidator {

	// Este método verifica si la estructura HTML es válida, procesando una cola de etiquetas HTML.
	// Devuelve una pila de etiquetas HTML no cerradas o mal anidadas.
	public static Stack<HtmlTag> isValidHtml(Queue<HtmlTag> tags) throws IOException {

		// Se inicializa una pila que almacenará las etiquetas abiertas que deben ser cerradas.
		Stack<HtmlTag> pilaEtiquetasResultado = new Stack<>();

		// Se recorre cada etiqueta en la cola de etiquetas HTML.
		for (HtmlTag etiqueta : tags) {
			// Si la etiqueta es una etiqueta de apertura (no es de cierre ni auto-cerrable).
			if (etiqueta.isOpenTag()) {
				// Se añade a la pila, ya que se espera que en algún punto sea cerrada.
				pilaEtiquetasResultado.add(etiqueta);
			} else {
				// Si la etiqueta no es auto-cerrable.
				if (!etiqueta.isSelfClosing()) {
					// Si la pila está vacía, significa que hay una etiqueta de cierre sin apertura.
					if (pilaEtiquetasResultado.isEmpty()) {
						return null; // Devuelve null indicando que la estructura HTML es inválida.
					} else {
						// Si la etiqueta de cierre coincide con la última etiqueta abierta.
						if (etiqueta.matches(pilaEtiquetasResultado.lastElement())) {
							// Se elimina la última etiqueta abierta de la pila, ya que ha sido cerrada correctamente.
							pilaEtiquetasResultado.pop();
						} else {
							// Si la etiqueta de cierre no coincide, se devuelve la pila con las etiquetas incorrectas.
							return pilaEtiquetasResultado;
						}
					}
				}
			}
		}
		// Devuelve la pila que, idealmente, debería estar vacía si todas las etiquetas han sido cerradas correctamente.
		// Esta línea asegura que el código compile, pero en la lógica podría significar que aún hay etiquetas abiertas sin cerrar.
		return pilaEtiquetasResultado;
	}
}


