/* Cls.java
 * Created on 23 oct. 2009
 */
package run.univ.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import run.univ.base.util.exc.RunUnivRuntimeException;

/**
 * Des outils pour gérer les classes.
 * 
 * @author jclain
 */
public class Cls {
    private static final StringBuilder getName(Type type, StringBuilder sb) {
        if (type instanceof Class) {
            sb.append(((Class<?>)type).getName());
        } else if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            Type otype = ptype.getOwnerType();
            if (otype != null) {
                getName(otype, sb);
                sb.append(".");
            }
            getName(ptype.getRawType(), sb);
            sb.append("<");
            boolean first = true;
            for (Type atype : ptype.getActualTypeArguments()) {
                if (first) first = false;
                else sb.append(",");
                getName(atype, sb);
            }
            sb.append(">");
        } else {
            sb.append("?");
        }
        return sb;
    }

    /**
     * Obtenir le nom pleinement qualifié d'un type. Seuls sont supportés les classes et les types
     * paramétrés.
     */
    public static final String getName(Type type) {
        if (type instanceof Class) {
            return ((Class<?>)type).getName();
        } else if (type instanceof ParameterizedType) {
            return getName(type, new StringBuilder()).toString();
        } else return null;
    }

    /** Générer un nom de classe paramétré de la forme <code>cls&lt;param0,...&gt;</code>. */
    public static final String getName(Class<?> cls, Class<?>... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getName());
        sb.append("<");
        boolean first = true;
        for (Class<?> param : params) {
            if (first) first = false;
            else sb.append(",");
            sb.append(param.getName());
        }
        sb.append(">");
        return sb.toString();
    }

    /**
     * Si object est une instance de la classe cls, retourner cette instance.
     * 
     * @throws ClassCastException si object n'est pas une instance de cls.
     */
    @SuppressWarnings("unchecked")
    public static final <T> T cast(Object o, Class<T> cls) {
        if (cls == null) return (T)o;
        else return cls.cast(o);
    }

    /**
     * Si object est une instance de la classe cls, retourner cette instance, sinon retourner
     * <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public static final <T> T castOrNull(Object o, Class<T> cls) {
        if (cls == null) return (T)o;
        else return cls.isInstance(o)? cls.cast(o): null;
    }

    /**
     * Enlever le package du nom de classe cn. Si cn est une classe imbriquée, le nom des classes
     * parentes *n'est pas* enlevé.
     */
    public static final String classname(String cn) {
        if (cn == null) return null;

        int pos;
        if ((pos = cn.lastIndexOf('.')) != -1) {
            cn = cn.substring(pos + 1);
        }
        return cn;
    }

    /**
     * Obtenir le nom de la classe c sans le package. Si c est une classe imbriquée, le nom des
     * classes parentes est inclu aussi.
     */
    public static final String classname(Class<?> c) {
        if (c == null) return null;
        return classname(c.getName());
    }

    /** Enlever le package et le nom des classes parentes si nécessaire du nom de classe cn. */
    public static final String basename(String cn) {
        if (cn == null) return null;

        int pos;
        if ((pos = cn.lastIndexOf('.')) != -1) {
            cn = cn.substring(pos + 1);
        }
        if ((pos = cn.lastIndexOf('$')) != -1) {
            cn = cn.substring(pos + 1);
        }
        return cn;
    }

    /** obtenir le nom de base de la classe c, sans les classes parentes. */
    public static final String basename(Class<?> c) {
        if (c == null) return null;
        return basename(c.getName());
    }

    /**
     * Obtenir la valeur d'un champ statique d'une classe.
     * 
     * @param required indique que le champ est requis. Si le champ n'est pas requis et qu'il n'est
     *        pas présent, retourner <code>null</code>.
     */
    public static final <F> F getStaticField(Class<?> c, String fieldName, Class<F> fieldClass,
            boolean required) {
        try {
            Field field = c.getField(fieldName);
            Object fieldValue;
            try {
                fieldValue = field.get(null);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Le champ " + fieldName + " de la classe "
                        + c.getName() + " doit être déclaré static");
            }
            if (fieldValue == null) return null;
            if (!fieldClass.isInstance(fieldValue)) {
                throw new IllegalArgumentException("Le champ " + fieldName + " de la classe "
                        + c.getName() + " doit être du type " + fieldClass.getName());
            }
            return fieldClass.cast(fieldValue);
        } catch (IllegalAccessException e) {
            throw new RunUnivRuntimeException(e);
        } catch (NoSuchFieldException e) {
            if (required) {
                throw new IllegalArgumentException("La classe " + c.getName()
                        + " doit contenir le champ " + fieldName);
            } else {
                return null;
            }
        }
    }

    private static final String prefixNameWith(String prefix, String name) {
        int prefix_length = prefix.length();
        StringBuilder sb = new StringBuilder(name.length() + prefix_length);
        sb.append(prefix);
        sb.append(name);
        sb.setCharAt(prefix_length, Character.toUpperCase(sb.charAt(prefix_length)));
        return sb.toString();
    }

    private static final Method getMethodForProperty(Class<?> c, String propertyName, int prefix) {
        switch (prefix) {
        case 0:
            // pas de préfixe
            break;
        case 1:
            // "get" + Method
            propertyName = prefixNameWith("get", propertyName);
            break;
        case 2:
            // "is" + Method
            propertyName = prefixNameWith("is", propertyName);
            break;
        case 3:
            // "_get" + Method
            propertyName = prefixNameWith("_get", propertyName);
            break;
        case 4:
            // "_is" + Method
            propertyName = prefixNameWith("_is", propertyName);
            break;
        }
        Method m;
        try {
            m = c.getMethod(propertyName, (Class[])null);
        } catch (SecurityException e) {
            throw new IllegalArgumentException("La méthode " + propertyName
                    + " n'est pas accessible");
        } catch (NoSuchMethodException e) {
            m = null;
        }
        return m;
    }

    private static final Field getFieldForProperty(Class<?> c, String propertyName, int prefix) {
        switch (prefix) {
        case 0:
            // pas de préfixe
            break;
        case 1:
            // "is" + Field
            propertyName = prefixNameWith("is", propertyName);
            break;
        case 2:
            // "_is" + Field
            propertyName = prefixNameWith("_is", propertyName);
            break;
        }
        Field f;
        try {
            f = c.getField(propertyName);
        } catch (SecurityException e) {
            throw new IllegalArgumentException("Le champ " + propertyName + " n'est pas accessible");
        } catch (NoSuchFieldException e) {
            f = null;
        }
        return f;
    }

    /**
     * Obtenir l'objet à utiliser pour accéder à une propriété statique d'une classe, instance de
     * {@link Field} ou {@link Method} selon le type de propriété.
     * <p>
     * Soit une propriété prop, on essaie de lire dans l'ordre les méthode prop(), getProp(),
     * isProp(), _getProp(), _isProp(). Si une de ces méthodes est trouvée, on retourne une instance
     * de {@link Method}. Sinon on essaie de lire les champs prop, isProp, _prop et _isProp. Si un
     * de ces champs est trouvé, on retourne une instance de {@link Field}.
     * </p>
     * 
     * @param c la classe à partir duquel on lit la propriété.
     * @param propName nom de la propriété.
     * @return une instance de {@link Method}, une instance de {@link Field} ou <code>null</code> si
     *         la propriété n'a pas été trouvée.
     */
    public static final Object getPropertyObject(Class<?> c, String propName) {
        // tout d'abord, chercher une méthode du nom fieldName
        Method m = getMethodForProperty(c, propName, 0);
        if (m == null) m = getMethodForProperty(c, propName, 1);
        if (m == null) m = getMethodForProperty(c, propName, 2);
        if (m == null) m = getMethodForProperty(c, propName, 3);
        if (m == null) m = getMethodForProperty(c, propName, 4);
        if (m != null) return m;

        Field f = getFieldForProperty(c, propName, 0);
        if (f == null) f = getFieldForProperty(c, propName, 1);
        if (f == null) f = getFieldForProperty(c, propName, 2);
        if (f != null) return f;

        return null;
    }

    /**
     * Obtenir l'objet à utiliser pour accéder à une propriété d'un objet, instance de {@link Field}
     * ou {@link Method} selon le type de propriété.
     * <p>
     * Soit une propriété prop, on essaie de lire dans l'ordre les méthode prop(), getProp(),
     * isProp(), _getProp(), _isProp(). Si une de ces méthodes est trouvée, on retourne une instance
     * de {@link Method}. Sinon on essaie de lire les champs prop, isProp, _prop et _isProp. Si un
     * de ces champs est trouvé, on retourne une instance de {@link Field}.
     * </p>
     * 
     * @param obj l'objet à partir duquel on lit la propriété.
     * @param propName nom de la propriété.
     * @return une instance de {@link Method}, une instance de {@link Field} ou <code>null</code> si
     *         la propriété n'a pas été trouvée.
     */
    public static final Object getPropertyObject(Object obj, String propName) {
        return getPropertyObject(obj.getClass(), propName);
    }

    private static final String getPropName(Object propObject) {
        if (propObject instanceof String) return (String)propObject;
        else if (propObject instanceof Field) return ((Field)propObject).getName();
        else if (propObject instanceof Method) return ((Method)propObject).getName();
        else return null;
    }

    private static final <P> P getPropValue(Object obj, Object propObject, Object field,
            Class<P> propClass) {
        Object value;
        if (field instanceof Field) {
            Field f = (Field)field;
            try {
                value = f.get(obj);
            } catch (Exception e) {
                throw new RunUnivRuntimeException(e);
            }
        } else if (field instanceof Method) {
            Method m = (Method)field;
            try {
                value = m.invoke(obj, (Object[])null);
            } catch (Exception e) {
                throw new RunUnivRuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Champ non trouvé: " + getPropName(propObject));
        }
        if (value != null && propClass != null && propClass.isInstance(propObject)) {
            throw new IllegalArgumentException("Le champ " + getPropName(propObject)
                    + " doit être du type " + propClass.getName());
        }
        return castOrNull(value, propClass);
    }

    /**
     * Obtenir la valeur d'une propriété d'un objet. Soit une propriété prop, on essaie de lire dans
     * l'ordre les méthode prop(), getProp(), isPropr(), _getProp(), _isProp(), puis les champs
     * prop, isProp, _prop et _isProp.
     * 
     * @param obj l'objet à partir duquel on lit la propriété.
     * @param propObject nom de la propriété (instance de String), ou instance de {@link Field} ou
     *        de {@link Method} obtenue de la méthode {@link #getPropertyObject(Object, String)}.
     * @param propClass Si non <code>null</code>, on s'attend à ce que la valeur retournée soit du
     *        type fieldClass.
     * @return la valeur de la propriété
     * @throws RunUnivRuntimeException si une erreur se produit lors de l'accès à la propriété.
     * @throws IllegalArgumentException si le champ n'est pas trouvé ou n'est pas du bon type.
     */
    public static final <P> P getProperty(Object obj, Object propObject, Class<P> propClass) {
        Object field = propObject;
        if (field instanceof String) field = getPropertyObject(obj, (String)field);

        return getPropValue(obj, propObject, field, propClass);
    }

    /**
     * Obtenir la valeur d'une propriété statique d'une classe. Soit une propriété prop, on essaie
     * de lire dans l'ordre les méthode prop(), getProp(), isPropr(), _getProp(), _isProp(), puis
     * les champs prop, isProp, _prop et _isProp.
     * 
     * @param c la classe à partir de laquelle on lit la propriété.
     * @param propObject nom de la propriété (instance de String), ou instance de {@link Field} ou
     *        de {@link Method} obtenue de la méthode {@link #getPropertyObject(Object, String)}.
     * @param propClass Si non <code>null</code>, on s'attend à ce que la valeur retournée soit du
     *        type fieldClass.
     * @return la valeur de la propriété
     * @throws RunUnivRuntimeException si une erreur se produit lors de l'accès à la propriété.
     * @throws IllegalArgumentException si le champ n'est pas trouvé ou n'est pas du bon type.
     */
    public static final <P> P getProperty(Class<?> c, Object propObject, Class<P> propClass) {
        Object field = propObject;
        if (field instanceof String) field = getPropertyObject(c, (String)field);

        return getPropValue(null, propObject, field, propClass);
    }

    /**
     * Obtenir la classe dont on donne le nom, et s'assurer qu'elle est une classe dérivée de
     * baseClass.
     * 
     * @throws IllegalArgumentException si la classe n'est pas trouvée ou pas du bon type.
     */
    @SuppressWarnings("unchecked")
    public static final <T> Class<T> valueOf(String className, Class<T> baseClass) {
        try {
            if (className == null) return null;
            Class<?> actualClass = Class.forName(className);
            if (baseClass != null && !baseClass.isAssignableFrom(actualClass)) {
                throw new IllegalArgumentException("La classe " + className
                        + " doit dériver de la classe " + getName(baseClass));
            }
            return (Class<T>)actualClass;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Classe non trouvée: " + className, e);
        }
    }

    /**
     * Obtenir la classe dont on donne le nom
     * 
     * @throws RunUnivRuntimeException si la classe n'est pas trouvée.
     */
    public static final Class<?> valueOf(String className) {
        return valueOf(className, null);
    }

    /** Tester si la classe spécifiée existe. */
    public static final boolean exists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /** Si objectOrClass est une classe, retourner la classe. Sinon, retourner la classe de l'objet. */
    public static final Class<?> getClass(Object objectOrClass) {
        if (objectOrClass == null || objectOrClass instanceof Class) {
            return (Class<?>)objectOrClass;
        } else return objectOrClass.getClass();
    }

    /**
     * Tester si l'objet o est une instance de la classe dont on donne le nom. Si la classe n'est
     * pas trouvée, retourner <code>false</code>.
     */
    public static final boolean isInstance(Object o, String className) {
        try {
            if (className == null) return false;
            Class<?> c = Class.forName(className);
            return c.isInstance(o);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /** Créer une instance de la class c. */
    public static final <T> T makeInstance(Class<T> c) {
        try {
            return c.newInstance();
        } catch (Exception e) {
            throw new RunUnivRuntimeException(Exc.unwrap(e, null));
        }
    }

    private static final void ensureInitialized(String className, ClassLoader classLoader)
            throws ClassNotFoundException {
        Class.forName(className, true, classLoader);
    }

    /**
     * S'assurer qu'une classe est initialisée. Cette méthode est utile si une classe non instanciée
     * contient du code dans une section static.
     * <p>
     * caller est l'objet qui appelle cette méthode. Cet argument est utilisé pour récupérer le
     * class loader.
     * </p>
     */
    public static final void ensureInitialized(Class<?> c, Object caller) {
        if (c == null) return;
        if (caller == null) throw new NullPointerException("caller must not be null");
        try {
            ensureInitialized(c.getName(), caller.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
        }
    }

    /**
     * S'assurer qu'une classe est initialisée. Cette méthode est utile si une classe non instanciée
     * contient du code dans une section static.
     * 
     * @param cl classLoader à utiliser pour charger éventuellement la classe
     */
    public static final void ensureInitialized(Class<?> c, ClassLoader cl) {
        if (c == null) return;
        try {
            ensureInitialized(c.getName(), cl);
        } catch (ClassNotFoundException e) {
        }
    }
}
