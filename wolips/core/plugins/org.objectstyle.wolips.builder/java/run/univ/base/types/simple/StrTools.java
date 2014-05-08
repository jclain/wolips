/* StrTools.java
 * Created on 23 oct. 2009
 */
package run.univ.base.types.simple;

import java.io.IOException;

/**
 * Des méthodes statiques pour gérer les instances de {@link String}.
 * 
 * @author jclain
 */
public class StrTools {
    public static final String toString(String value) {
        return value;
    }

    public static final <S extends Appendable> S append(S sb, String value) throws IOException {
        if (value != null) sb.append(value);
        return sb;
    }

    public static final StringBuffer append(StringBuffer sb, String value) {
        if (value != null) sb.append(value);
        return sb;
    }

    public static final StringBuilder append(StringBuilder sb, String value) {
        if (value != null) sb.append(value);
        return sb;
    }

    public static final String valueOf(String value, String defaultValue) {
        if (value != null) return value;
        else return defaultValue;
    }

    public static final String valueOf(Number value, String defaultValue) {
        if (value != null) return value.toString();
        else return defaultValue;
    }

    public static final String valueOf(Boolean value, String defaultValue) {
        if (value != null) return value.toString();
        else return defaultValue;
    }

    public static final String valueOf(String value) {
        return value;
    }

    public static final String valueOf(Number value) {
        return valueOf(value, null);
    }

    public static final String valueOf(Boolean value) {
        return valueOf(value, null);
    }

    /** Tester l'égalité de deux chaines. */
    public static final boolean equals(String s1, String s2) {
        return s1 == s2 || (s1 != null && s1.equals(s2));
    }

    /** Tester l'égalité de deux chaines sans tenir compte de la casse. */
    public static final boolean equalsIgnoreCase(String s1, String s2) {
        return (s1 == s2) || (s1 != null && s1.equalsIgnoreCase(s2));
    }

    /** Comparer deux chaines. */
    public static final int compare(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }

    /**
     * Comparer deux chaines sans tenir compte de la casse, en considérant qu'une valeur nulle est
     * toujours plus "petite" qu'une valeur non nulle.
     */
    public static final int compareIgnoreCase(String s1, String s2) {
        if (s1 == s2) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareToIgnoreCase(s2);
    }

    public static final int hashCode(String s) {
        if (s == null) return 0;
        else return s.hashCode();
    }

    /** Si str est <code>null</code> ou si str est une chaine vide "", retourner <code>true</code>. */
    public static final boolean isempty(String str) {
        return str == null || str.length() == 0;
    }
}
