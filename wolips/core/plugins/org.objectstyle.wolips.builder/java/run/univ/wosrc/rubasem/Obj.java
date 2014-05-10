/* Obj.java
 * Created on 04 mars 2005
 */
package run.univ.wosrc.rubasem;

/**
 * Des fonctions utilitaires pour gérer les objets.
 * <p>
 * Dans certains frameworks comme WebObjects, on ne peut insérer des valeurs nulles dans des
 * structures telles que {@link com.webobjects.foundation.NSDictionary} ou
 * {@link com.webobjects.foundation.NSArray}. WebObjects offre une valeur alternative qui représente
 * une valeur nulle. Cette classe offre un support pour ce genre d'objet.
 * </p>
 * 
 * @author jclain
 */
public class Obj {
    /** un objet dont la valeur doit être considérée comme équivalente à <code>null</code>. */
    private static Object ALT_NULL = null;

    /**
     * Méthode servant à spécifier la valeur alternative pour <code>null</code>. Il faut l'utiliser
     * ainsi:
     * 
     * <pre>
     * new Obj() {{
     *     setAltNull(MY_NULL);
     * }};
     * </pre>
     */
    protected static final void setAltNull(Object altNull) {
        Obj.ALT_NULL = altNull;
    }

    /**
     * Retourner un objet qui peut être utilisé pour représenter la valeur <code>null</code>.
     */
    public static final Object nullValue() {
        return ALT_NULL;
    }

    /**
     * Retourner vrai si un objet vaut <code>null</code> ou {@link #nullValue()}.
     */
    public static final boolean isNull(Object o) {
        return o == null || o == ALT_NULL;
    }

    /**
     * Tester si l'objet est nul avec {@link #isNull(Object)}: s'il est non nul, le retourner
     * inchangé, sinon retourner {@link #nullValue()}.
     */
    public static final Object valueOf(Object o) {
        return isNull(o)? ALT_NULL: o;
    }

    /**
     * Tester si l'objet est nul avec {@link #isNull(Object)}: s'il est non nul, le retourner
     * inchangé, sinon retourner <code>null</code>.
     */
    public static final Object valueOrNull(Object o) {
        return isNull(o)? null: o;
    }

    /**
     * Tester si l'objet est nul avec {@link #isNull(Object)}: s'il est non nul, le retourner
     * inchangé, sinon retourner defaultValue.
     */
    public static final Object valueOrDefault(Object o, Object defaultValue) {
        return isNull(o)? defaultValue: o;
    }

    /**
     * Tester l'égalité de deux objets, en considérant {@link #nullValue()} et <code>null</code>
     * comme égaux.
     */
    public static final boolean equals(Object o1, Object o2) {
        if (isNull(o1)) o1 = null;
        if (isNull(o2)) o2 = null;
        return (o1 == o2) || (o1 != null && o1.equals(o2));
    }

    /** Comparer deux objets éventuellement nuls. */
    public static final <T extends Comparable<? super T>> int compare(T o1, T o2) {
        if (o1 == null && o2 == null) return 0;
        else if (o1 == null) return -1;
        else if (o2 == null) return 1;
        else return o1.compareTo(o2);
    }

    public static final int hashCode(Object o) {
        if (o == null) return 0;
        else return o.hashCode();
    }
}