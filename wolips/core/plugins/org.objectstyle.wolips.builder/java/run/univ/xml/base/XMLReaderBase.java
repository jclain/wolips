/* XMLReaderBase.java
 * Created on 17 févr. 2005
 */
package run.univ.xml.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

import run.univ.io.DecodableStream;
import run.univ.io.DecodedStreamReader;
import run.univ.xml.SAXProperties;
import run.univ.xml.SAXFeatures;

/**
 * @author jclain
 */
public abstract class XMLReaderBase implements XMLReader {
    public static InputStream getInputStreamForInputSource(InputSource input) throws IOException {
        InputStream is = input.getByteStream();
        if (is == null) is = new URL(input.getSystemId()).openStream();
        if (!(is instanceof DecodableStream)) is = new DecodableStream(is);
        return is;
    }

    public static Reader getReaderForInputSource(InputSource input) throws IOException {
        InputStream is = null;
        Reader r = null;
        r = input.getCharacterStream();
        if (r == null) {
            is = input.getByteStream();
            if (is == null) is = new URL(input.getSystemId()).openStream();
            if (!(is instanceof DecodableStream)) is = new DecodableStream(is);
            r = new DecodedStreamReader(is);
        }
        return r;
    }

    protected abstract SAXFeatures getSupportedFeatures();

    protected abstract SAXProperties getSupportedProperties();

    protected Map<String, Boolean> features;

    public Map<String, Boolean> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Boolean> features) {
        this.features = features;
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return getSupportedFeatures().getFeature(features, name);
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        getSupportedFeatures().setFeature(features, name, value);
    }

    protected Map<String, Object> properties;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    protected LexicalHandler lexicalHandler;

    public LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }

    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return getSupportedProperties().getProperty(properties, name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        getSupportedProperties().setProperty(properties, name, value);
        if (SAXProperties.LEXICAL_HANDLER.equals(name)) {
            lexicalHandler = (LexicalHandler)value;
        }
    }

    protected EntityResolver entityResolver;

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    protected DTDHandler dtdHandler;

    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    protected ContentHandler contentHandler;

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    protected ErrorHandler errorHandler;

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void parse(String systemId) throws IOException, SAXException {
        parse(new InputSource(systemId));
    }

    public abstract void parse(InputSource input) throws IOException, SAXException;
}