/* WODBuilder.java
 * Created on 12 janv. 2009
 */
package run.univ.wosrc;

import java.io.IOException;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;

import run.univ.xml.base.Namespaces;

/**
 * @author jclain
 */
public abstract class WODBuilder {
	protected abstract void write(String text) throws IOException;

	protected void writenl(String text) throws IOException {
		write(text);
		write("\r\n");
	}

	private static final Pattern LettersAndNumbers = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*$");

	/**
	 * si le chaine s contient autre chose que des lettres et des chiffres, la
	 * mettre entre quotes.
	 */
	private static final String quote_maybe(String s) {
		if (LettersAndNumbers.matcher(s).matches())
			return s;
		return "\"" + s + "\"";
	}

	public void append(String bindingName, String className, Attributes attrs) throws IOException {
		write(bindingName);
		write(": ");
		write(className);
		writenl(" {");

		int max = attrs.getLength();
		for (int i = 0; i < max; i++) {
			// filtrer les déclarations de préfixe
			if (!Namespaces.isAttrNamespaceDecl(attrs.getQName(i))) {
				write("    ");
				if (attrs.getURI(i).equals(Defaults.QATTR_NS)) {
					write(quote_maybe(attrs.getLocalName(i)));
					write(" = \"");
					write(attrs.getValue(i));
					write("\"");
				} else {
					write(quote_maybe(attrs.getQName(i)));
					write(" = ");
					write(attrs.getValue(i));
				}
				writenl(";");
			}
		}
		writenl("}");
	}
}