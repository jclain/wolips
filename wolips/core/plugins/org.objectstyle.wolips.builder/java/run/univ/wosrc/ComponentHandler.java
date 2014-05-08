/* ComponentHandler.java
 * Created on 12 janv. 2009
 */
package run.univ.wosrc;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import org.objectstyle.wolips.builder.BuilderPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;

import run.univ.Cls;
import run.univ.base.xml.HTMLBuilder;
import run.univ.base.xml.XMLAttributes;
import run.univ.base.xml.XMLBuilder;
import run.univ.base.xml.base.AbstractXMLBuilder;
import run.univ.xml.base.Namespaces;

public class ComponentHandler implements ContentHandler, ErrorHandler, LexicalHandler {
	/** le flux de sortie vers le fichier html. */
	Writer htmlw;

	/** le constructeur du fichier html. */
	private AbstractXMLBuilder htmlb;

	/** le flux de sortie vers le fichier wod. */
	Writer wodw;

	/** le constructeur du fichier wod. */
	private WODBuilder wodb;

	/** le flux de sortie vers le fichier woo. */
	Writer woow;

	/** le constructeur du fichier woo. */
	private WOOBuilder woob;

	public ComponentHandler(Writer htmlWriter, Writer wodWriter, Writer wooWriter, boolean html, boolean indentHtml, String encoding) {
		this.htmlw = htmlWriter;
		// this.htmlb = new XMLBuilder(null, indentHtml, html, encoding) {
		// protected void write(String text) throws IOException {
		// htmlw.write(text);
		// }
		// };
		if (html)
			this.htmlb = new HTMLBuilder(htmlw);
		else
			this.htmlb = new XMLBuilder(htmlw);
		htmlb.setIndent(indentHtml);
		htmlb.setEncoding(encoding);

		this.wodw = wodWriter;
		this.wodb = new WODBuilder() {
			protected void write(String text) throws IOException {
				wodw.write(text);
			}
		};

		if (wooWriter != null) {
			this.woow = wooWriter;
			this.woob = new WOOBuilder(encoding) {
				protected void write(String text) throws IOException {
					woow.write(text);
				}
			};
		}
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void startDocument() throws SAXException {
		if (woob != null) {
			try {
				woob.defaultContent();
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
		// XXX ne pas écrire du XML, sauf si on nous demande de le faire.
		// Comment paramétrer cela?
		// htmlb.xmldecl();
	}

	public void endDocument() throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	HashMap<String, Integer> indexes = new HashMap<String, Integer>();

	public void startElement(String namespaceURI, String localName, String qName, Attributes saxattrs) throws SAXException {
		if (namespaceURI.equals(Defaults.WO_NS)) {
			// nom de la classe
			String className = (String) Defaults.TAG_ALIASES.get(localName);
			if (className == null)
				className = localName;

			// nom du binding
			String bindingName = Cls.basename(className);
			if (bindingName.startsWith("WO")) {
				bindingName = bindingName.substring(2);
			}
			if (!indexes.containsKey(bindingName)) {
				indexes.put(bindingName, new Integer(0));
			}
			indexes.put(bindingName, new Integer(((Integer) indexes.get(bindingName)).intValue() + 1));
			bindingName = bindingName + indexes.get(bindingName);

			// créer le tag
			try {
				wodb.append(bindingName, className, saxattrs);
				htmlb.start("WEBOBJECTS", new XMLAttributes("name", bindingName));
			} catch (IOException e) {
				throw new SAXException(e);
			}
		} else {
			XMLAttributes attrs = new XMLAttributes();
			int max = saxattrs.getLength();
			for (int i = 0; i < max; i++) {
				String qname = saxattrs.getQName(i);
				String value = saxattrs.getValue(i);

				// filtrer les déclarations de préfixe WO_NS et QATTR_NS
				if (!Namespaces.isAttrNamespaceDecl(qname) || (!value.equals(Defaults.WO_NS) && !value.equals(Defaults.QATTR_NS))) {
					attrs.add(qname, value);
				}
			}

			htmlb.start(qName, attrs);
			try {
				htmlb.throwSavedException();
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (namespaceURI.equals(Defaults.WO_NS)) {
			htmlb.end("WEBOBJECTS");
		} else {
			htmlb.end(qName);
		}
		try {
			htmlb.throwSavedException();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if (in_cdata) {
			htmlb.cdata(new String(ch, start, length));
		} else {
			htmlb.text(new String(ch, start, length));
		}
		try {
			htmlb.throwSavedException();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
	}

	public void processingInstruction(String target, String data) throws SAXException {
		htmlb.rawtext("<?" + target + " " + data + "?>");
		htmlb.nl();
		try {
			htmlb.throwSavedException();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void skippedEntity(String name) throws SAXException {
		htmlb.rawtext("&" + name + ";");
		try {
			htmlb.throwSavedException();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	protected boolean in_dtd;

	public void startDTD(String name, String publicId, String systemId) throws SAXException {
		htmlb.doctype(name, publicId, systemId, false);
		in_dtd = true;
		try {
			htmlb.throwSavedException();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void endDTD() throws SAXException {
		htmlb.rawtext(">");
		htmlb.nl();
		in_dtd = false;
		try {
			htmlb.throwSavedException();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	protected boolean in_cdata;

	public void startCDATA() throws SAXException {
		in_cdata = true;
	}

	public void endCDATA() throws SAXException {
		in_cdata = false;
	}

	public void startEntity(String name) throws SAXException {
	}

	public void endEntity(String name) throws SAXException {
	}

	public void comment(char[] ch, int start, int length) throws SAXException {
		if (in_dtd)
			return;

		htmlb.comment(new String(ch, start, length));
		try {
			htmlb.throwSavedException();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		throw exception;
	}

	public void error(SAXParseException exception) throws SAXException {
		throw exception;
	}

	public void warning(SAXParseException exception) throws SAXException {
		BuilderPlugin.getDefault().log(exception);
	}
}