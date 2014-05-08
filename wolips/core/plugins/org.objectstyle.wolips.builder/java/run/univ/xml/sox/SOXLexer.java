// Adapté d'après la distribution SOX. cf http://www.langdale.com.au/SOX/
package run.univ.xml.sox;

import java.io.Reader;
import java.util.Vector;

import run.univ.Str;
import run.univ.xml.base.Doctypes;
import run.univ.xml.base.XMLAttributes;

/**
 * Extract tokens from a python-like indented outline language.
 */

public class SOXLexer extends Lexer {
    /** caractère qui commence une déclaration initiale. */
    private static String decl = "<";

    private static String question = "?";

    /** true si on est en train de parser les declaration initiales. */
    private boolean parsing_decl;

    /** true si on a fait l'initialisation du lexer après avoir lu les déclarations initiales. */
    private boolean initialized;

    /** caractère qui commence en commentaire. */
    private static String comment = "#";

    private static String tab = "\t";

    private static String space = " ";

    private static String endline = "\n\r";

    private static String whitespace = space + tab;

    private static int space_per_tab = 8;

    private static String equals = "=";

    private static String tag = ">";

    private static String symbols = decl + question + equals + tag;

    private static String quotes = "'\"";

    private static String terminator = symbols + whitespace + endline;

    /**
     * Contruct from a configured reader.
     */
    public SOXLexer(Reader input) {
        super(input);
        parsing_decl = true;
        initialized = false;
    }

    /**
     * Construct without a reader. Requires setInput() to be called.
     */
    public SOXLexer() {
        super();
        parsing_decl = true;
        initialized = false;
    }

    // A history of indents
    private class IndentStack {
        private Vector<Integer> stack = new Vector<Integer>();

        private int amount = 0;

        int top() {
            return amount;
        }

        void push(int new_indent) {
            stack.add(new Integer(amount));
            amount = new_indent;
        }

        void pop() {
            amount = ((Integer)stack.remove(stack.size() - 1)).intValue();
        }
    }

    // The history of indents
    private IndentStack indent = new IndentStack();

    private int current_indent = 0;

    private int get_indent() {
        int indent;
        for (;;) {
            indent = 0;
            while (isa(whitespace)) {
                if (isa(tab)) indent += space_per_tab;
                else indent += 1;
                more();
            }
            if (!isa(endline)) break;
            more();
        }
        return indent;
    }

    /**
     * Une déclaration, comme &lt;?sox ...?&gt; ou &lt;!DOCTYPE ... &gt;
     */
    public class DeclToken extends Token {
        protected static final int PI_TYPE = 0, SOXDECL_TYPE = 1, DOCTYPE_TYPE = 2;

        /** type de déclaration: un parmi les valeurs PI_TYPE, SOXDECL_TYPE, DOCTYPE_TYPE. */
        protected int type;

        /** si type == PI_TYPE, le nom du target. */
        protected String target;

        /** si type == PI_TYPE ou SOXDECL_TYPE, les attributs lus dans le tag. */
        protected XMLAttributes attrs;

        DeclToken() throws LexerException {
            attrs = new XMLAttributes.Implementation();

            more();
            if (next == '?') {
                more();
                parse_pi_or_soxdecl();
            } else if (next == '!') {
                more();
                parse_doctype();
            } else {
                error("Type de déclaration initiale incorrect: <" + (char)next);
            }

            // Puis supprimer tout les espaces et les sauts de lignes qu'il y a après la déclaration
            while (isa(whitespace) || isa(endline))
                more();
        }

