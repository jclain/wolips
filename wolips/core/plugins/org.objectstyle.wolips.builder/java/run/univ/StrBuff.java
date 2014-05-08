//
//  StrBuff.java
//  urBase
//
//  Created by Jephte CLAIN on Thu Mar 04 2004.
//  Copyright (c) 2004 Universite de la Reunion. All rights reserved.
//

package run.univ;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Pattern;

/** Des fonctions utilitaires sur les StringBuffer */
public class StrBuff {
    /**
     * Obtenir une instance de StringBuffer pour travailler sur la chaine str. Si str==
     * <code>null</code>, retourner <code>null</code>.
     * 
     * @return une instance non nulle si str est non nul.
     */
    public static final StringBuffer valueOrNull(String str) {
        if (str != null) return new StringBuffer(str);
        else return null;
    }

    /**
     * Obtenir une instance de StringBuffer pour travailler sur la chaine str. Si str==
     * <code>null</code>, retourner un StringBuffer vide.
     * 
     * @return une instance non nulle
     */
    public static final StringBuffer valueOf(String str) {
        if (str != null) return new StringBuffer(str);
        else return new StringBuffer();
    }

    public final static boolean isempty(StringBuffer sb) {
        return sb == null || sb.length() == 0;
    }

    public static StringBuffer lower(StringBuffer sb, Locale locale) {
        if (sb == null) return null;
        if (locale == null) locale = Locale.getDefault();
        sb.replace(0, sb.length(), sb.toString().toLowerCase(locale));
        return sb;
    }

    public static StringBuffer upper(StringBuffer sb, Locale locale) {
        if (sb == null) return null;
        if (locale == null) locale = Locale.getDefault();
        sb.replace(0, sb.length(), sb.toString().toUpperCase(locale));
        return sb;
    }

