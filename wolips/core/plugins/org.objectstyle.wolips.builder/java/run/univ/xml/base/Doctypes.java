/* Doctypes.java
 * Created on 8 oct. 2004
 */
package run.univ.xml.base;

import java.util.ArrayList;
import java.util.HashMap;

import run.univ.Str;
import run.univ.StrBuff;

/**
 * Un registre de DTDs auquels sont associés des aliases.
 * <p>
 * Pour rajouter des aliases à ce registre, il faut étendre la classe de cette manière:
 * </p>
 * 
 * <pre>
 * new Doctypes() {
 *     final void init() {
 *       addDoctype(new Doctype(element, false, publicId, systemId, alias, nsURI, nsPrefix, nsAlias), null);
 *     }
 * }.init();
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

            StringBuffer sb = new StringBuffer();
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

    private static ArrayList doctypes = new ArrayList();

    private static HashMap doctypeByAlias = new HashMap();

    protected static final void addDoctype(Doctype doctype, String[] otherAliases) {
        String alias;

        doctypes.add(doctype);
        if ((alias = doctype.getDoctypeAlias()) != null) {
            doctypeByAlias.put(alias, doctype);
        }
        if (otherAliases != null) {
            for (int i = 0; i < otherAliases.length; i++) {
                doctypeByAlias.put(otherAliases[i], doctype);
            }
        }
        if ((alias = doctype.getNsAlias()) != null) {
            Namespaces.addNamespace(alias, doctype.getNsURI());
        }
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

    public static final String HTML4_FRAME_ALIAS = "html4_frame",
            HTML_4_01_FRAMESET_PUBLIC = "-//W3C//DTD HTML 4.01 Frameset//EN",
            HTML_4_01_FRAMESET_SYSTEM = "http://www.w3.org/TR/html4/frameset.dtd";

    public static final String HTML5_ALIAS = "html5";

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
     * transformer un alias avant de l'utiliser comme clé de dtds.
     * <p>
     * pour le moment, enlever éventuellement '&amp;' au début et ';' à la fin.
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
        return doctypeByAlias.containsKey(cookAlias(alias));
    }

    /**
     * Obtenir la déclaration &lt;!DOCTYPE correspondant à l'alias, ou null si cet alias est
     * invalide.
     */
    public static final String getDecl(String alias) {
        Doctype doctype = (Doctype)doctypeByAlias.get(cookAlias(alias));
        return doctype != null? doctype.getDoctypeDeclaration(): null;
    }

    public static final Doctype getDoctype(String alias) {
        return (Doctype)doctypeByAlias.get(cookAlias(alias));
    }

    public static final boolean isHtmlDTD(String alias) {
        Doctype doctype = (Doctype)doctypeByAlias.get(cookAlias(alias));
        return doctype != null? doctype.isHtml(): false;
    }

    // gestion des copies locales des DTDs
    private static String defaultDtdsBasedir() {
        // calcul du chemin de base vers les dtds locaux
        StringBuffer sb = new StringBuffer(Doctypes.class.getName());
        // remonter deux niveaux
        sb.setLength(sb.lastIndexOf("."));
        sb.setLength(sb.lastIndexOf("."));
        // ajouter dtds/
        sb.insert(0, '.');
        sb.append(".dtds.");
        StrBuff.replaceAll(sb, ".", "/");
        return sb.toString();
    }

    protected static String dtds_basedir = defaultDtdsBasedir();

    protected static final String html4_baseurl = "http://www.w3.org/TR/html4/";

    protected static final String html4_basedir = dtds_basedir + "html4/";

    protected static final String xhtml10_baseurl = "http://www.w3.org/TR/xhtml1/DTD/";

    protected static final String xhtml10_basedir = dtds_basedir + "xhtml10/";

    protected static final String xhtml11_baseurl = "http://www.w3.org/TR/xhtml11/DTD/";

    protected static final String xhtml11_basedir = dtds_basedir + "xhtml11/";

    protected static HashMap systemIdForPublicId = new HashMap();
    static {
        systemIdForPublicId.put(HTML_4_01_STRICT_PUBLIC, HTML_4_01_STRICT_SYSTEM);
        systemIdForPublicId.put(HTML_4_01_TRANSITIONAL_PUBLIC, HTML_4_01_TRANSITIONAL_SYSTEM);
        systemIdForPublicId.put(HTML_4_01_FRAMESET_PUBLIC, HTML_4_01_FRAMESET_SYSTEM);
        systemIdForPublicId.put(XHTML_1_0_STRICT_PUBLIC, XHTML_1_0_STRICT_SYSTEM);
        systemIdForPublicId.put(XHTML_1_0_TRANSITIONAL_PUBLIC, XHTML_1_0_TRANSITIONAL_SYSTEM);
        systemIdForPublicId.put(XHTML_1_0_FRAMESET_PUBLIC, XHTML_1_0_FRAMESET_SYSTEM);
        systemIdForPublicId.put(XHTML_1_1_PUBLIC, XHTML_1_1_SYSTEM);
    }

    public static final String getSystemIdForPublicId(String publicId) {
        return (String)systemIdForPublicId.get(publicId);
    }

    protected static HashMap basedirForBaseUrl = new HashMap();
    static {
        basedirForBaseUrl.put(html4_baseurl, html4_basedir);
        basedirForBaseUrl.put(xhtml10_baseurl, xhtml10_basedir);
        basedirForBaseUrl.put(xhtml11_baseurl, xhtml11_basedir);
    }

    public static final String getResPathForSystemId(String systemId) {
        if (systemId == null) return null;

        int pos = systemId.lastIndexOf('/') + 1;
        String baseUrl = systemId.substring(0, pos);
        String basedir = (String)basedirForBaseUrl.get(baseUrl);
        if (basedir == null) return null;

        String basename = systemId.substring(pos);
        return basedir + basename;
    }
}