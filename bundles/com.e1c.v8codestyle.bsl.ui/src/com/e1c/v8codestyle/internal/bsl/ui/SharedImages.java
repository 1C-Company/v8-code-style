/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.internal.bsl.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

/**
 * The common constants for plug-in images.
 *
 * @author Dmitriy Marmyshev
 */
public final class SharedImages
{
    private static final String T_OBJ16 = "/obj16/"; //$NON-NLS-1$

    public static final String IMG_OBJ16_FIELD = UiPlugin.PLUGIN_ID + T_OBJ16 + "attribute.png"; //$NON-NLS-1$
    public static final String IMG_OBJ16_LINK = UiPlugin.PLUGIN_ID + T_OBJ16 + "link.png"; //$NON-NLS-1$
    public static final String IMG_OBJ16_TEXT = UiPlugin.PLUGIN_ID + T_OBJ16 + "text.png"; //$NON-NLS-1$
    public static final String IMG_OBJ16_TYPE = UiPlugin.PLUGIN_ID + T_OBJ16 + "type.png"; //$NON-NLS-1$
    public static final String IMG_OBJ16_DESCRIPTION = UiPlugin.PLUGIN_ID + T_OBJ16 + "description.png"; //$NON-NLS-1$

    private static final String ICONS_PATH = "/icons"; //$NON-NLS-1$

    /**
     * Initialize image registry with given instance.
     *
     * @param reg the registry, cannot be {@code null}.
     */
    /* package */ static void initializeImageRegistry(ImageRegistry reg)
    {
        reg.put(IMG_OBJ16_FIELD, createImageDescriptorFromKey(IMG_OBJ16_FIELD));
        reg.put(IMG_OBJ16_LINK, createImageDescriptorFromKey(IMG_OBJ16_LINK));
        reg.put(IMG_OBJ16_TEXT, createImageDescriptorFromKey(IMG_OBJ16_TEXT));
        reg.put(IMG_OBJ16_TYPE, createImageDescriptorFromKey(IMG_OBJ16_TYPE));
        reg.put(IMG_OBJ16_DESCRIPTION, createImageDescriptorFromKey(IMG_OBJ16_DESCRIPTION));
    }

    private static ImageDescriptor createImageDescriptorFromKey(String key)
    {
        String path = ICONS_PATH + key.substring(UiPlugin.PLUGIN_ID.length());
        return UiPlugin.imageDescriptorFromPlugin(UiPlugin.PLUGIN_ID, path);
    }

    private SharedImages()
    {
        throw new IllegalAccessError("Utility class"); //$NON-NLS-1$
    }

}
