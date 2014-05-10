/* SB.java
 * Created on 23 oct. 2009
 */
package run.univ.wosrc.rubasem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Des outils pour gérer les instances de {@link Appendable}, en particulier {@link StringBuffer} et
 * {@link StringBuilder}.
 * 
 * @author jclain
 */
public class SB {
//    public static final Log log = Log.getLog(SB.class);

    public static final StringBuilder notnull(StringBuilder sb) {
        if (sb == null) return new StringBuilder();
        else return sb;
    }

    public static final StringBuffer notnull(StringBuffer sb) {
        if (sb == null) return new StringBuffer();
        else return sb;
    }

    public static final StringBuilder toStringBuilder(StringBuilder sb) {
        if (sb == null) return new StringBuilder();
        else return sb;
    }

    public static final StringBuilder toStringBuilder(CharSequence sb) {
        if (sb == null) return new StringBuilder();
        else return new StringBuilder(sb);
    }

    public static final StringBuilder valueOrNull(CharSequence cs) {
        if (cs == null) return null;
        else return toStringBuilder(cs);
    }

    public static final StringBuilder valueOf(CharSequence cs) {
        return toStringBuilder(cs);
    }

    public static final StringBuffer toStringBuffer(StringBuffer sb) {
        if (sb == null) return new StringBuffer();
        else return sb;
    }

    public static final StringBuffer toStringBuffer(CharSequence sb) {
        if (sb == null) return new StringBuffer();
        else return new StringBuffer(sb);
    }

    public static final <T extends Appendable> T append(T sb, CharSequence s) throws IOException {
        if (sb != null) sb.append(s);
        return sb;
    }

    public static final <T extends Appendable> T append(T sb, CharSequence s, int start, int end)
            throws IOException {
        if (sb != null) sb.append(s, start, end);
        return sb;
    }

    public static final <T extends Appendable> T append(T sb, char c) throws IOException {
        if (sb != null) sb.append(c);
        return sb;
    }

    public static final StringBuilder append(StringBuilder sb, CharSequence s) {
        notnull(sb).append(s);
        return sb;
    }

    public static final StringBuilder append(StringBuilder sb, CharSequence s, int start, int end) {
        notnull(sb).append(s, start, end);
        return sb;
    }

    public static final StringBuilder append(StringBuilder sb, char c) {
        notnull(sb).append(c);
        return sb;
    }

    public static final StringBuffer append(StringBuffer sb, CharSequence s) {
        notnull(sb).append(s);
        return sb;
    }

    public static final StringBuffer append(StringBuffer sb, CharSequence s, int start, int end) {
        notnull(sb).append(s, start, end);
        return sb;
    }

    public static final StringBuffer append(StringBuffer sb, char c) {
        notnull(sb).append(c);
        return sb;
    }

    public static final boolean isempty(StringBuilder sb) {
        return sb == null || sb.length() == 0;
    }

    public static StringBuilder lower(StringBuilder sb, Locale locale) {
        if (sb == null) return null;

        if (locale == null) locale = Locale.getDefault();
        sb.replace(0, sb.length(), sb.toString().toLowerCase(locale));
        return sb;
    }

    public static StringBuilder upper(StringBuilder sb, Locale locale) {
        if (sb == null) return null;

        if (locale == null) locale = Locale.getDefault();
        sb.replace(0, sb.length(), sb.toString().toUpperCase(locale));
        return sb;
    }

    /** Dans le buffer sb, remplacer toutes les occurences de from par to */
    public static final StringBuilder replaceAll(StringBuilder sb, String from, String to) {
        if (sb == null) return null;

        int pos, start, flen, tlen;
        flen = from.length();
        tlen = to.length();
        start = 0;
        do {
            pos = sb.indexOf(from, start);
            if (pos != -1) {
                sb.replace(pos, pos + flen, to);
                start = pos + tlen;
            }
        } while (pos != -1);
        return sb;
    }

    /** Dans la chaine str, remplacer toutes les occurences de from par to */
    public static final StringBuilder replaceAll(StringBuilder sb, Pattern from, String to) {
        if (sb == null || from == null) return sb;

        String newstr = from.matcher(sb).replaceAll(to);
        sb.replace(0, sb.length(), newstr);
        return sb;
    }

    private static final IllegalArgumentException fromsAndTosMustBeSameSize() {
        return new IllegalArgumentException(("froms et tos doivent être de la même taille"));
    }

