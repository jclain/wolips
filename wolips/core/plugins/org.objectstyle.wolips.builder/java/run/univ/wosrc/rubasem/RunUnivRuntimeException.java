/* RunUnivRuntimeException.java
 * Created on 6 avr. 2009
 */
package run.univ.wosrc.rubasem;

/**
 * @author jclain
 */
public class RunUnivRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RunUnivRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunUnivRuntimeException(String message) {
        super(message);
    }

    public RunUnivRuntimeException(Throwable cause) {
        this(null, cause);
    }

    public RunUnivRuntimeException() {
        super();
    }

    /**
     * Retourner la cause de cette exception si elle est de type cls, <code>null</code> sinon.
     */
    public <T extends Throwable> T getCause(Class<T> cls) {
        return ExcTools.getCause(this, cls);
    }

    /** Relancer la cause de cette exception si elle est de type cls. */
    public <T extends Throwable> void unwrapThrow(Class<T> cls) throws T {
        T e = getCause(cls);
        if (e != null) throw e;
    }

    /**
     * Retourner la cause de cette exception si elle est de type cls ET si cette exception ne
     * contient pas de message (i.e. {@link #getMessage()}==<code>null</code>) , <code>null</code>
     * sinon.
     */
    public <T extends Throwable> T unwrap(Class<T> cls) {
        return ExcTools.unwrap(this, cls);
    }

    public String getSummary() {
        return ExcTools.getSummary(this);
    }

    public String getTraceback() {
        return ExcTools.getTraceback(this);
    }
}