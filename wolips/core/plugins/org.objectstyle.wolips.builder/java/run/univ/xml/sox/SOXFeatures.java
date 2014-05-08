/* SOXFeatures.java
 * Created on 23 déc. 2004
 */
package run.univ.xml.sox;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import run.univ.Bool;
import run.univ.xml.SAXFeatures;

/**
 * @author jclain
 */
public class SOXFeatures extends SAXFeatures {
    public static final String MULTIPLE_TOP_ELEMENTS = "http://xml.univ.run/features/MultipleTopElements";

    static final Boolean NAMESPACES_DEFAULT = Boolean.TRUE,
            NAMESPACES_PREFIXES_DEFAULT = Boolean.FALSE, VALIDATION_DEFAULT = Boolean.FALSE,
            MULTIPLE_TOP_ELEMENTS_DEFAULT = Boolean.FALSE;

    static final void initFeatures(Map<String, Boolean> features) {
        features.put(NAMESPACES, NAMESPACES_DEFAULT);
        features.put(NAMESPACES_PREFIXES, NAMESPACES_PREFIXES_DEFAULT);
        features.put(VALIDATION, VALIDATION_DEFAULT);
        features.put(MULTIPLE_TOP_ELEMENTS, MULTIPLE_TOP_ELEMENTS_DEFAULT);
    }

    static final Map<String, Boolean> initFeatures() {
        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        initFeatures(features);
        return features;
    }

    private SOXFeatures() {
    }

    private static SOXFeatures instance;

    static final SOXFeatures getInstance() {
        if (instance == null) instance = new SOXFeatures();
        return instance;
    }

    public void setFeature(Map<String, Boolean> features, String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (VALIDATION.equals(name) && value) {
            throw new SAXNotSupportedException("validation feature cannot be turned on");
        }
        super.setFeature(features, name, value);
    }

    public static boolean isMultipleTopElements(Map<String, Boolean> features) {
        return Bool.valueOf(features.get(SOXFeatures.MULTIPLE_TOP_ELEMENTS)).booleanValue();
    }
}