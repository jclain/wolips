/* BigDecTools.java
 * Created on 10 sept. 2009
 */
package run.univ.base.types.simple;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jclain
 */
public class BigDecTools {
    public static final String toString(BigDecimal value) {
        if (value == null) return null;
        else return value.toString();
    }

    public static final <S extends Appendable> S append(S sb, BigDecimal value) throws IOException {
        if (value != null) sb.append(toString(value));
        return sb;
    }

    public static final StringBuffer append(StringBuffer sb, BigDecimal value) {
        if (value != null) sb.append(toString(value));
        return sb;
    }

    public static final StringBuilder append(StringBuilder sb, BigDecimal value) {
        if (value != null) sb.append(toString(value));
        return sb;
    }

    public static final BigDecimal valueOf(BigDecimal value, BigDecimal defaultValue) {
        if (value != null) return value;
        else return defaultValue;
    }

    /** Retourner value si value!=<code>null</code>, {@link BigDecimal#ZERO} sinon. */
    public static final BigDecimal nnOrZero(BigDecimal value) {
        return valueOf(value, BigDecimal.ZERO);
    }

    /** Retourner value si value!=<code>null</code>, {@link BigDecimal#ONE} sinon. */
    public static final BigDecimal nnOrOne(BigDecimal value) {
        return valueOf(value, BigDecimal.ONE);
    }

    public static final BigDecimal valueOf(String value, BigDecimal defaultValue) {
        if (value != null) return new BigDecimal(value);
        else return defaultValue;
    }

    public static final BigDecimal valueOf(Number value, BigDecimal defaultValue) {
        if (value != null) return new BigDecimal(value.toString());
        else return defaultValue;
    }

    public static final BigDecimal valueOf(BigDecimal value) {
        return value;
    }

    public static final BigDecimal valueOf(String value) {
        return valueOf(value, null);
    }

    public static final BigDecimal valueOf(Number value) {
        return valueOf(value, null);
    }

    /** Arrondir la valeur au nombre spécifié de chiffres après la virgule. */
    public static final BigDecimal round(BigDecimal value, int scale) {
        if (value != null) value = value.setScale(scale, RoundingMode.HALF_UP);
        return value;
    }

    /** Modifier l'échelle pour supprimer les zéros inutiles après la virgule. */
    public static final BigDecimal stripTz(BigDecimal value) {
        if (value != null) {
            value = value.stripTrailingZeros();
            if (value.scale() < 0) value = value.setScale(0);
        }
        return value;
    }

    /** Tester l'égalité de deux valeurs décimales. */
    public static final boolean equals(BigDecimal d1, BigDecimal d2) {
        return d1 == d2 || (d1 != null && d1.equals(d2));
    }

    /** Comparer deux valeurs décimales. */
    public static final int compare(BigDecimal d1, BigDecimal d2) {
        if (d1 == null && d2 == null) return 0;
        if (d1 == null) return -1;
        if (d2 == null) return 1;
        return d1.compareTo(d2);
    }

    public static final int hashCode(BigDecimal d) {
        if (d == null) return 0;
        else return d.hashCode();
    }
}
