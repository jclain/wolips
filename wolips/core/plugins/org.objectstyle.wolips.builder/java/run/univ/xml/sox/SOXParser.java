/* SOXParser.java
 * Created on 24 déc. 2004
 */
package run.univ.xml.sox;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

import run.univ.Cls;
import run.univ.Str;
import run.univ.io.DecodableStream;
import run.univ.io.DecodedStreamReader;
import run.univ.xml.base.Doctypes;
import run.univ.xml.base.Entities;
import run.univ.xml.base.Namespaces;
import run.univ.xml.base.XMLAttributes;
import run.univ.xml.base.XMLBuilder;
import run.univ.xml.base.Namespaces.Namespace;
import run.univ.xml.sox.Lexer.EndToken;
import run.univ.xml.sox.Lexer.LexerException;
import run.univ.xml.sox.Lexer.Token;
import run.univ.xml.sox.SOXLexer.CommentToken;
import run.univ.xml.sox.SOXLexer.DeclToken;
import run.univ.xml.sox.SOXLexer.DedentToken;
import run.univ.xml.sox.SOXLexer.EqualsToken;
import run.univ.xml.sox.SOXLexer.IndentToken;
import run.univ.xml.sox.SOXLexer.LineToken;
import run.univ.xml.sox.SOXLexer.NameToken;
import run.univ.xml.sox.SOXLexer.StringToken;
import run.univ.xml.sox.SOXLexer.TagToken;

/**
 * TODO tenir compte des passages à la ligne des fichiers SOX pour le texte!
 * 
 * @author jclain
 */
public abstract class SOXParser {
    public static class ParserException extends Exception {
        public ParserException() {
            super();
        }

        public ParserException(String message) {
            super(message);
        }

        public ParserException(String message, Throwable cause) {
            super(message, cause);
        }

        public ParserException(Throwable cause) {
            super(cause);
        }
    }

    public static class FatalErrorException extends ParserException {
        public FatalErrorException() {
            super();
        }

        public FatalErrorException(String message) {
            super(message);
        }

        public FatalErrorException(String message, Throwable cause) {
            super(message, cause);
        }

        public FatalErrorException(Throwable cause) {
            super(cause);
        }
    }

    public SOXParser() {
        this.lexer_factory = SOXLexer.class;
        token_stack = new ArrayList();
    }

    public SOXParser(Reader r) {
        this();
        setInput(r);
    }

    public SOXParser(DecodableStream ds) throws UnsupportedEncodingException {
        this();
        setInput(new DecodedStreamReader(ds));
    }

    /** La source des données. */
    protected Reader input;

    public Reader getInput() {
        return input;
    }

    public void setInput(Reader input) {
        this.input = input;
    }

    /**
     * parser le flux en entrée et générer les événements XML.
     * 
     * @throws Exception
     */
    public void parse() throws Exception {
        try {
            ns.reset();
            current_element = null;
            init_lexer();

            setDocumentLocator();
            startDocument();
            parse_document();
            endDocument();
        } catch (Exception e) {
            if (e instanceof FatalErrorException) {
                fatalError(e.getMessage(), e);
            } else {
                throw e;
            }
        }
    }

    /** @see org.xml.sax.ErrorHandler */
    public abstract void warning(String msg, Exception e) throws Exception;

    /** @see org.xml.sax.ErrorHandler */
    public abstract void error(String msg, Exception e) throws Exception;

    /** @see org.xml.sax.ErrorHandler */
    public abstract void fatalError(String msg, Exception e) throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void setDocumentLocator();

    public abstract void setDocumentLocation(int line, int column);

    /** @see org.xml.sax.ContentHandler */
    public abstract void startDocument() throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void endDocument() throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void startPrefixMapping(String prefix, String uri) throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void endPrefixMapping(String prefix) throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void startElement(String nsURI, String lName, String qName,
            org.xml.sax.Attributes attrs) throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void endElement(String nsURI, String lName, String qName) throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void characters(char[] ch, int start, int length) throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void processingInstruction(String target, String data) throws Exception;

    /** @see org.xml.sax.ContentHandler */
    public abstract void skippedEntity(String name) throws Exception;

    /** @see org.xml.sax.ext.LexicalHandler */
    public abstract void startDTD(String name, String publicId, String systemId) throws Exception;

    /** @see org.xml.sax.ext.LexicalHandler */
    public abstract void endDTD() throws Exception;

