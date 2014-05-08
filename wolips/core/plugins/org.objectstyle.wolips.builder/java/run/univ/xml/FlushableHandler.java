/* FlushableHandler.java
 * Created on 23 déc. 2005
 */
package run.univ.xml;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author jclain
 */
public interface FlushableHandler extends ContentHandler, LexicalHandler {
    public void flush() throws IOException;
}
