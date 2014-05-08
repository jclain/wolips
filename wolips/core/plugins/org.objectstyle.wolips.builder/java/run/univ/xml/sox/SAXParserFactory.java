/* SAXParserFactory.java
 * Created on 22 déc. 2004
 */
package run.univ.xml.sox;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.xml.sax.SAXException;

import run.univ.xml.SAXProperties;
import run.univ.xml.SAXFeatures;
import run.univ.xml.base.SAXParserFactoryBase;

/**
 * une instance de SAXParserFactory qui parse des fichiers SOX.
 * <p>
 * pour utiliser cette classe, il faut définir la propriété
 * <code>javax.xml.parsers.SAXParserFactory</code> à la valeur
 * <code>rubase.xml.sox.SAXParserFactory</code>.
 * </p>
 * 
 * @author jclain
 */
public class SAXParserFactory extends SAXParserFactoryBase {
    public static SAXParserFactory newInstance() {
        return new SAXParserFactory();
    }

    public SAXParserFactory() {
        features = SOXFeatures.initFeatures();
    }

    protected SAXFeatures getSupportedFeatures() {
        return SOXFeatures.getInstance();
    }

    protected SAXProperties getSupportedProperties() {
        return SOXProperties.getInstance();
    }

    protected SAXParser getNewSAXParser(SAXFeatures supportedFeatures,
            SAXProperties supportedProperties, Map<String, Boolean> features)
            throws ParserConfigurationException, SAXException {
        return new SAXParserForSOX(supportedFeatures, supportedProperties, features, null);
    }
}