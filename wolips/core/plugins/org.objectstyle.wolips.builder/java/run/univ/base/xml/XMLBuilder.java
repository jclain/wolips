package run.univ.base.xml;
/* XMLBuilder.java
 * Created on 28 janv. 2011
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import run.univ.base.xml.base.AbstractXMLBuilder;
import run.univ.base.xml.base.AppendableWriter;

/**
 * @author jclain
 */
public class XMLBuilder extends AbstractXMLBuilder {
    public XMLBuilder(Writer writer) {
        setWriter(writer);
    }

    public XMLBuilder(Appendable appendable) {
        this(new AppendableWriter(appendable));
    }

    public XMLBuilder() {
        this(new PrintWriter(System.out));
    }

    private Writer writer;

    public void setWriter(Writer writer) {
        if (writer == null) throw new NullPointerException("writer should not be null");
        this.writer = writer;
    }

    @Override
    protected void write(String text) {
        try {
            writer.write(text);
        } catch (IOException e) {
            saveException(e);
        }
    }

    @Override
    public void close() {
        try {
            flush();
            writer.close();
            writer = null;
        } catch (IOException e) {
            saveException(e);
        }
    }
}
