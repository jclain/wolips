/* ExcTools.java
 * Created on 7 avr. 2009
 */
package run.univ.base.util.exc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import run.univ.base.Arr;
import run.univ.base.Cls;
import run.univ.base.SB;
import run.univ.base.Str;

/**
 * @author jclain
 */
public class ExcTools {
    /**
     * Retourner la cause de t si elle est de type cls.
     */
    public static final <T extends Throwable> T getCause(Throwable t, Class<T> cls) {
        if (t == null) return null;
        return Cls.castOrNull(t.getCause(), cls);
    }

    /**
     * Si l'exception t ne contient pas de message, ou est de type {@link InvocationTargetException}
     * ou {@link ExecutionException}, et que sa cause est de type cls, alors retourner la cause,
     * sinon retourner <code>null</code>.
     */
    public static final <T extends Throwable> T unwrap(Throwable t, Class<T> cls) {
        if (t == null) return null;
        if (t.getMessage() == null || t instanceof InvocationTargetException
                || t instanceof ExecutionException) {
            Throwable cause = t.getCause();
            if (cause != null) t = cause;
        }
        return Cls.castOrNull(t, cls);
    }

    /**
     * Retourner le message d'une exception, ou <code>null</code> s'il n'y a pas de message associé.
     * Si unwrap==<code>true</code>, déballer l'exception avec {@link #unwrap(Throwable, Class)} 
     * d'abord.
     */
    public static final String getMessageOrNull(Throwable t, boolean unwrap) {
        if (t == null) return null;
        if (unwrap) t = unwrap(t, null);
        return t.getMessage();
    }

    /**
     * Déballer une exception, puis retourner son message, ou <code>null</code> s'il n'y a pas de
     * message associé.
     */
    public static final String getMessageOrNull(Throwable t) {
        return getMessageOrNull(t, true);
    }

    /**
     * Obtenir le message associé à l'exception. S'il n'y a pas de message, en créer un de la forme
     * "java.lang.Exception at File.java(12)".
     */
    public static final String getMessage(Throwable t, boolean unwrap) {
        if (t == null) return null;

        StringBuilder sb = SB.valueOrNull(getMessageOrNull(t, unwrap));
        if (sb == null) {
            sb = SB.valueOf(t.getClass().getName());
            SB.append(sb, " at ", Str.valueOf(Arr.firstOf(t.getStackTrace())));
        }
        return sb.toString();
    }

    /**
     * Déballer une exception, puis retourner le message associé à l'exception. s'il n'y a pas de
     * message, en créer un de la forme "java.lang.Exception at File.java(12)".
     */
    public static final String getMessage(Throwable t) {
        return getMessage(t, true);
    }

    /** Retourner la classe de l'exception, ainsi que son message s'il est non <code>null</code>. */
    public static final String getExceptionAndMessage(Throwable t, boolean unwrap) {
        if (t == null) return null;

        StringBuilder sb = SB.valueOf(t.getClass().getName());
        SB.append(sb, ": ", getMessageOrNull(t, unwrap));
        return sb.toString();
    }

    public static final String getExceptionAndMessage(Throwable t) {
        return getExceptionAndMessage(t, true);
    }

    private static final Throwable unwrapMaybe(Throwable t, boolean unwrap) {
        if (unwrap) t = unwrap(t, null);
        return t;
    }

    /** @return un résumé de toute la chaine des exceptions. */
    public static final String getSummary(Throwable t, boolean unwrap) {
        if (t == null) return null;

        t = unwrapMaybe(t, unwrap);
        StringBuilder sb = SB.valueOf(getExceptionAndMessage(t, false));
        while ((t = unwrapMaybe(getCause(t, null), unwrap)) != null) {
            SB.append(sb, ", caused by ", getExceptionAndMessage(t, false));
        }
        return sb.toString();
    }

    public static final String getSummary(Throwable t) {
        return getSummary(t, true);
    }

    /** Retourner une représentation chaine de l'exception avec le traceback. */
    public static final String getTraceback(Throwable t) {
        if (t == null) return null;

        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