        private void parse_pi_or_soxdecl() throws LexerException {
            NameToken targetToken = new NameToken();
            target = targetToken.getValue();
            type = "sox".equals(target)? SOXDECL_TYPE: PI_TYPE;

            boolean first_attr = true;
            while (!isa(question) && next != -1) {
                Token name, value;

                // récupérer le nom de l'attribut
                while (isa(whitespace))
                    more();
                if (isa(question) || next == -1) break;
                name = new NameToken();

                // passer le '='
                while (isa(whitespace))
                    more();
                if (isa(equals)) new EqualsToken();
                else error("Il faut un caractère '=' après le nom d'attribut '" + name.getValue()
                        + "'");

                // récupérer la valeur de l'attribut
                while (isa(whitespace))
                    more();
                if (isa(question) || next == -1) {
                    error("Il faut une valeur pour l'attribut " + name.getValue());
                }
                if (isa(quotes)) value = new StringToken();
                else value = new NameToken();

                if (!first_attr) append(' ');
                append(name.getValue());
                append('=');
                append(Str.htmlattr_quote(value.getValue()));
                attrs.add(name.getValue(), value.getValue());
                first_attr = false;
            }
            if (isa(question)) {
                more();
                if (isa(tag)) {
                    more();
                } else {
                    error("'?' doit être suivi de '>' pour terminer le bloc <?");
                }
            } else {
                error("Bloc <? non terminé");
            }
        }

        private void parse_doctype() throws LexerException {
            // XXX parser publicId et systemId
            // XXX on ne supporte pas [ et ]
            type = DOCTYPE_TYPE;

            String doctype = new NameToken().getValue();
            if (!doctype.equalsIgnoreCase("doctype")) {
                error("Seul <!DOCTYPE est supporté");
            }

            // Lire la chaine
            StringBuffer sb = new StringBuffer();
            while (!isa(tag) && next != -1) {
                Token value;

                // récupérer le prochain élément
                while (isa(whitespace))
                    more();
                if (isa(tag) || next == -1) break;
                if (isa(quotes)) {
                    value = new StringToken();
                    sb.append(" \"");
                    sb.append(value.getValue());
                    sb.append('"');
                } else {
                    value = new NameToken();
                    sb.append(' ');
                    sb.append(value.getValue());
                }
            }
            if (!isa(tag)) {
                error("Bloc <!DOCTYPE non terminé");
            }
            more();

            String alias = sb.toString().trim().toLowerCase();
            if (Doctypes.isValid(alias)) {
                append(Doctypes.getDecl(alias));
            } else {
                append("<!DOCTYPE");
                append(sb.toString());
                append('>');
            }
        }

        /** Tester s'il s'agit d'une déclaration &lt;?sox...?&gt;. */
        public boolean isSOXDecl() {
            return type == SOXDECL_TYPE;
        }

        /** Tester s'il s'agit d'une instruction de traitement &lt;target...?&gt;. */
        public boolean isProcessingInstruction() {
            return type == PI_TYPE;
        }

        /** Tester s'il s'agit d'une déclaration &lt;!DOCTYPE...&gt;. */
        public boolean isDoctype() {
            return type == DOCTYPE_TYPE;
        }

        /** Si type == PI_TYPE, retourner le nom de l'instruction de traitement. */
        public String getTarget() {
            return target;
        }

        /** Si type == PI_TYPE ou SOXDECL_TYPE, retourner les attributs de la déclaration. */
        public XMLAttributes getAttrs() {
            return attrs;
        }
    }

    /**
     * Un commentaire qui commence par un nombre quelconque de # et peut tenir sur plusieurs lignes
     * (les lignes sont alors aggrégées).
     * <p>
     * note: de la façon dont les choses sont faites, un CommentToken suivra toujours un LineToken
     * (à moins bien sûr que ce ne soit le premier) dans le flux des tokens retournés par
     * {@link Lexer#more()}.
     * </p>
     */
    public class CommentToken extends Token {
        CommentToken() {
            int new_indent;
            do {
                more();
                // supprimer les suites de #
                while (isa(comment))
                    more();
                // supprimer les espaces au début
                while (isa(whitespace))
                    more();
                // Séparer les commentaires aggrégés par ' '
                if (token.length() != 0) token.append(' ');
                // Lire le commentaire
                while (!isa(endline) && next != -1)
                    append();

                // calculer la nouvelle indentation
                new_indent = get_indent();
            } while (isa(comment));

            current_indent = new_indent;
        }
    }

