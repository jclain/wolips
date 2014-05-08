/* Enc.java
 * Created on 26 nov. 07
 */
package run.univ.base;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Des méthodes pour gérer les charset et encodings par défaut.
 * 
 * @author jclain
 */
public class Enc {
    public static final String UTF_8 = "UTF-8";

    public static final String ISO_8859_1 = "ISO-8859-1";

    public static final String MAC_ROMAN = "MacRoman";

    /**
     * Vérifier si un encoding est supporté par cette JVM.
     * 
     * @throws UnsupportedEncodingException si encoding n'est pas supporté.
     */
    public static final void checkEncoding(String encoding) throws UnsupportedEncodingException {
        try {
            Charset.forName(encoding);
        } catch (IllegalArgumentException e) {
            throw Exc.withCause(new UnsupportedEncodingException(encoding), e);
        }
    }

    /** l'encoding par défaut pour les méthodes qui transforment des tableaux de byte */
    public static final String DEFAULT_ENCODING = new OutputStreamWriter(
            new ByteArrayOutputStream()).getEncoding();

    /** charset par défaut pour cette jvm. */
    public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    /** @return encoding s'il est non null, sinon {@link DEFAULT_ENCODING}. */
    public static final String encodingOrDefault(String encoding) {
        return encoding != null? encoding: DEFAULT_ENCODING;
    }

    public static final Charset UTF_8_CHARSET;

    public static final Charset ISO_8859_1_CHARSET;

    public static final Charset MAC_ROMAN_CHARSET;
    static {
        Charset utf_8 = null;
        try {
            utf_8 = Charset.forName(UTF_8);
        } catch (Exception e) {
        }
        Charset iso_8859_1 = null;
        try {
            iso_8859_1 = Charset.forName(ISO_8859_1);
        } catch (Exception e) {
        }
        Charset mac_roman = null;
        try {
            mac_roman = Charset.forName(MAC_ROMAN);
        } catch (Exception e) {
        }
        UTF_8_CHARSET = utf_8;
        ISO_8859_1_CHARSET = iso_8859_1;
        MAC_ROMAN_CHARSET = mac_roman;
    }
}
