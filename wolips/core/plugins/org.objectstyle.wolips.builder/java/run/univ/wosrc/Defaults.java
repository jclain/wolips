/* Defaults.java
 * Created on 12 janv. 2009
 */
package run.univ.wosrc;

import java.util.HashMap;

/**
 * @author jclain
 */
public class Defaults {
    public static final String WO_ALIAS = "wo";

    public static final String QATTR_ALIAS = "wo.qattrs";

    public static final String QATTR_PREFIX = "q";

    public static final String WO_NS = "http://xml.univ.run/wotools/webobjects";

    public static final String QATTR_NS = "http://xml.univ.run/wotools/quoted_attributes";

    public static final HashMap<String, String> TAG_ALIASES = new HashMap<String, String>();
    static {
        TAG_ALIASES.put("ActiveImage", "WOActiveImage");
        TAG_ALIASES.put("activeimage", "WOActiveImage");
        TAG_ALIASES.put("ComponentContent", "WOComponentContent");
        TAG_ALIASES.put("componentcontent", "WOComponentContent");
        TAG_ALIASES.put("GenericElement", "WOGenericElement");
        TAG_ALIASES.put("genericelement", "WOGenericElement");
        TAG_ALIASES.put("Image", "WOImage");
        TAG_ALIASES.put("image", "WOImage");
        TAG_ALIASES.put("String", "WOString");
        TAG_ALIASES.put("string", "WOString");
        TAG_ALIASES.put("str", "WOString");
        TAG_ALIASES.put("HiddenField", "WOHiddenField");
        TAG_ALIASES.put("hiddenfield", "WOHiddenField");
        TAG_ALIASES.put("hidden", "WOHiddenField");
        TAG_ALIASES.put("PasswordField", "WOPasswordField");
        TAG_ALIASES.put("passwordfield", "WOPasswordField");
        TAG_ALIASES.put("password", "WOPasswordField");
        TAG_ALIASES.put("Text", "WOText");
        TAG_ALIASES.put("textarea", "WOText");
        TAG_ALIASES.put("TextField", "WOTextField");
        TAG_ALIASES.put("textfield", "WOTextField");
        TAG_ALIASES.put("text", "WOTextField");
        TAG_ALIASES.put("ImageButton", "WOImageButton");
        TAG_ALIASES.put("imagebutton", "WOImageButton");
        TAG_ALIASES.put("ResetButton", "WOResetButton");
        TAG_ALIASES.put("resetbutton", "WOResetButton");
        TAG_ALIASES.put("reset", "WOResetButton");
        TAG_ALIASES.put("SubmitButton", "WOSubmitButton");
        TAG_ALIASES.put("submitbutton", "WOSubmitButton");
        TAG_ALIASES.put("submit", "WOSubmitButton");
        TAG_ALIASES.put("CheckBox", "WOCheckBox");
        TAG_ALIASES.put("checkbox", "WOCheckBox");
        TAG_ALIASES.put("RadioButton", "WORadioButton");
        TAG_ALIASES.put("radiobutton", "WORadioButton");
        TAG_ALIASES.put("radio", "WORadioButton");
        TAG_ALIASES.put("Browser", "WOBrowser");
        TAG_ALIASES.put("browser", "WOBrowser");
        TAG_ALIASES.put("PopUpButton", "WOPopUpButton");
        TAG_ALIASES.put("popupbutton", "WOPopUpButton");
        TAG_ALIASES.put("select", "WOPopUpButton");
        TAG_ALIASES.put("FileUpload", "WOFileUpload");
        TAG_ALIASES.put("fileupload", "WOFileUpload");
        TAG_ALIASES.put("RadioButtonList", "WORadioButtonList");
        TAG_ALIASES.put("radiobuttonlist", "WORadioButtonList");
        TAG_ALIASES.put("CheckBoxList", "WOCheckBoxList");
        TAG_ALIASES.put("checkboxlist", "WOCheckBoxList");
        TAG_ALIASES.put("MetaRefresh", "WOMetaRefresh");
        TAG_ALIASES.put("metarefresh", "WOMetaRefresh");
        TAG_ALIASES.put("ActionURL", "WOActionURL");
        TAG_ALIASES.put("actionurl", "WOActionURL");
        TAG_ALIASES.put("JavaScript", "WOJavaScript");
        TAG_ALIASES.put("javascript", "WOJavaScript");
        TAG_ALIASES.put("ResourceURL", "WOResourceURL");
        TAG_ALIASES.put("resourceurl", "WOResourceURL");
        TAG_ALIASES.put("VBScript", "WOVBScript");
        TAG_ALIASES.put("vbscript", "WOVBScript");
        TAG_ALIASES.put("Applet", "WOApplet");
        TAG_ALIASES.put("applet", "WOApplet");
        TAG_ALIASES.put("Body", "WOBody");
        TAG_ALIASES.put("body", "WOBody");
        TAG_ALIASES.put("Conditional", "WOConditional");
        TAG_ALIASES.put("conditional", "WOConditional");
        TAG_ALIASES.put("cond", "WOConditional");
        TAG_ALIASES.put("EmbeddedObject", "WOEmbeddedObject");
        TAG_ALIASES.put("embeddedobject", "WOEmbeddedObject");
        TAG_ALIASES.put("Form", "WOForm");
        TAG_ALIASES.put("form", "WOForm");
        TAG_ALIASES.put("Frame", "WOFrame");
        TAG_ALIASES.put("frame", "WOFrame");
        TAG_ALIASES.put("GenericContainer", "WOGenericContainer");
        TAG_ALIASES.put("genericcontainer", "WOGenericContainer");
        TAG_ALIASES.put("Hyperlink", "WOHyperlink");
        TAG_ALIASES.put("hyperlink", "WOHyperlink");
        TAG_ALIASES.put("link", "WOHyperlink");
        TAG_ALIASES.put("NestedList", "WONestedList");
        TAG_ALIASES.put("nestedlist", "WONestedList");
        TAG_ALIASES.put("Param", "WOParam");
        TAG_ALIASES.put("param", "WOParam");
        TAG_ALIASES.put("Repetition", "WORepetition");
        TAG_ALIASES.put("repetition", "WORepetition");
        TAG_ALIASES.put("repeat", "WORepetition");
        TAG_ALIASES.put("SwitchComponent", "WOSwitchComponent");
        TAG_ALIASES.put("switchcomponent", "WOSwitchComponent");
        TAG_ALIASES.put("XMLNode", "WOXMLNode");
        TAG_ALIASES.put("xmlnode", "WOXMLNode");
    }
}
