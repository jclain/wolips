/* AbstractHTMLBuilder.java
 * Created on 13 avr. 2010
 */
package run.univ.wosrc.rubasem;

import static run.univ.wosrc.rubasem.EHTMLTag.HEAD;
import static run.univ.wosrc.rubasem.EHTMLTag.META;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


/**
 * @author jclain
 */
public abstract class AbstractHTMLBuilder extends AbstractXMLBuilder {
    public AbstractHTMLBuilder(String nl, boolean indent, String encoding) {
        super(nl, indent, encoding);
    }

    public AbstractHTMLBuilder() {
        this(null, false, null);
    }

    @Override
    protected BuilderStrategy getXMLBuilderStragegy(boolean indent) {
        if (indent) return new IndentedHTMLStrategy();
        else return new RawHTMLStrategy();
    }

    // ------------------------------------------------------------------------
    // Stratégie pour de l'HTML non indenté

    private static boolean isValidTag(String name) {
        return EHTMLTag.isValid(name);
    }

    public static final boolean isContentTag(String name) {
        return isValidTag(name) && EHTMLTag.get(name).isContent();
    }

    public static final boolean isBlockTag(String name) {
        return !isValidTag(name) || EHTMLTag.get(name).isBlock();
    }

    public static final boolean isInlineTag(String name) {
        return isValidTag(name) && !EHTMLTag.get(name).isBlock();
    }

    public static final boolean isEmptyTag(String name) {
        return isValidTag(name) && EHTMLTag.get(name).isEmpty();
    }

    public static final boolean isTagContainsInline(String name) {
        return isValidTag(name) && EHTMLTag.get(name).isContainsInline();
    }

    protected class RawHTMLTag extends Tag implements Cloneable {
        public RawHTMLTag(String name, XMLAttributes attrs, boolean start, RawHTMLTag parent) {
            super(name, attrs, start, false, parent);
        }

        @Override
        public RawHTMLTag clone() {
            try {
                return (RawHTMLTag)super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        private boolean commited;

        public boolean isCommited() {
            return commited;
        }

        public void setCommited() {
            commited = true;
        }

        private boolean textChildren;

        public boolean hasTextChildren() {
            return textChildren;
        }

        public void setHasTextChildren() {
            textChildren = true;
        }

        private boolean elementChildren;

        public void setHasElementChildren() {
            elementChildren = true;
        }

        public boolean hasChildren() {
            return textChildren || elementChildren;
        }

        public boolean hasElementChildrenOnly() {
            return elementChildren && !textChildren;
        }

        @Override
        public String getChildrenPrefix() {
            String name = getName();
            if (isValidTag(name)) {
                if (hasTextChildren()) return null;
                else if (isInlineTag(name)) return null;
                else if (isBlockTag(name)) return getNl();
            }
            // S'il y a des enfants de type texte, ne pas séparer passer à la ligne
            if (hasElementChildrenOnly()) return getNl();
            else return null;

        }
    }

    protected class RawHTMLStrategy extends BuilderStrategy {
        public RawHTMLStrategy() {
            elements = new LinkedList<RawHTMLTag>();
            commitables = new ArrayList<Tag>();
            endables = new ArrayList<Tag>();
        }

        private LinkedList<RawHTMLTag> elements;

        private RawHTMLTag currentTag() {
            if (elements.isEmpty()) return null;
            else return elements.getLast();
        }

        private void commitElements() {
            for (RawHTMLTag elem : elements) {
                if (!elem.isCommited()) {
                    commitables.add(elem);
                    elem.setCommited();
                }
            }
        }

        private ArrayList<Tag> commitables;

        public Collection<Tag> getCommitables() {
            return commitables;
        }

        protected void wroteCommitables() {
            commitables.clear();
        }

        private ArrayList<Tag> endables;

        public Collection<Tag> getEndables() {
            return endables;
        }

        protected void wroteEndables() {
            endables.clear();
        }

        private boolean inHead;

        public boolean isInHead() {
            return inHead;
        }

        public void _start(String name, XMLAttributes attrs) {
            if (HEAD.is(name)) inHead = true;
            for (RawHTMLTag elem : elements) {
                elem.setHasElementChildren();
            }
            RawHTMLTag parent = currentTag();
            elements.add(new RawHTMLTag(name, attrs, true, parent));
            writeCommitables();
        }

        @Override
        public void _addattrs(XMLAttributes attrs) {
            RawHTMLTag lastTag = currentTag();
            if (lastTag == null || lastTag.isCommited()) throw noUncommitedTagFoundException();
            lastTag.getAttrs().addAll(attrs);
        }

        public void end(String name) {
            RawHTMLTag ended = elements.removeLast();
            commitElements();
            if (ended.hasChildren()) {
                // l'écriture des enfants à déjà commité le tag ouvrant
                ended.setStart(false);
                endables.add(ended);
            } else if (!isEmptyTag(name)) {
                commitables.add(ended);
                ended = ended.clone();
                ended.setStart(false);
                endables.add(ended);
            } else {
                ended.setEmpty(true);
                commitables.add(ended);
            }
            writeCommitables();
            writeEndables();
            if (HEAD.is(name)) inHead = false;
        }

        public void nl() {
            text();
        }

        public void text() {
            RawHTMLTag currentTag = currentTag();
            if (currentTag != null) currentTag.setHasTextChildren();
            commitElements();
            writeCommitables();
        }

        public void comment() {
            RawHTMLTag currentTag = currentTag();
            if (currentTag != null) currentTag.setHasElementChildren();
            commitElements();
            writeCommitables();
        }

        public void flush() {
            commitElements();
            writeCommitables();
        }
    }

    // ------------------------------------------------------------------------
    // Stratégie pour de l'HTML indenté

    protected class IndentedHTMLStrategy extends RawHTMLStrategy {
        // pour le moment, comme RawHTMLStrategy
    }

    // ------------------------------------------------------------------------
    // Génération des éléments

    private boolean inHead, addMeta;

    private static final String HTTP_EQUIV = "http-equiv", CONTENT = "content",
            CONTENT_TYPE = "Content-Type", HTML_ENCODING_PREFIX = "text/html; charset=";

    public void start(String name, XMLAttributes attrs) {
        super.start(name, attrs);
        if (HEAD.is(name)) {
            inHead = true;
            addMeta = true;
        } else if (inHead && META.is(name) && attrs != null) {
            int httpEquivIndex = attrs.indexOf(HTTP_EQUIV);
            if (httpEquivIndex != -1) {
                String httpEquiv = attrs.getValue(httpEquivIndex);
                if (Str.equalsIgnoreCase(httpEquiv, CONTENT_TYPE)) {
                    addMeta = false;
                }
            }
        }
    }

    public void end(String name) {
        if (HEAD.is(name)) {
            if (addMeta) {
                String meta = META.getName();
                XMLAttributes attrs = new XMLAttributes();
                attrs.add(HTTP_EQUIV, CONTENT_TYPE);
                attrs.add(CONTENT, HTML_ENCODING_PREFIX + getEncoding());
                start(meta, attrs);
                end(meta);
            }
            inHead = false;
        }
        super.end(name);
    }

    @Override
    public void cdata(String text) {
        xbs.text();
        write(text);
    }
}
