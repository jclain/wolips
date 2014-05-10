/* AppendableWriter.java
 * Created on 28 janv. 2011
 */
package run.univ.wosrc.rubasem;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

/**
 * @author jclain
 */
public class AppendableWriter extends Writer {
    public AppendableWriter(Appendable appendable) {
        this.appendable = appendable;
    }

    private Appendable appendable;

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        appendable.append(new String(cbuf, off, len));
    }

    @Override
    public void flush() throws IOException {
        if (appendable instanceof Flushable) {
            ((Flushable)appendable).flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (appendable instanceof Closeable) {
            ((Closeable)appendable).close();
        }
    }
}
