/* Entities.java
 * Created on 1 mars 2005
 */
package run.univ.xml.base;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import run.univ.StrBuff;

/**
 * Gestion des entités XML.
 * 
 * @author jclain
 */
public class Entities {
    /** entités de base. */
    protected static final String[][] basic_entities = {
            {"&", "&amp;"},
            {"<", "&lt;"},
            {">", "&gt;"},
            {"\"", "&quot;"},
            {"'", "&apos;"}};

    /** entités qui correspondent à des caractères spéciaux/accentués dans le codage iso8859-1 */
    protected static final String[][] iso88591_entities = {
            {Character.toString((char)0xa0), "&nbsp;"},
            {Character.toString((char)0xa1), "&iexcl;"},
            {Character.toString((char)0xa2), "&cent;"},
            {Character.toString((char)0xa3), "&pound;"},
            {Character.toString((char)0xa4), "&curren;"},
            {Character.toString((char)0xa6), "&brvbar;"},
            {Character.toString((char)0xa7), "&sect;"},
            {Character.toString((char)0xa8), "&uml;"},
            {Character.toString((char)0xa9), "&copy;"},
            {Character.toString((char)0xaa), "&ordf;"},
            {Character.toString((char)0xab), "&laquo;"},
            {Character.toString((char)0xac), "&not;"},
            {Character.toString((char)0xae), "&reg;"},
            {Character.toString((char)0xaf), "&macr;"},
            {Character.toString((char)0xb0), "&deg;"},
            {Character.toString((char)0xb1), "&plusmn;"},
            {Character.toString((char)0xb2), "&sup2;"},
            {Character.toString((char)0xb3), "&sup3;"},
            {Character.toString((char)0xb4), "&acute;"},
            {Character.toString((char)0xb5), "&micro;"},
            {Character.toString((char)0xb6), "&para;"},
            {Character.toString((char)0xb7), "&middot;"},
            {Character.toString((char)0xb8), "&cedil;"},
            {Character.toString((char)0xb9), "&sup1;"},
            {Character.toString((char)0xba), "&ordm;"},
            {Character.toString((char)0xbb), "&raquo;"},
            {Character.toString((char)0xbc), "&frac14;"},
            {Character.toString((char)0xbd), "&frac12;"},
            {Character.toString((char)0xbe), "&frac34;"},
            {Character.toString((char)0xbf), "&iquest;"},
            {Character.toString((char)0xc0), "&Agrave;"},
            {Character.toString((char)0xc1), "&Aacute;"},
            {Character.toString((char)0xc2), "&Acirc;"},
            {Character.toString((char)0xc3), "&Atilde;"},
            {Character.toString((char)0xc4), "&Auml;"},
            {Character.toString((char)0xc5), "&Aring;"},
            {Character.toString((char)0xc6), "&AElig;"},
            {Character.toString((char)0xc7), "&Ccedil;"},
            {Character.toString((char)0xc8), "&Egrave;"},
            {Character.toString((char)0xc9), "&Eacute;"},
            {Character.toString((char)0xca), "&Ecirc;"},
            {Character.toString((char)0xcb), "&Euml;"},
            {Character.toString((char)0xcc), "&Igrave;"},
            {Character.toString((char)0xcd), "&Iacute;"},
            {Character.toString((char)0xce), "&Icirc;"},
            {Character.toString((char)0xcf), "&Iuml;"},
            {Character.toString((char)0xd0), "&ETH;"},
            {Character.toString((char)0xd1), "&Ntilde;"},
            {Character.toString((char)0xd2), "&Ograve;"},
            {Character.toString((char)0xd3), "&Oacute;"},
            {Character.toString((char)0xd4), "&Ocirc;"},
            {Character.toString((char)0xd5), "&Otilde;"},
            {Character.toString((char)0xd6), "&Ouml;"},
            {Character.toString((char)0xd7), "&times;"},
            {Character.toString((char)0xd8), "&Oslash;"},
            {Character.toString((char)0xd9), "&Ugrave;"},
            {Character.toString((char)0xda), "&Uacute;"},
            {Character.toString((char)0xdb), "&Ucirc;"},
            {Character.toString((char)0xdc), "&Uuml;"},
            {Character.toString((char)0xdd), "&Yacute;"},
            {Character.toString((char)0xde), "&THORN;"},
            {Character.toString((char)0xdf), "&szlig;"},
            {Character.toString((char)0xe0), "&agrave;"},
            {Character.toString((char)0xe1), "&aacute;"},
            {Character.toString((char)0xe2), "&acirc;"},
            {Character.toString((char)0xe3), "&atilde;"},
            {Character.toString((char)0xe4), "&auml;"},
            {Character.toString((char)0xe5), "&aring;"},
            {Character.toString((char)0xe6), "&aelig;"},
            {Character.toString((char)0xe7), "&ccedil;"},
            {Character.toString((char)0xe8), "&egrave;"},
            {Character.toString((char)0xe9), "&eacute;"},
            {Character.toString((char)0xea), "&ecirc;"},
            {Character.toString((char)0xeb), "&euml;"},
            {Character.toString((char)0xec), "&igrave;"},
            {Character.toString((char)0xed), "&iacute;"},
            {Character.toString((char)0xee), "&icirc;"},
            {Character.toString((char)0xef), "&iuml;"},
            {Character.toString((char)0xf0), "&eth;"},
            {Character.toString((char)0xf1), "&ntilde;"},
            {Character.toString((char)0xf2), "&ograve;"},
            {Character.toString((char)0xf3), "&oacute;"},
            {Character.toString((char)0xf4), "&ocirc;"},
            {Character.toString((char)0xf5), "&otilde;"},
            {Character.toString((char)0xf6), "&ouml;"},
            {Character.toString((char)0xf7), "&divide;"},
            {Character.toString((char)0xf8), "&oslash;"},
            {Character.toString((char)0xf9), "&ugrave;"},
            {Character.toString((char)0xfa), "&uacute;"},
            {Character.toString((char)0xfb), "&ucirc;"},
            {Character.toString((char)0xfc), "&uuml;"},
            {Character.toString((char)0xfd), "&yacute;"},
            {Character.toString((char)0xfe), "&thorn;"}};

