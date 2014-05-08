/* AbstractXMLBuilder.java
 * Created on 13 avr. 2010
 */
package run.univ.base.xml.base;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Pattern;

import run.univ.base.Enc;
import run.univ.base.SB;
import run.univ.base.Str;
import run.univ.base.io.LineSep;
import run.univ.base.xml.Doctypes;
import run.univ.base.xml.Namespaces;
import run.univ.base.xml.XMLAttribute;
import run.univ.base.xml.XMLAttributes;

/**
 * @author jclain
 */
public abstract class AbstractXMLBuilder implements IXMLBuilder {
    /** Un tag, ouvrant ou fermant, pour écriture sur le flux XML. */
    protected abstract class Tag {
        public Tag(String name, XMLAttributes attrs, boolean start, boolean empty, Tag parent) {
            this.name = name;
            this.attrs = attrs;
            this.start = start;
            this.empty = empty;
            this.parent = parent;
        }

        private Tag parent;

        public Tag getParent() {
            return parent;
        }

        private String name;

        public String getName() {
            return name;
        }

        private XMLAttributes attrs;

        public XMLAttributes getAttrs() {
            if (attrs == null) attrs = new XMLAttributes();
            return attrs;
        }

        private boolean start;

        /** Tester si cet objet représente un tag ouvrant. Sinon, c'est un tag fermant. */
        public boolean isStart() {
            return start;
        }

        public void setStart(boolean start) {
            this.start = start;
        }

        private boolean empty;

        /** Si start==<code>true</code>, tester si l'élément est complet, i.e. &lt;tag/&gt;. */
        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }

        /**
         * Obtenir la chaine a insérer avant l'élément sur le flux XML, ou <code>null</code> s'il ne
         * faut rien insérer.
         */
        public String getPrefix() {
            return null;
        }

        /**
         * Obtenir la chaine a insérer avant *chaque élément enfant* de cet élément sur le flux XML,
         * ou <code>null</code> s'il ne faut rien insérer.
         */
        public String getChildrenPrefix() {
            return null;
        }

