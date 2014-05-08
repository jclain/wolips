/* WOSrcGenerator.java
 * Created on 12 janv. 2009
 */
package run.univ.wosrc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import run.univ.Str;
import run.univ.xml.SAXFeatures;
import run.univ.xml.SAXProperties;
import run.univ.xml.base.Namespaces;
import run.univ.xml.sox.SAXParserFactory;
import run.univ.xml.sox.SOXFeatures;

/**
 * @author jclain
 */
public class WOSrcGenerator {
    static {
        new Namespaces() {
            final void init() {
                addNamespace(Defaults.WO_ALIAS, Defaults.WO_NS);
                addNamespace(Defaults.QATTR_ALIAS, Defaults.QATTR_NS, Defaults.QATTR_PREFIX);
            }
        }.init();
    }

    public WOSrcGenerator(InputSource input, Writer htmlOutput, Writer wodOutput, Writer wooOutput,
            String encoding, boolean close) {
        if (encoding == null) encoding = Str.UTF_8;

        this.input = input;
        this.htmlOutput = htmlOutput;
        this.wodOutput = wodOutput;
        this.wooOutput = wooOutput;
        this.encoding = encoding;
        this.close = close;
    }

    public WOSrcGenerator(InputSource input, Writer htmlOutput, Writer wodOutput, Writer wooOutput) {
        this(input, htmlOutput, wodOutput, wooOutput, null, false);
    }

    private static final InputSource openInput(String dfn, String encoding) throws IOException {
        InputSource input = new InputSource(dfn);
        if (encoding == null) {
            InputStream is = new BufferedInputStream(new FileInputStream(dfn));
            input.setByteStream(is);
        } else {
            Reader r = new BufferedReader(new InputStreamReader(new FileInputStream(dfn), encoding));
            input.setCharacterStream(r);
        }
        return input;
    }

    private static final Writer openOutput(String dfn, String encoding) throws IOException {
        if (dfn == null) return null;
        if (encoding == null) encoding = Str.UTF_8;
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dfn), encoding));
    }

    public WOSrcGenerator(String input, String html, String wod, String woo, String encoding)
            throws IOException {
        this(openInput(input, null), openOutput(html, encoding), openOutput(wod, encoding),
                openOutput(woo, null), encoding, true);
    }

    private InputSource input;

    private Writer htmlOutput;

    private Writer wodOutput;

    private Writer wooOutput;

    private String encoding;

    private boolean close;

    public void generate(boolean html, boolean indentHtml) throws ParserConfigurationException,
            SAXException, IOException {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            parserFactory.setValidating(false);

            SAXParser parser = parserFactory.newSAXParser();

            XMLReader xmlReader = parser.getXMLReader();
            ComponentHandler contentHandler = new ComponentHandler(htmlOutput, wodOutput,
                    wooOutput, html, indentHtml, encoding);
            xmlReader.setContentHandler(contentHandler);
            xmlReader.setErrorHandler(contentHandler);
            xmlReader.setFeature(SAXFeatures.NAMESPACES_PREFIXES, true);
            xmlReader.setFeature(SOXFeatures.MULTIPLE_TOP_ELEMENTS, true);
            xmlReader.setProperty(SAXProperties.LEXICAL_HANDLER, contentHandler);

            xmlReader.parse(input);
        } finally {
            htmlOutput.flush();
            wodOutput.flush();
            wooOutput.flush();

            if (close) {
                htmlOutput.close();
                wodOutput.close();
                wooOutput.close();
                InputStream is = input.getByteStream();
                Reader r = input.getCharacterStream();
                if (is != null) is.close();
                else if (r != null) r.close();
            }
        }
    }
}
