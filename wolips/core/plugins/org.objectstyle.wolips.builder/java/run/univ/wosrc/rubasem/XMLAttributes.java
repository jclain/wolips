/* XMLAttributes.java
 * Created on 12 avr. 2010
 */
package run.univ.wosrc.rubasem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.xml.sax.Attributes;


/**
 * Un ensemble d'attributs d'un élément XML.
 * 
 * @author jclain
 */
public class XMLAttributes implements Collection<XMLAttribute> {
    public XMLAttributes() {
        backend = new ArrayList<String>();
    }

    public XMLAttributes(XMLAttribute attr) {
        this();
        add(attr);
    }

    public XMLAttributes(XMLAttributes attrs) {
        this();
        if (attrs != null) {
            int count = attrs.size();
            for (int i = 0; i < count; i++) {
                add(attrs.getName(i), attrs.getValue(i));
            }
        }
    }

    public XMLAttributes(Attributes saxattrs) {
        this();
        if (saxattrs != null) {
            int count = saxattrs.getLength();
            for (int i = 0; i < count; i++) {
                add(saxattrs.getQName(i), saxattrs.getValue(i));
            }
        }
    }

    public XMLAttributes(String name, String value) {
        this();
        add(name, value);
    }

    protected ArrayList<String> backend;

    public int size() {
        return backend.size() / 2;
    }

    public boolean isEmpty() {
        return backend.isEmpty();
    }

    private int wrapBefore(int index) {
        int size = size();
        if (index < 0) index = Intervals.wrapBefore(index, size);
        if (index >= size) throw new ArrayIndexOutOfBoundsException(index);
        return index;
    }

    public String getName(int index) {
        return backend.get(2 * wrapBefore(index));
    }

    public String getValue(int index) {
        return backend.get(2 * wrapBefore(index) + 1);
    }

    public XMLAttribute get(int index) {
        index = wrapBefore(index);
        String name = backend.get(2 * index);
        String value = backend.get(2 * index + 1);
        return new XMLAttribute(name, value);
    }

    public void set(int index, String name, String value) {
        value = XMLAttribute.checkName(name, value);
        index = 2 * wrapBefore(index);
        backend.set(index, name);
        backend.set(index + 1, value);
    }

    public void set(int index, XMLAttribute attr) {
        if (attr != null) set(index, attr.getName(), attr.getValue());
    }

    public boolean contains(String name) {
        if (name != null) {
            int size = backend.size();
            for (int i = 0; i < size; i += 2) {
                if (Str.equals(backend.get(i), name)) return true;
            }
        }
        return false;
    }

    public boolean contains(XMLAttribute attr) {
        if (attr != null) {
            int size = backend.size();
            for (int i = 0; i < size; i += 2) {
                if (attr.equals(backend.get(i), backend.get(i + 1))) return true;
            }
        }
        return false;
    }

    public boolean contains(Object obj) {
        if (obj instanceof String) return contains((String)obj);
        else if (obj instanceof XMLAttribute) return contains((XMLAttribute)obj);
        else return false;
    }

    public boolean containsAll(Collection<?> coll) {
        if (coll == null) return false;
        for (Object item : coll) {
            if (!contains(item)) return false;
        }
        return true;
    }

    public int indexOf(String name) {
        if (name != null) {
            int size = backend.size();
            for (int i = 0; i < size; i += 2) {
                if (Str.equals(backend.get(i), name)) return i / 2;
            }
        }
        return -1;
    }

    public int indexOf(XMLAttribute attr) {
        if (attr != null) {
            int size = backend.size();
            for (int i = 0; i < size; i += 2) {
                if (attr.equals(backend.get(i), backend.get(i + 1))) return i / 2;
            }
        }
        return -1;
    }

    public int indexOf(Object obj) {
        if (obj instanceof String) return indexOf((String)obj);
        else if (obj instanceof XMLAttribute) return indexOf((XMLAttribute)obj);
        else return -1;
    }

    public boolean add(String name, String value) {
        value = XMLAttribute.checkName(name, value);
        backend.add(name);
        backend.add(value);
        return true;
    }

    public boolean add(XMLAttribute attr) {
        if (attr == null) return false;
        else return add(attr.getName(), attr.getValue());
    }

    public void add(int index, String name, String value) {
        value = XMLAttribute.checkName(name, value);
        index = 2 * wrapBefore(index);
        backend.add(index, name);
        backend.add(index + 1, value);
    }

    public void add(int index, XMLAttribute attr) {
        if (attr != null) add(index, attr.getName(), attr.getValue());
    }

    public boolean addAll(Collection<? extends XMLAttribute> attrs) {
        boolean added = false;
        if (attrs != null) {
            for (XMLAttribute attr : attrs) {
                added |= add(attr);
            }
        }
        return added;
    }

    public boolean remove(Object obj) {
        int index = indexOf(obj);
        if (index != -1) {
            index = 2 * index;
            backend.remove(index);
            backend.remove(index);
            return true;
        }
        return false;
    }

    public XMLAttribute remove(int index) {
        index = wrapBefore(index);
        XMLAttribute oldAttr = get(index);
        index = 2 * index;
        backend.remove(index);
        backend.remove(index);
        return oldAttr;
    }

    public boolean removeAll(Collection<?> attrs) {
        boolean removed = false;
        if (attrs != null) {
            for (Object item : attrs) {
                removed |= remove(item);
            }
        }
        return removed;
    }

    public boolean retainAll(Collection<?> attrs) {
        if (attrs == null) throw new NullPointerException("attrs should not be null");
        ArrayList<XMLAttribute> attrsToRemove = new ArrayList<XMLAttribute>();
        for (XMLAttribute attr : this) {
            if (!attrs.contains(attr)) attrsToRemove.add(attr);
        }
        return removeAll(attrsToRemove);
    }

    public void clear() {
        backend.clear();
    }

    public final class AttributeIterator implements Iterator<XMLAttribute> {
        public AttributeIterator() {
            index = 0;
            removeIndex = -1;
        }

        private int index, removeIndex;

        public boolean hasNext() {
            return index < XMLAttributes.this.size();
        }

        public XMLAttribute next() {
            if (index == size()) throw new NoSuchElementException();
            XMLAttribute attr = XMLAttributes.this.get(index);
            removeIndex = index;
            index++;
            return attr;
        }

        public void remove() {
            if (removeIndex == -1) throw new IllegalStateException();
            XMLAttributes.this.remove(removeIndex);
            removeIndex = -1;
        }
    }

    public Iterator<XMLAttribute> iterator() {
        return new AttributeIterator();
    }

    public Object[] toArray() {
        int count = size();
        XMLAttribute[] attrs = new XMLAttribute[count];
        for (int i = 0; i < count; i++) {
            attrs[i] = get(i);
        }
        return attrs;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] dest) {
        XMLAttribute[] attrs;
        int size = size();
        if (dest == null || dest.length < size) attrs = new XMLAttribute[size];
        else attrs = (XMLAttribute[])dest;
        for (int i = 0; i < size; i++) {
            attrs[i] = get(i);
        }
        return (T[])attrs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XMLAttributes) {
            XMLAttributes other = (XMLAttributes)obj;
            int size = size();
            if (size != other.size()) return false;
            for (int i = 0; i < size; i++) {
                if (!Str.equals(getName(i), other.getName(i))
                        || !Str.equals(getValue(i), other.getValue(i))) return false;
            }
            return true;
        } else return false;
    }
}
