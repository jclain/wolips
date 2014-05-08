// Adapté d'après la distribution SOX. cf http://www.langdale.com.au/SOX/
package run.univ.xml.sox;

import java.io.IOException;
import java.io.Reader;

/**
 * Basis for a lexer. Handles the fundamental token types.
 */
public abstract class Lexer {
    public static class LexerException extends Exception {
        private static final long serialVersionUID = 1L;

        public LexerException(String message, int line, int column) {
            super(message + (line != -1 || column != -1? " at": "")
                    + (line != -1? " line " + line: "") + (column != -1? " column " + column: ""));
        }
    }

    protected int next;

    private int pending = -1;

    private Reader input;

    private int lineno = 1;

    /**
     * Construct without input. The setInput() method must be called.
     */
    protected Lexer() {
    }

    /**
     * Construct with given input.
     */
    protected Lexer(Reader input) {
        setInput(input);
    }

    /**
     * Establish the input to be parsed.
     */
    public void setInput(Reader input) {
        this.input = input;
        pending = -1;
        lineno = 1;
        scanToStart();
    }

    /**
     * Initialise by reading in the first character.
     */
    protected void scanToStart() {
        more();
    }

    /**
     * Generate a LexerException exception contiaining the current line number.
     */
    public void error(String reason) throws LexerException {
        throw new LexerException(reason, lineno, -1);
    }

    /** obtenir le numéro de ligne associé au token courant. */
    public int getLine() {
        return lineno;
    }

    /** obtenir le numéro de colonne associé au token courant. */
    public int getColumn() {
        return -1;
    }

    /**
     * Read the next character from the input.
     */
    protected void more() {
        if (pending != -1) {
            next = pending;
            pending = -1;
        } else if (input == null) {
            next = -1;
        } else {
            try {
                next = input.read();
            } catch (IOException ex) {
                next = -1;
            }
        }

        // else if (Character.getType((char)next) == Character.LINE_SEPARATOR) lineno += 1;
        // XXX Character.getType('\r' ou '\n') retourne Character.CONTROL et non
        // Character.LINE_SEPARATOR
        // XXX il faudrait pouvoir traiter indiféremennt \r\n et \n pour le compte des lignes
        if (/* next == '\r' || */next == '\n') lineno++;
    }

    /**
     * Push back one character onto the input stream. Only one level of push back is supported: use
     * with care!
     */
    protected void pushback(int ch) {
        pending = next;
        next = ch;
    }

    /**
     * Convenience function to test if next belongs to a set of characters represented by the given
     * string.
     */
    protected boolean isa(String category) {
        return category.indexOf(next) != -1;
    }

    /**
     * A token that was extracted from the input character stream.
     */
    public abstract class Token {
        protected StringBuffer token;

        protected Token() {
            token = new StringBuffer();
        }

        protected Token(String initial) {
            token = new StringBuffer(initial);
        }

        protected void append() {
            token.append((char)next);
            more();
        }

        protected void append(String text) {
            token.append(text);
        }

        protected void append(char ch) {
            token.append(ch);
        }

        /**
         * The string representation or value of the token.
         */
        public String getValue() {
            return token.toString();
        }

        public String toString() {
            return token.toString();
        }
    }

    /**
     * Symbols are tokens that have no information except their identity.
     */
    public class SymbolToken extends Token {
        public SymbolToken() {
            append();
        }
    }

    /**
     * The end of file.
     */
    public class EndToken extends Token {
    }

    public abstract Token token() throws LexerException;
}