        /**
         * Obtenir la chaine a ajouter après l'élément sur le flux XML, ou <code>null</code> s'il ne
         * faut rien ajouter.
         */
        public String getSuffix() {
            return null;
        }
    }

    // ------------------------------------------------------------------------

    /** Une stratégie pour écrire sur le flux XML. */
    protected abstract class BuilderStrategy {
        private void appendIfNotNull(StringBuilder sb, String text) {
            if (text != null) sb.append(text);
        }

        private void writeIfNotNull(String text) {
            if (text != null) write(text);
        }

        public void start(Tag tag) {
            // Dans le préfixe, remplacer plusieurs NL par un seul
            StringBuilder sb = new StringBuilder();
            Tag parent = tag.getParent();
            if (parent != null) appendIfNotNull(sb, parent.getChildrenPrefix());
            appendIfNotNull(sb, tag.getPrefix());
            SB.replaceAll(sb, Str.NEWLINES, getNl());
            if (sb.length() > 0) write(sb.toString());
            // Ajouter le tag
            String name = tag.getName();
            if (name != null) write(XB.getStartTag(name, tag.getAttrs(), tag.isEmpty(), true));
            // Puis le suffixe
            writeIfNotNull(tag.getSuffix());
        }

        public void end(Tag tag) {
            // Dans le préfixe, remplacer plusieurs NL par un seul
            StringBuilder sb = new StringBuilder();
            appendIfNotNull(sb, tag.getChildrenPrefix());
            appendIfNotNull(sb, tag.getPrefix());
            SB.replaceAll(sb, Str.NEWLINES, getNl());
            if (sb.length() > 0) write(sb.toString());
            // Ajouter le tag
            String name = tag.getName();
            if (name != null) write(XB.getEndTag(name));
            // Puis le suffixe
            writeIfNotNull(tag.getSuffix());
        }

        /**
         * Obtenir la liste des éléments qu'il est possible d'écrire sur le flux en tant que tags
         * ouvrants ou complets.
         */
        public abstract Collection<Tag> getCommitables();

        /** Indiquer que les éléments de {@link #getCommitables()} ont été écrits sur le flux. */
        protected abstract void wroteCommitables();

        protected void writeCommitables() {
            for (Tag tag : xbs.getCommitables()) {
                start(tag);
            }
            wroteCommitables();
        }

        /**
         * Obtenir la liste des éléments qu'il est possible d'écrire sur le flux comme tags fermant.
         */
        public abstract Collection<Tag> getEndables();

        /** Indiquer que les éléments de {@link #getEndables()} ont été écrits sur le flux. */
        protected abstract void wroteEndables();

        protected void writeEndables() {
            for (Tag tag : xbs.getEndables()) {
                end(tag);
            }
            wroteEndables();
        }

        /**
         * Si l'un des attributs est une déclaration de namespace, le corriger si nécessaire, parce
         * que l'utilisateur utilise peut-être un alias. L'objet est modifié en place.
         * 
         * @param attrs
         */
        public void fixNamespaceAttrs(XMLAttributes attrs) {
            if (attrs != null) {
                int size = attrs.size();
                for (int i = 0; i < size; i++) {
                    XMLAttribute attr = attrs.get(i);
                    String name = attr.getName();
                    if (Namespaces.isAttrNamespaceDecl(name)) {
                        String uri = Namespaces.getURI(attr.getValue());
                        if (uri != null) attrs.set(i, name, uri);
                    }
                }
            }
        }

        /**
         * Ouvrir un tag.
         * <p>
         * A l'issu de cet appel, la méthode {@link #getCommitables()} donne la liste des éléments à
         * écrire sur le flux en tant que tags *ouvrant* &lt;tag&gt;. Il est possible d'utiliser
         * {@link #writeCommitables()} à cet effet.
         * </p>
         */
        public abstract void _start(String name, XMLAttributes attrs);

        public void start(String name, XMLAttributes attrs) {
            fixNamespaceAttrs(attrs);
            _start(name, attrs);
        }

        /**
         * Ajouter des attributs au dernier tag ouvrant. Le tag ne doit pas avoir été commité.
         * 
         * @throws IllegalStateException si le tag ouvrant a déjà été commité.
         */
        public abstract void _addattrs(XMLAttributes attrs);

        public void addattrs(XMLAttributes attrs) {
            fixNamespaceAttrs(attrs);
            _addattrs(attrs);
        }

        /**
         * Fermer un tag.
         * <p>
         * A l'issu de cet appel, la méthode {@link #getCommitables()} donne la liste des éléments à
         * écrire sur le flux en tant que tags *complets* &lt;tag /&gt;, et la méthode
         * {@link #getEndables()} donne la liste des tags à écrire sur le flux en tant que tags
         * *fermant* &lt;/tag&gt;. Il est possible d'utiliser les méthodes
         * {@link #writeCommitables()} et {@link #writeEndables()} à cet effet.
         * </p>
         */
        public abstract void end(String name);

        /**
         * Indiquer qu'il faut écrire un saut de ligne. Suivant le contexte, le saut de ligne peut
         * ne pas avoir de valeur sémantique.
         * <p>
         * A l'issu de cet appel, la méthode {@link #getCommitables()} donne la liste des éléments à
         * écrire sur le flux en tant que tags *ouvrant* &lt;tag&gt;. Il est possible d'utiliser
         * {@link #writeCommitables()} à cet effet. Puis, il est de la responsabilité de l'appelant
         * d'écrire effectivement le saut de ligne.
         * </p>
         */
        public abstract void nl();

        /**
         * Indiquer qu'il faut écrire du texte.
         * <p>
         * A l'issu de cet appel, la méthode {@link #getCommitables()} donne la liste des éléments à
         * écrire sur le flux en tant que tags *ouvrant* &lt;tag&gt;. Il est possible d'utiliser
         * {@link #writeCommitables()} à cet effet. Puis, il est de la responsabilité de l'appelant
         * d'écrire effectivement le texte.
         * </p>
         */
        public abstract void text();

        /**
         * Indiquer qu'il faut écrire des commentaires.
         * <p>
         * A l'issu de cet appel, la méthode {@link #getCommitables()} donne la liste des éléments à
         * écrire sur le flux en tant que tags *ouvrant* &lt;tag&gt;. Il est possible d'utiliser
         * {@link #writeCommitables()} à cet effet. Puis, il est de la responsabilité de l'appelant
         * d'écrire effectivement le commentaire
         * </p>
         */
        public abstract void comment();

        /**
         * Forcer l'écriture des éléments XML en attente.
         * <p>
         * A l'issu de cet appel, la méthode {@link #getCommitables()} donne la liste des éléments à
         * écrire sur le flux en tant que tags *ouvrant* &lt;tag&gt;. Il est possible d'utiliser
         * {@link #writeCommitables()} à cet effet.
         * </p>
         */
        public abstract void flush();
    }

    /** Retourner une stratégie pour écrire sur le flux XML. */
    protected BuilderStrategy getXMLBuilderStragegy(boolean indent) {
        if (indent) return new IndentedXMLStrategy();
        else return new RawXMLStragegy();
    }

    // ------------------------------------------------------------------------

    public AbstractXMLBuilder(String nl, boolean indent, String encoding) {
        setNl(nl);
        setIndent(indent);
        setEncoding(encoding);
    }

    public AbstractXMLBuilder() {
        this(null, false, null);
    }

    protected BuilderStrategy xbs;

    // ------------------------------------------------------------------------
    // Configuration

    private String nl;

    public String getNl() {
        return nl;
    }

    public void setNl(String nl) {
        if (nl == null) nl = LineSep.LF;
        this.nl = nl;
    }

    private boolean indent;

    public boolean canIndent() {
        return true;
    }

    public boolean isIndent() {
        return indent;
    }

    public void setIndent(boolean indent) {
        this.indent = indent;
        this.xbs = getXMLBuilderStragegy(indent);
    }

    private String encoding;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        if (encoding == null) encoding = Enc.UTF_8;
        this.encoding = encoding;
    }

    // ------------------------------------------------------------------------
    // Contrôle

    protected abstract void write(String text);

    public void flush() {
        xbs.flush();
    }

    public abstract void close();

    private IOException e;

    protected void saveException(IOException e) {
        this.e = e;
    }

    protected void clearException() {
        this.e = null;
    }

    public void throwSavedException() throws IOException {
        IOException e = this.e;
        this.e = null;
        if (e != null) throw e;
    }

    // ------------------------------------------------------------------------
    // Génération

    protected void writenl() {
        write(nl);
    }

    public void nl() {
        xbs.nl();
        writenl();
    }

    private static final String XML = "xml", VERSION = "version", ENCODING = "encoding";

    public void xmldecl(XMLAttributes attrs, boolean nl) {
        attrs = new XMLAttributes(attrs);
        // si nécessaire, ajouter l'attribut version *en première position*
        if (attrs.indexOf(VERSION) == -1) attrs.add(0, VERSION, "1.0");
        write(XB.getProcessingInstruction(XML, attrs));
        if (nl) nl();
    }

    public void xmldecl(XMLAttributes attrs) {
        xmldecl(attrs, true);
    }

    private static final String XML_STYLESHEET = "xml-stylesheet", TYPE = "type";

    public void xmlstylesheet(XMLAttributes attrs, boolean nl) {
        attrs = new XMLAttributes(attrs);
        // si nécessaire, ajouter l'attribut version *en première position*
        if (attrs.indexOf(VERSION) == -1) attrs.add(0, VERSION, "1.0");
        if (attrs.indexOf(TYPE) == -1) attrs.add(TYPE, "text/xsl");
        write(XB.getProcessingInstruction(XML_STYLESHEET, attrs));
        if (nl) nl();
    }

    public void xmlstylesheet(XMLAttributes attrs) {
        xmlstylesheet(attrs, true);
    }

    public void doctype(String rootElement, String publicId, String systemId, boolean close,
            boolean nl) {
        StringBuilder sb = new StringBuilder("<!DOCTYPE ");
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
        if (close) sb.append('>');
        write(sb.toString());
        if (nl) nl();
    }

    public void doctype(String rootElement, String publicId, String systemId, boolean close) {
        doctype(rootElement, publicId, systemId, close, close);
    }

    // public void doctype(String rootElement, Resource dtdfile, boolean nl) throws IOException {
    // String line;
    // BufferedReader r = dtdfile.getReader();
    //
    // write("<!DOCTYPE ");
    // write(rootElement);
    // write(" [");
    // nl();
    // while ((line = r.readLine()) != null) {
    // write(line);
    // nl();
    // }
    // write("]>");
    // if (nl) nl();
    // }

    public void inlinedtd(Reader dtdreader, boolean close) {
        // XXX à implémenter
        throw new UnsupportedOperationException();
    }

    private Pattern DOCTYPE_PATTERN = Pattern.compile("(?i)<!DOCTYPE\\s.*>");

    public void doctype(String doctype, boolean nl) {
        if (Doctypes.isValid(doctype)) {
            doctype = Doctypes.getDecl(doctype);
        } else if (!DOCTYPE_PATTERN.matcher(doctype).matches()) {
            throw new IllegalArgumentException("doctype non valide: " + doctype, null);
        }
        write(doctype);
        if (nl) nl();
    }

    public void doctype(String doctype) {
        doctype(doctype, true);
    }

    public void start(String name, XMLAttributes attrs) {
        xbs.start(name, attrs);
    }

    public void addattrs(XMLAttributes attrs) {
        xbs.addattrs(attrs);
    }

    public void addattr(XMLAttribute attr) {
        addattrs(new XMLAttributes(attr));
    }

    public void end(String name) {
        xbs.end(name);
    }

    public void rawtext(String text) {
        xbs.text();
        write(text);
    }

    public void text(String text) {
        xbs.text();
        write(XB.getQuotedText(text));
    }

    public void cdata(String text) {
        xbs.text();
        write(XB.getCData(text));
    }

    public void comment(String text) {
        xbs.comment();
        write(XB.getComment(text));
    }

    // ------------------------------------------------------------------------
    // Méthodes de convenance

    public void xmldecl() {
        xmldecl(new XMLAttributes(ENCODING, encoding), true);
    }

    public void xmlstylesheet(String href) {
        xmlstylesheet(new XMLAttributes("href", href), true);
    }

    public void start(String name) {
        start(name, null);
    }

    public void addattr(String name, String value) {
        addattrs(new XMLAttributes(name, value));
    }

    public void setNamespace(String uri) {
        addattr(new XMLAttribute(Namespaces.getAttrNamespaceDecl(null), uri));
    }

    public void addNamespace(String prefix, String uri) {
        addattr(new XMLAttribute(Namespaces.getAttrNamespaceDecl(prefix), uri));
    }

    /** écrire un élément XML simple avec éventuellement du texte à l'intérieur. */
    public void add(String name, XMLAttributes attrs, String text) {
        start(name, attrs);
        if (text != null) text(text);
        end(name);
    }

    public void add(String name, String text) {
        add(name, null, text);
    }

    /**
     * écrire un élément XML simple avec éventuellement du texte à l'intérieur dans une balise
     * &lt;![CDATA[...]]&gt;.
     */
    public void addcdata(String name, XMLAttributes attrs, String text) {
        start(name, attrs);
        if (text != null) cdata(text);
        end(name);
    }

    public void addcdata(String name, String text) {
        addcdata(name, null, text);
    }

    // ------------------------------------------------------------------------
    // Stratégie pour du XML sans mise en forme

    protected static final IllegalStateException noUncommitedTagFoundException() {
        return new IllegalStateException("addattrs: no uncommited tag found");
    }

    protected class RawXMLTag extends Tag {
        public RawXMLTag(String name, XMLAttributes attrs, boolean start) {
            super(name, attrs, start, false, null);
        }
    }

    /** Cette stratégie écrit les éléments les uns à la suite des autres, sans mise en forme. */
    protected class RawXMLStragegy extends BuilderStrategy {
        private Tag started;

        private Tag commitable;

        private boolean setCommitable() {
            if (started == null) {
                return false;
            } else {
                commitable = started;
                started = null;
                return true;
            }
        }

        public Collection<Tag> getCommitables() {
            if (commitable == null) return Collections.emptyList();
            else return Arrays.asList(new Tag[] {commitable});
        }

        protected void wroteCommitables() {
            commitable = null;
        }

        private Tag endable;

        public Collection<Tag> getEndables() {
            if (endable == null) return Collections.emptyList();
            else return Arrays.asList(new Tag[] {endable});
        }

        protected void wroteEndables() {
            endable = null;
        }

        public void _start(String name, XMLAttributes attrs) {
            setCommitable();
            started = new RawXMLTag(name, attrs, true);
            writeCommitables();
        }

        @Override
        public void _addattrs(XMLAttributes attrs) {
            if (started == null) throw noUncommitedTagFoundException();
            started.getAttrs().addAll(attrs);
        }

        public void end(String name) {
            if (setCommitable()) commitable.setEmpty(true);
            else endable = new RawXMLTag(name, null, false);
            writeCommitables();
            writeEndables();
        }

        public void nl() {
            text();
        }

        public void text() {
            setCommitable();
            writeCommitables();
        }

        public void comment() {
            setCommitable();
            writeCommitables();
        }

        public void flush() {
            setCommitable();
            writeCommitables();
        }
    }

    // ------------------------------------------------------------------------
    // Stratégie pour du XML indenté

    /**
     * Cettre stratégie met en forme les éléments XML au fur et à mesure qu'ils sont écrits.
     * <p>
     * L'algorithme est le suivant:
     * </p>
     * <ul>
     * <li>Si un élément contient UNIQUEMENT d'autres éléments enfants, alors les éléments enfants
     * sont écrits chacun sur une ligne, de cette manière:
     * 
     * <pre>
     * &lt;parent&gt;
     * &lt;child&gt;...&lt;/child&gt;
     * &lt;child&gt;...&lt;/child&gt;
     * ...
     * &lt;/parent&gt;
     * </pre>
     * </li>
     * <li>Si un élément contient du texte, alors ses enfants DIRECTS sont écrits sans mise en
     * forme, de cette manière:
     * 
     * <pre>
     * &lt;parent&gt;texte...&lt;child&gt;...&lt;/child&gt;texte...&lt;child&gt;...&lt;/child&gt;...&lt;/parent&gt;
     * </pre>
     * Les enfants de niveau 2 et plus sont susceptibles d'obéir à la première règle, comme dans cet
     * exemple:
     * 
     * <pre>
     * &lt;parent&gt;texte...&lt;child&gt;
     * &lt;child2&gt;...&lt;/child2&gt;
     * &lt;child2&gt;...&lt;/child2&gt;
     * &lt;/child&gt;texte...&lt;/parent&gt;
     * </pre>
     * </li>
     * </ul>
     */
    protected class IndentedXMLTag extends Tag {
        public IndentedXMLTag(String name, XMLAttributes attrs, boolean start, IndentedXMLTag parent) {
            super(name, attrs, start, false, parent);
        }

        private boolean commited;

        public boolean isCommited() {
            return commited;
        }

        public void setCommited() {
            commited = true;
        }

        private boolean textChildren;

        public boolean hasTextChildren() {
            return textChildren;
        }

        public void setHasTextChildren() {
            textChildren = true;
        }

        private boolean elementChildren;

        public void setHasElementChildren() {
            elementChildren = true;
        }

        public boolean hasChildren() {
            return textChildren || elementChildren;
        }

        public boolean hasElementChildrenOnly() {
            return elementChildren && !textChildren;
        }

        @Override
        public String getChildrenPrefix() {
            if (hasElementChildrenOnly()) return getNl();
            else return null;
        }
    }

    protected class IndentedXMLStrategy extends BuilderStrategy {
        public IndentedXMLStrategy() {
            elements = new LinkedList<IndentedXMLTag>();
            commitables = new ArrayList<Tag>();
            endables = new ArrayList<Tag>();
        }

        private LinkedList<IndentedXMLTag> elements;

        private IndentedXMLTag currentTag() {
            if (elements.isEmpty()) return null;
            else return elements.getLast();
        }

        private void commitElements() {
            for (IndentedXMLTag elem : elements) {
                if (!elem.isCommited()) {
                    commitables.add(elem);
                    elem.setCommited();
                }
            }
        }

        private ArrayList<Tag> commitables;

        public Collection<Tag> getCommitables() {
            return commitables;
        }

        protected void wroteCommitables() {
            commitables.clear();
        }

        private ArrayList<Tag> endables;

        public Collection<Tag> getEndables() {
            return endables;
        }

        protected void wroteEndables() {
            endables.clear();
        }

        public void _start(String name, XMLAttributes attrs) {
            for (IndentedXMLTag elem : elements) {
                elem.setHasElementChildren();
            }
            IndentedXMLTag parent = currentTag();
            elements.add(new IndentedXMLTag(name, attrs, true, parent));
            writeCommitables();
        }

        @Override
        public void _addattrs(XMLAttributes attrs) {
            IndentedXMLTag lastTag = currentTag();
            if (lastTag == null || lastTag.isCommited()) throw noUncommitedTagFoundException();
            lastTag.getAttrs().addAll(attrs);
        }

        public void end(String name) {
            IndentedXMLTag ended = elements.removeLast();
            commitElements();
            if (ended.hasChildren()) {
                ended.setStart(false);
                endables.add(ended);
            } else {
                ended.setEmpty(true);
                commitables.add(ended);
            }
            writeCommitables();
            writeEndables();
        }

        public void nl() {
            text();
        }

        public void text() {
            IndentedXMLTag currentTag = currentTag();
            if (currentTag != null) currentTag.setHasTextChildren();
            commitElements();
            writeCommitables();
        }

        public void comment() {
            commitElements();
            writeCommitables();
        }

        public void flush() {
            commitElements();
            writeCommitables();
        }
    }
}
