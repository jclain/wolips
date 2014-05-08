/* XMLBuilder.java
 * Created on 28 janv. 2011
 */
package run.univ.base.xml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import run.univ.base.xml.base.AbstractHTMLBuilder;
import run.univ.base.xml.base.AppendableWriter;

/**
 * @author jclain
 */
public class HTMLBuilder extends AbstractHTMLBuilder {
    public HTMLBuilder(Writer writer) {
        setWriter(writer);
    }

    public HTMLBuilder(Appendable appendable) {
        this(new AppendableWriter(appendable));
    }

    public HTMLBuilder() {
        this(new PrintWriter(System.out));
    }

    private Writer writer;

    public void setWriter(Writer writer) {
        if (writer == null) throw new NullPointerException();
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
