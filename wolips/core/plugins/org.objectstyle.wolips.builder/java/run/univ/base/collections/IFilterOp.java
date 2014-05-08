/* IFilterOp.java
 * Created on 22 oct. 2004
 */
package run.univ.base.collections;

/**
 * Un filtre à utiliser avec {@link run.univ.base.collections.FilterIterator}.
 * 
 * @author jclain
 */
public interface IFilterOp<T> {
    /** retourner true si l'objet doit être gardé, false sinon. */
    public boolean accept(T value);
}