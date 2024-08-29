import java.io.IOException;
import java.util.Queue;
import java.util.Stack;


public class HtmlValidator {

	public static Stack<HtmlTag> isValidHtml(Queue<HtmlTag> tags) throws IOException {

		Stack<HtmlTag> pilaEtiquetasResultado = new Stack<>();

		for (HtmlTag etiqueta : tags){
			if (etiqueta.openTag){
				pilaEtiquetasResultado.add(etiqueta);
			} else {
				if (pilaEtiquetasResultado.isEmpty()){
					return null;
				} else {
					if (etiqueta.matches(pilaEtiquetasResultado.lastElement())){

						pilaEtiquetasResultado.removeLast();
					} else {
						return pilaEtiquetasResultado;
					}
				}
			}
		}
		return pilaEtiquetasResultado; // this line is here only so this code will compile if you don't modify it
	}
}

