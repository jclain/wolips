/* DecodableStreamFactory
 * 19 Juil. 2006
 */
package run.univ.io;

import java.io.InputStream;

/**
 * @author jclain
 */
public class DecodableStreamFactory implements IDecodableStreamFactory {
    private static DecodableStreamFactory decodableStreamFactory;

    public static final DecodableStreamFactory getInstance() {
        if (decodableStreamFactory == null) {
            decodableStreamFactory = new DecodableStreamFactory();
        }
        return decodableStreamFactory;
    }

    public DecodableStream getDecodableStream(InputStream is) {
        if (is instanceof DecodableStream) return (DecodableStream)is;
        else return new DecodableStream(is);
    }
}