    private static void checkFromsAndTos(Object[] froms, Object[] tos) {
        if (froms == null || tos == null) {
            if (froms != null || tos != null) {
                throw fromsAndTosMustBeSameSize();
            }
        } else if (froms.length != tos.length) {
            throw fromsAndTosMustBeSameSize();
        }
    }

    public static final StringBuilder replaceAll(StringBuilder sb, String[] froms, String[] tos) {
        if (sb == null) return null;
        checkFromsAndTos(froms, tos);

        for (int i = 0; i < froms.length; i++) {
            replaceAll(sb, froms[i], tos[i]);
        }
        return sb;
    }

    public static final StringBuilder replaceAll(StringBuilder sb, Pattern[] froms, String[] tos) {
        if (sb == null) return null;
        checkFromsAndTos(froms, tos);

        for (int i = 0; i < froms.length; i++) {
            sb = replaceAll(sb, froms[i], tos[i]);
        }
        return sb;
    }

    private static final IllegalArgumentException fromAndToMustBeSameSize() {
        return new IllegalArgumentException("from et to doivent être de la même taille");
    }

    private static void checkFromsAndTos(String from, String to) {
        if (from == null || to == null) {
            if (from != null || to != null) {
                throw fromAndToMustBeSameSize();
            }
        } else if (from.length() != to.length()) {
            throw fromAndToMustBeSameSize();
        }
    }

    public static final StringBuilder translate(StringBuilder sb, String from, String to) {
        if (sb == null) return null;
        checkFromsAndTos(from, to);

        int max = sb.length();
        for (int i = 0; i < max; i++) {
            int pos = from.indexOf(sb.charAt(i));
            if (pos != -1) sb.setCharAt(i, to.charAt(pos));
        }
        return sb;
    }

    public static final StringBuilder padr(StringBuilder sb, int padlen, char padchar,
            String overflowString, String trailingString) {
        if (sb == null) return null;

        int len = sb.length();
        if (len != padlen || trailingString != null) {
            int sblen = padlen;
            if (trailingString != null) sblen += trailingString.length();

            sb.ensureCapacity(sblen);
            for (int i = 0; i < padlen - len; i++) {
                sb.append(padchar);
            }

            if (len > padlen && overflowString != null) {
                sb.setLength(padlen);
                int olen = overflowString.length();
                if (olen < padlen) {
                    sb.replace(padlen - olen, padlen, overflowString);
                }
            }

            if (trailingString != null) sb.append(trailingString);
        }
        return sb;
    }

    public static final StringBuilder padl(StringBuilder sb, int padlen, char padchar) {
        if (sb == null) return null;

        int len = sb.length();
        if (len < padlen) {
            sb.ensureCapacity(padlen);
            StringBuilder pad = new StringBuilder(padlen - len);
            for (int i = 0; i < padlen - len; i++) {
                pad.append(padchar);
            }
            sb.insert(0, pad);
        }
        return sb;
    }

    public static final StringBuilder trimNl(StringBuilder sb) {
        if (sb == null) return null;

        int max = sb.length();
        int pos = max;
        while (pos > 0) {
            char c = sb.charAt(pos - 1);
            if (c == '\r' || c == '\n') pos--;
            else break;
        }
        if (pos < max) {
            // la chaine doit être trimmée
            sb.setLength(pos);
        }
        return sb;
    }

    /**
     * Remplacer respectivement les caractères &amp;, &lt;, &gt; par &amp;amp;, &amp;lt;, &amp;gt;.
     */
    public static final StringBuilder html_quote(StringBuilder sb) {
        if (sb == null) return null;

        replaceAll(sb, "&", "&amp;");
        replaceAll(sb, "<", "&lt;");
        replaceAll(sb, ">", "&gt;");
        return sb;
    }

    /**
     * Comme {@link #html_quote}, mais le remplacement se fait pour que la valeur puisse être
     * utilisée dans un attribut HTML.
     * <p>
     * La valeur est encadrée avec " ou ' suivant son contenu.
     * </p>
     */
    public static final StringBuilder htmlattr_quote(StringBuilder sb) {
        if (sb == null) return null;

        replaceAll(sb, "&", "&amp;");
        replaceAll(sb, "<", "&lt;");
        replaceAll(sb, ">", "&gt;");
        if (sb.indexOf("\"") != -1) {
            if (sb.indexOf("'") != -1) {
                replaceAll(sb, "\"", "&quot;");
                sb.insert(0, '"');
                sb.append('"');
            } else {
                sb.insert(0, '\'');
                sb.append('\'');
            }
        } else {
            sb.insert(0, '"');
            sb.append('"');
        }
        return sb;
    }

