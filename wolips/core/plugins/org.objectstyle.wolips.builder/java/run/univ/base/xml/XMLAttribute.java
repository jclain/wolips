/* XMLAttribute.java
 * Created on 27 jan. 2011
 */
package run.univ.base.xml;

import run.univ.base.Str;

/**
 * Un attribut d'un élément XML.
 * 
 * @author jclain
 */
public class XMLAttribute {
    public static final String checkName(String name, String value) {
        if (name == null) throw new NullPointerException("name should not be null");
        if (value == null) value = name;
        return value;
    }

    public XMLAttribute(String name, String value) {
        value = checkName(name, value);
        this.name = name;
        this.value = value;
    }

    private String name;

    /** Obtenir le nom de l'attribut, e.g "href" ou "xml:lang". */
    public String getName() {
        return name;
    }

    private String value;

    /** Obtenir la valeur de l'attribut. */
    public String getValue() {
        return value;
    }

    public boolean equals(String name) {
        return Str.equals(this.name, name);
    }

    public boolean equals(String name, String value) {
        return Str.equals(this.name, name) && Str.equals(this.value, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XMLAttribute) {
            XMLAttribute other = (XMLAttribute)obj;
            return equals(other.getName(), other.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Str.hashCode(name);
    }
}