    /** Dans le buffer sb, remplacer toutes les occurences de from par to */
    public final static StringBuffer replaceAll(StringBuffer sb, String from, String to) {
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
    public final static StringBuffer replaceAll(StringBuffer sb, Pattern from, String to) {
        if (sb == null || from == null) return sb;
        String newstr = from.matcher(sb).replaceAll(to);
        sb.replace(0, sb.length(), newstr);
        return sb;
    }

    // public static final StringBuffer replaceAll(StringBuffer sb, String[] froms, String[] tos) {
    // if (sb == null) return null;
    // if (froms == null || tos == null) return sb;
    // if (tos.length < froms.length) {
    // tos = (String[])Arr.concat(froms, new String[froms.length - tos.length]);
    // }
    //
    // for (int i = 0; i < froms.length; i++) {
    // replaceAll(sb, froms[i], tos[i]);
    // }
    // return sb;
    // }
    //
    // public static final StringBuffer replaceAll(StringBuffer sb, Pattern[] froms, String[] tos) {
    // if (sb == null) return null;
    // if (froms == null || tos == null) return sb;
    // if (tos.length < froms.length) {
    // tos = (String[])Arr.concat(froms, new String[froms.length - tos.length]);
    // }
    //
    // for (int i = 0; i < froms.length; i++) {
    // sb = replaceAll(sb, froms[i], tos[i]);
    // }
    // return sb;
    // }

    public static final StringBuffer translate(StringBuffer sb, String from, String to) {
        if (sb == null) return null;
        if (from == null || to == null) return sb;
        if (to.length() < from.length()) to = Str.pad(to, from.length());

        int max = sb.length();
        for (int i = 0; i < max; i++) {
            int pos = from.indexOf(sb.charAt(i));
            if (pos != -1) sb.setCharAt(i, to.charAt(pos));
        }
        return sb;
    }

    public final static StringBuffer padr(StringBuffer sb, int padlen, char padchar,
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

    /** pad une chaine avec des padchar au début. */
    public final static StringBuffer padl(StringBuffer sb, int padlen, char padchar) {
        if (sb == null) return null;

        int len = sb.length();
        if (len < padlen) {
            sb.ensureCapacity(padlen);
            StringBuffer pad = new StringBuffer(padlen - len);
            for (int i = 0; i < padlen - len; i++) {
                pad.append(padchar);
            }
            sb.insert(0, pad);
        }
        return sb;
    }

    /** Retourner la valeur de s trimée d'éventuels caractères de fin de ligne. */
    public final static StringBuffer trimNl(StringBuffer sb) {
        if (sb == null) return sb;
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
    public final static StringBuffer html_quote(StringBuffer sb) {
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
    public final static StringBuffer htmlattr_quote(StringBuffer sb) {
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
    public final static StringBuffer url_quote(StringBuffer sb) {
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
     * note: cette fonction ne fait que défaire ce que {@link #url_quote(StringBuffer)}a fait. Ce
     * n'est pas une implémentation complète.
     * </p>
     */
    public final static StringBuffer url_unquote(StringBuffer sb) {
        replaceAll(sb, "+", " ");
        replaceAll(sb, "%3D", "=");
        replaceAll(sb, "%26", "&");
        replaceAll(sb, "%3F", "?");
        replaceAll(sb, "%2B", "+");
        replaceAll(sb, "%25", "%");
        return sb;
    }

    /** Dans le buffer sb, rajouter la représentation hexédécimale du byte b */
    public final static StringBuffer toHex(StringBuffer sb, byte b) {
        int l = b & 0x0F, u = (b & 0xF0) >> 4;
        u += u < 10? '0': 'A' - 10;
        l += l < 10? '0': 'A' - 10;
        sb.append((char)u);
        sb.append((char)l);
        return sb;
    }

    /**
     * Lire les caractères sur un Reader dans un StringBuffer, et retourner le StringBuffer.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public final static StringBuffer fromReader(StringBuffer sb, Reader r, boolean close) {
        if (r == null) return sb;

        char[] buffer = new char[8192];
        int br;
        try {
            while ((br = r.read(buffer)) != -1) {
                sb.append(buffer, 0, br);
            }
        } catch (IOException e) {
        }
        if (close) try {
            r.close();
        } catch (IOException e) {
        }
        return sb;
    }

    /**
     * Lire les caractères sur un Reader dans un StringBuffer, fermer le flux et retourner le
     * StringBuffer.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public final static StringBuffer fromReader(StringBuffer sb, Reader r) {
        return fromReader(sb, r, true);
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
    public final static StringBuffer fromInputStream(StringBuffer sb, InputStream is,
            String encoding, boolean close) throws UnsupportedEncodingException {
        if (is == null) return null;
        return fromReader(sb, new InputStreamReader(is, encoding), close);
    }

    /**
     * Lire les caractères sur un InputStream avec l'encoding par défaut, et retourner un String
     * associé.
     * <p>
     * Les exceptions sont ignorées.
     * </p>
     */
    public final static StringBuffer fromInputStream(StringBuffer sb, InputStream is, boolean close) {
        try {
            return fromInputStream(sb, is, Str.DEFAULT_ENCODING, close);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Capitaliser chaque mot d'une chaine (chaque première lettre des mots est mis en majuscule),
     * et ajouter le résultat dans le StringBuffer sb.
     * <p>
     * Les séparateurs de début et de fin de chaine sont supprimés.
     * </p>
     * <p>
     * cas particulier: si un mot commence par "l'" ou "d'", (comme par exemple "l'hermite"), alors
     * c'est la troisième lettre qui est capitalisée, ce qui donne "l'Hermite" dans l'exemple.
     * </p>
     * 
     * @param pattern expression régulière qui identifie le(s) caractère(s) qui sépare(nt) les mots.
     *        par défaut, il s'agit de {@link Str#SPACES}.
     * @param sepString chaine qui doit être utilisée pour séparer les mots dans la chaîne résultat,
     *        ou null s'il faut utiliser la même chaine que dans la chaine source.
     * @param trimSep true si les chaine de séparateur doivent être trimées (pas d'espaces avant ni
     *        après). Si après le trim, la chaine est vide, elle est remplacée par un espace.
     *        trimSep est ignoré si sepString != null.
     */
    public final static StringBuffer capwords(StringBuffer sb, String words, Pattern pattern,
            String sepString, boolean trimSep) {
        if (words == null) return sb;
        if (pattern == null) pattern = Str.SPACES;

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
                if (word.startsWith("l'") || word.startsWith("d'")) {
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
    public final static StringBuffer capwords(StringBuffer sb, String words, String splitRx) {
        Pattern pattern = splitRx != null? Pattern.compile(splitRx): null;
        return capwords(sb, words, pattern, null, true);
    }

    /**
     * Ajouter les chaines de texts à sb. Si sb contient déjà du texte, insérer sep avant les
     * chaines de texts.
     */
    public final static StringBuffer appendWithSep(StringBuffer sb, String[] texts, String sep) {
        if (texts == null || texts.length == 0) return sb;
        if (sb == null) sb = new StringBuffer();
        if (sep != null && sb.length() > 0) sb.append(sep);
        for (int i = 0; i < texts.length; i++) {
            sb.append(texts[i]);
        }
        return sb;
    }

    /** Ajouter text à sb. Si sb contient déjà du texte, insérer sep avant text. */
    public final static StringBuffer appendWithSep(StringBuffer sb, String text, String sep) {
        if (text == null) return sb;
        if (sb == null) sb = new StringBuffer();
        if (sep != null && sb.length() > 0) sb.append(sep);
        sb.append(text);
        return sb;
    }

    public final static StringBuffer append(StringBuffer sb, String[] texts) {
        return appendWithSep(sb, texts, null);
    }

    public final static StringBuffer append(StringBuffer sb, String text) {
        return appendWithSep(sb, text, null);
    }

    public final static StringBuffer wrap(StringBuffer sb, String str, int width, int leftMargin,
            int leftMarginFirstLine) {
        if (str == null) return sb;

        // XXX à faire

        return sb;
    }
}