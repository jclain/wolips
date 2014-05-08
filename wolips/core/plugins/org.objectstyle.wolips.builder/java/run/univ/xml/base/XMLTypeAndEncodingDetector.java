/* XMLTypeAndEncodingDetector.java
 * Created on 11 janv. 2005
 */
package run.univ.xml.base;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import run.univ.Str;

/**
 * des outils pour gérer un flux XML.
 * 
 * @author jclain
 */
public class XMLTypeAndEncodingDetector {
    protected boolean xml;

    public boolean isXml() {
        return xml;
    }

    protected boolean sox;

    public boolean isSox() {
        return sox;
    }

    protected String encoding;

    public String getEncoding() {
        return encoding;
    }

    /**
     * Détecter le type de flux et l'encoding.
     * <p>
     * Attention! le flux n'est pas rembobiné, et les caractères lus pour détecter le type et
     * l'encoding sont perdus!
     * </p>
     * 
     * @return true si l'on a pu détecter le type de flux, false sinon.
     */
    public boolean detectXmlTypeAndEncoding(Reader r) throws IOException {
        xml = false;
        sox = false;
        encoding = null;

        // tout d'abord lire <?(sox|xml)[? \t\n\r]
        int c;
        if (r.read() != '<') return false;
        if (r.read() != '?') return false;
        char[] typeArray = new char[3];
        for (int i = 0; i < 3; i++) {
            if ((c = r.read()) == -1) return false;
            typeArray[i] = (char)c;
        }
        String type = new String(typeArray);
        if (type.equals("xml")) {
            sox = false;
        } else if (type.equals("sox")) {
            sox = true;
        } else {
            return false;
        }
        c = r.read();
        if (c != '?' && c != ' ' && c != '\t' && c != '\r' && c != '\n') return false;
        xml = true;

        // puis lire les attributs
        final int SPACES = 0;
        final int NAME = 1;
        final int EQUAL = 2;
        final int VALUE = 3;
        int state = SPACES;
        String name = null;
        String value = null;
        StringBuffer token = null;
        if (c != '?') c = r.read();
        while (c != -1) {
            if (state == SPACES) {
                // on lit des espaces jusqu'au prochain name=value ou ?>
                if (Character.isWhitespace((char)c)) {
                    c = r.read(); // lire le prochaine caractère
                } else if (c == '?') {
                    char prev = (char)c;
                    c = r.read(); // lire le prochaine caractère
                    if (c == '>') {
                        // fin du tag <?(sox|xml) ...?>
                        break;
                    }
                    state = NAME;
                    token = new StringBuffer(prev);
                    token.append((char)c);

                } else {
                    state = NAME;
                    token = new StringBuffer();
                }

            } else if (state == NAME) {
                while (c != '=' && !Character.isWhitespace((char)c)) {
                    token.append((char)c);
                    c = r.read(); // lire le prochaine caractère
                }
                name = token.toString();
                state = EQUAL;

            } else if (state == EQUAL) {
                while (Character.isWhitespace((char)c)) {
                    c = r.read(); // lire le prochaine caractère
                }
                if (c != '=') {
                    // syntaxe invalide
                    break;
                }
                c = r.read(); // lire le prochaine caractère
                state = VALUE;
                token = new StringBuffer();

            } else if (state == VALUE) {
                while (Character.isWhitespace((char)c)) {
                    c = r.read(); // lire le prochaine caractère
                }
                int quote;
                if (c == '\'' || c == '"') {
                    quote = c;
                    c = r.read(); // lire le prochaine caractère
                } else {
                    // syntaxe invalide
                    break;
                }
                while (c != -1 && c != quote) {
                    token.append((char)c);
                    c = r.read(); // lire le prochaine caractère
                }
                if (c == -1) {
                    // syntaxe invalide
                    break;
                }
                value = token.toString();

                if (name.equals("encoding")) {
                    // nous avons trouvé encoding="..."
                    try {
                        Charset cs = Charset.forName(value);
                        encoding = cs.name();
                    } catch (UnsupportedCharsetException e) {
                        throw new UnsupportedEncodingException("encoding non supporté: " + e);
                    } catch (IllegalCharsetNameException e) {
                        throw new UnsupportedEncodingException("encoding non valide: " + e);
                    }
                    break;
                }
                // sinon, on recommence un cyle SPACES NAME EQUAL VALUE
                c = r.read(); // lire le prochaine caractère
                state = SPACES;
            }
        }

        return true;
    }

    protected boolean html;

    public boolean isHtml() {
        return html;
    }

    /**
     * Détecter si le flux est un flux html.
     * <p>
     * note: Il faut appeler cette méthode après {@link #detectXmlTypeAndEncoding(Reader)}, car
     * elle se base sur la valeur de {@link #isXml()}et {@link #isSox()}.
     * </p>
     * <p>
     * Attention! le flux n'est pas rembobiné, et les caractères lus pour détecter le type et
     * l'encoding sont perdus!
     * </p>
     * <p>
     * XXX l'algorithme n'est pas génial.
     * </p>
     * 
     * @return true si le flux est de type html, false sinon.
     */
    public boolean detectHtml(Reader r) throws IOException {
        html = false;

        int c;
        if (isXml() && isSox()) {
            // chercher "^html>"
            char[] typeArray = new char[5];
            while (true) {
                // lire cinq caractères
                int count;
                for (count = 0; count < 5; count++) {
                    if ((c = r.read()) == -1) return false;
                    if (c == '\r' || c == '\n') break;
                    typeArray[count] = (char)c;
                    if (c == '>' && count < 4) {
                        count++;
                        break;
                    }
                }

                // Il doivent être html>
                String type = new String(typeArray, 0, count);
                if (count == 5 && type.equalsIgnoreCase("html>")) {
                    return html = true;
                } else if (type.endsWith(">")) {
                    // si c'est un autre tag que html>, alors c'est raté
                    return false;
                }

                // sinon essayer sur la prochaine ligne
                while ((c = r.read()) != -1) {
                    if (c == '\r' || c == '\n') break;
                }
                if (c == -1) return false;
            }
        } else {
            // chercher <html[ \t\r\n>]
            char[] typeArray = new char[4];
            while (true) {
                // chercher '<'
                while ((c = r.read()) != '<') {
                    if (c == -1) return false;
                }

                // lire les quatre caractères suivants
                for (int i = 0; i < 4; i++) {
                    if ((c = r.read()) == -1) return false;
                    typeArray[i] = (char)c;
                }
                c = r.read();
                if (typeArray[0] != '?' && typeArray[0] != '!'
                        && (c != ' ' && c != '\t' && c != '\r' && c != '\n' && c != '>'))
                    return false;

                // Il doivent être html
                String type = new String(typeArray);
                if (type.equalsIgnoreCase("html")) {
                    return html = true;
                }
            }
        }
    }

    private String defaultEncoding;

    /**
     * Retourner l'encoding par défaut pour ce type de fichier. Il faut appeler cette méthode après
     * {@link #detectXmlTypeAndEncoding(Reader)}et {@link #detectHtml(Reader)}
     */
    public String getDefaultEncoding() {
        if (defaultEncoding == null) {
            if (xml) {
                if (sox) defaultEncoding = Str.ISO_8859_1;
                else defaultEncoding = Str.UTF_8;
            } else if (html) defaultEncoding = Str.ISO_8859_1;
        }
        return defaultEncoding;
    }
}