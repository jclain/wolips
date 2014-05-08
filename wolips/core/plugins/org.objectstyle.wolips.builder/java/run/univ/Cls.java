/* Cls.java
 * Created on 4 septembre 2004, 14:44
 */

package run.univ;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Des outils pour gérer les classes java: instanciation, invocation de méthode.
 * <p>
 * Les méthodes lancent {@link IllegalArgumentException}si une erreur se produit.
 * </p>
 * 
 * @author jclain
 */
public class Cls {
    static final Class[][] type_map = new Class[][] {
            new Class[] {Boolean.class, Boolean.TYPE},
            new Class[] {Character.class, Character.TYPE},
            new Class[] {Byte.class, Byte.TYPE},
            new Class[] {Short.class, Short.TYPE},
            new Class[] {Integer.class, Integer.TYPE},
            new Class[] {Long.class, Long.TYPE},
            new Class[] {Float.class, Float.TYPE},
            new Class[] {Double.class, Double.TYPE},
            new Class[] {Void.class, Void.TYPE}};

    /**
     * Retourner true si parmi les classes du tableau, l'une des classes peut être mappée vers un
     * type primitif.
     * 
     * @param types un tableau non nul.
     */
    private static boolean canMapTypes(Class[] types) {
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < type_map.length; j++) {
                if (type_map[j][0] == types[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * retourner un tableau de classes, ou les classes ayant un type primitif correspondant ont été
     * mappées vers le type primitif.
     * <p>
     * e.g., <code>mapTypes(new Class[]{Integer.class})</code> retourne le tableau
     * <code>{Integer.TYPE}</code>
     * </p>
     */
    private static Class[] mapTypes(Class[] types) {
        types = (Class[])types.clone();
        for (int i = 0; i < types.length; i++) {
            if (types[0] != null) {
                for (int j = 0; j < type_map.length; j++) {
                    if (type_map[j][0] == types[i]) {
                        types[i] = type_map[j][1];
                        break;
                    }
                }
            }
        }
        return types;
    }

    // private static Class mapToPrimitive(Class type) {
    // for (int i = 0; i < type_map.length; i++) {
    // if (type_map[i][0] == type) return type_map[i][1];
    // }
    // return null;
    // }

    private static Class mapToWrapper(Class type) {
        for (int i = 0; i < type_map.length; i++) {
            if (type_map[i][1] == type) return type_map[i][0];
        }
        return null;
    }

    /**
     * Obtenir un constructeur avec les arguments dont le type est dans le tableau de classes.
     * <p>
     * Si un constructeur avec un type d'objet n'est pas trouvé, on essaye avec les types primitifs
     * associés. Ainsi, le constructeur <code>MyClass(int i)</code> sera trouvé avec
     * <code>getConstructor(MyClass.class, new Class[]{Integer.class})</code>
     * </p>
     * <p>
     * Si un élément du tableau est null, cela signifie que l'on accepte n'importe quel type de
     * données à cette position.
     * </p>
     * 
     * @param c une classe dont on veut le constructeur
     * @param types un tableau, éventuellement null, de classes d'objets représentant le type des
     *        arguments. Si le tableau vaut null, on cherche un constructeur sans arguments.
     * @return une instance de {@link Constructor}.
     * @throws WrappedException si le constructeur n'a pas été trouvé.
     * @throws IllegalArgumentException si le constructeur n'est pas public.
     */
    public static Constructor getConstructor(Class c, Class[] types) {
        try {
            return c.getConstructor(types);
        } catch (NoSuchMethodException e) {
            if (types == null) {
                throw new IllegalArgumentException("Pas de constructeur par défaut: " + c.getName());
            }

            // Si le constructeur n'est pas trouvé, re-essayer en remplacant les classes
            // représentant les types de bases par les types correspondant.
            if (canMapTypes(types)) {
                try {
                    return c.getConstructor(mapTypes(types));
                } catch (NoSuchMethodException f) {
                }
            }

            // si le constructeur n'a toujours pas été trouvé, on parcours la liste des
            // constructeurs et on cherche un constructeur qui correspond aux types des arguments
            // qui ont été donnés.
            Constructor[] declaredConstructors = c.getDeclaredConstructors();
            for (int i = 0; i < declaredConstructors.length; i++) {
                Constructor cons = declaredConstructors[i];
                Class[] params = cons.getParameterTypes();

                // chercher les constructeurs qui ont le même nombre d'arguments...
                if (params.length != types.length) continue;

                // pour chacun des arguments, vérifier la compatibilité
                boolean found = true;
                for (int j = 0; j < params.length; j++) {
                    Class paramClass = params[j];
                    if (paramClass.isPrimitive()) paramClass = mapToWrapper(paramClass);
                    if (types[j] != null && !paramClass.isAssignableFrom(types[j])) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    if (!Modifier.isPublic(cons.getModifiers())) {
                        throw new IllegalArgumentException("Le constructeur doit être public: "
                                + c.getName());
                    }
                    return cons;
                }
            }

            throw new RuntimeException(e);
        }
    }

    /**
     * Obtenir un constructeur avec les arguments dont le type est déduit du tableau d'objet donné.
     * <p>
     * Ce tableau pourra ainsi être utilisé avec {@link #makeInstance(Constructor, Object[])}
     * </p>
     * 
     * @see #getConstructor(Class, Class[])
     */
    public static Constructor getConstructor(Class c, Object[] args) {
        Class[] cs = null;
        if (args != null) {
            cs = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                Object o = args[i];
                cs[i] = o != null? o.getClass(): null;
            }
        }
        return getConstructor(c, cs);
    }

    /**
     * Obtenir un constructeur ayant un seul argument du type de l'objet arg.
     * 
     * @see #getConstructor(Class, Class[])
     */
    public static Constructor getConstructor(Class c, Object arg) {
        Class[] cs = new Class[] {arg != null? arg.getClass(): null};
        return getConstructor(c, cs);
    }

    /**
     * Obtenir le constructeur par défaut.
     * 
     * @see #getConstructor(Class, Class[])
     */
    public static Constructor getConstructor(Class c) {
        return getConstructor(c, (Class[])null);
    }

    /**
     * Créer une instance d'un objet étant donne son constructeur et une liste d'arguments.
     * 
     * @throws WrappedException si une erreur se produit lors de l'appel au constructeur.
     */
    public static Object makeInstance(Constructor cons, Object[] args) {
        try {
            return cons.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Créer une instance d'un objet étant donné son constructeur acceptant comme argument un objet
     * unique arg.
     * 
     * @see #makeInstance(Constructor, Object[])
     */
    public static Object makeInstance(Constructor cons, Object arg) {
        return makeInstance(cons, new Object[] {arg});
    }

    /**
     * équivalent à <code>makeInstance(getConstructor(c, args), args)</code>
     * 
     * @see #getConstructor(Class, Class[])
     * @see #makeInstance(Constructor, Object[])
     */
    public static Object makeInstance(Class c, Object[] args) {
        return makeInstance(getConstructor(c, args), args);
    }

    /**
     * équivalent à <code>makeInstance(getConstructor(c, arg), arg)</code>
     * 
     * @see #getConstructor(Class, Class[])
     * @see #makeInstance(Constructor, Object[])
     */
    public static Object makeInstance(Class c, Object arg) {
        return makeInstance(getConstructor(c, arg), arg);
    }

    /**
     * équivant à <code>makeInstance(getConstructor(c), null)</code>
     * 
     * @see #getConstructor(Class)
     * @see #makeInstance(Constructor, Object[])
     */
    public static Object makeInstance(Class c) {
        if (c == null) return null;
        try {
            return c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** @return une instance de la classe nommée className. */
    public static Object makeInstance(String className) {
        try {
            return makeInstance(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtenir une méthode ayant un argument unique du type de l'objet arg.
     * <p>
     * TODO: Si un constructeur avec un type d'objet n'est pas trouvé, on essaye avec les types
     * primitifs associés. Ainsi, la méthode <code>MyMeth(int i)</code> sera trouvé avec
     * <code>getMethod(MyClass.class, "MyMeth", new Integer(0))</code>
     * </p>
     * 
     * @param c une classe dont on veut trouver la méthode
     * @param arg un objet. on cherche une méthode ayant un argument compatible avec ce type
     *        d'objet. Si arg est nul, on recherche une méthode n'ayant pas d'argument.
     * @return une instance de {@link Method}.
     * @throws IllegalArgumentException si la méthode n'a pas été trouvée.
     */
    public static Method getMethod(Class c, String m, Object arg) {
        if (arg == null) {
            try {
                return c.getMethod(m, null);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        Class argc = arg.getClass();
        try {
            return c.getMethod(m, new Class[] {argc});
        } catch (NoSuchMethodException e) {
            // essayer avec le type primitif associé
            for (int i = 0; i < type_map.length; i++) {
                if (type_map[i][0] == argc) {
                    try {
                        return c.getMethod(m, new Class[] {type_map[i][1]});
                    } catch (NoSuchMethodException f) {
                    }
                }
            }

            // essayer aussi avec les supertypes de argc
            argc = argc.getSuperclass();
            while (argc != null) {
                try {
                    return c.getMethod(m, new Class[] {argc});
                } catch (NoSuchMethodException f) {
                }
                argc = argc.getSuperclass();
            }

            // en dernière extrémité, lancer l'exception originale
            throw new RuntimeException(e);
        }
    }

    /**
     * équivalent à <code>getMethod(c, m, null)</code>.
     * 
     * @see #getMethod(Class, String, Object)
     */
    public static Method getMethod(Class c, String m) {
        return getMethod(c, m, null);
    }

    /**
     * invoquer la méthode meth (de l'objet o si o est non nul), avec les arguments args
     * 
     * @param o un objet non nul, sauf si meth est une méthode statique.
     * @param meth une instance de Method
     * @param args un tableau d'argument, éventuellement nul.
     * @return le résultat de la méthode.
     * @throws WrappedException si une exception se produit durant l'appele de la méthode.
     */
    public static Object invokeMethod(Object o, Method meth, Object[] args) {
        try {
            return meth.invoke(o, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * équivalent à <code>invokeMethod(o, meth, new Object[]{arg})</code>
     * 
     * @see #invokeMethod(Object, Method, Object[])
     */
    public static Object invokeMethod(Object o, Method meth, Object arg) {
        return invokeMethod(o, meth, new Object[] {arg});
    }

    /**
     * équivalent à <code>invokeMethod(o, meth, null)</code>
     * 
     * @see #invokeMethod(Object, Method, Object[])
     */
    public static Object invokeMethod(Object o, Method meth) {
        return invokeMethod(o, meth, null);
    }

    /**
     * invoquer la méthode m de l'objet o avec l'argument arg.
     */
    public static Object invokeMethod(Object o, String m, Object arg) {
        return invokeMethod(o, getMethod(o.getClass(), m, arg));
    }

    /**
     * invoquer la méthode statique m de la classe c avec l'argument arg.
     */
    public static Object invokeMethod(Class c, String m, Object arg) {
        return invokeMethod(null, getMethod(c, m, arg), arg);
    }

    /**
     * invoquer la méthode m de l'objet o.
     */
    public static Object invokeMethod(Object o, String m) {
        return invokeMethod(o, getMethod(o.getClass(), m));
    }

    /**
     * obtenir le nom de la classe sans le package.
     * <p>
     * Si c est une classe imbriquée, le nom des classes parentes est inclu aussi.
     * </p>
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
     * obtenir le nom de la classe sans le package.
     * <p>
     * Si c est une classe imbriquée, le nom des classes parentes est inclu aussi.
     * </p>
     */
    public static final String classname(Class c) {
        if (c == null) return null;
        return classname(c.getName());
    }

    /** obtenir le nom de base de la classe, sans les classes parentes. */
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

    /** obtenir le nom de base de la classe, sans les classes parentes. */
    public static final String basename(Class c) {
        if (c == null) return null;
        return basename(c.getName());
    }

    /**
     * Obtenir la valeur d'un champ statique d'une classe.
     * 
     * @param required indique que le champ est requis. Si le champ n'est pas requis et qu'il n'est
     *        pas présent, retourner null.
     */
    public static Object getStaticField(Class c, String fieldName, Class fieldClass,
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
            if (fieldValue != null && !fieldClass.isInstance(fieldValue)) {
                throw new IllegalArgumentException("Le champ " + fieldName + " de la classe "
                        + c.getName() + " doit être du type " + fieldClass.getName());
            }
            return fieldValue;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            if (required) {
                throw new IllegalArgumentException("La classe " + c.getName()
                        + " doit contenir le champ " + fieldName);
            } else {
                return null;
            }
        }
    }

    private static String prefixNameWith(String prefix, String name) {
        int prefix_length = prefix.length();
        StringBuffer sb = new StringBuffer(name.length() + prefix_length);
        sb.append(prefix);
        sb.append(name);
        sb.setCharAt(prefix_length, Character.toUpperCase(sb.charAt(prefix_length)));
        return sb.toString();
    }

    private static Method getMethodForProperty(Class c, String propertyName, int prefix) {
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
            m = c.getMethod(propertyName, null);
        } catch (SecurityException e) {
            throw new IllegalArgumentException("La méthode " + propertyName
                    + " n'est pas accessible");
        } catch (NoSuchMethodException e) {
            m = null;
        }
        return m;
    }

    private static Field getFieldForProperty(Class c, String propertyName, int prefix) {
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
     * {@link Field}ou {@link Method}selon le type de propriété.
     * <p>
     * Soit une propriété prop, on essaie de lire dans l'ordre les méthode prop(), getProp(),
     * isProp(), _getProp(), _isProp(). Si une de ces méthodes est trouvée, on retourne une instance
     * de {@link Method}. Sinon on essaie de lire les champs prop, isProp, _prop et _isProp. Si un
     * de ces champs est trouvé, on retourne une instance de {@link Field}.
     * </p>
     * 
     * @param c la classe à partir duquel on lit la propriété.
     * @param propName nom de la propriété.
     * @return une instance de {@link Method}, une instance de {@link Field}ou null si la propriété
     *         n'a pas été trouvée.
     */
    public static Object getPropertyObject(Class c, String propName) {
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
     * ou {@link Method}selon le type de propriété.
     * <p>
     * Soit une propriété prop, on essaie de lire dans l'ordre les méthode prop(), getProp(),
     * isProp(), _getProp(), _isProp(). Si une de ces méthodes est trouvée, on retourne une instance
     * de {@link Method}. Sinon on essaie de lire les champs prop, isProp, _prop et _isProp. Si un
     * de ces champs est trouvé, on retourne une instance de {@link Field}.
     * </p>
     * 
     * @param obj l'objet à partir duquel on lit la propriété.
     * @param propName nom de la propriété.
     * @return une instance de {@link Method}, une instance de {@link Field}ou null si la propriété
     *         n'a pas été trouvée.
     */
    public static Object getPropertyObject(Object obj, String propName) {
        return getPropertyObject(obj.getClass(), propName);
    }

    private static String getPropName(Object propObject) {
        if (propObject instanceof String) return (String)propObject;
        else if (propObject instanceof Field) return ((Field)propObject).getName();
        else if (propObject instanceof Method) return ((Method)propObject).getName();
        else return null;
    }

    private static Object getPropValue(Object obj, Object propObject, Object field, Class propClass) {
        Object value;
        if (field instanceof Field) {
            Field f = (Field)field;
            try {
                value = f.get(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (field instanceof Method) {
            Method m = (Method)field;
            try {
                value = m.invoke(obj, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Champ non trouvé: " + getPropName(propObject));
        }
        if (value != null && propClass != null && propClass.isInstance(propObject)) {
            throw new IllegalArgumentException("Le champ " + getPropName(propObject)
                    + " doit être du type " + propClass.getName());
        }
        return value;
    }

    /**
     * Obtenir la valeur d'une propriété d'un objet. Soit une propriété prop, on essaie de lire dans
     * l'ordre les méthode prop(), getProp(), isPropr(), _getProp(), _isProp(), puis les champs
     * prop, isProp, _prop et _isProp.
     * 
     * @param obj l'objet à partir duquel on lit la propriété.
     * @param propObject nom de la propriété (instance de String), ou instance de {@link Field}ou de
     *        {@link Method}obtenue de la méthode {@link #getPropertyObject(Object, String)}.
     * @param propClass Si non nul, on s'attend à ce que la valeur retournée soit du type
     *        fieldClass.
     * @return la valeur de la propriété
     * @throws WrappedException si une erreur se produit lors de l'accès à la propriété.
     * @throws IllegalArgumentException si le champ n'est pas trouvé ou n'est pas du bon type.
     */
    public static Object getProperty(Object obj, Object propObject, Class propClass) {
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
     * @param propObject nom de la propriété (instance de String), ou instance de {@link Field}ou de
     *        {@link Method}obtenue de la méthode {@link #getPropertyObject(Object, String)}.
     * @param propClass Si non nul, on s'attend à ce que la valeur retournée soit du type
     *        fieldClass.
     * @return la valeur de la propriété
     * @throws WrappedException si une erreur se produit lors de l'accès à la propriété.
     * @throws IllegalArgumentException si le champ n'est pas trouvé ou n'est pas du bon type.
     */
    public static Object getProperty(Class c, Object propObject, Class propClass) {
        Object field = propObject;
        if (field instanceof String) field = getPropertyObject(c, (String)field);

        return getPropValue(null, propObject, field, propClass);
    }

    public static Class valueOf(String className) {
        if (Str.isempty(className)) return null;
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean exists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /** Si objectOrClass est une classe, retourner la classe. Sinon, retourner la classe de l'objet. */
    public static final Class getClass(Object objectOrClass) {
        if (objectOrClass == null || objectOrClass instanceof Class) return (Class)objectOrClass;
        return objectOrClass.getClass();
    }
}