/* LineSep.java
 * Created on 10 févr. 2005
 */
package run.univ;


/**
 * Une classe permettant de normaliser les caractères de fin de ligne d'une chaine ou d'un flux.
 * 
 * @author jclain
 */
public class LineSep {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String CRLF = "\r\n", CR = "\r", LF = "\n";
}