//
//  Str.java
//  urBase
//
//  Created by Jephte CLAIN on Thu Feb 19 2004.
//  Copyright (c) 2004 Universite de la Reunion. All rights reserved.
//

package run.univ;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Des fonctions utilitaires sur les String.
 * <p>
 * Ces méthodes sont comme celles de la classe String, mais supportent le traitement sur une valeur
 * nulle (auquel on retourne systématiquement null), ou permettent d'utiliser des valeurs qui
 * sortent des limites (comme {@link #substr(String, int, int)}par exemple).
 * </p>
 */
public class Str {
    public static final String UTF_8 = "UTF-8", ISO_8859_1 = "ISO-8859-1", MAC_ROMAN = "MacRoman";

    /** @throws UnsupportedCharsetException si encoding n'est pas un encoding supporté. */
    public static final void checkEncoding(String encoding) throws UnsupportedEncodingException {
        try {
            Charset.forName(encoding);
        } catch (UnsupportedCharsetException e) {
            UnsupportedEncodingException uee = new UnsupportedEncodingException(encoding);
            uee.initCause(e);
            throw uee;
        }
    }

    /** l'encoding par défaut pour les méthodes qui transforment des tableaux de byte */
    public static final String DEFAULT_ENCODING = new OutputStreamWriter(
            new ByteArrayOutputStream()).getEncoding();

    /**
     * charset par défaut pour cette jvm. sous Java 1.5, on peut utiliser
     * {@link Charset#defaultCharset()}.
     */
    public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_ENCODING);

    /** @return encoding s'il est non null, sinon {@link #DEFAULT_ENCODING}. */
    public static final String encodingOrDefault(String encoding) {
        return encoding != null? encoding: DEFAULT_ENCODING;
    }

    /** Retourner s si s est non nul, "" sinon */
    public final static String notnull(String s) {
        return s == null? "": s;
    }

    /** Retourner true si s est nulle ou vide */
    public final static boolean isempty(String s) {
        return s == null || s.length() == 0;
    }

    /** Retourner null si la chaine est vide, sinon retourner la même chaine. */
    public final static String notEmptyOrNull(String s) {
        return s == null || s.length() == 0? null: s;
    }

    /**
     * @return <code>null</code> si s vaut <code>null</code>, sinon "'s'".
     */
    public final static String quotedOrNull(String s) {
        return s == null? null: "'" + s + "'";
    }

    /** Retourner la valeur trimmée de s si elle est non nulle, null sinon */
    public final static String trim(String s) {
        return s == null? null: s.trim();
    }

    /** Retourner la valeur de s trimée d'éventuels caractères de fin de ligne. */
    public final static String trimNl(String s) {
        if (s == null) return s;
        int max = s.length();
        int pos = max;
        while (pos > 0) {
            char c = s.charAt(pos - 1);
            if (c == '\r' || c == '\n') pos--;
            else break;
        }
        if (pos < max) {
            // la chaine doit être trimmée
            s = s.substring(0, pos);
        }
        return s;
    }

    /** Retourner la chaine transformée en minuscule */
    public final static String lower(String s) {
        if (s == null) return null;
        return s.toLowerCase();
    }

    /** Retourner la chaine transformée en majuscule */
    public final static String upper(String s) {
        if (s == null) return null;
        return s.toUpperCase();
    }

    public final static String valueOf(Collection coll, String start, String sep, String end) {
        if (coll == null) return null;

        if (start == null) start = "[";
        if (sep == null) sep = ", ";
        if (end == null) end = "]";

        StringBuffer sb = new StringBuffer(start);
        boolean first = true;
        for (Iterator it = coll.iterator(); it.hasNext();) {
            if (first) first = false;
            else sb.append(sep);
            sb.append(valueOf(it.next()));
        }
        sb.append(end);
        return sb.toString();
    }

    public final static String valueOf(Collection coll, String sep) {
        return valueOf(coll, null, sep, null);
    }

    public final static String valueOf(Map map, String start, String sep, String end) {
        if (map == null) return null;

        if (start == null) start = "[";
        if (sep == null) sep = ", ";
        if (end == null) end = "]";

        StringBuffer sb = new StringBuffer(start);
        boolean first = true;
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            if (first) first = false;
            else sb.append(sep);
            Entry entry = (Entry)it.next();
            sb.append(valueOf(entry.getKey()));
            sb.append("=");
            sb.append(valueOf(entry.getValue()));
        }
        sb.append(end);
        return sb.toString();
    }

    public final static String valueOf(Map map, String sep) {
        return valueOf(map, null, sep, null);
    }

    public final static String valueOf(Object[] array, String start, String sep, String end) {
        if (array == null) return null;
        return valueOf(Arrays.asList(array), start, sep, end);
    }

    public final static String valueOf(Object[] array, String sep) {
        return valueOf(Arrays.asList(array), null, sep, null);
    }

    /**
     * Retourner une représentation chaine de l'objet o.
     * <p>
     * Si o est un tableau de byte[], cette méthode retourne fromBytes(o). <br>
     * Sinon, cette méthode est équivalente à o.toString() si o n'est pas nul. Si o vaut null, alors
     * cette méthode retourne null.
     * </p>
     */
    public final static String valueOf(Object o) {
        if (o == null) return null;
        if (o instanceof String) return (String)o;
        if (o instanceof byte[]) return fromBytes((byte[])o);
        if (o instanceof Reader) return fromReader((Reader)o);
        if (o instanceof InputStream) return fromInputStream((InputStream)o);
        if (o instanceof Object[]) {
            return valueOf(Arrays.asList((Object[])o), null, null, null);
        }
        if (o instanceof Collection) {
            return valueOf((Collection)o, null, null, null);
        }
        return o.toString();
    }

    /**
     * retourner une chaine de la forme "class:value" où value est la représentation chaine de o et
     * class sa classe d'objet.
     */
    public static final String classAndValue(Object o) {
        if (o == null) return "null";
        return o.getClass().getName() + ":" + valueOf(o);
    }

    /** Dans la chaine str, remplacer toutes les occurences de from par to */
    public final static String replaceAll(String str, String from, String to) {
        if (str == null) return null;
        return StrBuff.replaceAll(new StringBuffer(str), from, to).toString();
    }

    /** Dans la chaine str, remplacer toutes les occurences de from par to */
    public final static String replaceAll(String str, Pattern from, String to) {
        if (str == null || from == null) return str;
        return from.matcher(str).replaceAll(to);
    }

    // public static final String replaceAll(String str, String[] froms, String[] tos) {
    // if (str == null) return null;
    // return StrBuff.replaceAll(new StringBuffer(str), froms, tos).toString();
    // }
    //
    // public static final String replaceAll(String str, Pattern[] froms, String[] tos) {
    // if (str == null) return null;
    // return StrBuff.replaceAll(new StringBuffer(str), froms, tos).toString();
    // }

    public static final String translate(String s, String from, String to) {
        if (s == null) return null;
        return StrBuff.translate(new StringBuffer(s), from, to).toString();
    }

    /**
     * pad une chaine avec des padchar à la fin.
     * 
     * @param overflowString si n'est pas null, interdire à str de dépasser la taille padlen. Si
     *        c'est le cas, la chaine est tronquée, et on lui ajoute la chaine overflowString.
     * @param trailingString si n'est pas null, après avoir fait le pad, on rajoute la chaine
     *        trailingString.
     */
    public final static String padr(String str, int padlen, char padchar, String overflowString,
            String trailingString) {
        if (str == null) return null;

        if (str.length() != padlen || trailingString != null) {
            str = StrBuff.padr(
                    new StringBuffer(str),
                    padlen,
                    padchar,
                    overflowString,
                    trailingString).toString();
        }
        return str;
    }

    /** pad une chaine avec des padchar au début. */
    public final static String padl(String str, int padlen, char padchar) {
        if (str == null) return null;
        if (str.length() < padlen) {
            str = StrBuff.padl(new StringBuffer(str), padlen, padchar).toString();
        }
        return str;
    }

    /** pad une chaine avec des '0' au début. */
    public final static String pad0(String str, int padlen) {
        return padl(str, padlen, '0');
    }

    /** pad une chaine avec des espaces à la fin. */
    public final static String pad(String str, int padlen) {
        return padr(str, padlen, ' ', null, null);
    }

    /** pad une chaine avec des espaces à la fin. */
    public final static String pad(String str, int padlen, String overflowString,
            String trailingString) {
        return padr(str, padlen, ' ', overflowString, trailingString);
    }

    /** Retourner les count premier caractères de la chaine */
    public final static String left(String str, int count) {
        if (str != null && str.length() > count) {
            str = str.substring(0, count);
        }
        return str;
    }

    /** Retourner les count derniers caractères de la chaine */
    public final static String right(String str, int count) {
        if (str != null) {
            int start = str.length() - count;
            if (start < 0) start = 0;
            if (start > 0) {
                str = str.substring(start);
            }
        }
        return str;
    }

    /** Retourner les caractères de start à end de la chaine */
    public final static String substr(String str, int start, int end) {
        if (str == null) return null;

        int l = str.length();
        if (l > 0) {
            while (start < 0)
                start += l;
            while (end < 0)
                end += l;
        } else {
            if (start < 0) start = 0;
            if (end < 0) end = 0;
        }
        if (start >= l || start >= end) return "";
        if (end >= l + 1) end = l;

        return str.substring(start, end);
    }

    /** Retourner la sous-chaine de start à la fin de la chaine */
    public final static String substr(String str, int start) {
        if (str == null) return null;
        return substr(str, start, str.length());
    }

    /**
     * Remplacer respectivement les caractères &amp;, &lt;, &gt; par &amp;amp;, &amp;lt;, &amp;gt;.
     */
    public final static String html_quote(String str) {
        if (str == null) return null;
        return StrBuff.html_quote(new StringBuffer(str)).toString();
    }

    /**
     * Comme {@link #html_quote}, mais le remplacement se fait pour que la valeur puisse être
     * utilisée dans un attribut HTML.
     * <p>
     * La valeur est encadrée avec " ou ' suivant son contenu.
     * </p>
     */
    public final static String htmlattr_quote(String str) {
        if (str == null) return null;
        return StrBuff.htmlattr_quote(new StringBuffer(str)).toString();
    }

    /**
     * remplacer respectivement les caractères '%', '?', '&', '=', '+', ' ' par "%25", "%3F", "%26",
     * %3D, "%2B", "+".
     */
    public final static String url_quote(String str) {
        if (str == null) return null;
        return StrBuff.url_quote(new StringBuffer(str)).toString();
    }

    /**
     * remplacer respectivement les chaines "%25", "%3F", "%26", %3D, "%2B", "+" par '%', '?', '&',
     * '=', '+', ' '.
     * <p>
     * note: cette fonction ne fait que défaire ce que {@link #url_quote(String)}a fait. Ce n'est
     * pas une implémentation complète.
     * </p>
     */
    public final static String url_unquote(String str) {
        if (str == null) return null;
        return StrBuff.url_unquote(new StringBuffer(str)).toString();
    }

    /**
     * Transformer un array de bytes en chaine de caractère, avec l'encoding spécifié.
     * 
     * @param ba un byte array, éventuellement nul.
     * @return la chaine encodée.
     * @throws UnsupportedEncodingException si l'encoding est invalide.
     */
    public final static String fromBytes(byte[] ba, String encoding)
            throws UnsupportedEncodingException {
        if (ba == null) return null;
        return new String(ba, 0, ba.length, encoding);
    }

    /**
     * Comme {@link #fromBytes(byte[], String)}mais avec l'encoding par défaut
     */
    public final static String fromBytes(byte[] ba) {
        if (ba == null) return null;
        return new String(ba, 0, ba.length);
    }

    /**
     * Transformer une chaine en array de bytes avec l'encoding spécifié
     * 
     * @throws UnsupportedEncodingException si l'encoding est invalide.
     */
    public final static byte[] getBytes(String s, String encoding)
            throws UnsupportedEncodingException {
        if (s == null) return null;
        return s.getBytes(encoding);
    }

    /**
     * Transformer une chaine en array de bytes avec l'encoding par défaut.
     */
    public final static byte[] getBytes(String s) {
        if (s == null) return null;
        return s.getBytes();
    }

    /**
     * Lire les caractères sur un Reader, et retourner un String associé.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public final static String fromReader(Reader r, boolean close) {
        if (r == null) return null;
        return StrBuff.fromReader(new StringBuffer(), r, close).toString();

    }

    /**
     * Lire les caractères sur un Reader, fermer le flux, et retourner un String associé.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public final static String fromReader(Reader r) {
        return fromReader(r, true);
    }

    /**
     * Lire les caractères sur un InputStream avec l'encoding spécifié, et retourner un String
     * associé.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     * 
     * @throws UnsupportedEncodingException si l'encoding est invalide.
     */
    public final static String fromInputStream(InputStream is, String encoding, boolean close)
            throws UnsupportedEncodingException {
        if (is == null) return null;
        return StrBuff.fromInputStream(new StringBuffer(), is, encoding, close).toString();
    }

    /**
     * Lire les caractères sur un InputStream avec l'encoding spécifié, fermer le flux, et retourner
     * un String associé.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public final static String fromInputStream(InputStream is, String encoding)
            throws UnsupportedEncodingException {
        return fromInputStream(is, encoding, true);
    }

    /**
     * Lire les caractères sur un InputStream avec l'encoding par défaut, fermer le flux, et
     * retourner un String associé.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public final static String fromInputStream(InputStream is) {
        try {
            return fromInputStream(is, DEFAULT_ENCODING, true);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /** Retourner la représentation hexadécimale d'un byte */
    public final static String toHex(byte b) {
        int lb = b & 0x0F, ub = (b & 0xF0) >> 4;
        ub += ub < 10? '0': 'A' - 10;
        lb += lb < 10? '0': 'A' - 10;
        return new StringBuffer().append((char)ub).append((char)lb).toString();
    }

    /** Retourner la représentation hexadécimale d'un array de byte */
    public final static String toHex(byte[] ba) {
        if (ba == null) return null;

        StringBuffer sb = new StringBuffer(2 * ba.length);
        for (int i = 0; i < ba.length; i++) {
            StrBuff.toHex(sb, ba[i]);
        }
        return sb.toString();
    }

    public static final Pattern SPACES = Pattern.compile("\\s+"), SPACES_OR_DASHES = Pattern
            .compile("(?:\\s|-)+");

    public static final Pattern PATH_SEPARATOR = Pattern.compile("\\s*"
            + System.getProperty("path.separator") + "\\s*"), COMMA = Pattern.compile("\\s*,\\s*"),
            COLON = Pattern.compile("\\s*:\\s*"), SEMI_COLON = Pattern.compile("\\s*;\\s*");

    public static final Pattern NEWLINE = Pattern.compile("(?:\\r?\\n|\\r)"), NEWLINES = Pattern
            .compile("(?:\\r?\\n|\\r)(?:\\s*(?:\\r?\\n|\\r))*");

    /**
     * @return pattern.split(str), ou un tableau de taille nulle si str==null. Par défaut, on
     *         splitte sur {@link #SPACES}.
     */
    public final static String[] split(String str, Pattern pattern) {
        if (str == null) return new String[0];
        if (pattern == null) pattern = SPACES;
        return pattern.split(str);
    }

    public final static String[] split(String str) {
        return split(str, null);
    }

    /**
     * Comme {@link String#split(java.lang.String)}, mais on inclue aussi les séparateurs dans le
     * tableau retourné. Par défaut, on splitte sur {@link #SPACES}.
     * <p>
     * Si pattern contient un et un seul groupe, c'est la valeur du groupe qui est insérée plutôt
     * que le séparateur entier.
     * </p>
     * <p>
     * note: le tableau retourné est 2 fois plus grand que le tableau qui serait retourné par
     * {@link #split(String, Pattern)}.
     * </p>
     */
    public final static String[] splitx(String str, Pattern pattern) {
        if (str == null) return new String[0];
        if (pattern == null) pattern = SPACES;

        Matcher m = pattern.matcher(str);
        boolean useGroup = m.groupCount() == 1;
        int index = 0;
        ArrayList matches = new ArrayList();
        while (m.find()) {
            matches.add(str.substring(index, m.start()));
            if (useGroup) matches.add(str.substring(m.start(1), m.end(1)));
            else matches.add(str.substring(m.start(), m.end()));
            index = m.end();
        }
        if (index < str.length()) {
            // ajouter le reste de la chaine
            matches.add(str.substring(index));
            matches.add(null);
        }
        String[] matchesArray = new String[matches.size()];
        return (String[])matches.toArray(matchesArray);
    }

    /**
     * Splitter une chaine sur {@link #SPACES}.
     * 
     * @see #splitx(String, Pattern)
     */
    public final static String[] splitx(String str) {
        return splitx(str, null);
    }

    /**
     * Capitaliser les mots d'une chaine.
     * 
     * @see StrBuff#capwords(StringBuffer, String, Pattern, String, boolean)
     */
    public final static String capwords(String str, Pattern pattern, String sepString,
            boolean trimSep) {
        if (str == null) return null;
        return StrBuff.capwords(new StringBuffer(), str, pattern, sepString, trimSep).toString();
    }

    /** équivalent à <code>capwords(str, pattern, sepString, true)</code> */
    public final static String capwords(String str, Pattern pattern, String sepString) {
        return capwords(str, pattern, sepString, true);
    }

    /** équivalent à <code>capwords(str, null, null, true)</code> */
    public final static String capwords(String str) {
        return capwords(str, null, null, true);
    }

    /** XXX faire la doc */
    public final static String wrap(String str, int width, int leftMargin, int leftMarginFirstLine) {
        if (str == null) return null;
        return StrBuff.wrap(new StringBuffer(), str, width, leftMargin, leftMarginFirstLine)
                .toString();
    }

    public final static String appendWithSep(String src, String[] texts, String sep) {
        if (src == null && (texts == null || texts.length == 0)) return null;
        return StrBuff.appendWithSep(StrBuff.valueOf(src), texts, sep).toString();
    }

    public final static String appendWithSep(String src, String text, String sep) {
        if (src == null && text == null) return null;
        return StrBuff.appendWithSep(StrBuff.valueOf(src), text, sep).toString();
    }

    public final static String appendWithSep(String src, String[] texts) {
        return appendWithSep(src, texts, null);
    }

    public final static String appendWithSep(String src, String text) {
        return appendWithSep(src, text, null);
    }

    /** Tester l'égalité de deux chaines. */
    public final static boolean equals(String s1, String s2) {
        return (s1 == s2) || (s1 != null && s1.equals(s2));
    }

    /** Tester si s1 commence par s2. */
    public static final boolean startsWith(String s1, String s2) {
        return s1 != null && s1.startsWith(s2);
    }

    /**
     * Comparer deux chaines, en considérant qu'une valeur nulle est toujours plus "petite" que
     * valeur non nulle.
     */
    public static int compare(String s1, String s2) {
        if (s1 == s2) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }

    /** Tester l'égalité de deux chaines sans tenir compte de la casse. */
    public final static boolean equalsIgnoreCase(String s1, String s2) {
        return (s1 == s2) || (s1 != null && s1.equalsIgnoreCase(s2));
    }

    /**
     * Comparer deux chaines sans tenir compte de la casse, en considérant qu'une valeur nulle est
     * toujours plus "petite" que valeur non nulle.
     */
    public static int compareIgnoreCase(String s1, String s2) {
        if (s1 == s2) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareToIgnoreCase(s2);
    }

    private static final String[] REGEX_SPECIAL_CHARS = new String[] {"\\", // doit être en premier
            "*",
            "?",
            ".",
            "[",
            "]",
            "{",
            "}",
            "|",
            "$",
            "^"}, REGEX_REPLS = new String[] {
            "\\\\",
            "\\*",
            "\\?",
            "\\.",
            "\\[",
            "\\]",
            "\\{",
            "\\}",
            "\\|",
            "\\$",
            "\\^"};

    /** Mettre en echappement les caractères spéciaux d'une expression régulière. */
    public static final String escapeRegex(String pattern) {
        if (pattern == null) return null;
        boolean doEscape = false;
        for (int i = 0; i < REGEX_SPECIAL_CHARS.length; i++) {
            String regexSpecialChar = REGEX_SPECIAL_CHARS[i];
            if (pattern.indexOf(regexSpecialChar) != -1) {
                doEscape = true;
                break;
            }
        }
        if (!doEscape) return pattern;
        StringBuffer sb = new StringBuffer(pattern);
        for (int i = 0; i < REGEX_SPECIAL_CHARS.length; i++) {
            String regexSpecialChar = REGEX_SPECIAL_CHARS[i];
            String regexRepl = REGEX_REPLS[i];
            if (sb.indexOf(regexSpecialChar) != -1) {
                StrBuff.replaceAll(sb, regexSpecialChar, regexRepl);
            }
        }
        return sb.toString();
    }

    private static final String[] REPL_SPECIAL_CHARS = new String[] {"\\", "$"},
            REPL_REPLS = new String[] {"\\\\", "\\$"};

    /**
     * echapper les caractères posant problème dans les chaine de remplacement utilisées pour
     * remplacer les occurences d'une expression régulière.
     * <p>
     * Par exemple, si repl n'est pas fixé (par exemple, saisi par l'utilisateur), on ne doit pas
     * écrire:
     * <p>
     * 
     * <pre>
     * dest = source.replaceAll(pattern, repl);
     * </pre>
     * <p>
     * mais:
     * </p>
     * 
     * <pre>
     * dest = source.replaceAll(pattern, Str.escapeReplacement(repl));
     * </pre>
     */
    public static String escapeReplacement(String repl) {
        if (repl == null) return null;
        boolean doEscape = false;
        for (int i = 0; i < REPL_SPECIAL_CHARS.length; i++) {
            String replSpecialChar = REPL_SPECIAL_CHARS[i];
            if (repl.indexOf(replSpecialChar) != -1) {
                doEscape = true;
                break;
            }
        }
        if (!doEscape) return repl;
        StringBuffer sb = new StringBuffer(repl);
        for (int i = 0; i < REPL_SPECIAL_CHARS.length; i++) {
            String replSpecialChar = REPL_SPECIAL_CHARS[i];
            String replRepl = REPL_REPLS[i];
            if (sb.indexOf(replSpecialChar) != -1) {
                StrBuff.replaceAll(sb, replSpecialChar, replRepl);
            }
        }
        return sb.toString();
    }

    public static final String[] SIZE_SUFFIXES = new String[] {"", " Ko", " Mo", " Go"};

    /** XXX mettre 1 chiffre après la virgule */
    public static final String formatSize(long value) {
        int si = 0;
        while (value > 2048 && si + 1 < SIZE_SUFFIXES.length) {
            value /= 1024;
            si++;
        }
        return value + SIZE_SUFFIXES[si];
    }
}