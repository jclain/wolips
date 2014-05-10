/* Exc.java
 * Created on 23 oct. 2009
 */
package run.univ.wosrc.rubasem;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Des fonctions utilitaires pour gérer les exceptions.
 * 
 * @author jclain
 */
public class Exc extends ExcTools {
    /**
     * Obtenir t si c'est une instance de {@link Exception}, ou lancer l'exception si c'est une
     * instance de {@link Error}.
     */
    public static final Exception getExceptionOrThrow(Throwable t) {
        if (t == null) return null;
        else if (t instanceof Exception) return (Exception)t;
        else if (t instanceof Error) throw (Error)t;
        else {
            // Si c'est une instance de Throwable, la wrapper
            return new RunUnivException(t);
        }
    }

    /**
     * Obtenir la cause de t si c'est une instance de {@link Exception}, ou lancer l'exception si
     * c'est une instance de {@link Error}.
     */
    public static final Exception getCauseOrThrow(Throwable t) {
        return getExceptionOrThrow(getCause(t, null));
    }

    /**
     * Obtenir la cause de t si c'est une instance de {@link InvocationTargetException} ou
     * {@link ExecutionException}, sinon retourner t si c'est une instance de {@link Exception},
     * sinon lancer l'exception si c'est une instance de {@link Error}.
     */
    public static final Exception unwrapOrThrow(Throwable t) {
        return getExceptionOrThrow(unwrap(t, null));
    }

    /**
     * Une méthode permettant de créer une exception avec une cause, dans le cas où le constructeur
     * ne le permet pas.
     */
    public static final <T extends Exception> T withCause(T e, Throwable cause) {
        if (cause != null) e.initCause(cause);
        return e;
    }

    private static String getMessage(String message, Throwable cause) {
        if (message == null) message = getMessageOrNull(cause);
        return message;
    }

    /** méthode de convenance pour créer une instance de IOException avec une cause. */
    public static final IOException IOException(String message, Throwable cause) {
        return withCause(new IOException(getMessage(message, cause)), cause);
    }

    /** méthode de convenance pour créer une instance de SQLException avec une cause. */
    public static final SQLException SQLException(String message, Throwable cause) {
        return withCause(new SQLException(getMessage(message, cause)), cause);
    }

    /** méthode de convenance pour créer une instance de IllegalArgumentException. */
    public static final IllegalArgumentException IllegalArgumentException(String message,
            Throwable cause) {
        return withCause(new IllegalArgumentException(getMessage(message, cause)), cause);
    }

    public static final IllegalArgumentException unexpectedType(Object source,
            Class<?> expectedClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("Attendu un objet de type ");
        sb.append(expectedClass.getName());
        sb.append(" (obtenu une ");
        if (source == null) {
            sb.append("valeur nulle");
        } else {
            sb.append("instance de ");
            sb.append(source.getClass().getName());
        }
        sb.append(")");
        return new IllegalArgumentException(sb.toString());
    }

    public static final IllegalArgumentException unexpectedType(Object source,
            Class<?>[] expectedClasses) {
        StringBuilder sb = new StringBuilder();
        sb.append("Attendu un objet ");
        int maxm1 = expectedClasses.length - 1;
        if (maxm1 > 0) {
            sb.append("d'un des types suivants: ");
            for (int i = 0; i <= maxm1; i++) {
                if (i == maxm1) sb.append(" ou ");
                else if (i > 0) sb.append(", ");
                sb.append(expectedClasses[i].getName());
            }
        } else {
            sb.append("de type ");
            sb.append(expectedClasses[0].getName());
        }
        sb.append(" (obtenu une ");
        if (source == null) sb.append("valeur nulle");
        else {
            sb.append("instance de ");
            sb.append(source.getClass().getName());
        }
        sb.append(")");
        return new IllegalArgumentException(sb.toString());
    }

    public static final IllegalArgumentException cannotCompare(Object source, Object dest) {
        return new IllegalArgumentException("Ne peut comparer qu'avec une instance de "
                + source.getClass() + " (obtenu une "
                + (dest != null? "instance de " + dest.getClass(): "valeur nulle") + ")");
    }

    public static final IllegalArgumentException cannotConvert(Object source, Class<?> dest) {
        return new IllegalArgumentException("Ne peut convertir un objet de type "
                + source.getClass() + " en " + dest);
    }

    /** méthode de convenance pour créer une instance de IllegalStateException. */
    public static final IllegalStateException IllegalStateException(String message, Throwable cause) {
        return withCause(new IllegalStateException(getMessage(message, cause)), cause);
    }

    public static final IllegalStateException propertyMustBeDefined(Object name) {
        return new IllegalStateException("La propriété " + name + " doit être définie");
    }

    /** méthode de convenance pour créer une instance de NoSuchElementException. */
    public static final NoSuchElementException NoSuchElementException(String message,
            Throwable cause) {
        return withCause(new NoSuchElementException(getMessage(message, cause)), cause);
    }

    /** méthode de convenance pour créer une instance de ParserConfigurationException. */
    public static final ParserConfigurationException ParserConfigurationException(String message,
            Throwable cause) {
        return withCause(new ParserConfigurationException(getMessage(message, cause)), cause);
    }

    /** méthode de convenance pour créer une instance de SAXException. */
    public static final SAXException SAXException(String message, Exception cause) {
        return withCause(new SAXException(getMessage(message, cause), cause), cause);
    }

    /** méthode de convenance pour créer une instance de SAXParseException. */
    public static final SAXParseException SAXParseException(String message, Locator locator,
            Exception cause) {
        return withCause(new SAXParseException(getMessage(message, cause), locator, cause), cause);
    }

    /** méthode de convenance pour créer une instance de NamingException. */
    public static final NamingException NamingException(String message, Throwable cause,
            Name resolvedName, Object resolvedObject, Name remainingName) {
        NamingException e = withCause(new NamingException(getMessage(message, cause)), cause);
        e.setRootCause(e);
        if (resolvedName != null) e.setResolvedName(resolvedName);
        if (resolvedObject != null) e.setResolvedObj(resolvedObject);
        if (remainingName != null) e.setRemainingName(remainingName);
        return e;
    }

    /** méthode de convenance pour créer une instance de NamingException. */
    public static final NamingException NamingException(String message, Throwable cause) {
        return NamingException(message, cause, null, null, null);
    }

    /** méthode de convenance pour créer une instance de ParseException. */
    public static final ParseException ParseException(String message, int errorOffset, Throwable cause) {
        return withCause(new ParseException(getMessage(message, cause), errorOffset), cause);
    }
}
