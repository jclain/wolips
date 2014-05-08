/* SAXParserFactoryBase.java
 * Created on 18 févr. 2005
 */
package run.univ.xml.base;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import run.univ.xml.SAXProperties;
import run.univ.xml.SAXFeatures;

/**
 * @author jclain
 */
public abstract class SAXParserFactoryBase extends SAXParserFactory {
    protected abstract SAXFeatures getSupportedFeatures();

    protected abstract SAXProperties getSupportedProperties();

    protected abstract SAXParser getNewSAXParser(SAXFeatures supportedFeatures,
            SAXProperties supportedProperties, Map<String, Boolean> features)
            throws ParserConfigurationException, SAXException;

    protected Map<String, Boolean> features;

    /* @see javax.xml.parsers.SAXParserFactory#getFeature(java.lang.String) */
    public boolean getFeature(String name) throws ParserConfigurationException,
            SAXNotRecognizedException, SAXNotSupportedException {
        return getSupportedFeatures().getFeature(features, name);
    }

    /* @see javax.xml.parsers.SAXParserFactory#setFeature(java.lang.String, boolean) */
    public void setFeature(String name, boolean value) throws ParserConfigurationException,
            SAXNotRecognizedException, SAXNotSupportedException {
        getSupportedFeatures().setFeature(features, name, value);
        if (name != null) {
            if (name.equals(SAXFeatures.NAMESPACES)) setNamespaceAware(value);
            if (name.equals(SAXFeatures.VALIDATION)) setValidating(value);
        }
    }

    /* @see javax.xml.parsers.SAXParserFactory#newSAXParser() */
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        setFeature(SAXFeatures.NAMESPACES, isNamespaceAware());
        setFeature(SAXFeatures.VALIDATION, isValidating());
        return getNewSAXParser(getSupportedFeatures(), getSupportedProperties(), features);
    }
}