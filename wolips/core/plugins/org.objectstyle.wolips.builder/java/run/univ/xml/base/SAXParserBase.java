/* SAXParserBase.java
 * Created on 17 févr. 2005
 */
package run.univ.xml.base;

import java.util.Map;

import javax.xml.parsers.SAXParser;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderAdapter;

import run.univ.xml.SAXProperties;
import run.univ.xml.SAXFeatures;

/**
 * @author jclain
 */
public abstract class SAXParserBase extends SAXParser {
    protected abstract SAXFeatures getSupportedFeatures();

    protected abstract SAXProperties getSupportedProperties();

    protected Map<String, Boolean> features;

    public Map<String, Boolean> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Boolean> features) {
        this.features = features;
    }

    /* @see javax.xml.parsers.SAXParser#isNamespaceAware() */
    public boolean isNamespaceAware() {
        return SAXFeatures.isNamespaceAware(features);
    }

    /* @see javax.xml.parsers.SAXParser#isValidating() */
    public boolean isValidating() {
        return SAXFeatures.isValidating(features);
    }

    protected Map<String, Object> properties;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /* @see javax.xml.parsers.SAXParser#getProperty(java.lang.String) */
    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return getSupportedProperties().getProperty(properties, name);
    }

    /* @see javax.xml.parsers.SAXParser#setProperty(java.lang.String, java.lang.Object) */
    public void setProperty(String name, Object value) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        getSupportedProperties().setProperty(properties, name, value);
    }

    protected XMLReader xmlReader;

    /* @see javax.xml.parsers.SAXParser#getParser() */
    public org.xml.sax.Parser getParser() throws SAXException {
        return new XMLReaderAdapter(getXMLReader());
    }

    /* @see javax.xml.parsers.SAXParser#getXMLReader() */
    public abstract XMLReader getXMLReader() throws SAXException;
}