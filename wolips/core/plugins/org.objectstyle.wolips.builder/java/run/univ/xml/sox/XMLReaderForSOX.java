/* XMLReaderForSOX.java
 * Created on 23 déc. 2004
 */
package run.univ.xml.sox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

import run.univ.xml.SAXFeatures;
import run.univ.xml.SAXProperties;
import run.univ.xml.base.XMLReaderBase;

/**
 * @author jclain
 */
public class XMLReaderForSOX extends XMLReaderBase {
    public static final SAXParseException SAXParseException(String message, Locator locator,
            Exception cause) {
        if (message == null && cause != null) message = cause.getMessage();
        SAXParseException spe = new SAXParseException(message, locator, cause);
        spe.initCause(cause);
        return spe;
    }

    public XMLReaderForSOX(SAXFeatures supportedFeatures, SAXProperties supportedProperties,
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

    public XMLReaderForSOX() {
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

    protected static class SOXParserForXMLReader extends SOXParser {
        public SOXParserForXMLReader(XMLReaderBase xmlReader, InputSource input) throws IOException {
            // this.xmlReader = xmlReader;
            contentHandler = xmlReader.getContentHandler();
            errorHandler = xmlReader.getErrorHandler();
            lexicalHandler = xmlReader.getLexicalHandler();

            setInput(input);
        }

        // private XMLReaderBase xmlReader;

        private ContentHandler contentHandler;

        private ErrorHandler errorHandler;

        private LexicalHandler lexicalHandler;

        /** les identifiants de l'InputSource qui est parsée. */
        private String publicId, systemId;

        public void setInput(InputSource input) throws IOException {
            publicId = input.getPublicId();
            systemId = input.getSystemId();
            setInput(XMLReaderBase.getReaderForInputSource(input));
        }

        private LocatorImpl locator;

        public void setDocumentLocator() {
            if (contentHandler == null) return;
            locator = new LocatorImpl();
            locator.setPublicId(publicId);
            locator.setSystemId(systemId);
            contentHandler.setDocumentLocator(locator);
        }

        public void setDocumentLocation(int line, int column) {
            if (locator == null) return;
            locator.setLineNumber(line);
            locator.setColumnNumber(column);
        }

        public void warning(String msg, Exception e) throws SAXException {
            if (errorHandler == null) return;
            errorHandler.warning(SAXParseException(msg, locator, e));
        }

        public void error(String msg, Exception e) throws SAXException {
            if (errorHandler == null) return;
            errorHandler.error(SAXParseException(msg, locator, e));
        }

        public void fatalError(String msg, Exception e) throws SAXException {
            if (errorHandler == null) return;
            errorHandler.fatalError(SAXParseException(msg, locator, e));
        }

        public void startDocument() throws SAXException {
            if (contentHandler == null) return;
            contentHandler.startDocument();
        }

        public void endDocument() throws SAXException {
            if (contentHandler == null) return;
            contentHandler.endDocument();
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            if (contentHandler == null) return;
            contentHandler.startPrefixMapping(prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            if (contentHandler == null) return;
            contentHandler.endPrefixMapping(prefix);
        }

        public void startElement(String nsURI, String lName, String qName, Attributes attrs)
                throws SAXException {
            if (contentHandler == null) return;
            contentHandler.startElement(nsURI, lName, qName, attrs);
        }

        public void endElement(String nsURI, String lName, String qName) throws SAXException {
            if (contentHandler == null) return;
            contentHandler.endElement(nsURI, lName, qName);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            if (contentHandler == null) return;
            contentHandler.characters(ch, start, length);
        }

        public void skippedEntity(String name) throws SAXException {
            if (contentHandler == null) return;
            contentHandler.skippedEntity(name);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            if (contentHandler == null) return;
            contentHandler.processingInstruction(target, data);
        }

        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            if (lexicalHandler == null) return;
            lexicalHandler.startDTD(name, publicId, systemId);
        }

        public void endDTD() throws SAXException {
            if (lexicalHandler == null) return;
            lexicalHandler.endDTD();
        }

        public void startEntity(String name) throws SAXException {
            if (lexicalHandler == null) return;
            lexicalHandler.startEntity(name);
        }

        public void endEntity(String name) throws SAXException {
            if (lexicalHandler == null) return;
            lexicalHandler.endEntity(name);
        }

        public void startCDATA() throws SAXException {
            if (lexicalHandler == null) return;
            lexicalHandler.startCDATA();
        }

        public void endCDATA() throws SAXException {
            if (lexicalHandler == null) return;
            lexicalHandler.endCDATA();
        }

        public void comment(char[] ch, int start, int length) throws SAXException {
            if (lexicalHandler == null) return;
            lexicalHandler.comment(ch, start, length);
        }
    };

    public void parse(InputSource input) throws IOException, SAXException {
        SOXParserForXMLReader parser = new SOXParserForXMLReader(this, input);

        parser.setNamespaceAware(SAXFeatures.isNamespaceAware(features));
        parser.setNamespacePrefixes(SAXFeatures.isNamespacePrefixes(features));
        parser.setValidating(SAXFeatures.isValidating(features));
        parser.setMultipleTopElements(SOXFeatures.isMultipleTopElements(features));
        parser.setLexicalEvents(SAXProperties.isLexicalHandler(properties));

        try {
            parser.parse();
        } catch (Exception e) {
            if (!(e instanceof SAXException)) {
                SAXException saxe = new SAXException(e);
                saxe.initCause(e);
                e = saxe;
            }
            throw (SAXException)e;
        }
    }
}