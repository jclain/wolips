/* FilterIterator.java
 * Created on 22 oct. 2004
 */
package run.univ.base.collections;

import java.util.Iterator;

/**
 * @author jclain
 */
public class FilterIterator<E> extends AbstractIterator<E> {
    private Iterator<E> it;

    private IFilterOp<E> filterOp;

    /** un élément est-il disponible. */
    private boolean available;

    /** l'élément suivant, si available==true. */
    private E next;

    public FilterIterator(Iterator<E> it, IFilterOp<E> filterOp) {
        this.it = it;
        this.filterOp = filterOp;
        this.available = false;
    }

    public boolean hasNext() {
        if (available) {
            return true;
        } else {
            if (!it.hasNext()) return false;
            do {
                next = it.next();
                if (filterOp.accept(next)) {
                    available = true;
                    return true;
                }
            } while (it.hasNext());
            return false;
        }
    }

    public E next() {
        if (available) {
            available = false;
            return next;
        } else {
            while (!filterOp.accept(next = it.next())) {
            }
            return next;
        }
    }

    public void remove() {
        if (available) available = false;
        it.remove();
    }
}