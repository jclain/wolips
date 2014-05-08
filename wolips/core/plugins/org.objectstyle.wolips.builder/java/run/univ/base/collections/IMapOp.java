/* IMapOp.java
 * Created on 22 oct. 2004
 */
package run.univ.base.collections;

/**
 * Une opération à utiliser avec {@link run.univ.base.collections.MapIterator}.
 * 
 * @author jclain
 */
public interface IMapOp<S, D> {
    /** mapper l'objet. */
    public D map(S value);
}