    /**
     * remplacer respectivement les caractères '%', '?', '&', '=', '+', ' ' par "%25", "%3F", "%26",
     * %3D, "%2B", "+".
     */
    public static final StringBuilder url_quote(StringBuilder sb) {
        if (sb == null) return null;

        replaceAll(sb, "%", "%25");
        replaceAll(sb, "+", "%2B");
        replaceAll(sb, "?", "%3F");
        replaceAll(sb, "&", "%26");
        replaceAll(sb, "=", "%3D");
        replaceAll(sb, " ", "+");
        return sb;
    }

    /**
     * remplacer respectivement les chaines "%25", "%3F", "%26", %3D, "%2B", "+" par '%', '?', '&',
     * '=', '+', ' '.
     * <p>
     * note: cette fonction ne fait que défaire ce que {@link #url_quote(StringBuilder)} a fait. Ce
     * n'est pas une implémentation complète.
     * </p>
     */
    public static final StringBuilder url_unquote(StringBuilder sb) {
        if (sb == null) return null;

        replaceAll(sb, "+", " ");
        replaceAll(sb, "%3D", "=");
        replaceAll(sb, "%26", "&");
        replaceAll(sb, "%3F", "?");
        replaceAll(sb, "%2B", "+");
        replaceAll(sb, "%25", "%");
        return sb;
    }

    /** Dans le buffer sb, rajouter la représentation hexédécimale du byte b */
    public static final StringBuilder toHex(StringBuilder sb, byte b) {
        if (sb == null) return null;

        int l = b & 0x0F, u = (b & 0xF0) >> 4;
        u += u < 10? '0': 'A' - 10;
        l += l < 10? '0': 'A' - 10;
        return sb.append((char)u).append((char)l);
    }

    /**
     * Lire les caractères sur un {@link Reader} dans un {@link StringBuilder}, et retourner
     * l'instance.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public static final StringBuilder fromReader(StringBuilder sb, Reader r, boolean close) {
        if (sb == null || r == null) return sb;

        char[] buffer = new char[8192];
        int br;
        try {
            while ((br = r.read(buffer)) != -1) {
                sb.append(buffer, 0, br);
            }
        } catch (IOException e) {
//            log.error("SB#fromReader", e);
        }
        if (close) Closeable.close(r);
        return sb;
    }

    /**
     * Lire les caractères sur un {@link Reader} dans un {@link StringBuilder}, fermer le flux et
     * retourner l'instance.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public static final StringBuilder fromReader(StringBuilder sb, Reader r) {
        return fromReader(sb, r, true);
    }

    /**
     * Lire les caractères sur un {@link InputStream} avec l'encoding spécifié, et retourner un
     * {@link StringBuilder} qui contient les caractères lus.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     * 
     * @throws UnsupportedEncodingException si l'encoding est invalide.
     */
    public static final StringBuilder fromInputStream(StringBuilder sb, InputStream is,
            String encoding, boolean close) throws UnsupportedEncodingException {
        if (sb == null || is == null) return null;
        return fromReader(sb, new InputStreamReader(is, encoding), close);
    }

