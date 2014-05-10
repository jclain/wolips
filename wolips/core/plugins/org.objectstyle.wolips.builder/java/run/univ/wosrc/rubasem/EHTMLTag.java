/* EHTMLTag.java
 * Created on 16 avr. 2010
 */
package run.univ.wosrc.rubasem;

import java.util.HashMap;


/**
 * @author jclain
 */
public enum EHTMLTag {
    // Structure du document
    HTML(false, true, false, false, false), HEAD(false, true, true, false, false), BODY(false,
            true, true, false, false),
    //
    FRAMESET(false, true, true, false, false), FRAME(false, true, true, false, false), NOFRAMES(
            false, true, true, false, false),
    //
    TITLE(true, true), META(false, false, false, true, false), LINK(false, false, false, true,
            false), BASE(false, false, false, true, false), BASEFONT(false, false, false, true,
            false),
    // ------------------------------------------------------------------------
    // Tags de type bloc
    DIV(true, true), CENTER(true, false),
    //
    H1(true, true), H2(true, true), H3(true, true), H4(true, true), H5(true, true), H6(true, true),
    //
    P(true, true), PRE(true, true), BLOCKQUOTE(true, true),
    //
    UL(true, true), LI(true, true), OL(true, true), DL(true, true), DT(true, true), DD(true, true),
    //
    BR(true, false, true, false), HR(true, false, true, false),
    //
    FORM(true, false), FIELDSET(true, false),
    //
    TABLE(false, true), LEGEND(false, false), CAPTION(false, false), THEAD(false, true), TBODY(
            false, true), TFOOT(false, true), OPTGROUP(false, true), COLGROUP(false, true), COL(
            false, true, true, false), TR(false, true), TH(true, true), TD(true, true),
    //
    ISINDEX(true, false, true, false), MENU(true, true), DIR(true, true),
    // ------------------------------------------------------------------------
    // Tags de type inline
    SPAN, BDO, IFRAME,
    //
    FONT, STYLE, SCRIPT, NOSCRIPT,
    //
    OBJECT, APPLET, PARAM(true),
    //
    A, IMG(true),
    //
    INPUT(true), TEXTAREA, SELECT, OPTION, LABEL, BUTTON, AREA(true),
    //
    TT, I, B, U, S, STRIKE, EM, STRONG, BIG, SMALL, SUB, SUP,
    //
    CITE, CODE, DFN, ADDRESS, KBD, MAP, SAMP, VAR, NOBR, Q, INS, DEL, ABBR, ACRONYM;

    private static HashMap<String, EHTMLTag> TAGS = new HashMap<String, EHTMLTag>();
    static {
        for (EHTMLTag tag : values()) {
            TAGS.put(tag.getName(), tag);
        }
    }

    public static final boolean isValid(String name) {
        return TAGS.containsKey(Str.lower(name));
    }

    public static final EHTMLTag get(String name) {
        return TAGS.get(Str.lower(name));
    }

    private EHTMLTag(boolean content, boolean block, boolean causesBreak, boolean empty,
            boolean containsInline) {
        this.content = content;
        this.block = block;
        this.causesBreak = causesBreak;
        this.empty = empty;
        this.containsInline = containsInline;
    }

    private EHTMLTag(boolean block, boolean causesBreak, boolean empty, boolean containsInline) {
        this(true, block, causesBreak, empty, containsInline);
    }

    private EHTMLTag(boolean block, boolean causesBreak) {
        this(block, causesBreak, false, false);
    }

    private EHTMLTag(boolean empty) {
        this(false, false, empty, true);
    }

    private EHTMLTag() {
        this(false);
    }

    public String getName() {
        return Str.lower(toString());
    }

    public boolean is(String name) {
        return Str.equalsIgnoreCase(toString(), name);
    }

    private boolean content;

    public boolean isContent() {
        return content;
    }

    private boolean block;

    public boolean isBlock() {
        return block;
    }

    private boolean causesBreak;

    public boolean isCausesBreak() {
        return causesBreak;
    }

    private boolean empty;

    public boolean isEmpty() {
        return empty;
    }

    private boolean containsInline;

    public boolean isContainsInline() {
        return containsInline;
    }
}
