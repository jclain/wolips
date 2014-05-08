/* XMLElement.java
 * Created on 16 nov. 2004
 */
package run.univ.xml.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Un élément XML simple.
 * <p>
 * Cet élément XML est de la forme:
 * </p>
 * 
 * <pre>&lt;name attrs...&gt;text&lt;children...&gt;&lt;/name&gt;</pre>
 * <p>
 * avec text et children étant facultatifs.
 * </p>
 * 
 * @author jclain
 */
public interface XMLElement {
    /** obtenir le nom de cet élément XML. */
    public String getName();

    public void setName(String name);

    /** obtenir les attributs de cet élément XML. */
    public XMLAttributes getAttrs();

    public void setAttrs(XMLAttributes attrs);

    /** retourner true si cet élément a un texte associé. */
    public boolean hasText();

    /** obtenir le texte associé à cet élément, ou null s'il n'y a pas de texte associé. */
    public String getText();

    public void setText(String text);

    /** retourner true si cet élément a des fils. */
    public boolean hasChildrenElements();

    /** retourner un itérateur vers les éléments fils, ou null s'il n'a pas de fils. */
    public Iterator childElementIterator();

    public void addChildElement(XMLElement child);

    /**
     * Méthode de convenance pour écrire cet élément XML avec un XMLBuilder.
     * <p>
     * l'élément est fermé si close==true. Sinon, on peut continuer à lui adjoindre du contenu.
     * </p>
     * <p>
     * Si nl==false, on ne rajoute pas de saut de ligne à la fin.
     * </p>
     */
    public void write(XMLBuilder xmlBuilder, boolean close, boolean nl) throws IOException;

    /**
     * équivalent à
     * 
     * <pre>
     * write(xmlBuilder, true, true);
     * </pre>
     */
    public void write(XMLBuilder xmlBuilder) throws IOException;

    public static class Implementation implements XMLElement {
        public Implementation() {
        }

        public Implementation(String name) {
            this.name = name;
        }

        public Implementation(String name, XMLAttributes attrs) {
            this.name = name;
            this.attrs = attrs;
        }

        public Implementation(String name, String text) {
            this.name = name;
            this.text = text;
        }

        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        protected XMLAttributes attrs;

        public XMLAttributes getAttrs() {
            return attrs;
        }

        public void setAttrs(XMLAttributes attrs) {
            this.attrs = attrs;
        }

        protected String text;

        public boolean hasText() {
            return text != null;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        protected ArrayList children;

        public boolean hasChildrenElements() {
            return childElementIterator().hasNext();
        }

        public Iterator childElementIterator() {
            return children != null? children.iterator(): EmptyIterator.getInstance();
        }

        public void addChildElement(XMLElement child) {
            if (children == null) children = new ArrayList();
            children.add(child);
        }

        public void write(XMLBuilder xmlBuilder, boolean close, boolean nl) throws IOException {
            xmlBuilder.start(getName(), getAttrs());
            if (hasText()) xmlBuilder.text(getText());
            if (hasChildrenElements()) {
                xmlBuilder.nl();
                for (Iterator it = childElementIterator(); it.hasNext();) {
                    XMLElement element = (XMLElement)it.next();
                    element.write(xmlBuilder, true, nl || it.hasNext());
                }
            }
            if (close) xmlBuilder.end(getName());
            if (nl) xmlBuilder.nl();
        }

        public void write(XMLBuilder xmlBuilder) throws IOException {
            write(xmlBuilder, true, true);
        }
    }
}