    /**
     * Lire les caractères sur un {@link InputStream} avec l'encoding par défaut, et retourner un
     * {@link StringBuilder} qui contient les caractères lus.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public static final StringBuilder fromInputStream(StringBuilder sb, InputStream is,
            boolean close) {
        try {
            return fromInputStream(sb, is, Enc.DEFAULT_ENCODING, close);
        } catch (UnsupportedEncodingException e) {
//            log.error("SB#fromInputStream", e);
            return sb;
        }
    }

    /**
     * Capitaliser chaque mot d'une chaine (chaque première lettre des mots est mis en majuscule),
     * et ajouter le résultat dans le StringBuilder sb.
     * <p>
     * Les séparateurs de début et de fin de chaine sont supprimés.
     * </p>
     * <p>
     * cas particulier: si un mot commence par "l'" ou "d'", (comme par exemple "l'hermite"), alors
     * c'est la troisième lettre qui est capitalisée, ce qui donne "l'Hermite" dans l'exemple.
     * </p>
     * 
     * @param pattern expression régulière qui identifie le(s) caractère(s) qui sépare(nt) les mots.
     *        par défaut, il s'agit de {@link Str#SPACES_OR_DASHES}.
     * @param sepString chaine qui doit être utilisée pour séparer les mots dans la chaîne résultat,
     *        ou null s'il faut utiliser la même chaine que dans la chaine source.
     * @param trimSep true si les chaine de séparateur doivent être trimées (pas d'espaces avant ni
     *        après). Si après le trim, la chaine est vide, elle est remplacée par un espace.
     *        trimSep est ignoré si sepString != null.
     */
    public static final StringBuilder capwords(StringBuilder sb, String words, Pattern pattern,
            String sepString, boolean trimSep) {
        if (sb == null || words == null) return sb;
        if (pattern == null) pattern = Str.SPACES_OR_DASHES;

        boolean first = true;
        String prevSep = null;
        String[] tokens = Str.splitx(words, pattern);
        for (int i = 0; i < tokens.length; i += 2) {
            String word = tokens[i];
            String sep;
            if (sepString != null) {
                sep = sepString;
            } else {
                sep = tokens[i + 1];
                if (trimSep) {
                    sep = Str.trim(sep);
                    if (Str.isempty(sep)) sep = " ";
                }
            }

            if (!Str.isempty(word)) {
                if (!first && !Str.isempty(prevSep)) {
                    sb.append(prevSep);
                }
                if (word.startsWith("l'") || word.startsWith("d'") || word.startsWith("L'")
                        || word.startsWith("D'")) {
                    sb.append(Str.substr(word, 0, 2).toLowerCase());
                    sb.append(Str.substr(word, 2, 3).toUpperCase());
                    sb.append(Str.substr(word, 3).toLowerCase());
                } else {
                    sb.append(Str.substr(word, 0, 1).toUpperCase());
                    sb.append(Str.substr(word, 1).toLowerCase());
                }

                first = false;
            }
            prevSep = sep;
        }
        return sb;
    }

    /** équivalent à <code>capwords(sb, words, Pattern.compile(splitRx), null, true)</code> */
    public static final StringBuilder capwords(StringBuilder sb, String words, String splitRx) {
        Pattern pattern = splitRx != null? Pattern.compile(splitRx): null;
        return capwords(sb, words, pattern, null, true);
    }

    /**
     * Ajouter les chaines de texts à sb, sans séparation entre elles.
     * <p>
     * Si texts==<code>null</code>, retourner sb inchangé. Si sb==<code>null</code>, créer une
     * nouvelle instance. Si sb contient déjà du texte, insérer sepBefore avant les chaines de
     * texts, et sepAfter après.
     * </p>
     */
    public static final StringBuilder append(StringBuilder sb, String sepBefore, String[] texts,
            String sepAfter) {
        if (texts == null || texts.length == 0) return sb;
        if (sb == null) sb = new StringBuilder();
        boolean appendSep = sb.length() > 0;
        if (appendSep && sepBefore != null) sb.append(sepBefore);
        for (int i = 0; i < texts.length; i++) {
            sb.append(texts[i]);
        }
        if (appendSep && sepAfter != null) sb.append(sepAfter);
        return sb;
    }

    /**
     * Ajouter text à sb.
     * <p>
     * Si texts==<code>null</code>, retourner sb inchangé. Si sb==<code>null</code>, créer une
     * nouvelle instance. Si sb contient déjà du texte, insérer sepBefore avant text, et sepAfter
     * après.
     * </p>
     */
    public static final StringBuilder append(StringBuilder sb, String sepBefore, String text,
            String sepAfter) {
        if (text == null) return sb;
        if (sb == null) sb = new StringBuilder();
        boolean appendSep = sb.length() > 0;
        if (appendSep && sepBefore != null) sb.append(sepBefore);
        sb.append(text);
        if (appendSep && sepAfter != null) sb.append(sepAfter);
        return sb;
    }

    /**
     * Ajouter les chaines de texts à sb, sans séparation entre elles. Si sb contient déjà du texte,
     * insérer sep avant les chaines de texts.
     */
    public static final StringBuilder append(StringBuilder sb, String sep, String[] texts) {
        return append(sb, sep, texts, null);
    }

    /**
     * Ajouter les chaines de texts à sb, sans séparation entre elles. Si sb contient déjà du texte,
     * insérer un espace avant les chaines de texts.
     */
    public static final StringBuilder append(StringBuilder sb, String[] texts) {
        return append(sb, " ", texts, null);
    }

