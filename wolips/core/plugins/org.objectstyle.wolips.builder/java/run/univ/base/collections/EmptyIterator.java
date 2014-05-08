/* EmptyIterator.java
 * Created on 17 nov. 2004
 */
package run.univ.base.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Un itérateur vide.
 * 
 * @author jclain
 */
public final class EmptyIterator extends AbstractIterator<Object> {
    private static EmptyIterator instance;

    @SuppressWarnings("unchecked")
    public static final <E> Iterator<E> getInstance() {
        if (instance == null) instance = new EmptyIterator();
        return (AbstractIterator<E>)instance;
    }

    public boolean hasNext() {
        return false;
    }

    public Object next() {
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
