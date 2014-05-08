/* RecursiveHandler.java
 * Created on 14 mars 2006
 */
package run.univ.xml;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Un handler qui peut récursivement déléguer le contrôle à d'autres handlers.
 * 
 * @see rubase.xml.ToplevelHandler
 * @author jclain
 */
public class RecursiveHandler extends DefaultHandler {
    /**
     * instance principale de RecursiveHandler, qui pilote les autres. vaut null si cette instance
     * est le toplevel.
     */
    private ToplevelHandler toplevel;

    protected ToplevelHandler getToplevel() {
        return toplevel;
    }

    protected void setToplevel(ToplevelHandler toplevel) {
        this.toplevel = toplevel;
    }

    public LinkedList getStack() {
        return getToplevel().getStack();
    }

    /** handler parent. c'est celui qui a déclenché la création de cette instance. */
    private RecursiveHandler parentHandler;

    public RecursiveHandler getParentHandler() {
        return parentHandler;
    }

    protected void setParentHandler(RecursiveHandler parent) {
        this.parentHandler = parent;
    }

    public RecursiveHandler getFirstHandler() {
        return getToplevel().getFirstHandler();
    }

    /** @return true si ce handler est le premier de la pile. */
    public boolean isFirstHandler() {
        return getFirstHandler() == this;
    }

    protected void pushHandler(RecursiveHandler handler, RecursiveHandler parent) {
        getToplevel().pushHandler(handler, parent);
    }

    public void pushHandler(RecursiveHandler handler) {
        getToplevel().pushHandler(handler, this);
    }

    /**
     * Empiler le nouvel handler, et dispatcher un événement
     * {@link org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)}
     * au nouvel handler.
     */
    public void pushHandler(RecursiveHandler handler, String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        pushHandler(handler);
        getToplevel().startElement(uri, localName, qName, attributes);
    }

    public void popHandler() {
        getToplevel().popHandler();
    }

    /**
     * Dépiler le handler, et dispatcher un événement
     * {@link org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)}
     * au handler du dessous.
     */
    public void popHandler(String uri, String localName, String qName) throws SAXException {
        popHandler();
        getToplevel().endElement(qName, localName, qName);
    }
}