    /** Ajouter text à sb. Si sb contient déjà du texte, insérer sep avant text. */
    public static final StringBuilder append(StringBuilder sb, String sep, String text) {
        return append(sb, sep, text, null);
    }

    /** Ajouter text à sb. Si sb contient déjà du texte, insérer un espace avant text. */
    public static final StringBuilder append(StringBuilder sb, String text) {
        return append(sb, " ", text, null);
    }

    private static final Pattern RE_BOUNDARY = Pattern.compile("[ \t]+");

    private static void wrapLine(StringBuilder sb, String str, int width, int startCol,
            String prefix, String nl) {
        int strindex = 0;
        boolean firstLine = true;
        Matcher m = RE_BOUNDARY.matcher(str);
        while (true) {
            if (!m.find(strindex)) {
                if (prefix != null && !firstLine) sb.append(prefix);
                sb.append(str.substring(strindex));
                return;
            }
            if (!firstLine) {
                if (prefix != null) sb.append(prefix);
                startCol = Str.sizeOf(prefix);
            }
            int mstart = m.start(), mend = m.end();
            int start = startCol + m.start() - strindex, end = startCol + m.end() - strindex;
            int lastmstart = -1, lastmend = -1, laststart = -1, lastend = -1;
            while (start <= width) {
                lastmstart = mstart;
                lastmend = mend;
                laststart = start;
                lastend = end;
                if (m.find()) {
                    mstart = m.start();
                    mend = m.end();
                    start = startCol + m.start() - strindex;
                    end = startCol + m.end() - strindex;
                } else {
                    // peut-être la fin de la chaine convient-elle
                    if (str.length() - strindex <= width) {
                        sb.append(str.substring(strindex));
                        return;
                    }
                    break;
                }
            }
            if (start > width && laststart != -1) {
                mstart = lastmstart;
                mend = lastmend;
                start = laststart;
                end = lastend;
            }
            sb.append(str.substring(strindex, mstart));
            sb.append(nl);
            strindex = mend;
            firstLine = false;
        }
    }

    /**
     * Réorganiser *chaque* ligne de str, indépendamment les unes des autres, de façon que chque
     * ligne fasse width caractères de large maximum.
     * 
     * @param startCol considérer que la *première* ligne de str commence à cette colonne. Utiliser
     *        -1 pour calculer automatiquement la valeur en fonction du contenu actuel de sb: il
     *        s'agit du nombre de caractères de la dernière ligne de sb.
     * @param prefix ajouter cette chaine à chaque début de ligne, sauf pour la première ligne. Pour
     *        les lignes autres que la première, startCol est la longueur de prefix.
     * @param nl caractère de fin de ligne à utiliser, parmi {@link LineSep#LF}, {@link LineSep#CR},
     *        {@link LineSep#CRLF}.
     */
    public static final StringBuilder wrap(StringBuilder sb, String str, int width, int startCol,
            String prefix, String nl) {
        if (str == null) return sb;
        if (sb == null) sb = new StringBuilder();
        if (nl == null) {
            Matcher m = Str.NEWLINE.matcher(str);
            if (m.find()) nl = m.group();
            else nl = LineSep.LINE_SEPARATOR;
        }
        if (startCol < 0) {
            // trouver startCol à partir du contenu de sb: il s'agit du nombre de caractères de la
            // *dernière* ligne de sb.
            Matcher m = Str.NEWLINE.matcher(sb);
            int lastpos = 0;
            while (m.find()) {
                lastpos = m.end();
            }
            startCol = Str.sizeOf(sb.substring(lastpos));
        }
        String[] lines = Str.NEWLINE.split(str, -1);
        for (int i = 0; i < lines.length; i++) {
            wrapLine(sb, lines[i], width, startCol, prefix, nl);
            boolean notLastLine = i + 1 < lines.length;
            if (notLastLine) {
                sb.append(nl);
                if (prefix != null) sb.append(prefix);
                startCol = Str.sizeOf(prefix);
            }
        }
        return sb;
    }

    /** Equivalent à <code>wrap(sb, str, width, -1, prefix, nl)</code>. */
    public static final StringBuilder wrap(StringBuilder sb, String str, int width, String prefix,
            String nl) {
        return wrap(sb, str, width, -1, prefix, nl);
    }
}
