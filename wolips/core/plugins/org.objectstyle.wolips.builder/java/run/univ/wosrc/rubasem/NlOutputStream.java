/* NlOutputStream.java
 * Created on 10 févr. 2005
 */
package run.univ.wosrc.rubasem;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Une implémentation de {@link FilterOutputStream} permettant de normaliser les caractères de fin
 * de ligne d'un flux.
 * 
 * @author jclain
 */
public class NlOutputStream extends FilterOutputStream {
    public NlOutputStream(OutputStream os) {
        super(os);
    }

    public NlOutputStream(OutputStream os, String nl) {
        this(os);
        setNl(nl);
    }

    protected String nl = LineSep.LF;

    public String getNl() {
        return nl;
    }

    public void setNl(String nl) {
        this.nl = nl;
    }

    protected void writeNl() throws IOException {
        for (int i = 0, max = nl.length(); i < max; i++) {
            super.write(nl.charAt(i));
        }
    }

    protected boolean crlf = false;

    public void write(int b) throws IOException {
        if (b == '\r') {
            if (crlf) writeNl();
            crlf = true;
        } else if (b == '\n') {
            if (crlf) crlf = false;
            writeNl();
        } else {
            if (crlf) {
                crlf = false;
                writeNl();
            }
            super.write(b);
        }
    }

    public void flush() throws IOException {
        if (crlf) writeNl();
        super.flush();
    }
}