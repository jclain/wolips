/* NlWriter.java
 * Created on 10 févr. 2005
 */
package run.univ.wosrc.rubasem;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Une implémentation de {@link FilterWriter} permettant de normaliser les caractères de fin de
 * ligne d'un flux.
 * 
 * @author jclain
 */
public class NlWriter extends FilterWriter {
    public NlWriter(Writer w) {
        super(w);
    }

    public NlWriter(Writer w, String nl) {
        this(w);
        setNl(nl);
    }

    protected char[] nl = LineSep.LF.toCharArray();

    public String getNl() {
        return new String(nl);
    }

    public void setNl(String nl) {
        this.nl = nl.toCharArray();
    }

    private char[] wbuff;

    private static final int BUFF_SIZE = 1024;

    protected boolean crlf = false;

    protected void writeNl() throws IOException {
        super.write(nl, 0, nl.length);
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        int index, start = off, max = off + len;

        while (start < max) {
            // copier d'abord les caractères qui ne sont pas des sauts de ligne
            for (index = start; index < max;) {
                char c = cbuf[index];
                if (c == '\r' || c == '\n') break;
                index++;
            }
            if (index > start) {
                if (crlf) {
                    crlf = false;
                    writeNl();
                }
                super.write(cbuf, start, index - start);
                start = index;
            }

            if (start < max) {
                // puis traiter les caractères de fin de ligne
                char c = cbuf[start++];
                if (c == '\r') {
                    if (crlf) writeNl();
                    crlf = true;
                } else if (c == '\n') {
                    if (crlf) crlf = false;
                    writeNl();
                }
            }
        }
    }

    public void write(int c) throws IOException {
        synchronized (lock) {
            if (wbuff == null) wbuff = new char[BUFF_SIZE];
            wbuff[0] = (char)c;
            write(wbuff, 0, 1);
        }
    }

    public void write(String str, int off, int len) throws IOException {
        synchronized (lock) {
            char cbuff[];
            if (len <= BUFF_SIZE) {
                if (wbuff == null) wbuff = new char[BUFF_SIZE];
                cbuff = wbuff;
            } else {
                cbuff = new char[len];
            }
            str.getChars(off, (off + len), cbuff, 0);
            write(cbuff, 0, len);
        }
    }

    public void flush() throws IOException {
        if (crlf) writeNl();
        super.flush();
    }

    public void close() throws IOException {
        flush();
        super.close();
    }
}