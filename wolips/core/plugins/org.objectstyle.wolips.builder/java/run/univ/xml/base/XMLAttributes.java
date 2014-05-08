/* XMLAttributes.java
 * Created on 16 nov. 2004
 */
package run.univ.xml.base;

import java.util.ArrayList;

import run.univ.Str;

/**
 * Un ensemble d'attributs d'un tag XML.
 * 
 * @author jclain
 */
public interface XMLAttributes {
    public int count();

    public XMLAttributes add(String name, String value);

    public XMLAttributes add(int pos, String name, String value);

    public int indexOf(String name);

    public String getName(int index);

    public String getValue(int index);

    public static class Implementation implements XMLAttributes {
        protected ArrayList attrs;

        public Implementation() {
            attrs = new ArrayList();
        }

        public Implementation(String name, String value) {
            this();
            add(name, value);
        }

        public Implementation(XMLAttributes attrs) {
            this();
            if (attrs != null) {
                for (int i = 0; i < attrs.count(); i++) {
                    add(attrs.getName(i), attrs.getValue(i));
                }
            }
        }

        public XMLAttributes add(String name, String value) {
            attrs.add(name);
            attrs.add(value);
            return this;
        }

        public XMLAttributes add(int pos, String name, String value) {
            attrs.add(pos, name);
            attrs.add(pos + 1, value);
            return this;
        }

        public int count() {
            return attrs.size() / 2;
        }

        public int indexOf(String name) {
            int max = attrs.size();
            for (int i = 0; i < max; i += 2) {
                if (Str.equals((String)attrs.get(i), name)) {
                    return i / 2;
                }
            }
            return -1;
        }

        public String getName(int index) {
            return (String)attrs.get(index * 2);
        }

        public String getValue(int index) {
            return (String)attrs.get(index * 2 + 1);
        }
    }

    public static class FromSAXAttributes implements XMLAttributes {
        /**
         * Faire une copie de travail au lieu de faire un "proxy". En effet, l'instance attrs peut
         * être partagée, et l'on peut ne plus y avoir accès si on ne l'utilise pas tout de suite.
         */
        public static XMLAttributes copyOf(org.xml.sax.Attributes saxattrs) {
            XMLAttributes attrs = new Implementation();
            int max = saxattrs.getLength();
            for (int i = 0; i < max; i++) {
                attrs.add(saxattrs.getQName(i), saxattrs.getValue(i));
            }
            return attrs;
        }

        public FromSAXAttributes(org.xml.sax.Attributes attrs) {
            this.attrs = attrs;
        }

        protected org.xml.sax.Attributes attrs;

        public XMLAttributes add(String name, String value) {
            throw new UnsupportedOperationException();
        }

        public XMLAttributes add(int pos, String name, String value) {
            throw new UnsupportedOperationException();
        }

        public int count() {
            return attrs != null? attrs.getLength(): 0;
        }

        public int indexOf(String name) {
            return attrs.getIndex(name);
        }

        public String getName(int index) {
            return attrs.getQName(index);
        }

        public String getValue(int index) {
            return attrs.getValue(index);
        }
    }
}