/* XMLBuilder.java
 * Created on 16 nov. 2004
 */
package run.univ.xml.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import run.univ.LineSep;
import run.univ.Str;
import run.univ.StrBuff;

/**
 * Des outils pour générer du XML.
 * 
 * @author jclain
 */
public abstract class XMLBuilder {
    public static final StringBuffer getAttributes(StringBuffer sb, XMLAttributes attrs) {
        if (attrs == null) return sb;

        for (int i = 0; i < attrs.count(); i++) {
            sb.append(' ');
            sb.append(attrs.getName(i));
            sb.append('=');
            sb.append(Str.htmlattr_quote(attrs.getValue(i)));
        }
        return sb;
    }

    public static final StringBuffer getProcessingInstruction(StringBuffer sb, String name,
            XMLAttributes attrs) {
        sb.append("<?");
        sb.append(name);
        getAttributes(sb, attrs);
        sb.append("?>");
        return sb;
    }

    public static final StringBuffer getStartTag(StringBuffer sb, String name, XMLAttributes attrs,
            boolean closed, String nlOrNull) {
        sb.append('<');
        sb.append(name);
        getAttributes(sb, attrs);
        if (closed) {
            if (nlOrNull != null) {
                sb.append(nlOrNull);
                sb.append("/");
            } else sb.append(" /");
        } else if (nlOrNull != null) {
            sb.append(nlOrNull);
        }
        sb.append('>');
        return sb;
    }

    public static final StringBuffer getEndTag(StringBuffer sb, String name, String nlOrNull) {
        sb.append("</");
        sb.append(name);
        if (nlOrNull != null) sb.append(nlOrNull);
        sb.append('>');
        return sb;
    }

    public static final StringBuffer getComment(StringBuffer sb, String text) {
        if (text == null) return sb;

        sb.append("<!--");
        StringBuffer tmp = new StringBuffer(text);
        StrBuff.replaceAll(tmp, "--", "- -");
        sb.append(tmp);
        sb.append("-->");
        return sb;
    }

    public static final StringBuffer getCData(StringBuffer sb, String text, boolean htmlMode) {
        if (text == null) return sb;

        if (htmlMode) {
            sb.append(text);
        } else {
            if (text.indexOf('\n') == -1 && text.indexOf('\r') == -1) {
                sb.append(StrBuff.html_quote(new StringBuffer(text)));
            } else {
                sb.append("<![CDATA[");
                StringBuffer tmp = new StringBuffer(text);
                StrBuff.replaceAll(tmp, "]]>", "]]]]>&gt;<![CDATA[");
                sb.append(tmp);
                sb.append("]]>");
            }
        }

        return sb;
    }

    public static final StringBuffer getCData(StringBuffer sb, String text) {
        return getCData(sb, text, false);
    }

    public static final StringBuffer getQuotedText(StringBuffer sb, String text) {
        if (text == null) return sb;

        sb.append(StrBuff.html_quote(new StringBuffer(text)));
        return sb;
    }

    public static final String getAttributes(XMLAttributes attrs) {
        return getAttributes(new StringBuffer(), attrs).toString();
    }

    public static final String getStartTag(String name, XMLAttributes attrs, boolean closed,
            String nlOrNull) {
        return getStartTag(new StringBuffer(), name, attrs, closed, nlOrNull).toString();
    }

    public static final String getEndTag(String name, String nlOrNull) {
        return getEndTag(new StringBuffer(), name, nlOrNull).toString();
    }

    public static final String getComment(String text) {
        return getComment(new StringBuffer(), text).toString();
    }

    public static final String getCData(String text, boolean htmlMode) {
        return getCData(new StringBuffer(), text, htmlMode).toString();
    }

    public static final String getCData(String text) {
        return getCData(text, false).toString();
    }

    public static final String getQuotedText(String text) {
        return getQuotedText(new StringBuffer(), text).toString();
    }

    public XMLBuilder(String nl, boolean indent, boolean html, String encoding) {
        committed = true;
        elements = new ArrayList();
        setNl(nl);
        setIndent(indent);
        setHtml(html);
        setEncoding(encoding);
    }

    public XMLBuilder() {
        this(null, false, false, null);
    }

    public XMLBuilder(boolean html) {
        this(null, false, html, null);
    }

    protected String nl = LineSep.LF;

    public String getNl() {
        return nl;
    }

    public void setNl(String nl) {
        if (nl == null) nl = LineSep.LF;
        this.nl = nl;
    }

    protected boolean indent;

    public boolean isIndent() {
        return indent;
    }

    public void setIndent(boolean indent) {
        this.indent = indent;
    }

    private static final List HTML_EMPTY_TAGS = Arrays.asList(new String[] {
            "br",
            "hr",
            "img",
            "meta"});

    private boolean htmlInHead, htmlAddMeta;

