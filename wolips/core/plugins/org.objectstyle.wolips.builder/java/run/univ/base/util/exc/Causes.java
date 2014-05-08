/* Causes.java
 * Created on 15 oct. 08
 */
package run.univ.base.util.exc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Une exception qui peut encapsuler plusieurs causes.
 * 
 * @author jclain
 */
public class Causes extends RunUnivException {
    private static final long serialVersionUID = 2843640476212334608L;

    public Causes() {
        super();
        this.causes = new ArrayList<Throwable>(1);
    }

    public Causes(Throwable... causes) {
        this();
        if (causes != null) {
            for (Throwable cause : causes) {
                addCause(cause);
            }
        }
    }

    private List<Throwable> causes;

    public void addCause(Throwable cause) {
        causes.add(cause);
    }

    public List<Throwable> getCauses() {
        return Collections.unmodifiableList(causes);
    }

    @Override
    public Throwable getCause() {
        return causes.isEmpty()? null: causes.get(0);
    }

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append("Causes multiples: ");
        boolean first = true;
        for (Throwable cause : causes) {
            if (first) first = false;
            else sb.append("; ");
            sb.append(cause.getMessage());
        }
        return sb.toString();
    }
}
