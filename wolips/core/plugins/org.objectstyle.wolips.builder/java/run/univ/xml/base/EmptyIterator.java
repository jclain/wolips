/* EmptyIterator.java
 * Created on 12 janv. 2009
 */
package run.univ.xml.base;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator implements Iterator {
    private static EmptyIterator instance;

    public static final EmptyIterator getInstance() {
        if (instance == null) instance = new EmptyIterator();
        return instance;
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
