/* IDecodableStreamFactory.java
 * 19 juil. 2006
 */
package run.univ.io;

import java.io.InputStream;

/**
 * @author jclain
 */
public interface IDecodableStreamFactory {
    public DecodableStream getDecodableStream(InputStream is);
}
