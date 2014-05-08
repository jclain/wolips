/* SAXProperties.java
 * Created on 12 janv. 2009
 */
package run.univ.xml;

import java.util.Map;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author jclain
 */
public class SAXProperties {
    public static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";

    public static final String DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";

    protected Object getPropertyNoCheck(Map<String, Object> properties, String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        return properties.get(name);
    }

    public Object getProperty(Map<String, Object> properties, String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (properties == null || !properties.containsKey(name)) {
            throw new SAXNotRecognizedException("unrecognized property: " + name);
        }
        return getPropertyNoCheck(properties, name);
    }

    protected void setPropertyNoCheck(Map<String, Object> properties, String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(LEXICAL_HANDLER) && !(value instanceof LexicalHandler)) {
            throw new SAXNotSupportedException("value must be an instance of LexicalHandler");
        }
        properties.put(name, value);
    }

    public void setProperty(Map<String, Object> properties, String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null || properties == null || !properties.containsKey(name)) {
            throw new SAXNotRecognizedException("unrecognized property: " + name);
        }
        setPropertyNoCheck(properties, name, value);
    }

    public static boolean isLexicalHandler(Map<String, Object> properties) {
        return properties != null && properties.get(LEXICAL_HANDLER) != null;
    }
}