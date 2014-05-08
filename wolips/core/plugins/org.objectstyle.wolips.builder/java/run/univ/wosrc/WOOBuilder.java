/* WOOBuilder.java
 * Created on 12 janv. 2009
 */
package run.univ.wosrc;

import java.io.IOException;

import run.univ.Str;

/**
 * @author jclain
 */
public abstract class WOOBuilder {
	public WOOBuilder(String encoding) {
		this.encoding = encoding;
	}

	private String encoding;

	private static final String NSISOLATIN1_ENCODING = "NSISOLatin1StringEncoding", NSUTF8_ENCODING = "NSUTF8StringEncoding";

	private String getNSEncoding() {
		if (Str.equalsIgnoreCase(encoding, Str.UTF_8))
			return NSUTF8_ENCODING;
		else if (Str.equalsIgnoreCase(encoding, Str.ISO_8859_1))
			return NSISOLATIN1_ENCODING;
		else
			return NSISOLATIN1_ENCODING;
	}

	protected abstract void write(String text) throws IOException;

	public void writenl(String text) throws IOException {
		write(text);
		write("\r\n");
	}

	public void defaultContent() throws IOException {
		writenl("{");
		writenl("    \"WebObjects Release\" = \"WebObjects 5.0\";");
		writenl("    encoding = " + getNSEncoding() + ";");
		writenl("}");
	}
}