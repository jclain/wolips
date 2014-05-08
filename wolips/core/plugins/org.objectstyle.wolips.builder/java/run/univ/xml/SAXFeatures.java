/* SAXFeatures.java
 * Created on 12 janv. 2009
 */
package run.univ.xml;

import java.util.Map;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import run.univ.Bool;

/**
 * @author jclain
 */
public class SAXFeatures {
    public static final String NAMESPACES = "http://xml.org/sax/features/namespaces";

    public static final String NAMESPACES_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

    public static final String VALIDATION = "http://xml.org/sax/features/validation";

    protected boolean getFeatureNoCheck(Map<String, Boolean> features, String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        return features.get(name).booleanValue();
    }

    public boolean getFeature(Map<String, Boolean> features, String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (features == null || !features.containsKey(name)) {
            throw new SAXNotRecognizedException("unrecognized feature: " + name);
        }
        return getFeatureNoCheck(features, name);
    }

    protected void setFeatureNoCheck(Map<String, Boolean> features, String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        features.put(name, new Boolean(value));
    }

    public void setFeature(Map<String, Boolean> features, String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (features == null || !features.containsKey(name)) {
            throw new SAXNotRecognizedException("unrecognized feature: " + name);
        }
        setFeatureNoCheck(features, name, value);
    }

    public static boolean isNamespaceAware(Map<String, Boolean> features) {
        return Bool.valueOf(features.get(NAMESPACES)).booleanValue();
    }

    public static boolean isNamespacePrefixes(Map<String, Boolean> features) {
        return Bool.valueOf(features.get(NAMESPACES_PREFIXES)).booleanValue();
    }

    public static boolean isValidating(Map<String, Boolean> features) {
        return Bool.valueOf(features.get(VALIDATION)).booleanValue();
    }
}