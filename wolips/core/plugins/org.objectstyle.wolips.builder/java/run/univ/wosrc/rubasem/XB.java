/* XB.java
 * Created on 13 avr. 2010
 */
package run.univ.wosrc.rubasem;

/**
 * Des outils pour générer du XML.
 * 
 * @author jclain
 */
public class XB {
	// Méthodes avec StringBuilder

	public static final StringBuilder appendAttribute(StringBuilder sb, String name, String value) {
		if (name == null)
			return sb;
		if (value == null)
			value = name;
		sb.append(' ');
		sb.append(name);
		sb.append('=');
		sb.append(Str.htmlattr_quote(value));
		return sb;
	}

	public static final StringBuilder appendAttributes(StringBuilder sb, XMLAttributes attrs) {
		if (attrs == null)
			return sb;
		for (int i = 0; i < attrs.size(); i++) {
			appendAttribute(sb, attrs.getName(i), attrs.getValue(i));
		}
		return sb;
	}

	public static final StringBuilder appendProcessingInstruction(StringBuilder sb, String name, XMLAttributes attrs) {
		sb.append("<?");
		sb.append(name);
		appendAttributes(sb, attrs);
		sb.append("?>");
		return sb;
	}

	public static final StringBuilder appendStartTag(StringBuilder sb, String name, XMLAttributes attrs, boolean empty, boolean closed) {
		sb.append('<');
		sb.append(name);
		appendAttributes(sb, attrs);
		if (empty)
			sb.append(" /");
		if (closed)
			sb.append('>');
		return sb;
	}

	public static final StringBuilder appendEndTag(StringBuilder sb, String name) {
		sb.append("</");
		sb.append(name);
		sb.append('>');
		return sb;
	}

	public static final StringBuilder appendComment(StringBuilder sb, String text) {
		if (text == null || text.length() == 0) return sb;
		sb.append("<!--");
		StringBuilder tmp = new StringBuilder(text);
		// faire le remplacement deux fois au cas où il y a plus de deux -- à la suite
		SB.replaceAll(tmp, "--", "- -");
		SB.replaceAll(tmp, "--", "- -");
		// traiter le dernier '-' pour ne pas faire conflit avec le '-->' de fin
		int length = tmp.length();
		if (length > 0 && tmp.charAt(length - 1) == '-') tmp.append(" ");
		sb.append(tmp);
		sb.append("-->");
		return sb;
	}

	public static final StringBuilder appendCData(StringBuilder sb, String text) {
		if (text == null)
			return sb;
		if (text.indexOf('\n') == -1 && text.indexOf('\r') == -1) {
			sb.append(SB.html_quote(new StringBuilder(text)));
		} else {
			sb.append("<![CDATA[");
			StringBuilder tmp = new StringBuilder(text);
			SB.replaceAll(tmp, "]]>", "]]]]>&gt;<![CDATA[");
			sb.append(tmp);
			sb.append("]]>");
		}
		return sb;
	}

	public static final StringBuilder appendQuotedText(StringBuilder sb, String text) {
		if (text == null)
			return sb;
		sb.append(SB.html_quote(new StringBuilder(text)));
		return sb;
	}

	// ------------------------------------------------------------------------
	// Méthodes avec String

	public static final String getAttribute(String name, String value) {
		return appendAttribute(new StringBuilder(), name, value).toString();
	}

	public static final String getAttributes(XMLAttributes attrs) {
		return appendAttributes(new StringBuilder(), attrs).toString();
	}

	public static final String getProcessingInstruction(String name, XMLAttributes attrs) {
		return appendProcessingInstruction(new StringBuilder(), name, attrs).toString();
	}

	public static final String getStartTag(String name, XMLAttributes attrs, boolean empty, boolean closed) {
		return appendStartTag(new StringBuilder(), name, attrs, empty, closed).toString();
	}

	public static final String getEndTag(String name) {
		return appendEndTag(new StringBuilder(), name).toString();
	}

	public static final String getComment(String text) {
		return appendComment(new StringBuilder(), text).toString();
	}

	public static final String getCData(String text) {
		return appendCData(new StringBuilder(), text).toString();
	}

	public static final String getQuotedText(String text) {
		return appendQuotedText(new StringBuilder(), text).toString();
	}
}
