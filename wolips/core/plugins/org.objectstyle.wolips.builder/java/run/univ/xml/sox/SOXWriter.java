/* SOXWriter.java
 * Created on 23 déc. 2005
 */
package run.univ.xml.sox;

import java.io.IOException;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import run.univ.LineSep;
import run.univ.Str;
import run.univ.xml.FlushableHandler;

/**
 * @author jclain
 */
public class SOXWriter implements FlushableHandler {
    public static final String DEFAULT_SPACER = "\t";

    private Writer output;

    private int indent;

    private boolean firstElement;

    public SOXWriter(Writer writer, String spacer, String nl, String encoding) {
        indent = 0;
        firstElement = true;
        output = writer;
        setSpacer(spacer);
        setNl(nl);
        setEncoding(encoding);
    }

    public SOXWriter(Writer writer) {
        this(writer, null, null, null);
    }

    public void flush() throws IOException {
        output.flush();
    }

    void write(String text) {
        try {
            output.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String spacer = DEFAULT_SPACER;

    public String getSpacer() {
        return spacer;
    }

    public void setSpacer(String spacer) {
        if (spacer == null) spacer = DEFAULT_SPACER;
        this.spacer = spacer;
    }

    private String nl = LineSep.LF;

    public String getNl() {
        return nl;
    }

    public void setNl(String nl) {
        if (nl == null) nl = LineSep.LF;
        this.nl = nl;
    }

    private String encoding;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        if (encoding == null) encoding = Str.ISO_8859_1;
        this.encoding = encoding;
    }

    void nl() {
        write(nl);
    }

    void indent(String text, boolean nl) {
        if (nl) nl();
        for (int i = 0; i < indent; i++) {
            write(spacer);
        }
        write(text);
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
        write("<?sox version=\"1.0\" encoding=\"" + encoding + "\"?>");
        nl();
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        indent(qName, !firstElement);
        firstElement = false;
        write(">");

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < atts.getLength(); i++) {
            sb.append(' ');
            sb.append(atts.getQName(i));

            String value = atts.getValue(i);
            if (value.indexOf('"') != -1) {
                sb.append("=\"\"\"");
                sb.append(value);
                sb.append("\"\"\"");
            } else {
                sb.append("=\"");
                sb.append(value);
                sb.append("\"");
            }
        }
        write(sb.toString());
        indent++;
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        indent--;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        StringBuffer sb = new StringBuffer();
        sb.append(ch, start, length);
        if (sb.indexOf("\"") != -1) {
            sb.insert(0, "\"\"\"");
            sb.append("\"\"\"");
        }
        indent(sb.toString(), true);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
        write("<?" + target + " " + data + "?>");
        nl();
    }

    public void skippedEntity(String name) throws SAXException {
    }

    protected boolean in_dtd;

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        StringBuffer sb = new StringBuffer("<!DOCTYPE ");
        sb.append(name);
        if (publicId != null) {
            sb.append(" PUBLIC \"");
            sb.append(publicId);
            sb.append('"');
            if (systemId != null) {
                sb.append(" \"");
                sb.append(systemId);
                sb.append('"');
            }
        } else if (systemId != null) {
            sb.append(" SYSTEM \"");
            sb.append(systemId);
            sb.append('"');
        }
        write(sb.toString());

        in_dtd = true;
    }

    public void endDTD() throws SAXException {
        write(">");
        nl();

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

        nl();
        write("# " + new String(ch, start, length));
    }
}