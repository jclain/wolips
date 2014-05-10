/* Doctypes.java
 * Created on 12 avr. 2010
 */
package run.univ.wosrc.rubasem;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Un registre de DTDs auquels sont associés des aliases.
 * <p>
 * Pour rajouter des aliases à ce registre, il faut étendre la classe de cette manière:
 * </p>
 * 
 * <pre>
 * new Doctypes() {{
 *   addDoctype(new Doctype(element, false, publicId, systemId, alias, nsURI, nsPrefix, nsAlias), null);
 * }};
 * </pre>
 * 
 * @author jclain
 */
public class Doctypes {
    public static class Doctype {
        public Doctype(String doctypeAlias, String elementName, boolean html, String publicId,
                String systemId, String nsPrefix, String nsURI, String nsAlias) {
            if (doctypeAlias == null) doctypeAlias = elementName;
            this.doctypeAlias = doctypeAlias;
            this.elementName = elementName;
            this.html = html;
            this.publicId = publicId;
            this.systemId = systemId;

            if (nsAlias == null) nsAlias = nsPrefix;
            this.nsPrefix = nsPrefix;
            this.nsURI = nsURI;
            this.nsAlias = nsAlias;

            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE ");
            sb.append(elementName);
            if (publicId != null) {
                sb.append(" PUBLIC ");
                sb.append(Str.htmlattr_quote(publicId));
                if (systemId != null) {
                    sb.append(" ");
                    sb.append(Str.htmlattr_quote(systemId));
                }
            } else if (systemId != null) {
                sb.append(" SYSTEM ");
                sb.append(Str.htmlattr_quote(systemId));
            } else if (Str.equals(HTML5_ALIAS, doctypeAlias) && Str.equals(elementName, "html")
                    && html) {
                // cas particulier de HTML5
            } else {
                throw new IllegalArgumentException("il faut une valeur publicId ou systemId");
            }
            sb.append(">");
            doctypeDeclaration = sb.toString();
        }

        public Doctype(String doctypeAlias, String elementName, boolean html, String publicId,
                String systemId) {
            this(doctypeAlias, elementName, html, publicId, systemId, null, null, null);
        }

        /** informations sur la déclaration &lt;!DOCTYPE */
        protected String elementName, publicId, systemId, doctypeAlias;

        /** informations sur la déclaration &lt;!DOCTYPE */
        protected boolean html;

        public String getElementName() {
            return elementName;
        }

        public String getPublicId() {
            return publicId;
        }

        public String getSystemId() {
            return systemId;
        }

        public String getDoctypeAlias() {
            return doctypeAlias;
        }

        public boolean isHtml() {
            return html;
        }

        /** informations sur le namespace associé. */
        protected String nsPrefix, nsURI, nsAlias;

        public String getNsPrefix() {
            return nsPrefix;
        }

        public String getNsURI() {
            return nsURI;
        }

        public String getNsAlias() {
            return nsAlias;
        }

        /** déclaration &lt;!DOCTYPE associée. */
        protected String doctypeDeclaration;

        public String getDoctypeDeclaration() {
            return doctypeDeclaration;
        }
    }

    private static ArrayList<Doctype> doctypes = new ArrayList<Doctype>();

    private static HashMap<String, Doctype> alias2doctypes = new HashMap<String, Doctype>();

    protected static final void addDoctype(Doctype doctype, String[] otherAliases) {
        doctypes.add(doctype);
        String alias = doctype.getDoctypeAlias();
        if (alias != null) alias2doctypes.put(alias, doctype);
        if (otherAliases != null) {
            for (int i = 0; i < otherAliases.length; i++) {
                alias2doctypes.put(otherAliases[i], doctype);
            }
        }
        alias = doctype.getNsAlias();
        if (alias != null) Namespaces.addNamespace(alias, doctype.getNsURI());
    }

    public static final String HTML2_ALIAS = "html2",
            HTML_2_0_PUBLIC = "-//IETF//DTD HTML 2.0//EN";

    public static final String HTML3_ALIAS = "html3",
            HTML_3_2_PUBLIC = "-//W3C//DTD HTML 3.2 Final//EN";

    public static final String HTML4_STRICT_ALIAS = "html4_strict",
            HTML_4_01_STRICT_PUBLIC = "-//W3C//DTD HTML 4.01//EN",
            HTML_4_01_STRICT_SYSTEM = "http://www.w3.org/TR/html4/strict.dtd";