    private static final String HEAD_TAG = "head", META_TAG = "meta";

    private static final String HTTP_EQUIV_ATTR = "http-equiv", CONTENT_ATTR = "content",
            CONTENT_TYPE_VALUE = "Content-Type",
            HTML_ENCODING_PREFIX_VALUE = "text/html; charset=";

    /**
     * true si on est en mode HTML.
     * <p>
     * pour le moment, on se contente de commiter automatiquement chaque tag, ce qui interdit la
     * génération de tags tels que &lt;tag/&gt;. <br>
     * TODO certains tags comme &lt;br&gt; devrait être écrits &lt;br /&gt; plutôt que
     * &lt;br&gt;&lt;/br&gt;.
     * </p>
     * <p>
     * De plus, les sections CDATA sont écrites telles quelles, sans
     * <code>&lt;![CDATA[...]]&gt;</code>.
     * </p>
     * <p>
     * Enfin, on rajoute automatiquement un tag &lt;meta http-equiv="Content-Type"
     * content="text/html; charset=encoding" />&gt; dans la section &lt;head&gt; si encoding est
     * différent de <code>null</code>.
     * </p>
     */
    protected boolean html;

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    protected String encoding;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        if (encoding == null) encoding = Str.UTF_8;
        this.encoding = encoding;
    }

    protected abstract void write(String text) throws IOException;

    // -----------------------------------------------------
    // variables et méthodes utilisées par le mode indent=false

    private boolean committed;

    private String name;

    private XMLAttributes attrs;

    private void commit(boolean closed) throws IOException {
        if (!committed) {
            write(getStartTag(name, attrs, closed, null));

            committed = true;
            name = null;
            attrs = null;
        }
    }

    // -----------------------------------------------------
    // variables et méthodes utilisées par le mode indent=true

    private class Element extends XMLElement.Implementation {
        boolean committed, hasText, hasNonEmptyChildren, hasChildren;

        Element(String name, XMLAttributes attrs) {
            super(name, attrs);
        }

        public boolean hasText() {
            return hasText;
        }

        public boolean hasChildrenElements() {
            return hasChildren;
        }

        void commit(boolean closed, String nlOrNull) throws IOException {
            if (!committed) {
                XMLBuilder.this.write(getStartTag(name, attrs, closed, nlOrNull));
                committed = true;
                name = null;
                attrs = null;
            }
        }

        void end(String name, String nlOrNull) throws IOException {
            if (committed) {
                XMLBuilder.this.write(getEndTag(name, nlOrNull));
            } else {
                commit(!hasChildren && !hasText, nlOrNull);
            }
        }
    }

    private ArrayList elements;

    private void parentHasChild(boolean addNl) throws IOException {
        int size = elements.size();
        if (size > 0) {
            Element parent = (Element)elements.get(size - 1);
            if (!parent.hasChildren) {
                parent.hasChildren = true;
                parent.commit(false, addNl? nl: null);
            }

        }
    }

    private void startElement(String name, XMLAttributes attrs) throws IOException {
        parentHasChild(true);
        Element element = new Element(name, attrs);
        if (html && !HTML_EMPTY_TAGS.contains(Str.lower(name))) element.commit(false, nl);
        elements.add(element);
    }

    private void endElement(String name) throws IOException {
        // startElement a déjà commité les parents
        Element element = (Element)elements.remove(elements.size() - 1);
        if (element.hasText || element.hasChildren) {
            for (Iterator it = elements.iterator(); it.hasNext();) {
                Element parent = (Element)it.next();
                parent.hasNonEmptyChildren = true;
            }
        }
        boolean addNl = false;
        if (elements.size() > 0) {
            Element parent = (Element)elements.get(elements.size() - 1);
            if (element.hasChildren || parent.hasNonEmptyChildren) addNl = true;
        }
        element.end(name, addNl? nl: null);
    }

    private void elementHasText(boolean cdata) throws IOException {
        parentHasChild(false);
    }

    // -----------------------------------------------------
    // Gestion des éléments

    private void writenl() throws IOException {
        write(nl);
    }

    public void nl() throws IOException {
        commit(false);
        writenl();
    }

    public void xmldecl(XMLAttributes attrs, boolean nl) throws IOException {
        attrs = new XMLAttributes.Implementation(attrs);
        if (attrs.indexOf("version") == -1) {
            // l'attribut version doit être en première position
            attrs.add(0, "version", "1.0");
        }
        write(getProcessingInstruction(new StringBuffer(), "xml", attrs).toString());
        if (nl) nl();
    }

    public void xmlstylesheet(XMLAttributes attrs, boolean nl) throws IOException {
        attrs = new XMLAttributes.Implementation(attrs);
        if (attrs.indexOf("version") == -1) {
            // mettre l'attribut version en première position
            attrs.add(0, "version", "1.0");
        }
        if (attrs.indexOf("type") == -1) attrs.add("type", "text/xsl");
        write(getProcessingInstruction(new StringBuffer(), "xml-stylesheet", attrs).toString());
        if (nl) nl();
    }

    public void doctype(String rootElement, String publicId, String systemId, boolean close)
            throws IOException {
        StringBuffer sb = new StringBuffer("<!DOCTYPE ");
        sb.append(rootElement);
        if (publicId != null) {
            sb.append(" PUBLIC ");
            sb.append(Str.htmlattr_quote(publicId));
            if (systemId != null) {
                sb.append(' ');
                sb.append(Str.htmlattr_quote(systemId));
            }
        } else if (systemId != null) {
            sb.append(" SYSTEM ");
            sb.append(Str.htmlattr_quote(systemId));
        }
        if (close) {
            sb.append('>');
        }

        write(sb.toString());
    }

    private Pattern DOCTYPE_PATTERN = Pattern.compile("<!DOCTYPE\\s.*>");

    public void doctype(String doctype, boolean nl) throws IOException {
        if (Doctypes.isValid(doctype)) {
            doctype = Doctypes.getDecl(doctype);
        } else {
            if (!DOCTYPE_PATTERN.matcher(doctype).matches()) {
                throw new IllegalArgumentException("doctype non valide: " + doctype);
            }
        }
        write(doctype);
        if (nl) nl();
    }

    public void xmldecl() throws IOException {
        xmldecl(new XMLAttributes.Implementation("encoding", encoding), true);
    }

    public void xmlstylesheet(String href) throws IOException {
        xmlstylesheet(new XMLAttributes.Implementation("href", href), true);
    }

    public void doctype(String doctype) throws IOException {
        doctype(doctype, true);
    }

    public void start(String name, XMLAttributes attrs) throws IOException {
        if (indent) {
            startElement(name, attrs);
        } else {
            commit(false);

            committed = false;
            this.name = name;
            this.attrs = attrs;

            if (html && !HTML_EMPTY_TAGS.contains(Str.lower(name))) commit(false);
        }
        if (html && encoding != null) {
            if (Str.equalsIgnoreCase(name, HEAD_TAG)) {
                htmlInHead = true;
                htmlAddMeta = true;
            } else if (htmlInHead && Str.equalsIgnoreCase(name, META_TAG) && attrs != null) {
                int httpEquivIndex = attrs.indexOf(HTTP_EQUIV_ATTR);
                if (httpEquivIndex != -1) {
                    String httpEquiv = attrs.getValue(httpEquivIndex);
                    if (Str.equalsIgnoreCase(httpEquiv, CONTENT_TYPE_VALUE)) {
                        htmlAddMeta = false;
                    }
                }
            }
        }
    }

    public void end(String name) throws IOException {
        if (html && encoding != null) {
            if (Str.equalsIgnoreCase(name, HEAD_TAG) && htmlAddMeta) {
                XMLAttributes attrs = new XMLAttributes.Implementation();
                attrs.add(HTTP_EQUIV_ATTR, CONTENT_TYPE_VALUE);
                attrs.add(CONTENT_ATTR, HTML_ENCODING_PREFIX_VALUE + encoding);
                start(META_TAG, attrs);
                end(META_TAG);
            }
        }
        if (indent) {
            endElement(name);
        } else {
            if (committed) {
                write(getEndTag(name, nl));
            } else {
                commit(true);
            }
        }
        if (html && encoding != null) htmlInHead = false;
    }

    public void rawtext(String text) throws IOException {
        if (indent) elementHasText(false);
        else commit(false);
        write(text);
    }

    public void text(String text) throws IOException {
        if (indent) elementHasText(false);
        else commit(false);
        write(getQuotedText(text));
    }

    public void cdata(String text) throws IOException {
        if (indent) elementHasText(true);
        else commit(false);
        write(getCData(text, html));
    }

    public void comment(String text) throws IOException {
        if (indent) parentHasChild(true);
        else commit(false);
        write(getComment(text));
    }

    // -----------------------------------------------------
    // Méthode de convenance

    /** écrire un élément XML simple avec éventuellement du texte à l'intérieur. */
    public void element(String name, XMLAttributes attrs, String text) throws IOException {
        start(name, attrs);
        if (text != null) text(text);
        end(name);
    }

    public void element(String name, String text) throws IOException {
        element(name, null, text);
    }

    /**
     * écrire un élément XML simple avec éventuellement du texte à l'intérieur dans une balise
     * &lt;![CDATA[...]]&gt;.
     */
    public void elementcdata(String name, XMLAttributes attrs, String text) throws IOException {
        start(name, attrs);
        if (text != null) cdata(text);
        end(name);
    }

    public void elementcdata(String name, String text) throws IOException {
        elementcdata(name, null, text);
    }
}