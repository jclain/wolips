/* LazyIterator.java
 * Created on 9 mai 2005
 */
package run.univ.base.collections;

import java.util.Iterator;

import run.univ.base.Exc;
import run.univ.base.util.exc.RunUnivRuntimeException;

/**
 * Un itérateur sur une collection qui n'appelle la méthode iterator() de la collection que
 * lorsqu'on commence à utiliser les méthodes de l'itérateur.
 * 
 * @author jclain
 */
public final class LazyIterator<E> extends AbstractIterator<E> {
    public LazyIterator(Iterable<E> itop) {
        this.iterable = itop;
    }

    private Iterable<E> iterable;

    private Iterator<E> it;

    private Iterator<E> getIt() throws Exception {
        if (it == null && iterable != null) it = iterable.iterator();
        if (it == null) it = EmptyIterator.getInstance();
        return it;
    }

    public boolean hasNext() {
        Iterator<E> it;
        try {
            it = getIt();
        } catch (Exception e) {
            return false;
        }
        return it.hasNext();
    }

    public E next() {
        Iterator<E> it;
        try {
            it = getIt();
        } catch (Exception e) {
            throw Exc.NoSuchElementException(null, e);
        }
        return it.next();
    }

    public void remove() {
        Iterator<E> it;
        try {
            it = getIt();
        } catch (Exception e) {
            throw new RunUnivRuntimeException(Exc.unwrap(e, null));
        }
        it.remove();
    }

    public void close() throws Exception {
        if (it != null && it instanceof AbstractIterator) {
            ((AbstractIterator<E>)it).close();
        }
        it = null;
    }
}
