/* Intervals.java
 * Created on 26 nov. 07
 */
package run.univ.wosrc.rubasem;

/**
 * Des méthodes pour gérer les indices dans des tableaux.
 * 
 * @author jclain
 */
public class Intervals {
    /**
     * Modifier index pour que la valeur soit dans l'intervalle [0, size].
     * <p>
     * Cette méthode est faite pour les scénario où on doit ajouter dans une liste. Une valeur de
     * retour de 0 à size-1 signifie que l'on doit ajouter avant l'élément spécifié. Une valeur de
     * retour égale à size signifie que l'on doit ajouter à la fin de la liste.
     * </p>
     */
    public static final int wrapAt(int index, int size) {
        if (size < 0) return index;
        else if (size == 0) return 0;

        int size1 = size + 1;
        while (index < 0)
            index += size1;
        if (index > size) index = size;
        return index;
    }

    /**
     * Modifier index pour que la valeur soit dans l'intervalle [0, size-1].
     * <p>
     * Cette méthode est faite pour les scénario où on doit insérer dans une liste, ou accéder à un
     * élément par son index.
     * </p>
     */
    public static final int wrapBefore(int index, int size) {
        if (size <= 0) return index;

        while (index < 0)
            index += size;
        if (index >= size) index = size - 1;
        return index;
    }
}