    /** @see org.xml.sax.ext.LexicalHandler */
    public abstract void startEntity(String name) throws Exception;

    /** @see org.xml.sax.ext.LexicalHandler */
    public abstract void endEntity(String name) throws Exception;

    /** @see org.xml.sax.ext.LexicalHandler */
    public abstract void startCDATA() throws Exception;

    /** @see org.xml.sax.ext.LexicalHandler */
    public abstract void endCDATA() throws Exception;

    /** @see org.xml.sax.ext.LexicalHandler */
    public abstract void comment(char[] ch, int start, int length) throws Exception;

    /** La classe utilisé pour instancier un lexer. */
    private Class lexer_factory;

    /** Le lexer utilisé pour lire les tokens. */
    private Lexer lexer;

    /** Initialiser le lexer. */
    protected void init_lexer() throws Exception {
        lexer = (Lexer)lexer_factory.newInstance();
        lexer.setInput(input);
        nextToken();
    }

    /** Le token courant. */
    protected Token current_token;

    /** pile de token pour permettre le pushback. */
    protected ArrayList token_stack;

    /** Méthode de convenance pour classifier le token courant. */
    protected boolean isa(Class c) {
        return c.isInstance(current_token);
    }

    /** Méthode de convenance pour classifier un token. */
    protected boolean isa(Token token, Class c) {
        return c.isInstance(token);
    }

    /**
     * Méthode de convenance pour tester la fin du flux sur la base du token courant.
     */
    protected boolean isEOF() {
        return isa(EndToken.class);
    }

    private boolean namespaceAware = true;

    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    private boolean namespacePrefixes = false;

    public void setNamespacePrefixes(boolean namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }

    public void setValidating(boolean validating) {
        // ignorer cette valeur
    }

    private boolean lexicalEvents = false;

    public void setLexicalEvents(boolean lexicalEvents) {
        this.lexicalEvents = lexicalEvents;
    }

    private boolean multipleTopElements;

    public void setMultipleTopElements(boolean multipleTopElements) {
        this.multipleTopElements = multipleTopElements;
    }

    private ArrayList defaultNamespaces = new ArrayList();

    private void addToDefaultNamespaces(String prefix, String uri) {
        defaultNamespaces.add(new String[] {prefix, uri});
    }

    private void addToDefaultNamespaces(Namespace ns) {
        addToDefaultNamespaces(ns.getPrefix(), ns.getURI());
    }

    /**
     * Obtenir le prochain token du lexer
     */
    protected Token nextToken() throws Exception {
        if (token_stack.size() > 0) {
            current_token = (Token)token_stack.get(0);
            token_stack.remove(0);
        } else {
            try {
                current_token = lexer.token();
                setDocumentLocation(lexer.getLine(), lexer.getColumn());
            } catch (LexerException e) {
                throw new FatalErrorException("Erreur du lexer: " + e.getMessage(), e);
            }
        }

        return current_token;
    }

    protected void prevToken(Token token) {
        token_stack.add(0, this.current_token);
        this.current_token = token;
    }

    protected void expected(Class expected, Token got, String msg) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (msg != null) {
            sb.append(msg);
            sb.append(": ");
        }
        if (expected != null) {
            sb.append("attendu ");
            sb.append(Cls.basename(expected));
            sb.append(", ");
        }
        sb.append("obtenu ");
        sb.append(Cls.basename(got.getClass()));

