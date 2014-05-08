/* XMLWriter.java
 * Created on 23 déc. 2005
 */
package run.univ.xml;

import java.io.IOException;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import run.univ.xml.base.XMLAttributes;
import run.univ.xml.base.XMLBuilder;

/**
 * @author jclain
 */
public class XMLWriter implements FlushableHandler {
    private boolean firstElement;

    private Writer output;

    private XMLBuilder xmlb;

    protected XMLBuilder newXMLBuilder(String nl, boolean indent, String encoding) {
        return new XMLBuilder(nl, indent, false, encoding) {
            protected void write(String text) throws IOException {
                output.write(text);
            }
        };
    }

    public XMLWriter(Writer writer, String nl, boolean indent, String encoding) {
        firstElement = false;
        output = writer;
        xmlb = newXMLBuilder(nl, indent, encoding);
    }

    public XMLWriter(Writer writer) {
        this(writer, null, true, null);
    }

    public void flush() throws IOException {
        output.flush();
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
        try {
            xmlb.xmldecl();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        try {
            firstElement = true;
            xmlb.start(qName, XMLAttributes.FromSAXAttributes.copyOf(atts));
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            xmlb.end(qName);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            if (in_cdata) {
                xmlb.cdata(new String(ch, start, length));
            } else {
                xmlb.text(new String(ch, start, length));
            }
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
        try {
            xmlb.rawtext("<?" + target + " " + data + "?>");
            xmlb.nl();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void skippedEntity(String name) throws SAXException {
        try {
            xmlb.rawtext("&" + name + ";");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    protected boolean in_dtd;

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        try {
            xmlb.doctype(name, publicId, systemId, false);
            in_dtd = true;
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void endDTD() throws SAXException {
        try {
            xmlb.rawtext(">");
            xmlb.nl();
        } catch (IOException e) {
            throw new SAXException(e);
        }

        in_dtd = false;
    }

    protected boolean in_cdata;

    public void startCDATA() throws SAXException {
        in_cdata = true;
    }

    public void endCDATA() throws SAXException {
        in_cdata = false;
    }

    public void startEntity(String name) throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (in_dtd) return;

        try {
            xmlb.comment(new String(ch, start, length));
            if (!firstElement) xmlb.nl();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
}