/* LineSep.java
 * Created on 10 f�vr. 2005
 */
package run.univ.wosrc.rubasem;

import java.io.OutputStream;
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * Une classe permettant de normaliser les caractères de fin de ligne d'une chaine ou d'un flux.
 * 
 * @author jclain
 */
public class LineSep {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String CRLF = "\r\n", CR = "\r", LF = "\n";

    protected static final Pattern CRLF_PATTERN = Pattern.compile(CRLF), CR_PATTERN = Pattern
            .compile(CR), LF_PATTERN = Pattern.compile(LF);

    public static final String toCR(String s) {
        if (s == null) return null;
        s = CRLF_PATTERN.matcher(s).replaceAll(CR);
        s = LF_PATTERN.matcher(s).replaceAll(CR);
        return s;
    }

    public static final String toLF(String s) {
        if (s == null) return null;
        s = CRLF_PATTERN.matcher(s).replaceAll(LF);
        s = CR_PATTERN.matcher(s).replaceAll(LF);
        return s;
    }

    public static final String toCRLF(String s) {
        if (s == null) return null;
        s = toLF(s);
        s = LF_PATTERN.matcher(s).replaceAll(CRLF);
        return s;
    }

    public static final OutputStream toCR(OutputStream os) {
        return new NlOutputStream(os, CR);
    }

    public static final OutputStream toLF(OutputStream os) {
        return new NlOutputStream(os, LF);
    }

    public static final OutputStream toCRLF(OutputStream os) {
        return new NlOutputStream(os, CRLF);
    }

    public static final Writer toCR(Writer w) {
        return new NlWriter(w, CR);
    }

    public static final Writer toLF(Writer w) {
        return new NlWriter(w, LF);
    }

    public static final Writer toCRLF(Writer w) {
        return new NlWriter(w, CRLF);
    }
}