    public static final String HTML4_ALIAS = "html4", HTML4_TRANS_ALIAS = "html4_trans",
            HTML_4_01_TRANSITIONAL_PUBLIC = "-//W3C//DTD HTML 4.01 Transitional//EN",
            HTML_4_01_TRANSITIONAL_SYSTEM = "http://www.w3.org/TR/html4/loose.dtd";

    public static final String HTML5_ALIAS = "html5";

    public static final String HTML4_FRAME_ALIAS = "html4_frame",
            HTML_4_01_FRAMESET_PUBLIC = "-//W3C//DTD HTML 4.01 Frameset//EN",
            HTML_4_01_FRAMESET_SYSTEM = "http://www.w3.org/TR/html4/frameset.dtd";

    public static final String XHTML_ALIAS = "xhtml", XHTML10_ALIAS = "xhtml10",
            XHTML10_STRICT_ALIAS = "xhtml10_strict",
            XHTML_1_0_STRICT_PUBLIC = "-//W3C//DTD XHTML 1.0 Strict//EN",
            XHTML_1_0_STRICT_SYSTEM = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";

    public static final String XHTML10_TRANS_ALIAS = "xhtml10_trans",
            XHTML_1_0_TRANSITIONAL_PUBLIC = "-//W3C//DTD XHTML 1.0 Transitional//EN",
            XHTML_1_0_TRANSITIONAL_SYSTEM = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";

    public static final String XHTML10_FRAME_ALIAS = "xhtml10_frame",
            XHTML_1_0_FRAMESET_PUBLIC = "-//W3C//DTD XHTML 1.0 Frameset//EN",
            XHTML_1_0_FRAMESET_SYSTEM = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd";

    public static final String XHTML11_ALIAS = "xhtml11",
            XHTML_1_1_PUBLIC = "-//W3C//DTD XHTML 1.1//EN",
            XHTML_1_1_SYSTEM = "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd";

    static {
        // HTML 2.0
        addDoctype(new Doctype(HTML2_ALIAS, "HTML", true, HTML_2_0_PUBLIC, null), null);

        // HTML 3.2
        addDoctype(new Doctype(HTML3_ALIAS, "HTML", true, HTML_3_2_PUBLIC, null), null);

        // HTML 4.01
        addDoctype(new Doctype(HTML4_STRICT_ALIAS, "HTML", true, HTML_4_01_STRICT_PUBLIC,
                HTML_4_01_STRICT_SYSTEM), null);
        addDoctype(
                new Doctype(HTML4_TRANS_ALIAS, "HTML", true, HTML_4_01_TRANSITIONAL_PUBLIC,
                        HTML_4_01_TRANSITIONAL_SYSTEM, "", Namespaces.HTML4, "html"),
                new String[] {HTML4_ALIAS});
        addDoctype(new Doctype(HTML4_FRAME_ALIAS, "HTML", true, HTML_4_01_FRAMESET_PUBLIC,
                HTML_4_01_FRAMESET_SYSTEM), null);

        // HTML 5
        addDoctype(
                new Doctype(HTML5_ALIAS, "html", true, null, null, "", Namespaces.XHTML, null),
                null);

        // XHTML 1.0
        addDoctype(new Doctype(XHTML10_STRICT_ALIAS, "html", true, XHTML_1_0_STRICT_PUBLIC,
                XHTML_1_0_STRICT_SYSTEM, "", Namespaces.XHTML, "xhtml"), new String[] {
                XHTML_ALIAS,
                XHTML10_ALIAS});
        addDoctype(new Doctype(XHTML10_TRANS_ALIAS, "html", true, XHTML_1_0_TRANSITIONAL_PUBLIC,
                XHTML_1_0_TRANSITIONAL_SYSTEM, "", Namespaces.XHTML, null), null);
        addDoctype(new Doctype(XHTML10_FRAME_ALIAS, "html", true, XHTML_1_0_FRAMESET_PUBLIC,
                XHTML_1_0_FRAMESET_SYSTEM, "", Namespaces.XHTML, null), null);

        // XHTML 1.1
        addDoctype(new Doctype(XHTML11_ALIAS, "html", true, XHTML_1_1_PUBLIC, XHTML_1_1_SYSTEM, "",
                Namespaces.XHTML, null), null);
    }

