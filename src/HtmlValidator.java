import java.io.IOException;
import java.util.Queue;
import java.util.Stack;


public class HtmlValidator {

	public static Stack<HtmlTag> isValidHtml(Queue<HtmlTag> tags) throws IOException {

		Stack<HtmlTag> pilaEtiquetasResultado = new Stack<>();

		for (HtmlTag etiqueta : tags){
			if (etiqueta.openTag){
				pilaEtiquetasResultado.push(etiqueta);
			} else {
				if (pilaEtiquetasResultado.isEmpty()){
					return pilaEtiquetasResultado;
				} else {
					if (etiqueta.matches(pilaEtiquetasResultado.peek())){

						pilaEtiquetasResultado.pop();
					} else {
						return pilaEtiquetasResultado;
					}
				}
			}
		}
		return pilaEtiquetasResultado; // this line is here only so this code will compile if you don't modify it
	}
}

