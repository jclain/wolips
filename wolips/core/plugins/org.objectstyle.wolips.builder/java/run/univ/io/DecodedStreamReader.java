/* DecodedStreamReader.java
 * Created on 7 oct. 2004
 */
package run.univ.io;

import java.io.FilterReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import run.univ.Str;

/**
 * Un flux de caractères à utiliser avec les instances de {@link run.univ.io.DecodableStream}.
 * 
 * @author jclain
 */
public class DecodedStreamReader extends FilterReader {
    private static final Reader decodeStream(InputStream is, String defaultEncoding)
            throws UnsupportedEncodingException {
        if (is instanceof DecodableStream) {
            DecodableStream ds = (DecodableStream)is;
            return new InputStreamReader(ds, ds.getEncoding());
        }
        return new InputStreamReader(is, Str.encodingOrDefault(defaultEncoding));
    }

    public DecodedStreamReader(InputStream is, String defaultEncoding)
            throws UnsupportedEncodingException {
        super(decodeStream(is, defaultEncoding));
    }

    public DecodedStreamReader(InputStream is) throws UnsupportedEncodingException {
        this(is, null);
    }
}