    /**
     * Transformer un alias avant de l'utiliser comme clé de alias2doctypes.
     * <p>
     * Enlever éventuellement '&amp;' au début et ';' à la fin.
     * </p>
     */
    protected static String cookAlias(String alias) {
        if (alias == null) return null;
        if (alias.startsWith("&") && alias.endsWith(";")) {
            alias = alias.substring(1, alias.length() - 1);
        }
        return alias;
    }

    /** Tester la validité d'un alias. */
    public static final boolean isValid(String alias) {
        return alias2doctypes.containsKey(cookAlias(alias));
    }

    /**
     * Obtenir la déclaration &lt;!DOCTYPE correspondant à l'alias, ou <code>null</code> si cet
     * alias est invalide.
     */
    public static final String getDecl(String alias) {
        Doctype doctype = (Doctype)alias2doctypes.get(cookAlias(alias));
        return doctype != null? doctype.getDoctypeDeclaration(): null;
    }

    /**
     * Obtenir l'objet Doctype correspondant à l'alias, ou <code>null</code> si l'alias est
     * invalide.
     */
    public static final Doctype getDoctype(String alias) {
        return alias2doctypes.get(cookAlias(alias));
    }

    public static final boolean isHtmlDTD(String alias) {
        Doctype doctype = (Doctype)alias2doctypes.get(cookAlias(alias));
        return doctype != null? doctype.isHtml(): false;
    }

    // gestion des copies locales des DTDs
    private static String defaultResourcesBasedir() {
        // calcul du chemin de base vers les dtds locaux
        StringBuilder sb = new StringBuilder(Doctypes.class.getName());
        // remonter deux niveaux: la classe, puis le package
        sb.setLength(sb.lastIndexOf("."));
        sb.setLength(sb.lastIndexOf("."));
        // ajouter resources/
        sb.insert(0, '.');
        sb.append(".resources.");
        SB.replaceAll(sb, ".", "/");
        return sb.toString();
    }

    protected static final String RESOURCES_BASEDIR = defaultResourcesBasedir();

    protected static final String HTML4_BASEURL = "http://www.w3.org/TR/html4/";

    protected static final String HTML4_BASEDIR = RESOURCES_BASEDIR + "html4/";

    protected static final String XHTML10_BASEURL = "http://www.w3.org/TR/xhtml1/DTD/";

    protected static final String XHTML10_BASEDIR = RESOURCES_BASEDIR + "xhtml10/";

    protected static final String XHTML11_BASEURL = "http://www.w3.org/TR/xhtml11/DTD/";

    protected static final String XHTML11_BASEDIR = RESOURCES_BASEDIR + "xhtml11/";

    protected static HashMap<String, String> publicId2SystemId = new HashMap<String, String>();
    static {
        publicId2SystemId.put(HTML_4_01_STRICT_PUBLIC, HTML_4_01_STRICT_SYSTEM);
        publicId2SystemId.put(HTML_4_01_TRANSITIONAL_PUBLIC, HTML_4_01_TRANSITIONAL_SYSTEM);
        publicId2SystemId.put(HTML_4_01_FRAMESET_PUBLIC, HTML_4_01_FRAMESET_SYSTEM);
        publicId2SystemId.put(XHTML_1_0_STRICT_PUBLIC, XHTML_1_0_STRICT_SYSTEM);
        publicId2SystemId.put(XHTML_1_0_TRANSITIONAL_PUBLIC, XHTML_1_0_TRANSITIONAL_SYSTEM);
        publicId2SystemId.put(XHTML_1_0_FRAMESET_PUBLIC, XHTML_1_0_FRAMESET_SYSTEM);
        publicId2SystemId.put(XHTML_1_1_PUBLIC, XHTML_1_1_SYSTEM);
    }

    public static final String getSystemIdForPublicId(String publicId) {
        return publicId2SystemId.get(publicId);
    }

    protected static HashMap<String, String> baseUrl2Basedir = new HashMap<String, String>();
    static {
        baseUrl2Basedir.put(HTML4_BASEURL, HTML4_BASEDIR);
        baseUrl2Basedir.put(XHTML10_BASEURL, XHTML10_BASEDIR);
        baseUrl2Basedir.put(XHTML11_BASEURL, XHTML11_BASEDIR);
    }

    public static final String getResPathForSystemId(String systemId) {
        if (systemId == null) return null;

        int pos = systemId.lastIndexOf('/') + 1;
        String baseUrl = systemId.substring(0, pos);
        String basedir = baseUrl2Basedir.get(baseUrl);
        if (basedir == null) return null;

        String basename = systemId.substring(pos);
        return basedir + basename;
    }
}