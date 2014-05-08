//
//  Bool.java
//  urBase
//
//  Created by Jephte CLAIN on Thu Mar 04 2004.
//  Copyright (c) 2004 Universite de la Reunion. All rights reserved.
//

package run.univ;

/** Des fonctions utilitaires sur les Boolean */
public class Bool {
    /**
     * Indiquer si une chaine représente "VRAI".
     * 
     * @param s une chaine de caractères
     * @return true si s est une valeur "VRAI", false sinon (y compris si s est nulle).
     */
    public static final boolean isTrue(String s) {
        if (s == null) return false;
        if (s.equals("y") || s.equals("Y") || s.equals("o") || s.equals("O") || s.equals("t")
                || s.equals("T") || s.equals("1")) return true;
        s = s.trim().toLowerCase();
        return s.equals("y") || s.equals("o") || s.equals("t") || s.equals("1") || s.equals("yes")
                || s.equals("oui") || s.equals("true") || s.equals("vrai") || s.equals("on");
    }

    /**
     * Indiquer si une chaine représente "FAUX".
     * 
     * @param s une chaine de caractère
     * @return true si s est une valeur "FAUX", false sinon (y compris si s est nulle).
     */
    public static final boolean isFalse(String s) {
        if (s == null) return false;
        if (s.equals("n") || s.equals("N") || s.equals("f") || s.equals("F") || s.equals("0"))
            return true;
        s = s.trim().toLowerCase();
        return s.equals("n") || s.equals("f") || s.equals("0") || s.equals("no") || s.equals("non")
                || s.equals("false") || s.equals("faux") || s.equals("off");
    }

    /**
     * Obtenir la valeur booléenne d'un objet en tant qu'instance de la classe Boolean.
     * <p>
     * note: si o est une instance de String, et qu'il ne représente pas une valeur "VRAI" ou
     * "FAUX", on retourne la valeur defaultValue.
     * </p>
     * 
     * @param o un objet quelconque
     * @return Boolean.TRUE si l'objet représente vrai, Boolean.FALSE si l'objet représente faux,
     *         defaultValue si l'objet est nul ou invalide.
     */
    public static final Boolean valueOf(Object o, Boolean defaultValue) {
        if (o == null) return defaultValue;

        if (o instanceof Boolean) return (Boolean)o;
        if (o instanceof String) {
            String s = (String)o;
            if (isTrue(s)) return Boolean.TRUE;
            if (isFalse(s)) return Boolean.FALSE;
            return defaultValue;
        }
        if (o instanceof Number) return new Boolean(((Number)o).intValue() != 0);
        // une instance d'un objet quelconque est considérée comme vraie
        return Boolean.TRUE;
    }

    /**
     * Obtenir la valeur booléenne d'un objet en tant qu'instance de la classe Boolean.
     * <p>
     * note: cette méthode ne retourne jamais null.
     * </p>
     * 
     * @param o un objet quelconque
     * @return Boolean.TRUE si l'objet représente vrai, Boolean.FALSE si l'objet représente faux ou
     *         est nul.
     */
    public static final Boolean valueOf(Object o) {
        return valueOf(o, Boolean.FALSE);
    }

    /**
     * Retourner la valeur booléenne d'un objet en tant que boolean.
     * 
     * @param o un objet quelconque.
     * @return true si l'objet est vrai, false si l'objet est faux ou nul.
     */
    public static final boolean booleanValue(Object o, boolean defaultValue) {
        return valueOf(o, new Boolean(defaultValue)).booleanValue();
    }

    /**
     * Retourner la valeur booléenne d'un objet en tant que boolean.
     * 
     * @param o un objet quelconque.
     * @return true si l'objet est vrai, false si l'objet est faux ou nul.
     */
    public static final boolean booleanValue(Object o) {
        return valueOf(o, Boolean.FALSE).booleanValue();
    }

    /** Comparer deux valeurs booléennes. On assume que null < false < true. */
    public static final int compare(Boolean b1, Boolean b2) {
        if (b1 == null && b2 == null) return 0;
        if (b1 == null) return -1;
        if (b2 == null) return 1;
        return compare(b1.booleanValue(), b2.booleanValue());
    }

    /** Comparer deux valeurs booléennes. On assume que false < true. */
    public static final int compare(boolean b1, boolean b2) {
        if (b1 == b2) return 0;
        return b1 == false? -1: 1;
    }
}