/* Namespaces.java
 * Created on 8 oct. 2004
 */
package run.univ.xml.base;

import java.util.ArrayList;
import java.util.HashMap;

import run.univ.Str;

/**
 * Des outils pour gérer les namespaces XML, dont un registre de URI de namespaces XML auquels
 * peuvent être associés des aliases.
 * <p>
 * Pour rajouter des aliases à ce registre, il faut étendre la classe de cette manière:
 * </p>
 * 
 * <pre>
 * new Namespaces() {
 *     final void init() {
 *         addURI(alias, uri); // dans ce cas alias==prefix
 *         addURI(prefix, uri, alias);
 *     }
 * }.init();
 * </pre>
 * 
 * @author jclain
 */
public class Namespaces {
    public static class Namespace {
        public Namespace(String alias, String uri, String prefix) {
            this.alias = alias;
            this.uri = uri;
            this.prefix = prefix;
        }

        public Namespace(String alias, String uri) {
            this(alias, uri, alias);
        }

        /** informations sur le namespace. */
        protected String alias, uri, prefix;

        public String getAlias() {
            return alias;
        }

        public String getURI() {
            return uri;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    private static ArrayList namespaces = new ArrayList();

    private static HashMap nsByAlias = new HashMap();

    protected static final void addNamespace(Namespace namespace) {
        String alias;

        synchronized (namespaces) {
            synchronized (nsByAlias) {
                namespaces.add(namespace);
                if ((alias = namespace.getAlias()) != null) {
                    nsByAlias.put(alias, namespace);
                }
            }
        }
    }

    /** Ajouter un namespace à la liste des namespaces connus. */
    protected static final void addNamespace(String alias, String uri) {
        addNamespace(new Namespace(alias, uri));
    }

    /** Ajouter un namespace à la liste des namespaces connus. */
    protected static final void addNamespace(String alias, String uri, String prefix) {
        addNamespace(new Namespace(alias, uri, prefix));
    }

    public static final String HTML4 = "http://www.w3.org/TR/REC-html40";

    public static final String XML = "http://www.w3.org/XML/1998/namespace";

    public static final String XHTML = "http://www.w3.org/1999/xhtml";

    public static final String XSLT = "http://www.w3.org/1999/XSL/Transform";

    public static final String XSLFO = "http://www.w3.org/1999/XSL/Format";

    public static final String XPATH_DATATYPES = "http://www.w3.org/2004/07/xpath-datatypes";

    public static final String XPATH_FUNCTIONS = "http://www.w3.org/2004/07/xpath-functions";

    public static final String ERRORS = "http://www.w3.org/2004/07/xqt-errors";

    public static final String XSCHEMA = "http://www.w3.org/2001/XMLSchema";

    public static final String XSCHEMA_DATATYPES = "http://www.w3.org/2001/XMLSchema-datatypes";

    public static final String XSCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

    public static final String XLINK = "http://www.w3.org/1999/xlink";

    static {
        addNamespace("xsl", XSLT);
        addNamespace("fo", XSLFO);
        addNamespace("xs", XSCHEMA);
        addNamespace("xsi", XSCHEMA_INSTANCE);
        addNamespace("xdt", XPATH_DATATYPES);
        addNamespace("fn", XPATH_FUNCTIONS);
        addNamespace("err", ERRORS);
        // les déclarations suivantes sont faites dans la class Doctypes:
        // addNamespace("html", HTML4);
        // addNamespace("xhtml", XHTML);
    }

    /**
     * transformer un alias avant de l'utiliser comme clé de nsByAlias.
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
        return nsByAlias.containsKey(cookAlias(alias));
    }

    /** Obtenir l'objet Namespace correspondant à l'alias, ou null si cet alias est invalide. */
    public static final Namespace getNamespace(String alias) {
        return (Namespace)nsByAlias.get(cookAlias(alias));
    }

    /** Obtenir l'URI correspondant à l'alias, ou null si cet alias est invalide. */
    public static final String getURI(String alias) {
        Namespace ns = getNamespace(alias);
        return ns != null? ns.getURI(): null;
    }

    /** est-ce un attribut qui représente une déclaration de namespace? */
    public static final boolean isAttrNamespaceDecl(String attrname) {
        return attrname != null && (attrname.equals("xmlns") || attrname.startsWith("xmlns:"));
    }

    /** Créer un nom d'attribut qui soit une déclaration de namespace à partir du nom du préfixe. */
    public static final String getAttrNamespaceDecl(String prefix) {
        return Str.isempty(prefix)? "xmlns": "xmlns:" + prefix;
    }

    /**
     * Si l'attribut est une déclaration de namespace (isAttrNamespaceDecl(attrname) doit être
     * true), obtenir le nom du préfixe.
     */
    public static final String getAttrNamespacePrefix(String attrname) {
        if (attrname == null) return null;
        if (attrname.equals("xmlns")) return "";
        return attrname.substring(6);
    }

    /**
     * Obtenir le nom du préfixe dans un nom d'élément, comme "wo" dans "wo:string". Retourner une
     * chaine vide si le nom n'a pas de préfixe.
     */
    public static final String getNamespacePrefix(String name) {
        if (name == null) return null;
        int pos;
        if ((pos = name.indexOf(':')) == -1) return "";
        return Str.substr(name, 0, pos + 1);
    }
}