    /** entités qui ne sont pas transformées en caractères. */
    protected static final List dontUnquote = Arrays.asList(new String[] {"&nbsp;"});

    /**
     * remplacer dans le StringBuffer sb toutes les occurences des caractères par les entities
     * correspondant.
     */
    public static final StringBuffer quote(StringBuffer sb) {
        for (int i = 0; i < basic_entities.length; i++) {
            StrBuff.replaceAll(sb, basic_entities[i][0], basic_entities[i][1]);
        }
        for (int i = 0; i < iso88591_entities.length; i++) {
            StrBuff.replaceAll(sb, iso88591_entities[i][0], iso88591_entities[i][1]);
        }
        return sb;
    }

    public static final String quote(String s) {
        if (s == null) return null;
        return quote(new StringBuffer(s)).toString();
    }

    public static final Pattern ENTITY_PATTERN = Pattern.compile("&([a-zA-Z]+);");

    public static final Pattern DECIMAL_PATTERN = Pattern.compile("&#([0-9]+);");

    public static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("&#x([0-9a-fA-F]+);");

    /**
     * remplacer dans le StringBuffer sb toutes les occurences d'entities par les caractères
     * correspondant.
     * 
     * @param unquoteAll true s'il faut unquoter toutes les entités, false s'il faut ignorer les
     *        entités de {@link #dontUnquote}.
     */
    public static final StringBuffer unquote(StringBuffer sb, boolean unquoteAll) {
        // remplacer les entités de la forme &#n;
        Matcher m = DECIMAL_PATTERN.matcher(sb);
        int pos = 0;
        while (m.find(pos)) {
            sb.replace(m.start(), m.end(), Character.toString((char)Integer.parseInt(m.group(1))));
            pos = m.start() + 1;
        }
        m = HEXADECIMAL_PATTERN.matcher(sb);
        pos = 0;
        while (m.find(pos)) {
            sb.replace(m.start(), m.end(), Character.toString((char)Integer
                    .parseInt(m.group(1), 16)));
            pos = m.start() + 1;
        }
        // puis remplacer les entités
        if (ENTITY_PATTERN.matcher(sb).find()) {
            for (int i = iso88591_entities.length - 1; i >= 0; i--) {
                if (unquoteAll || !dontUnquote.contains(iso88591_entities[i][1])) {
                    StrBuff.replaceAll(sb, iso88591_entities[i][1], iso88591_entities[i][0]);
                }
            }
            for (int i = basic_entities.length - 1; i >= 0; i--) {
                if (unquoteAll || !dontUnquote.contains(basic_entities[i][1])) {
                    StrBuff.replaceAll(sb, basic_entities[i][1], basic_entities[i][0]);
                }
            }
        }
        return sb;
    }

    public static final String unquote(String s, boolean unquoteAll) {
        if (s == null) return null;
        return unquote(new StringBuffer(s), unquoteAll).toString();
    }

    public static final StringBuffer unquote(StringBuffer sb) {
        return unquote(sb, false);
    }

    public static final String unquote(String s) {
        if (s == null) return null;
        return unquote(new StringBuffer(s), false).toString();
    }
}