        throw new FatalErrorException(sb.toString(), null);
    }

    private NamespaceSupport ns = new NamespaceSupport();

    private String[] parts = new String[3];

    /**
     * Instance unique pour les éléments qui n'ont pas d'attribut.
     */
    private XMLAttributes empty_attrs = new XMLAttributes.Implementation();

    /**
     * Instance unique pour les éléments qui n'ont pas d'attribut.
     */
    private AttributesImpl empty_saxattrs = new AttributesImpl();

    /** élement courant. */
    private Element current_element;

    private static final Pattern PUBLIC_PATTERN = Pattern
            .compile("<!DOCTYPE\\s+(\\S+)\\s+PUBLIC\\s+\"(.*?)\"(?:\\s+\"(.*?)\")?");

    private static final Pattern SYSTEM_PATTERN = Pattern
            .compile("<!DOCTYPE\\s+(\\S+)\\s+SYSTEM\\s+\"(.*?)\"");

    private static final Pattern HTML5_PATTERN = Pattern.compile("<!DOCTYPE\\s+html");

    protected void document_type(String doctype) throws Exception {
        String name, publicId, systemId;
        Matcher m;

        if ((m = PUBLIC_PATTERN.matcher(doctype)).lookingAt()) {
            name = m.group(1);
            publicId = m.group(2);
            systemId = m.group(3);
        } else if ((m = SYSTEM_PATTERN.matcher(doctype)).lookingAt()) {
            name = m.group(1);
            publicId = null;
            systemId = m.group(2);
        } else if ((m = HTML5_PATTERN.matcher(doctype)).lookingAt()) {
            name = "html";
            publicId = null;
            systemId = null;
        } else {
            error("doctype non valide: " + doctype, null);
            return;
        }

        startDTD(name, publicId, systemId);
        endDTD();
    }

    protected void start_element(Element element, String name, XMLAttributes attrs)
            throws Exception {
        if (namespaceAware) {
            if (element.isTopElement() && defaultNamespaces.size() != 0) {
                boolean newAttrs = true;
                for (Iterator it = defaultNamespaces.iterator(); it.hasNext();) {
                    String[] alias_uri = (String[])it.next();
                    String alias = alias_uri[0];
                    String uri = alias_uri[1];

                    String attrname = Namespaces.getAttrNamespaceDecl(alias);
                    if (attrs.indexOf(attrname) == -1) {
                        if (newAttrs) {
                            attrs = new XMLAttributes.Implementation(attrs);
                            newAttrs = false;
                        }
                        attrs.add(attrname, uri);
                    }
                }
            }

            // gestion des namespaces
            ns.pushContext();
            for (int i = 0; i < attrs.count(); i++) {
                String attrname = attrs.getName(i);
                if (Namespaces.isAttrNamespaceDecl(attrname)) {
                    String attrvalue = attrs.getValue(i);
                    String prefix = Namespaces.getAttrNamespacePrefix(attrname);
                    startPrefixMapping(prefix, attrvalue);
                    ns.declarePrefix(prefix, attrvalue);
                }
            }

            // construire les attributs
            AttributesImpl saxattrs;
            if (attrs == empty_attrs) {
                saxattrs = empty_saxattrs;
            } else {
                saxattrs = new AttributesImpl();
                for (int i = 0; i < attrs.count(); i++) {
                    String attrname = attrs.getName(i);
                    boolean namespacePrefix = Namespaces.isAttrNamespaceDecl(attrname);
                    if (namespacePrefixes || !namespacePrefix) {
                        String attrvalue = attrs.getValue(i);
                        if (namespacePrefix) {
                            // traiter différemment xmlns:
                            parts[0] = "";
                            parts[1] = Namespaces.getAttrNamespacePrefix(attrname);
                            parts[2] = attrname;
                        } else {
                            if (ns.processName(attrname, parts, true) == null) {
                                throw new FatalErrorException("namespace non déclaré: "
                                        + Namespaces.getNamespacePrefix(attrname) + ", attrname="
                                        + attrname + ", attrvalue=" + attrvalue);
                            }
                        }
                        saxattrs.addAttribute(parts[0], parts[1], parts[2], "CDATA", attrvalue);
                    }
                }
            }
            // invoquer la méthode de traitement
            if (ns.processName(name, parts, false) == null) {
                throw new FatalErrorException("namespace non déclaré: "
                        + Namespaces.getNamespacePrefix(name));
            }
            startElement(parts[0], parts[1], parts[2], saxattrs);

        } else {
            // construire les attributs
            AttributesImpl saxattrs;
            if (attrs == empty_attrs) {
                saxattrs = empty_saxattrs;
            } else {
                saxattrs = new AttributesImpl();
                for (int i = 0; i < attrs.count(); i++) {
                    String attrname = attrs.getName(i);
                    String attrvalue = attrs.getValue(i);
                    saxattrs.addAttribute("", attrname, attrname, "CDATA", attrvalue);
                }
            }
            // invoquer la méthode de traitement
            startElement("", name, name, saxattrs);
        }
    }

    protected void end_element(String name) throws Exception {
        if (namespaceAware) {
            // invoquer la méthode de traitement
            if (ns.processName(name, parts, false) == null) {
                throw new FatalErrorException("namespace non déclaré: "
                        + Namespaces.getNamespacePrefix(name));
            }
            endElement(parts[0], parts[1], parts[2]);

            // gestion des namespaces
            for (Enumeration e = ns.getDeclaredPrefixes(); e.hasMoreElements();) {
                String prefix = (String)e.nextElement();
                endPrefixMapping(prefix);
            }
            ns.popContext();
        } else {
            endElement("", name, name);
        }
    }

    protected void text(String text, boolean cdata) throws Exception {
        char[] chars;
        if (cdata) {
            chars = text.toCharArray();
            startCDATA();
            characters(chars, 0, chars.length);
            endCDATA();
        } else {
            text = Entities.unquote(text);
            Matcher m = Entities.ENTITY_PATTERN.matcher(text);
            if (m.find()) {
                // S'il reste des entités, les envoyer au fur et à mesure.
                int pos = 0;
                do {
                    // avant l'entité
                    if (m.start() > pos) {
                        chars = text.substring(pos, m.start()).toCharArray();
                        characters(chars, 0, chars.length);
                    }
                    // l'entité
                    skippedEntity(m.group(1));

                    pos = m.end();
                } while (m.find(pos));
                // envoyer le texte restant
                if (text.length() > pos) {
                    chars = text.substring(pos).toCharArray();
                    characters(chars, 0, chars.length);
                }
            } else {
                chars = text.toCharArray();
                characters(chars, 0, chars.length);
            }
        }
    }

    protected void comment(String text) throws Exception {
        if (lexicalEvents) {
            char[] chars = text.toCharArray();
            comment(chars, 0, chars.length);
        }
    }

    private static final Pattern RE_CLASS_OR_ID = Pattern.compile("(\\.|#)([^#.]+)");

    protected class Element {
        /** élément parent. */
        private Element parent;

        /** nom de l'élément. */
        private String name;

        /** attributs de cet élément */
        private XMLAttributes attrs;

        /** l'élément ouvrant a-t-il déjà été écrit? */
        private boolean committed;

        public Element(String name) throws Exception {
            this.parent = current_element;
            current_element = this;

            Matcher m;
            ArrayList cs = null;
            ArrayList ids = null;
            if ((m = RE_CLASS_OR_ID.matcher(name)).find()) {
                String actualName = name.substring(0, m.start());
                String csOrIds = name.substring(m.start());
                while ((m = RE_CLASS_OR_ID.matcher(csOrIds)).lookingAt()) {
                    String prefix = m.group(1);
                    if (prefix.equals(".")) {
                        if (cs == null) cs = new ArrayList();
                        cs.add(m.group(2));
                    } else if (prefix.equals("#")) {
                        if (ids == null) ids = new ArrayList();
                        ids.add(m.group(2));
                    }
                    csOrIds = csOrIds.substring(m.end());
                }
                if (Str.isempty(csOrIds)) {
                    name = actualName;
                    if (Str.isempty(name)) name = "div";
                } else {
                    cs = null;
                    ids = null;
                }
            }

            this.name = name;
            this.committed = false;
            if (parent != null) {
                parent.haveChild();
                parent.commit();
            }

            if (ids != null) {
                for (Iterator it = ids.iterator(); it.hasNext();) {
                    String id = (String)it.next();
                    setAttr("id", id);
                }
            }
            if (cs != null) {
                StringBuffer sb = new StringBuffer();
                boolean first = true;
                for (Iterator it = cs.iterator(); it.hasNext();) {
                    String c = (String)it.next();
                    if (first) first = false;
                    else sb.append(" ");
                    sb.append(c);
                }
                setAttr("class", sb.toString());
            }
        }

        public boolean isTopElement() {
            return parent == null;
        }

        public void haveChild() throws Exception {
            commit();
        }

        public void commit() throws Exception {
            if (!committed) {
                start_element(this, name, attrs != null? attrs: empty_attrs);
                committed = true;
            }
        }

        public void close() throws Exception {
            commit();
            end_element(name);

            if (current_element != this)
                throw new FatalErrorException("Element fermant mal placé");
            current_element = parent;
        }

        public void setAttr(String name, String value) throws Exception {
            if (committed) throw new FatalErrorException("Attribut non attendu: " + name);

            if (Namespaces.isAttrNamespaceDecl(name) && Namespaces.isValid(value)) {
                value = Namespaces.getURI(value);
            }

            if (attrs == null) attrs = new XMLAttributes.Implementation();
            attrs.add(name, value);
        }

        public void appendText(String text, boolean cdata) throws Exception {
            if (current_element != this)
                throw new FatalErrorException("Données non attendues: '" + text + "'");

            commit();
            text(text, cdata);
        }
    }

    /**
     * Méthode qui fait effectivement le parsing du flux en entrée.
     */
    private void parse_document() throws Exception {
        // lire d'accord les déclaration au début du document
        int pos;
        boolean soxdecl = false; // avons-nous déjà eu une déclaration <?sox
        String doctype_alias = null; // doctype, s'il est donné dans l'en-tête <?sox
        String xmlstylesheet_href = null; // stylesheet XML, s'il est donné dans l'en-tête <?sox
        String[] namespace_aliases = null; // liste des namespaces par défaut, s'il est donné dans
        // l'en-tête <?sox
        Token doctype = null; // doctype, s'il est donné avec un en-tête <!DOCTYPE
        boolean PIs = false; // avons-nous rencontré une instruction de traitement?

        while (isa(DeclToken.class) || isa(CommentToken.class)) {
            if (isa(CommentToken.class)) {
                if (lexicalEvents) comment(current_token.getValue());

            } else if (isa(DeclToken.class)) {
                DeclToken decl = (DeclToken)current_token;
                if (decl.isSOXDecl()) {
                    if (PIs) {
                        warning("<?sox doit être la première déclaration", null);
                    }
                    if (soxdecl) {
                        error("Une seule déclaration <?sox est autorisée", null);
                    } else {
                        soxdecl = true;
                        XMLAttributes attrs = decl.getAttrs();

                        // intialiser le doctype
                        if ((pos = attrs.indexOf("doctype")) != -1) {
                            String alias = attrs.getValue(pos);
                            if (Doctypes.isValid(alias)) {
                                doctype_alias = alias;
                            } else {
                                error("Type de document non valide: " + alias, null);
                            }
                        }
                        // initialiser le stylesheet XML
                        if ((pos = attrs.indexOf("stylesheet")) != -1) {
                            xmlstylesheet_href = attrs.getValue(pos);
                        }
                        // initialiser la liste des namespaces par défaut
                        if ((pos = attrs.indexOf("namespaces")) != -1) {
                            String t = attrs.getValue(pos);
                            Pattern COMMA = Pattern.compile("\\p{Space}*,\\p{Space}*");
                            namespace_aliases = COMMA.split(t);
                        }
                    }
                } else if (decl.isDoctype()) {
                    if (doctype_alias != null || doctype != null) {
                        boolean warning = false;
                        if (doctype_alias != null) {
                            // si l'alias et le doctype sont les même, afficher seulement un warning
                            String declstring = Doctypes.getDecl(doctype_alias);
                            if (declstring.equals(decl.getValue())) {
                                warning = true;
                            }
                        }
                        if (warning) {
                            warning("Une seule déclaration doctype est nécessaire", null);

                        } else {
                            error("Une seule déclaration doctype est autorisée", null);
                        }
                    } else {
                        doctype = decl;
                        document_type(decl.getValue());
                    }
                } else if (decl.isProcessingInstruction()) {
                    processingInstruction(decl.getTarget(), decl.getValue());
                    PIs = true;
                }
            }

            nextToken();
        }
        if (xmlstylesheet_href != null) {
            XMLAttributes attrs = new XMLAttributes.Implementation("version", "1.0");
            attrs.add("href", xmlstylesheet_href);
            attrs.add("type", "text/xsl");
            processingInstruction("xml-stylesheet", XMLBuilder.getAttributes(attrs).trim());
        }
        if (doctype_alias != null) {
            Doctypes.Doctype dt = Doctypes.getDoctype(doctype_alias);
            addToDefaultNamespaces("", dt.getNsURI());
            document_type(dt.getDoctypeDeclaration());
        }
        if (namespace_aliases != null && namespace_aliases.length != 0) {
            for (int i = 0; i < namespace_aliases.length; i++) {
                String alias = namespace_aliases[i];
                if (Namespaces.isValid(alias)) {
                    addToDefaultNamespaces(Namespaces.getNamespace(alias));
                } else {
                    error("alias de namespace invalide: " + alias, null);
                }
            }
        }

        // puis parser le reste du document
        boolean auMoinsUnElement = false;
        if (multipleTopElements) {
            while (true) {
                if (parse_element()) {
                    auMoinsUnElement = true;
                } else {
                    if (isEOF()) break;
                    else throw new FatalErrorException("Il ne peut y avoir "
                            + "que des éléments à la racine");
                }
            }
        } else {
            if (parse_element()) auMoinsUnElement = true;
            if (!isEOF()) {
                expected(EndToken.class, current_token, "Il faut au plus un élément racine");
            }
        }
        if (!auMoinsUnElement) throw new FatalErrorException("Il faut au moins un élément racine");
    }

    /** signification de l'indice 0 dans context: si true, le précédent token était NameToken. */
    private static final int ctx_wasa_name = 0;

    /** signification de l'indice 1 dans context: si true, on parse du texte pour la première fois. */
    private static final int ctx_sometext_parsed = 1, ctx_text_before = 1;

    /** signification de l'indice 2 dans context: si true, on a parsé une chaine multiligne. */
    private static final int ctx_multiline_string = 2;

    /** signification de l'indice 3 dans context: si true, on a parsé une chaine quotée avec '. */
    private static final int ctx_singlequoted_string = 3;

    private void unexpected_comment(CommentToken token) throws Exception {
        if (lexicalEvents) {
            warning("Commentaire ignoré: " + token.getValue(), null);
        }
    }

    private void parse_comment(CommentToken token, Element element) throws Exception {
        element.commit();
        if (lexicalEvents) {
            comment(token.getValue());
        }
    }

    private boolean parse_comment(Element element) throws Exception {
        if (isa(CommentToken.class)) {
            parse_comment((CommentToken)current_token, element);
            nextToken();
            return true;
        }
        return false;
    }

    /**
     * parser un élément à partir de l'élément courant.
     */
    private boolean parse_element() throws Exception {
        // un nom XML
        Token name = current_token;
        if (!isa(name, NameToken.class)) return false;

        // puis le symbole >
        Token tag = nextToken();
        if (isa(tag, TagToken.class)) {
            nextToken();
        } else {
            prevToken(name);
            return false;
        }

        // créer l'élément
        Element element = new Element(name.getValue());

        // parser une liste d'attributs
        while (parse_attribute(element))
            ;

        // puis parser du texte qui fait partie de cet élément
        String text; // chaque élément de texte lu.
        boolean[] context = new boolean[] {false, false, false, false};

        if ((text = parse_text(context)) != null) {
            element.appendText(text, context[ctx_singlequoted_string]);
        }

        // puis parser le ou les enfants
        CommentToken pendingComment = null;
        if (!parse_element()) {
            while (isa(LineToken.class)) {
                nextToken();
            }
            if (isa(CommentToken.class)) {
                pendingComment = (CommentToken)current_token;
                nextToken();
            }

            if (isa(IndentToken.class)) {
                nextToken();
            } else {
                if (pendingComment != null) parse_comment(pendingComment, element);
                element.close();
                return true;
            }

            // si du texte n'a pas encore été parsé, continuer à parser des
            // attributs, éventuellement séparés par des sauts de lignes
            if (!context[ctx_sometext_parsed]) {
                while (true) {
                    if (parse_attribute(element)) {
                        if (pendingComment != null) {
                            unexpected_comment(pendingComment);
                            pendingComment = null;
                        }
                    } else {
                        break;
                    }

                    while (isa(LineToken.class))
                        nextToken();
                    if (isa(CommentToken.class)) {
                        // s'il y a un commentaire, vérifier qu'il n'y a pas d'attributs après.
                        pendingComment = (CommentToken)current_token;
                        nextToken();
                    }
                }
            }
            if (pendingComment != null) {
                parse_comment(pendingComment, element);
                pendingComment = null;
            }

            boolean something_parsed;
            do {
                something_parsed = false;
                // puis parser du texte qui fait partie de cet élément
                while ((text = parse_text(context)) != null) {
                    something_parsed = true;
                    element.appendText(text, context[ctx_singlequoted_string]);
                }
                // sauter les lignes vides
                while (isa(LineToken.class)) {
                    something_parsed = true;
                    nextToken();
                }
                parse_comment(element);
                // puis parser les éléments fils
                while (parse_element()) {
                    context[ctx_text_before] = false;
                    something_parsed = true;
                }
                // sauter les lignes vides
                while (isa(LineToken.class)) {
                    something_parsed = true;
                    nextToken();
                }
                parse_comment(element);
            } while (something_parsed);

            if (isa(DedentToken.class)) {
                nextToken();
            } else {
                expected(DedentToken.class, current_token, null);
            }
        }

        // fin de l'élément
        element.close();

        // tout a été parsé correctement
        return true;
    }

    /**
     * parser un attribut name=value
     * 
     * @return false si un attribut n'a pas été parsé
     */
    private boolean parse_attribute(Element elem) throws Exception {
        // un nom XML
        if (!isa(NameToken.class)) return false;
        Token nameToken = current_token;

        // puis le symbole =
        /* Token equals= */nextToken();
        if (isa(EqualsToken.class)) {
            nextToken();
        } else {
            prevToken(nameToken);
            return false;
        }

        // puis le texte de la valeur
        Token valueToken = parse_string();
        if (valueToken == null) {
            expected(null, current_token, "Attendu la valeur de l'attribut");
        }

        String value = valueToken.getValue();
        if (valueToken instanceof StringToken && !((StringToken)valueToken).isSingleQuoted()) {
            value = Entities.unquote(value, true);
        }

        elem.setAttr(nameToken.getValue(), value);
        return true;
    }

    /**
     * Parser une valeur texte *unique*.
     * 
     * @return null si cela n'a pas été possible.
     */
    private Token parse_string() throws Exception {
        Token name_or_string = current_token;
        if (!isa(NameToken.class) && !isa(StringToken.class)) return null;

        Token tag_or_equals = nextToken();
        if (isa(name_or_string, NameToken.class)
                && (isa(tag_or_equals, TagToken.class) || isa(tag_or_equals, EqualsToken.class))) {
            // le prochain est un élément ou un attribut
            prevToken(name_or_string);
            return null;
        }
        return name_or_string;
    }

    /**
     * Parser autant de texte que possible.
     * <p>
     * Les NameToken sont séparés de leur "voisins" par un espace, alors que les StringToken sont
     * concaténés sans espaces intermédiaires.
     * </p>
     * 
     * @param context <p>
     *        <b>en entrée </b>: un tableau {wasa_name, text_before}. text_before est vrai si on a
     *        parsé du texte avant (auquel cas l'appel à cet méthode sert à compléter le résultat
     *        d'un appel précédent. wasa_name est vrai si le dernier token du texte précédent était
     *        un NameToken. Si ce tableau vaut null, il est équivalent à un tableau qui contient
     *        {false, false}. wasa_name n'est pris en compte que si text_before est vrai.
     *        </p>
     *        <p>
     *        <b>en sortie </b>: si le tableau n'était pas null, le même tableau qui contient true
     *        pour wasa_name si le dernire token parsé était NameToken, et true pour text_before.
     *        </p>
     * @return le texte parsé, null si l'on n'a pas pu parser de texte.
     */
    private String parse_text(boolean[] context) throws Exception {
        boolean sometext_parsed;
        boolean isa_name, wasa_name, text_before, multiline, singlequoted;
        StringBuffer sb = new StringBuffer();
        Token name_or_string = null;

        // récupérer le contexte pour initialiser text_before et wasa_name
        text_before = false;
        wasa_name = false;
        multiline = false;
        singlequoted = false;
        if (context != null) {
            if (context.length > 0) wasa_name = context[ctx_wasa_name];
            if (context.length > 1) text_before = context[ctx_text_before];
            if (context.length > 2) multiline = context[ctx_multiline_string];
            if (context.length > 3) singlequoted = context[ctx_singlequoted_string];
            if (!text_before) wasa_name = false;
        }
        // lire autant de texte que possible
        sometext_parsed = false;
        while ((name_or_string = parse_string()) != null) {
            if (name_or_string instanceof StringToken) {
                StringToken stringToken = (StringToken)name_or_string;
                multiline = stringToken.isMultiLine();
                singlequoted = stringToken.isSingleQuoted();
            }
            sometext_parsed = true;
            isa_name = isa(name_or_string, NameToken.class);
            if (text_before) {
                if (isa_name || wasa_name) {
                    sb.append(' ');
                }
            }
            sb.append(name_or_string.getValue());
            wasa_name = isa_name;
            text_before = true;
        }
        // mettre à jour le contexte
        if (context != null) {
            if (context.length > 0) context[ctx_wasa_name] = wasa_name;
            if (context.length > 1) context[ctx_text_before] = text_before;
            if (context.length > 2) context[ctx_multiline_string] = multiline;
            if (context.length > 3) context[ctx_singlequoted_string] = singlequoted;
        }
        // retourner le texte parsé
        return sometext_parsed? sb.toString(): null;
    }
}