/* Str.java
 * Created on 23 oct. 2009
 */
package run.univ.wosrc.rubasem;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Des méthodes utilitaires pour gérer les instances de {@link String}.
 * <p>
 * Ces méthodes sont comme celles de la classe {@link String}, mais supportent la valeur
 * <code>null</code> (pour laquelle on retourne systématiquement <code>null</code>), ou permettent
 * d'utiliser des valeurs qui sortent des limites (comme {@link #substr(String, int, int)} par
 * exemple).
 * </p>
 * 
 * @author jclain
 */
public class Str extends StrTools {
//    public static final String valueOf(IValue<?> value, String defaultValue) {
//        if (value != null) return value.toString();
//        else return defaultValue;
//    }

//    public static final String valueOf(Date value, String defaultValue) {
//        if (value != null) return Dates.toIso(value);
//        else return defaultValue;
//    }

    public static final String valueOf(Object value, String defaultValue) {
        if (value instanceof String) return (String)value;
        else if (Obj.isNull(value)) return defaultValue;
//        else if (value instanceof IValue) return ((IValue<?>)value).toString();
//        else if (value instanceof Date) return Dates.toIso((Date)value);
        else if (value instanceof Collection) {
            return Coll.join((Collection<?>)value, "[", null, "]");
        } else if (value instanceof Map) {
            return Coll.join((Map<?, ?>)value, "{", null, null, "}");
        } else return value.toString();
    }

//    public static final String valueOf(IValue<?> value) {
//        return valueOf(value, null);
//    }

    public static final String valueOf(Date value) {
        return valueOf(value, null);
    }

    public static final String valueOf(Object value) {
        return valueOf(value, null);
    }

    /** Si str est <code>null</code>, retourner une chaine vide "", sinon retourner str. */
    public static final String notnull(String str) {
        return str == null? "": str;
    }

    /**
     * Si str est <code>null</code> ou si str est une chaine vide, retourner <code>null</code>,
     * sinon retourner str.
     */
    public static final String neOrNull(String str) {
        return str == null || str.length() == 0? null: str;
    }

    /**
     * Si str n'est pas <code>null</code>, retourner la valeur trimmée de str, sinon retourner
     * <code>null</code>.
     */
    public static final String trim(String s) {
        return s == null? null: s.trim();
    }

    /** Supprimer tous les caractères '\r' et '\n' à la fin de la chaine str. */
    public static final String trimNl(String str) {
        if (str == null) return str;
        int max = str.length();
        int pos = max;
        while (pos > 0) {
            char c = str.charAt(pos - 1);
            if (c == '\r' || c == '\n') pos--;
            else break;
        }
        if (pos < max) {
            // la chaine doit être trimmée
            str = str.substring(0, pos);
        }
        return str;
    }

    /** Retourner la chaine str transformée en minuscule */
    public static final String lower(String str) {
        if (str == null) return null;
        return str.toLowerCase();
    }

    /** Retourner la chaine str transformée en majuscule */
    public static final String upper(String str) {
        if (str == null) return null;
        return str.toUpperCase();
    }

