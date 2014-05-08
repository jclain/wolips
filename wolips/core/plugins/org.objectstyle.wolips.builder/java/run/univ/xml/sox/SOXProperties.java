/* SOXProperties.java
 * Created on 23 déc. 2004
 */
package run.univ.xml.sox;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import run.univ.xml.SAXProperties;

/**
 * @author jclain
 */
class SOXProperties extends SAXProperties {
    static final void initProperties(Map<String, Object> properties) {
        properties.put(LEXICAL_HANDLER, null);
        properties.put(DECLARATION_HANDLER, null);
    }

    static final Map<String, Object> initProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        initProperties(properties);
        return properties;
    }

    private SOXProperties() {
    }

    private static SOXProperties instance;

    static final SOXProperties getInstance() {
        if (instance == null) instance = new SOXProperties();
        return instance;
    }

    public Object getProperty(Map<String, Object> properties, String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (DECLARATION_HANDLER.equals(name)) {
            throw new SAXNotSupportedException("unsupported property: " + name);
        }
        return super.getProperty(properties, name);
    }

    public void setProperty(Map<String, Object> properties, String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (DECLARATION_HANDLER.equals(name)) {
            throw new SAXNotSupportedException("unsupported property: " + name);
        }
        super.setProperty(properties, name, value);
    }
}