/* MapIterator.java
 * Created on 4 septembre 2004, 15:27
 */

package run.univ.base.collections;

import java.util.Collection;
import java.util.Iterator;


/**
 * @author jclain
 */

public final class MapIterator<S, E> extends AbstractIterator<E> {
    public MapIterator(Iterator<S> it, IMapOp<S, E> mapOp) {
        if (mapOp == null) throw new NullPointerException("mapOp ne doit pas être nul");
        this.it = it;
        this.mapOp = mapOp;
    }

    public MapIterator(Collection<S> coll, IMapOp<S, E> mapOp) {
        this(new LazyIterator<S>(coll), mapOp);
    }

    private Iterator<S> it;

    private IMapOp<S, E> mapOp;

    public boolean hasNext() {
        return it.hasNext();
    }

    public E next() {
        return mapOp.map(it.next());
    }

    public void remove() {
        it.remove();
    }
}