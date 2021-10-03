/**
 *
 */
package com.e1c.v8codestyle.internal.bsl.ui.properties;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.internal.bsl.ui.properties.messages"; //$NON-NLS-1$
    public static String ModuleStructurePropertyPage_Automatically_create_module_structure;
    public static String ModuleStructurePropertyPage_Open_template;
    public static String ModuleStructurePropertyPage_Open_template_tooltip;
    public static String ModuleStructurePropertyPage_Save_custom_template_to_project_settings;
    public static String ModuleStructurePropertyPage_Save_settings;
    public static String ModuleStructurePropertyPage_Select_module_type_to_create_custom_structure_templates;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
