/* Arr.java
 * Created on 23 oct. 2009
 */
package run.univ.base;

import java.util.Arrays;

/**
 * Des outils pour gérer les tableaux.
 * 
 * @author jclain
 */
public class Arr {
    /**
     * Obtenir le premier élément d'un tableau, ou <code>null</code> si le tableau est
     * <code>null</code> ou vide.
     */
    public static final <T> T firstOf(T[] items) {
        if (items != null && items.length > 0) return items[0];
        else return null;
    }

    /**
     * Retourner une représentation sous forme de chaine d'un tableau. Les éléments sont séparés par
     * la chaine sep, et sont entourés de start et end. Par défaut, sep est ", ".
     */
    public static final String join(Object[] array, String start, String sep, String end) {
        if (array == null) return null;
        else return Coll.join(Arrays.asList(array), start, sep, end);
    }

    public static final String join(Object[] array, String sep) {
        return join(array, null, sep, null);
    }

    public static final String join(Object[] array) {
        return join(array, null, null, null);
    }
}
