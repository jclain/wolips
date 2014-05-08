/* DecodableStream.java
 * Created on 6 oct. 2004
 */
package run.univ.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;

import run.univ.Str;

/**
 * Un flux dont on peut détecter l'encoding.
 * 
 * @author jclain
 */
public class DecodableStream extends PushbackInputStream {
    /** taille du buffer nécessaire pour la détection de l'encoding */
    private static final int DETECT_BUFFER_SIZE = 4;

    /** l'encoding a-t-il déjà été détecté? */
    protected boolean detected;

    /** encoding du fichier tel qu'il a été détecté. */
    protected String encoding;

    public DecodableStream(InputStream is) {
        super(is, DETECT_BUFFER_SIZE);
    }

    protected DecodableStream(InputStream is, int size) {
        super(is, size);
    }

    /**
     * Détecter l'encoding.
     * <p>
     * retourner le nombre de caractères qui ont été pris en compte pour la détection.
     * </p>
     */
    protected int detectEncoding() throws IOException {
        byte[] buf = new byte[DETECT_BUFFER_SIZE];
        int len = super.read(buf);
        if (len > 0) super.unread(buf, 0, len);
        detected = true;
        if (len >= 3 && buf[0] == (byte)0xEF && buf[1] == (byte)0xBB && buf[2] == (byte)0xBF) {
            encoding = "UTF-8";
            return 3;
        }
        if (len >= 2 && buf[0] == (byte)0xFE && buf[1] == (byte)0xFF) {
            encoding = "UTF-16BE";
            return 2;
        }
        if (len >= 2 && buf[0] == (byte)0xFF && buf[1] == (byte)0xFE) {
            encoding = "UTF-16LE";
            return 2;
        }
        if (len >= 4 && buf[0] == (byte)0xFF && buf[1] == (byte)0xFE && buf[2] == (byte)0x00
                && buf[3] == (byte)0x00) {
            throw new UnsupportedEncodingException("UTF-32LE not supported");
        }
        if (len >= 4 && buf[0] == (byte)0x00 && buf[1] == (byte)0x00 && buf[2] == (byte)0xFE
                && buf[3] == (byte)0xFF) {
            throw new UnsupportedEncodingException("UTF-32BE not supported");
        }
        return 0;
    }

    /* @see java.io.InputStream#read() */
    public int read() throws IOException {
        if (!detected) detectEncoding();
        return super.read();
    }

    /**
     * Retourner l'encoding détecté, ou null si aucun encoding n'a pu être détecté.
     * 
     * @throws UnsupportedEncodingException si l'encoding n'est pas supporté ou invalide.
     */
    public String getDetectedEncoding() throws UnsupportedEncodingException {
        if (!detected) {
            try {
                detectEncoding();
            } catch (UnsupportedEncodingException e) {
                throw e;
            } catch (IOException e) {
                // Si un autre type d'erreur se produit, on assume qu'il nous a été impossible de
                // détecter l'encoding.
            }
        }
        return encoding;
    }

    /**
     * @return l'encoding par défaut pour ce type de fichier, ou null s'il n'y a pas d'encoding par
     *         défaut.
     */
    public String getDefaultEncoding() {
        return null;
    }

    /**
     * Retourner l'encoding à utiliser pour décoder ce flux.
     * 
     * @throws WrappedException si l'encoding n'est pas supporté. l'exception lancée intègre une
     *         instance de {@link UnsupportedEncodingException}.
     */
    public String getEncoding() {
        try {
            String encoding = getDetectedEncoding();
            if (encoding == null) encoding = getDefaultEncoding();
            return Str.encodingOrDefault(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}