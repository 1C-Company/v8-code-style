/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *     Vadim Goncharov - issue #262
 *******************************************************************************/

package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.dcs.model.settings.DcsPackage.Literals.DATA_COMPOSITION_CONDITIONAL_APPEARANCE;
import static com._1c.g5.v8.dt.dcs.model.settings.DcsPackage.Literals.DATA_COMPOSITION_CONDITIONAL_APPEARANCE__ITEMS;
import static com._1c.g5.v8.dt.dcs.model.settings.DcsPackage.Literals.DATA_COMPOSITION_SETTINGS;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.dcs.model.settings.DataCompositionConditionalAppearance;
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.metadata.ExternalPropertyManagerProvider;
import com._1c.g5.v8.dt.metadata.IExternalPropertyManager;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

/**
 * The check find form attributes of type "Dynamic List" that use conditional appearance.
 * @author Vadim Goncharov
 */
public class DynamicListConditionalAppearanceUseCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-dynamic-list-conditional-appearance-use"; //$NON-NLS-1$

    private final IBmModelManager bmModelManager;

    /**
     * Instantiates a new dynamic list conditional appearance use check.
     *
     * @param bmModelManager the BmModelManager
     */
    @Inject
    public DynamicListConditionalAppearanceUseCheck(IBmModelManager bmModelManager)
    {
        super();
        this.bmModelManager = bmModelManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DynamicListConditionalAppearanceUseCheck_title)
            .description(Messages.DynamicListConditionalAppearanceUseCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(710, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(DATA_COMPOSITION_SETTINGS)
            .containment(DATA_COMPOSITION_CONDITIONAL_APPEARANCE)
            .features(DATA_COMPOSITION_CONDITIONAL_APPEARANCE__ITEMS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        DataCompositionConditionalAppearance dcca = (DataCompositionConditionalAppearance)object;
        if (monitor.isCanceled() || dcca.getItems() == null || dcca.getItems().isEmpty())
        {
            return;
        }

        IExternalPropertyManager manager =
            ExternalPropertyManagerProvider.INSTANCE.getExternalPropertyManager(bmModelManager.getModel(dcca));
        if (manager == null)
        {
            throw new IllegalStateException("ExternalPropertyManagerProvider not initialized");
        }

        DynamicListExtInfo dl = manager.getOwner((EObject)dcca, DynamicListExtInfo.class);
        if (monitor.isCanceled() || dl == null)
        {
            return;
        }
        FormAttribute formAttribute = EcoreUtil2.getContainerOfType(dl, FormAttribute.class);
        if (monitor.isCanceled() || formAttribute == null)
        {
            return;
        }

        resultAceptor.addIssue(MessageFormat.format(
            Messages.DynamicListConditionalAppearanceUseCheck_Dynamic_list_use_conditional_appearance,
            formAttribute.getName()), dcca, DATA_COMPOSITION_CONDITIONAL_APPEARANCE__ITEMS);

    }
}
