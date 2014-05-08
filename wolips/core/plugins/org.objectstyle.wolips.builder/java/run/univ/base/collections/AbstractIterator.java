/* AbstractIterator.java
 * Created on 25 mai 2005
 */
package run.univ.base.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import run.univ.base.Coll;

/**
 * @author jclain
 */
public abstract class AbstractIterator<E> implements Iterator<E> {
    /** Libérer les resources utilisées par cet itérateur. */
    public void close() throws Exception {
    }

    public <D> MapIterator<E, D> map(IMapOp<E, D> mapOp) {
        return new MapIterator<E, D>(this, mapOp);
    }

    public FilterIterator<E> filter(IFilterOp<E> filterOp) {
        return new FilterIterator<E>(this, filterOp);
    }

    public List<E> toList(List<E> destList) {
        if (destList == null) destList = new ArrayList<E>();
        try {
            while (hasNext())
                destList.add(next());
            return destList;
        } finally {
            try {
                close();
            } catch (Exception e) {
            }
        }
    }

    public List<E> toList() {
        return toList(null);
    }

    public E[] toArray() {
        return Coll.toArray(toList());
    }

    public E[] toArray(E[] a) {
        return toList().toArray(a);
    }

    public void iterate() {
        try {
            while (hasNext())
                next();
        } finally {
            try {
                close();
            } catch (Exception e) {
            }
        }
    }
}
