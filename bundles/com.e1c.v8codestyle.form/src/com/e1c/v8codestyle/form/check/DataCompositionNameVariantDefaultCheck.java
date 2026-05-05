/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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

package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA;
import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA__SETTINGS_VARIANTS;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import com._1c.g5.v8.dt.dcs.model.core.Presentation;
import com._1c.g5.v8.dt.dcs.model.schema.DataCompositionSchema;
import com._1c.g5.v8.dt.dcs.model.settings.SettingsVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

/**
 * Check data coposition schema variant name.
 * @author Ivan Sergeev
 */
public class DataCompositionNameVariantDefaultCheck
    extends BasicCheck<Object>
{
    private static final String CHECK_ID = "data-composition-variant-name-default"; //$NON-NLS-1$
    private static final String VARIANT_NAME = "Report variant name"; //$NON-NLS-1$
    private static final String DELIMITER = ","; //$NON-NLS-1$
    private static final Set<String> DEFAULT_NAME_LIST = Set.of("Основной", "Default"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final String DEFAULT_NAME = String.join(DELIMITER, DEFAULT_NAME_LIST);

    @Inject
    public DataCompositionNameVariantDefaultCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DataCompositionNameVariantDefault_Title)
            .description(Messages.DataCompositionNameVariantDefault_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(674, getCheckId(), CorePlugin.PLUGIN_ID))
            .parameter(VARIANT_NAME, String.class, DEFAULT_NAME,
                Messages.DataCompositionNameVariantDefault_Parametr_Title)
            .topObject(DATA_COMPOSITION_SCHEMA)
            .features(DATA_COMPOSITION_SCHEMA__SETTINGS_VARIANTS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        DataCompositionSchema dcs = (DataCompositionSchema)object;

        EList<SettingsVariant> variants = dcs.getSettingsVariants();
        if (variants == null)
        {
            return;
        }
        for (SettingsVariant settingsVariant : variants)
        {
            String name = settingsVariant.getName();
            Presentation presentation = settingsVariant.getPresentation();
            EMap<String, String> presentationValue = presentation.getLocalValue().getContent();
            if (parameters.getString(VARIANT_NAME).toLowerCase().contains(name.toLowerCase()))
            {
                if (presentationValue.isEmpty())
                {
                    resultAcceptor.addIssue(Messages.DataCompositionNameVariantDefault_Issue);
                    continue;
                }
                String presentationName = presentationValue.get(0).getValue();
                if (parameters.getString(VARIANT_NAME).toLowerCase().contains(presentationName.toLowerCase()))
                {
                    resultAcceptor.addIssue(Messages.DataCompositionNameVariantDefault_Issue);
                }
            }
            else
            {
                if (presentationValue.isEmpty())
                {
                    continue;
                }
                else
                {
                    String presentationName = presentationValue.get(0).getValue();
                    if (parameters.getString(VARIANT_NAME).toLowerCase().contains(presentationName.toLowerCase()))
                    {
                        resultAcceptor.addIssue(Messages.DataCompositionNameVariantDefault_Issue);
                    }
                }
            }
        }
    }
}
