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
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__SYNONYM;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.google.inject.Inject;

/**
 * Check MD object synonym should not be empty or should match a pattern
 *
 * @author Dmitriy Marmyshev
 */
public final class MdObjectSynonymCheck
    extends AbstractUiStringCheck
{
    private static final String CHECK_ID = "mdo-synonym"; //$NON-NLS-1$
    private static final String PARAM_CHECK_SUB_OBJECTS = "checkSubObjects"; //$NON-NLS-1$

    @Inject
    public MdObjectSynonymCheck(IV8ProjectManager v8ProjectManager)
    {
        super(MD_OBJECT__SYNONYM, v8ProjectManager);
    }
    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        super.configureCheck(builder);
        builder.title("Metada object synonym")
            .description("Metada object synonym")
            .extension(new StandardCheckExtension(474, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(SkipAdoptedInExtensionMdObjectExtension.instance())
            .topObject(MD_OBJECT)
            .checkTop()
            .containment(MD_OBJECT)
            .features(MD_OBJECT__SYNONYM)
            .parameter(PARAM_CHECK_SUB_OBJECTS, Boolean.class, Boolean.TRUE.toString(),
                "Check subordinate MD-objects");
    }

    @Override
    protected String getUiStringPatternTitle()
    {
        return "Synonym pattern";
    }

    @Override
    protected String getUiStringIsEmptyForAll()
    {
        return "Synonym is empty for all languages";
    }

    @Override
    protected String getUiStringIsEmpty(String languageCode)
    {
        return MessageFormat.format("Synonym for language \"{0}\" is empty", languageCode);
    }

    @Override
    protected String getUiStringShouldMatchPattern(String languageCode, String patternText)
    {
        return MessageFormat.format("Synonym for language \"{0}\" should match pattern: \"{1}\"", languageCode,
            patternText);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof Configuration || object instanceof IBmObject && !((IBmObject)object).bmIsTop()
            && !parameters.getBoolean(PARAM_CHECK_SUB_OBJECTS))
        {
            // Skip here, there is another synonym check for Configuration or if not need to check sub-object
            return;
        }
        super.check(object, resultAceptor, parameters, monitor);
    }

}
