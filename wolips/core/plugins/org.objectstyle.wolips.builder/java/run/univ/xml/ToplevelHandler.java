/* ToplevelHandler.java
 * Created on 14 mars 2006
 */
package run.univ.xml;

import java.io.IOException;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Un handler qui pilote un ensemble de handlers récursifs organisés en pile.
 * <p>
 * Ce handler est celui qui est passé à l'API de SAX. Les handlers qui font effectivement le travail
 * sont ajoutés (avec {@link #pushHandler(RecursiveHandler)}) au fur et à mesure.
 * </p>
 * 
 * @see rubase.xml.RecursiveHandler
 * @author jclain
 */
public class ToplevelHandler extends RecursiveHandler {
    public ToplevelHandler(RecursiveHandler firstHandler) {
        pushHandler(firstHandler);
    }

    public ToplevelHandler getToplevel() {
        return this;
    }

    /**
     * pile des handlers qui ne sont pas en cours. le handler courant ne figure pas dans cette pile.
     */
    private LinkedList stack;

    public LinkedList getStack() {
        if (stack == null) stack = new LinkedList();
        return stack;
    }

    private RecursiveHandler currentHandler;

    public RecursiveHandler getFirstHandler() {
        if (stack == null || stack.size() == 0) return currentHandler;
        return (RecursiveHandler)stack.get(0);
    }

    protected void pushHandler(RecursiveHandler handler, RecursiveHandler parent) {
        handler.setToplevel(getToplevel());
        handler.setParentHandler(parent);
        if (currentHandler != null) getStack().addLast(currentHandler);
        currentHandler = handler;
    }

    public void pushHandler(RecursiveHandler handler) {
        pushHandler(handler, null);
    }

    /**
     * Dépiler le handler courant, même si cela enlève le dernier handler. On se retrouve alors dans
     * une situation où l'objet est invalide.
     */
    public void popHandlerDontKeep() {
        LinkedList stack = getStack();
        if (stack.size() > 0) currentHandler = (RecursiveHandler)stack.removeLast();
        else currentHandler = null;
    }

    /** Dépiler le handler courant, en évitant de dépiler le premier handler de la pile. */
    public void popHandler() {
        LinkedList stack = getStack();
        if (stack.size() > 0) currentHandler = (RecursiveHandler)stack.removeLast();
    }

    // private void debugPrintHandler() {
    // int level = getStack().size();
    // for (int i = 0; i < level; i++) {
    // System.out.print(" ");
    // }
    // System.out.println(level + ": " + currentHandler.toString());
    // System.out.flush();
    // }

    private RecursiveHandler currentHandler() throws SAXException {
        if (currentHandler == null) throw new SAXException("no current handler");
        return currentHandler;
    }

    // EntityResolver
    public InputSource resolveEntity(String publicId, String systemId) throws IOException,
            SAXException {
        return currentHandler().resolveEntity(publicId, systemId);
    }

    // DTDHandler

    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        currentHandler().notationDecl(name, publicId, systemId);
    }

    public void unparsedEntityDecl(String name, String publicId, String systemId,
            String notationName) throws SAXException {
        currentHandler().unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    // ContentHandler

    public void setDocumentLocator(Locator locator) {
        try {
            currentHandler().setDocumentLocator(locator);
        } catch (SAXException e) {
        }
    }

    public void startDocument() throws SAXException {
        currentHandler().startDocument();
    }

    public void endDocument() throws SAXException {
        currentHandler().endDocument();
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        currentHandler().startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        currentHandler().endPrefixMapping(prefix);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        currentHandler().startElement(namespaceURI, localName, qName, atts);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        currentHandler().endElement(namespaceURI, localName, qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        currentHandler().characters(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        currentHandler().ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        currentHandler().processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
        currentHandler().skippedEntity(name);
    }

    // ErrorHandler

    public void warning(SAXParseException exception) throws SAXException {
        currentHandler().warning(exception);
    }

    public void error(SAXParseException exception) throws SAXException {
        currentHandler().error(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        currentHandler().fatalError(exception);
    }
}