    /**
     * Represents an XML name.
     */
    public class NameToken extends Token {
        NameToken() {
            append();
            while (!isa(terminator) && next != -1)
                append();
        }
    }

    /**
     * A (possibly) multiline, quoted string.
     */
    public class StringToken extends Token {
        protected boolean multiLine;

        /**
         * true si la chaine est multiligne.
         */
        public boolean isMultiLine() {
            return multiLine;
        }

        protected boolean singleQuoted;

        /** true si la chaine utilise des quotes ' plutôt que des doubles-quotes ". */
        public boolean isSingleQuoted() {
            return singleQuoted;
        }

        StringToken() throws LexerException {
            multiLine = false;
            singleQuoted = false;

            char start = (char)next;
            more();
            if (next == start) {
                more();

                // multiline string
                if (next == start) {
                    multiLine = true;
                    singleQuoted = start == '\'';

                    int offset = current_indent;
                    more();
                    for (;;) {
                        if (next == -1) error("unterminated string");
                        if (next == start) {
                            more();
                            if (next == start) {
                                more();
                                if (next == start) {
                                    more();
                                    break;
                                }
                                append(start);
                            }
                            append(start);
                        } else if (isa(endline)) {
                            append();
                            // delete leading white space
                            int elided = 0;
                            while (isa(whitespace) && elided < offset) {
                                if (isa(tab)) elided += 8;
                                else elided += 1;
                                more();
                            }
                        } else {
                            append();
                        }
                    }
                }

                // empty string
                else {
                    singleQuoted = start == '\'';
                }
            }

            // single line string
            else {
                singleQuoted = start == '\'';
                while (next != start) {
                    if (isa(endline) || next == -1) error("unterminated string");
                    append();
                }
                more();
            }
        }
    }

    /**
     * Indicates an element.
     */
    public class TagToken extends SymbolToken {
    }

    /**
     * Indicates an attribute.
     */
    public class EqualsToken extends SymbolToken {
    }

    /**
     * Marks the end of a statement
     */
    public class LineToken extends Token {
        LineToken() {
            current_indent = get_indent();
        }
    }

    /**
     * Marks beginning of new scope.
     */
    public class IndentToken extends Token {
        IndentToken() {
            indent.push(current_indent);
        }
    }

    /**
     * Marks the end of scope.
     */
    public class DedentToken extends Token {
        DedentToken() throws LexerException {
            indent.pop();
            if (current_indent > indent.top()) {
                indent.push(current_indent);
                error("incorrect indenting");
            }
        }
    }

    /**
     * Retrieve the next token from the input.
     */
    public Token token() throws LexerException {
        if (next == -1) {
            // En fin de fichier, on s'arrange pour envoyer autant de DedentToken que nécessaire
            // avant d'envoyer EndToken.
            current_indent = 0;
            if (indent.top() == 0) return new EndToken();
        }

        if (parsing_decl) {
            if (isa(decl)) {
                return new DeclToken();
            } else if (isa(comment)) {
                return new CommentToken();
            }
        }

        if (!initialized) {
            // Calculer l'indentation initiale
            current_indent = get_indent();
            initialized = true;
            parsing_decl = false;
        }

        if (isa(comment)) return new CommentToken();

        if (current_indent < indent.top()) return new DedentToken();

        if (current_indent > indent.top()) return new IndentToken();

        while (isa(whitespace)) {
            more();
        }

        if (isa(tag)) return new TagToken();

        if (isa(equals)) return new EqualsToken();

        if (isa(quotes)) return new StringToken();

        if (isa(endline)) return new LineToken();

        return new NameToken();
    }
}