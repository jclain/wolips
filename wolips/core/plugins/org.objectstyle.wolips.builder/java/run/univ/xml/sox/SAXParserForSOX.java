/* SAXParserForSOX.java
 * Created on 23 déc. 2004
 */
package run.univ.xml.sox;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import run.univ.xml.SAXProperties;
import run.univ.xml.SAXFeatures;
import run.univ.xml.base.SAXParserBase;

/**
 * @author jclain
 */
public class SAXParserForSOX extends SAXParserBase {
    public SAXParserForSOX(SAXFeatures supportedFeatures, SAXProperties supportedProperties,
            Map<String, Boolean> features, Map<String, Object> properties) {
        if (supportedFeatures == null) supportedFeatures = SOXFeatures.getInstance();
        if (supportedProperties == null) supportedProperties = SOXProperties.getInstance();
        if (features == null) features = SOXFeatures.initFeatures();
        else features = new HashMap<String, Boolean>(features);
        if (properties == null) properties = SOXProperties.initProperties();
        else properties = new HashMap<String, Object>(properties);

        this.supportedFeatures = supportedFeatures;
        this.supportedProperties = supportedProperties;
        setFeatures(features);
        setProperties(properties);
    }

    public SAXParserForSOX() {
        this(null, null, null, null);
    }

    private SAXFeatures supportedFeatures;

    protected SAXFeatures getSupportedFeatures() {
        return supportedFeatures;
    }

    private SAXProperties supportedProperties;

    protected SAXProperties getSupportedProperties() {
        return supportedProperties;
    }

    private XMLReader xmlReader;

    /* @see javax.xml.parsers.SAXParser#getXMLReader() */
    public XMLReader getXMLReader() throws SAXException {
        if (xmlReader == null) {
            xmlReader = new XMLReaderForSOX(supportedFeatures, supportedProperties, features,
                    properties);
        }
        return xmlReader;
    }
}