    /**
     * Si str n'est pas <code>null</code>, retourner la chaine str encadrée de quotes ('), sinon
     * retourner <code>null</code>.
     */
    public static final String quotedOrNull(String str) {
        return str == null? null: "'" + str + "'";
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
    public static final String replaceAll(String str, String from, String to) {
        if (str == null) return null;
        return SB.replaceAll(SB.valueOf(str), from, to).toString();
    }

    /**
     * Remplacer dans str toutes les occurences de chacune des chaines du tableau froms par
     * l'occurence correspondante dans le tableau tos.
     * <p>
     * les tableaux froms et tos doivent être de même longueur.
     * </p>
     * 
     * @throws IllegalArgumentException si les tableaux froms et tos ne sont pas tous les deux
     *         <code>null</code> ou s'ils ne sont pas de même longueur.
     */
    public static final String replaceAll(String str, String[] froms, String[] tos) {
        if (str == null) return null;
        return SB.replaceAll(SB.valueOf(str), froms, tos).toString();
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
        StringBuilder sb = SB.valueOf(pattern);
        for (int i = 0; i < REGEX_SPECIAL_CHARS.length; i++) {
            String regexSpecialChar = REGEX_SPECIAL_CHARS[i];
            String regexRepl = REGEX_REPLS[i];
            if (sb.indexOf(regexSpecialChar) != -1) {
                SB.replaceAll(sb, regexSpecialChar, regexRepl);
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
        StringBuilder sb = SB.valueOf(repl);
        for (int i = 0; i < REPL_SPECIAL_CHARS.length; i++) {
            String replSpecialChar = REPL_SPECIAL_CHARS[i];
            String replRepl = REPL_REPLS[i];
            if (sb.indexOf(replSpecialChar) != -1) {
                SB.replaceAll(sb, replSpecialChar, replRepl);
            }
        }
        return sb.toString();
    }

    /**
     * Dans la chaine str, chercher toutes les occurences de from, et les remplacer par to.
     * <p>
     * note: Les caractères '\' et '$' dans to sont interprétés conformément à la documentation de
     * {@link Matcher#replaceAll(String)}. L'on peut utiliser {@link #escapeReplacement(String)} si
     * l'on veut s'assurer que ces caractères sont remplacés littéralement.
     * </p>
     */
    public static final String replaceAll(String str, Pattern from, String to) {
        if (str == null || from == null) return str;
        return from.matcher(str).replaceAll(to);
    }

    /**
     * Dans la chaine str, chercher la première occurence de from, et la remplacer par to.
     * <p>
     * note: Les caractères '\' et '$' dans to sont interprétés conformément à la documentation de
     * {@link Matcher#replaceAll(String)}. L'on peut utiliser {@link #escapeReplacement(String)} si
     * l'on veut s'assurer que ces caractères sont remplacés littéralement.
     * </p>
     */
    public static final String replaceFirst(String str, Pattern from, String to) {
        if (str == null || from == null) return str;
        return from.matcher(str).replaceFirst(to);
    }

    /**
     * Pour chacun des patterns du tableau froms, chercher toutes les occurences dans str, et les
     * remplacer par la chaine correspondante dans le tableau to.
     * <p>
     * note: les tableaux froms et tos doivent être de même longueur.
     * </p>
     * <p>
     * note: Les caractères '\' et '$' dans to sont interprétés conformément à la documentation de
     * {@link Matcher#replaceAll(String)}. L'on peut utiliser {@link #escapeReplacement(String)} si
     * l'on veut s'assurer que ces caractères sont remplacés littéralement.
     * </p>
     * 
     * @throws IllegalArgumentException si les tableaux froms et tos ne sont pas tous les deux
     *         <code>null</code> ou s'ils ne sont pas de même longueur.
     */
    public static final String replaceAll(String str, Pattern[] froms, String[] tos) {
        if (str == null) return null;
        return SB.replaceAll(SB.valueOf(str), froms, tos).toString();
    }

    /**
     * Remplacer dans str chacun des caractères de from par le caractère correspondant dans to.
     * <p>
     * note: les chaines froms et tos doivent être de même longueur.
     * </p>
     * 
     * @throws IllegalArgumentException si les chaines froms et tos ne sont pas toutes les deux
     *         <code>null</code> ou si elles ne sont pas de même longueur.
     */
    public static final String translate(String str, String from, String to) {
        if (str == null) return null;
        return SB.translate(SB.valueOf(str), from, to).toString();
    }

    /**
     * compléter une chaine avec des padchar à la fin jusqu'à atteindre la longueur padlen.
     * 
     * @param overflowString si n'est pas <code>null</code>, interdire à str de dépasser la taille
     *        padlen. Si c'est le cas, la chaine est tronquée à padlen, et on lui ajoute la chaine
     *        overflowString (note: cela a pour conséquence que la chaine finale a une taille
     *        padlen+overflowString.length())
     * @param trailingString si n'est pas <code>null</code>, après avoir fait le pad, on rajoute la
     *        chaine trailingString.
     */
    public static final String padr(String str, int padlen, char padchar, String overflowString,
            String trailingString) {
        if (str == null) return null;

        if (str.length() != padlen || trailingString != null) {
            str = SB.padr(SB.valueOf(str), padlen, padchar, overflowString, trailingString)
                    .toString();
        }
        return str;
    }

    /** compléter une chaine avec des padchar au début jusqu'à atteindre la longueur padlen. */
    public static final String padl(String str, int padlen, char padchar) {
        if (str == null) return null;
        if (str.length() < padlen) {
            str = SB.padl(SB.valueOf(str), padlen, padchar).toString();
        }
        return str;
    }

    /** compléter une chaine avec des '0' au début jusqu'à padlen. */
    public static final String pad0(String str, int padlen) {
        return padl(str, padlen, '0');
    }

    /** compléter une chaine avec des espaces à la fin jusqu'à padlen. */
    public static final String pad(String str, int padlen) {
        return padr(str, padlen, ' ', null, null);
    }

    /**
     * compléter une chaine avec des espaces à la fin jusqu'à padlen.
     * 
     * @see #padr(String, int, char, String, String)
     */
    public static final String pad(String str, int padlen, String overflowString,
            String trailingString) {
        return padr(str, padlen, ' ', overflowString, trailingString);
    }

    /** Retourner les count premier caractères de la chaine. */
    public static final String left(String str, int count) {
        if (str != null && str.length() > count) {
            str = str.substring(0, count);
        }
        return str;
    }

    /** Retourner les count derniers caractères de la chaine. */
    public static final String right(String str, int count) {
        if (str != null) {
            int start = str.length() - count;
            if (start < 0) start = 0;
            if (start > 0) str = str.substring(start);
        }
        return str;
    }

    /** Retourner les caractères de start à end de la chaine */
    public static final String substr(String str, int start, int end) {
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
    public static final String substr(String str, int start) {
        if (str == null) return null;
        return substr(str, start, str.length());
    }

    /**
     * Remplacer respectivement les caractères &amp;, &lt;, &gt; par &amp;amp;, &amp;lt;, &amp;gt;.
     */
    public static final String html_quote(String str) {
        if (str == null) return null;
        return SB.html_quote(SB.valueOf(str)).toString();
    }

    /**
     * Comme {@link #html_quote}, mais le remplacement se fait pour que la valeur puisse être
     * utilisée dans un attribut HTML.
     * <p>
     * La valeur est encadrée avec " ou ' suivant son contenu.
     * </p>
     */
    public static final String htmlattr_quote(String str) {
        if (str == null) return null;
        return SB.htmlattr_quote(SB.valueOf(str)).toString();
    }

    /**
     * remplacer respectivement les caractères '%', '?', '&', '=', '+', ' ' par "%25", "%3F", "%26",
     * %3D, "%2B", "+".
     */
    public static final String url_quote(String str) {
        if (str == null) return null;
        return SB.url_quote(SB.valueOf(str)).toString();
    }

    /**
     * remplacer respectivement les chaines "%25", "%3F", "%26", %3D, "%2B", "+" par '%', '?', '&',
     * '=', '+', ' '.
     * <p>
     * note: cette fonction ne fait que défaire ce que {@link #url_quote(String)} a fait. Ce n'est
     * pas une implémentation complète.
     * </p>
     */
    public static final String url_unquote(String str) {
        if (str == null) return null;
        return SB.url_unquote(SB.valueOf(str)).toString();
    }

    /**
     * Transformer un array de bytes en chaine de caractère, avec l'encoding spécifié.
     * 
     * @param ba un byte array, éventuellement nul.
     * @return la chaine encodée.
     * @throws UnsupportedEncodingException si l'encoding est invalide.
     */
    public static final String fromBytes(byte[] ba, String encoding)
            throws UnsupportedEncodingException {
        if (ba == null) return null;
        return new String(ba, 0, ba.length, encoding);
    }

    /**
     * Comme {@link #fromBytes(byte[], String)} mais avec l'encoding par défaut
     */
    public static final String fromBytes(byte[] ba) {
        if (ba == null) return null;
        return new String(ba, 0, ba.length);
    }

    /**
     * Transformer une chaine en array de bytes avec l'encoding spécifié
     * 
     * @throws UnsupportedEncodingException si l'encoding est invalide.
     */
    public static final byte[] getBytes(String str, String encoding)
            throws UnsupportedEncodingException {
        if (str == null) return null;
        return str.getBytes(encoding);
    }

    /**
     * Transformer une chaine en array de bytes avec l'encoding par défaut.
     */
    public static final byte[] getBytes(String str) {
        if (str == null) return null;
        return str.getBytes();
    }

    /**
     * Lire les caractères sur un Reader, et retourner un String associé. Les exceptions sont
     * ignorées.
     */
    public static final String fromReader(Reader r, boolean close) {
        if (r == null) return null;
        return SB.fromReader(new StringBuilder(), r, close).toString();

    }

    /**
     * Lire les caractères sur un Reader, fermer le flux, et retourner un String associé. Les
     * exceptions sont ignorées.
     */
    public static final String fromReader(Reader r) {
        return fromReader(r, true);
    }

    /**
     * Lire les caractères sur un InputStream avec l'encoding spécifié, et retourner un String
     * associé. Les exceptions sont ignorées.
     * 
     * @throws UnsupportedEncodingException si l'encoding est invalide.
     */
    public static final String fromInputStream(InputStream is, String encoding, boolean close)
            throws UnsupportedEncodingException {
        if (is == null) return null;
        return SB.fromInputStream(new StringBuilder(), is, encoding, close).toString();
    }

    /**
     * Lire les caractères sur un InputStream avec l'encoding spécifié, fermer le flux, et retourner
     * un String associé. Les exceptions sont ignorées.
     */
    public static final String fromInputStream(InputStream is, String encoding)
            throws UnsupportedEncodingException {
        return fromInputStream(is, encoding, true);
    }

    /**
     * Lire les caractères sur un InputStream avec l'encoding par défaut, fermer le flux, et
     * retourner un String associé. Les exceptions sont ignorées.
     */
    public static final String fromInputStream(InputStream is) {
        try {
            return fromInputStream(is, Enc.DEFAULT_ENCODING, true);
        } catch (UnsupportedEncodingException e) {
            throw new RunUnivRuntimeException(e);
        }
    }

    /** Retourner la représentation hexadécimale d'un byte */
    public static final String toHex(byte b) {
        int lb = b & 0x0F, ub = (b & 0xF0) >> 4;
        ub += ub < 10? '0': 'A' - 10;
        lb += lb < 10? '0': 'A' - 10;
        return new StringBuilder().append((char)ub).append((char)lb).toString();
    }

    /** Retourner la représentation hexadécimale d'un array de byte */
    public static final String toHex(byte[] ba) {
        if (ba == null) return null;

        StringBuilder sb = new StringBuilder(2 * ba.length);
        for (int i = 0; i < ba.length; i++) {
            SB.toHex(sb, ba[i]);
        }
        return sb.toString();
    }

    /**
     * Tester si la chaine est la représentation hexadécimale d'un array de byte. La chaine doit
     * avoir une taille paire, i.e. "A" n'est pas valide mais "0A" l'est.
     */
    public static final boolean isHex(String str) {
        if (str == null) return false;
        int l = str.length();
        if (l % 2 != 0) return false;
        for (int i = 0; i < l; i++) {
            char c = str.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Construire un array de byte à partir d'une chaine. On assume que isHex(str) est vrai, sinon
     * le résultat est indéfini.
     */
    public static final byte[] fromHex(String str) {
        if (str == null) return null;
        int l = str.length();
        byte[] ba = new byte[l / 2];
        int j = 0;
        for (int i = 0; i < l; i += 2) {
            // byte est signé, il faut donc utiliser la taille au-dessus pour éviter les erreurs.
            // Par exemple, Byte.parseByte("FA", 16) provoque une erreur de dépassement de capacité.
            byte b = (byte)Short.parseShort(str.substring(i, i + 2), 16);
            ba[j++] = b;
        }
        return ba;
    }

    public static final Pattern SPACES = Pattern.compile("\\s+");

    public static final Pattern SPACES_OR_DASHES = Pattern.compile("(?:\\s|-)+");

    public static final Pattern PATH_SEPARATOR = Pattern.compile("\\s*"
            + System.getProperty("path.separator") + "\\s*");

    public static final Pattern COMMA = Pattern.compile("\\s*,\\s*");

    public static final Pattern DASH = Pattern.compile("\\s*-\\s*");

    public static final Pattern PLUS = Pattern.compile("\\s*\\+\\s*");

    public static final Pattern COLON = Pattern.compile("\\s*:\\s*");

    public static final Pattern SEMI_COLON = Pattern.compile("\\s*;\\s*");

    public static final Pattern NEWLINE = Pattern.compile("(?:\\r?\\n|\\r)");

    public static final Pattern NEWLINES = Pattern
            .compile("(?:\\r?\\n|\\r)(?:\\s*(?:\\r?\\n|\\r))*");

    /**
     * Retourner <code>pattern.split(str)</code>, ou un tableau de taille nulle si str==
     * <code>null</code>. Par défaut, on splitte sur {@link #SPACES}.
     */
    public static final String[] split(String str, Pattern pattern) {
        if (str == null) return new String[0];
        if (pattern == null) pattern = SPACES;
        return pattern.split(str);
    }

    /**
     * Splitter une chaine sur {@link #SPACES}.
     * 
     * @see #split(String, Pattern)
     */
    public static final String[] split(String str) {
        return split(str, null);
    }

    /** Joindre les chaines strs en les séparant par sep. Les chaines nulles sont ignorées. */
    public static final String join(String sep, String... strs) {
        StringBuilder sb = new StringBuilder();
        if (strs != null) {
            if (sep == null) sep = " ";
            boolean first = true;
            for (String str : strs) {
                if (str == null) continue;
                if (first) first = false;
                else sb.append(sep);
                sb.append(str);
            }
        }
        return sb.toString();
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
    public static final String[] splitx(String str, Pattern pattern) {
        if (str == null) return new String[0];
        if (pattern == null) pattern = SPACES;

        Matcher m = pattern.matcher(str);
        boolean useGroup = m.groupCount() == 1;
        int index = 0;
        ArrayList<String> matches = new ArrayList<String>();
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
        return matches.toArray(matchesArray);
    }

    /**
     * Splitter une chaine sur {@link #SPACES}.
     * 
     * @see #splitx(String, Pattern)
     */
    public static final String[] splitx(String str) {
        return splitx(str, null);
    }

    /** Mettre en majuscule la première lettre de la chaine. */
    public static final String capitalize(String str) {
        if (str == null) return null;
        else if ("".equals(str)) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Capitaliser les mots d'une chaine.
     * 
     * @see StrBuff#capwords(StringBuilder, String, Pattern, String, boolean)
     */
    public static final String capwords(String str, Pattern pattern, String sepString,
            boolean trimSep) {
        if (str == null) return null;
        return SB.capwords(new StringBuilder(), str, pattern, sepString, trimSep).toString();
    }

    /** équivalent à <code>capwords(str, pattern, sepString, true)</code> */
    public static final String capwords(String str, Pattern pattern, String sepString) {
        return capwords(str, pattern, sepString, true);
    }

    /** équivalent à <code>capwords(str, null, null, true)</code> */
    public static final String capwords(String str) {
        return capwords(str, null, null, true);
    }

    /**
     * Réorganiser *chaque* ligne de str, indépendamment les unes des autres, de façon que chaque
     * ligne fasse width caractères de large maximum, en comptant la largeur de prefix.
     * <p>
     * Note: dans le calcul de la taille des lignes, on considère qu'une tabulation fait 8
     * caractères.
     * </p>
     * 
     * @param startCol considérer que la *première* ligne commence à cette colonne. La valeur
     *        "normale" est 0.
     * @param prefix ajouter cette chaine à chaque début de ligne, sauf pour la première ligne. Pour
     *        les lignes autres que la première, startCol est la longueur de prefix.
     * @param nl caractère de fin de ligne à utiliser, parmi {@link LineSep#LF}, {@link LineSep#CR},
     *        {@link LineSep#CRLF}.
     */
    public static final String wrap(String str, int width, int startCol, String prefix, String nl) {
        if (str == null) return null;
        return SB.wrap(new StringBuilder(), str, width, startCol, prefix, nl).toString();
    }

    /** Equivalent à <code>wrap(str, width, 0, prefix, nl)</code>. */
    public static final String wrap(String str, int width, String prefix, String nl) {
        return wrap(str, width, 0, prefix, nl);
    }

    /** Equivalent à <code>wrap(str, width, 0, null, null)</code>. */
    public static final String wrap(String str, int width) {
        return wrap(str, width, 0, null, null);
    }

    /**
     * Ajouter les chaines de texts à src, sans séparation entre elles. Si src n'est vide, insérer
     * sepBefore avant les chaines de texts, et sepAfter après.
     */
    public static final String append(String src, String sepBefore, String[] texts, String sepAfter) {
        if (src == null && (texts == null || texts.length == 0)) return null;
        return SB.append(SB.valueOf(src), sepBefore, texts, sepAfter).toString();
    }

    /** Ajouter text à src. Si src n'est vide, insérer sepBefore avant, et sepAfter après. */
    public static final String append(String src, String sepBefore, String text, String sepAfter) {
        if (src == null && text == null) return null;
        return SB.append(SB.valueOf(src), sepBefore, text, sepAfter).toString();
    }

    /** Equivalent à append(str, " ", texts, null) */
    public static final String append(String src, String[] texts) {
        return append(src, " ", texts, null);
    }

    /** Equivalent à append(str, " ", text, null) */
    public static final String append(String src, String text) {
        return append(src, " ", text, null);
    }

    /** Tester si s1 commence par s2. */
    public static final boolean startsWith(String s1, String s2) {
        return s1 != null && s1.startsWith(s2);
    }

    /** Tester si s1 se termine par s2. */
    public static final boolean endsWith(String s1, String s2) {
        return s1 != null && s1.endsWith(s2);
    }

    public static final String[] SIZE_SUFFIXES = new String[] {"", " Ko", " Mo", " Go", " To"};

    private static final BigDecimal FS_THRESHOLD = new BigDecimal(2048), FS_UNIT = new BigDecimal(
            1024);

    /**
     * Formatter un nombre d'octets pour affichage, en choisissant le préfixe le plus approprié: Ko,
     * Mo, Go, To. Au maximum scale chiffres sont affichés après la virgule.
     * <p>
     * Cette méthode considère qu'à partir de 2048 d'une certaine unité, on passe à l'unité au
     * dessus. Par exemple, 2048 sera affiché tel quel, mais 2049 sera affiché "2 Ko".
     * </p>
     */
    public static final String formatSize(BigDecimal value, int scale) {
        int si = 0;
        value = value.setScale(scale);
        while (value.compareTo(FS_THRESHOLD) == 1 && si + 1 < SIZE_SUFFIXES.length) {
            value = value.divide(FS_UNIT);
            si++;
        }
        value = BigDec.stripTz(BigDec.round(value, scale));
        return valueOf(value) + SIZE_SUFFIXES[si];
    }

    public static final String formatSize(BigDecimal value) {
        return formatSize(value, 0);
    }

    /**
     * Formatter un nombre d'octets pour affichage, en choisissant le préfixe le plus approprié: Ko,
     * Mo, Go, To. Au maximum scale chiffres sont affichés après la virgule.
     * <p>
     * Cette méthode considère qu'à partir de 2048 d'une certaine unité, on passe à l'unité au
     * dessus. Par exemple, 2048 sera affiché tel quel, mais 2049 sera affiché "2 Ko".
     * </p>
     */
    public static final String formatSize(long value, int scale) {
        int si = 0;
        if (scale == 0) {
            while (value > 2048 && si + 1 < SIZE_SUFFIXES.length) {
                value /= 1024;
                si++;
            }
            return valueOf(value) + SIZE_SUFFIXES[si];
        } else {
            return formatSize(new BigDecimal(value), scale);
        }
    }

    public static final String formatSize(long value) {
        return formatSize(value, 0);
    }

    /**
     * Retourner la représentation sous forme de chaine de la valeur value.
     * <ul>
     * <li>Si value est une instance de {@link Number}, retourner le nombre converti en chaine.</li>
     * <li>Si value est une chaine, retourner la chaine avec des quotes de part et d'autres et
     * mettre en echappement les quotes de la chaine.</li>
     * <li>TODO: supporter des types supplémentaires (en particulier, les dates)</li>
     * </ul>
     */
    public static final String sql_quote(Object value) {
        if (value == null) return null;
        else if (value instanceof Number) {
            return valueOf(value);
        } else {
            return "'" + valueOf(value).replaceAll("'", "''") + "'";
        }
    }

    private static final Pattern TAB = Pattern.compile("\t");

    /** Calculer la taille visuelle de str, considérant qu'une tabulation fait 8 espaces. */
    public static final int sizeOf(CharSequence str) {
        if (str == null) return 0;
        int nbtabs = 0;
        Matcher m = Str.TAB.matcher(str);
        while (m.find()) {
            nbtabs++;
        }
        return str.length() + 7 * nbtabs;
    }
}
