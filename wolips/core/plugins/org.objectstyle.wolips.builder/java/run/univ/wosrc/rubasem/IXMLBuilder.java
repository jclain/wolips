/* IXMLBuilder.java
 * Created on 13 avr. 2010
 */
package run.univ.wosrc.rubasem;

import java.io.IOException;
import java.io.Reader;


/**
 * Un objet qui peut construire un flux XML sur une destination.
 * <p>
 * Le flux est construit élément par élément. Aucun controle n'est effectué sur la validité du flux
 * généré. De plus, les exceptions sur le flux sont sauvegardées, et sont disponibles à la demande.
 * </p>
 * 
 * @author jclain
 */
public interface IXMLBuilder {
    // ------------------------------------------------------------------------
    // Configuration

    /**
     * Obtenir le caractère de fin de ligne utilisé dans le flux de sortie. Par défaut, il s'agit de
     * {@link LineSep#LF}.
     */
    public String getNl();

    public void setNl(String nl);

    /** Tester si cet objet supporte l'indentation du flux XML. */
    public boolean canIndent();

    /** Tester si le flux en sortie doit être indenté. */
    public boolean isIndent();

    public void setIndent(boolean indent);

    /** Obtenir l'encoding dans lequel est écrit le flux XML. */
    public String getEncoding();

    public void setEncoding(String encoding);

    // ------------------------------------------------------------------------
    // Contrôle

    /** Forcer l'écriture des éléments XML éventuellement en attente dans le flux sous-jaçent. */
    public void flush();

    /** Fermer le flux. Après l'appel de cette méthode, cet objet n'est plus utilisable. */
    public void close();

    /**
     * Si une exception s'est produite à cause du flux sous-jaçent dans la dernière méthode appelée,
     * la lancer.
     */
    public void throwSavedException() throws IOException;

    // ------------------------------------------------------------------------
    // Génération

    /** Ajouter un saut de ligne. */
    public void nl();

    /** Ajouter une déclaration &lt?xml...?&gt;. */
    public void xmldecl(XMLAttributes attrs);

    /** Ajouter une déclaration &lt?xml-stylesheet...?&gt;. */
    public void xmlstylesheet(XMLAttributes attrs);

    /**
     * Ajouter une déclaration &lt;!DOCTYPE rootElement PUBLIC publicId SYSTEM systemId&gt;.
     * <p>
     * Si close==<code>false</code>, la déclaration n'est pas fermée, et peut être complétée avec la
     * méthode {@link #inlinedtd(Reader, boolean)}.
     * </p>
     */
    public void doctype(String rootElement, String publicId, String systemId, boolean close);

    /**
     * Inclure une déclaration de DTD dans une balise &lt;!DOCTYPE..., qui ne doit pas avoir été
     * fermée.
     * <p>
     * XXX non implémenté pour le moment
     * </p>
     */
    public void inlinedtd(Reader dtdreader, boolean close);

    /**
     * Ajouter une déclaration &lt;!DOCTYPE ...&gt; littérale.
     * 
     * @param doctype soit une valeur &lt;!DOCTYPE ...&gt; littérale, soit un alias de doctype
     *        défini dans {@link Doctypes}.
     */
    public void doctype(String doctype);

    /** Ajouter un tag ouvrant. */
    public void start(String name, XMLAttributes attrs);

    /**
     * Ajouter des attributs. Les valeurs des attributs "xmlns" et "xmlns:prefix" peuvent être un
     * alias défini dans {@link Namespaces}: elles sont corrigées si nécessaire.
     */
    public void addattrs(XMLAttributes attrs);

    /** Ajouter un attribut. */
    public void addattr(XMLAttribute attr);

    /** Ajouter un tag fermant. */
    public void end(String name);

    /**
     * Ajouter un texte non quoté.
     * <p>
     * Attention à ne pas générer du XML invalide!
     * </p>
     */
    public void rawtext(String text);

    /** Ajouter un texte, qui est quoté pour ne pas risquer de rendre le flux invalide. */
    public void text(String text);

    /**
     * Ajouter un texte dans une section cdata, e.g. &lt;![CDATA[text]]&gt;. Si le texte contient
     * des valeurs "]]&gt;", elles sont quotées pour ne pas rendre le flux invalide.
     */
    public void cdata(String text);

    /**
     * Ajouter un commentaire. Si le commentaire contient des valeurs "--", elles sont remplacées
     * par "- -" pour ne pas rendre le commentaire invalide.
     */
    public void comment(String comment);

    // ------------------------------------------------------------------------
    // Méthodes de convenance

    /**
     * Equivalent à <code>xmldecl(attrs)</code> avec attrs contenant l'attribut encoding="ENC" (où
     * "ENC" est l'encoding retourné par {@link #getEncoding()}).
     */
    public void xmldecl();

    /** Equivalent à <code>xmlstylesheet(attrs)</code> avec attrs contenant l'attribut href="href". */
    public void xmlstylesheet(String href);

    /** Equivalent à <code>start(name, null)</code>. */
    public void start(String name);

    /**
     * Ajouter un attribut. Si l'attribut est une déclaration de namespace, la valeur peut être un
     * alias défini dans {@link Namespaces}.
     */
    public void addattr(String name, String value);

    /**
     * Spécifier le namespace par défaut. Equivalent à <code>addattr("xmlns", uri)</code>, uri
     * pouvant être un alias défini dans {@link Namespaces}.
     */
    public void setNamespace(String uri);

    /**
     * Spécifier le namespace par défaut. Equivalent <code>addattr("xmlns:" + prefix, uri)</code>,
     * uri pouvant être un alias défini dans {@link Namespaces}.
     */
    public void addNamespace(String prefix, String uri);

    /** Equivalent à <code>start(name, attrs); text(text); end(name, null);</code> */
    public void add(String name, XMLAttributes attrs, String text);

    /** Equivalent à <code>start(name, null); text(text); end(name, null);</code> */
    public void add(String name, String text);

    /** Equivalent à <code>start(name, attrs); cdata(text); end(name, null);</code> */
    public void addcdata(String name, XMLAttributes attrs, String text);

    /** Equivalent à <code>start(name, null); cdata(text); end(name, null);</code> */
    public void addcdata(String name, String text);
}
