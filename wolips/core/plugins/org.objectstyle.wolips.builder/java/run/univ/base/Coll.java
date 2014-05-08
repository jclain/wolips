/* Coll.java
 * Created on 17 févr. 2010
 */
package run.univ.base;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Des outils pour gérer les collections.
 * 
 * @author jclain
 */
public class Coll {
    /**
     * Retourner une représentation sous forme de chaine d'une collection. Les éléments sont séparés
     * par la chaine sep, et sont entourés de start et end. Par défaut, sep est ", ".
     */
    public static final String join(Collection<?> coll, String start, String sep, String end) {
        if (coll == null) return null;
        if (sep == null) sep = ", ";

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        if (start != null) sb.append(start);
        for (Object elem : coll) {
            if (first) first = false;
            else sb.append(sep);
            sb.append(Str.valueOf(elem));
        }
        if (end != null) sb.append(end);
        return sb.toString();
    }

    public static final String join(Collection<?> coll, String sep) {
        return join(coll, null, sep, null);
    }

    public static final String join(Collection<?> coll) {
        return join(coll, null, null, null);
    }

    /**
     * Retourner une représentation sous forme de chaine d'un dictionnaire. Les éléments sont de la
     * forme "key vsep value", sont séparés par la chaine sep, et sont entourés de start et end. Par
     * défaut, vsep est ": " et sep est ", ".
     */
    public static final String join(Map<?, ?> map, String start, String vsep, String sep, String end) {
        if (map == null) return null;
        if (vsep == null) vsep = ": ";
        if (sep == null) sep = ", ";

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        if (start != null) sb.append(start);
        for (Entry<?, ?> elem : map.entrySet()) {
            if (first) first = false;
            else sb.append(sep);
            sb.append(Str.valueOf(elem.getKey()));
            sb.append(vsep);
            sb.append(Str.valueOf(elem.getValue()));
        }
        if (end != null) sb.append(end);
        return sb.toString();
    }

    public static final String join(Map<?, ?> map, String vsep, String sep) {
        return join(map, null, vsep, sep, null);
    }

    public static final String join(Map<?, ?> map) {
        return join(map, null, null, null, null);
    }

    /**
     * Vérifier si source contient tous les éléments de ref.
     * <p>
     * Si source est <code>null</code> ou vide, retourner <code>true</code>. Si ref est
     * <code>null</code> ou vide, retourner <code>false</code>.
     * </p>
     */
    public static final <E> boolean containsAll(Collection<E> source, Collection<? extends E> ref) {
        if (source == null || source.isEmpty()) return true;
        if (ref == null || ref.isEmpty()) return false;
        for (E element : source) {
            if (!ref.contains(element)) return false;
        }
        return true;
    }

    /**
     * Vérifier si source contient au moins un des éléments de ref.
     * <p>
     * Si source est <code>null</code> ou vide, retourner <code>false</code>. Si ref est
     * <code>null</code> ou vide, retourner <code>true</code>.
     * </p>
     */
    public static final <E> boolean containsAny(Collection<E> source, Collection<? extends E> ref) {
        if (source == null || source.isEmpty()) return false;
        if (ref == null || ref.isEmpty()) return true;
        for (E element : source) {
            if (ref.contains(element)) return true;
        }
        return false;
    }

    /** Tester l'égalité de deux collections. */
    public static final <E> boolean equals(Collection<E> c1, Collection<E> c2) {
        return c1 == c2 || (c1 != null && c1.equals(c2));
    }

    /** Tester l'égalité de deux dictionnaires. */
    public static final <K, V> boolean equals(Map<K, V> c1, Map<K, V> c2) {
        return c1 == c2 || (c1 != null && c1.equals(c2));
    }

    /** Retourner un tableau avec les éléments de la collection. */
    @SuppressWarnings("unchecked")
    public static final <E> E[] toArray(Collection<E> coll) {
        if (coll == null) return null;
        else return (E[])coll.toArray();
    }
}
