/* BigDec.java
 * Created on 10 juin 2009
 */
package run.univ.wosrc.rubasem;

import java.math.BigDecimal;


/**
 * Des méthodes utilitaires pour gérer les instances de {@link BigDecimal}.
 * 
 * @author jclain
 */
public class BigDec extends BigDecTools {
//    public static final BigDecimal valueOf(IValue<?> value, BigDecimal defaultValue) {
//        if (value != null) return valueOf(value.toString(), defaultValue);
//        else return defaultValue;
//    }

    public static final BigDecimal valueOf(Object value, BigDecimal defaultValue) {
        if (value instanceof BigDecimal) return (BigDecimal)value;
        else if (Obj.isNull(value)) return defaultValue;
        else if (value instanceof String) return new BigDecimal((String)value);
        else if (value instanceof Number) return new BigDecimal(((Number)value).toString());
//        else if (value instanceof IValue) {
//            return valueOf(((IValue<?>)value).toString(), defaultValue);
//        } 
        else throw Exc.cannotConvert(value, BigDecimal.class);
    }

//    public static final BigDecimal valueOf(IValue<?> value) {
//        return valueOf(value, null);
//    }

    public static final BigDecimal valueOf(Object value) {
        return valueOf(value, null);
    }

    public static final BigDecimal valueOf(BigDecimal value, int scale) {
        if (value == null) return null;
        else return value.setScale(scale);
    }

    public static final BigDecimal valueOf(Object value, int scale) {
        if (value instanceof BigDecimal) return ((BigDecimal)value).setScale(scale);
        else if (Obj.isNull(value)) return null;
        else return valueOf(value).setScale(scale);
    }
}
