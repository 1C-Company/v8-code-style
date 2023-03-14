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
 *******************************************************************************/

package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.CONTAINING_SOURCE_DERIVED_PROPERTY__CONTAINING_SOURCE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.DERIVED_PROPERTY__SOURCE;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com._1c.g5.v8.dt.mcore.ContainingSourceDerivedProperty;
import com._1c.g5.v8.dt.mcore.DerivedProperty;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check the use unknown form parameter access in form module
 * @author Vadim Goncharov
 */
public class UnknownFormParameterAccessCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "unknown-form-parameter-access"; //$NON-NLS-1$

    private static final String PARAMETERS_KEYWORD = "Parameters"; //$NON-NLS-1$

    private static final String PARAMETERS_KEYWORD_RU = "Параметры"; //$NON-NLS-1$

    private final DynamicFeatureAccessComputer dynamicComputer;

    /**
     * Instantiates a new unknown form parameter access check.
     *
     * @param dynamicComputer the dynamic computer, cannot be {@code null}
     */
    @Inject
    public UnknownFormParameterAccessCheck(DynamicFeatureAccessComputer dynamicComputer)
    {
        super();
        this.dynamicComputer = dynamicComputer;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.UnknownFormParameterAccessCheck_title)
            .description(Messages.UnknownFormParameterAccessCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(741, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DynamicFeatureAccess dfa = (DynamicFeatureAccess)object;

        String dfaName = dfa.getName();
        Expression src = dfa.getSource();
        if (!(src instanceof StaticFeatureAccess) || !isFormParameterAccess((StaticFeatureAccess)src)
            || monitor.isCanceled())
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(dfa, Module.class);

        if (!monitor.isCanceled() && isEmptySource(dynamicComputer.resolveObject(dfa, module.environments())))
        {
            resultAcceptor.addIssue(
                MessageFormat.format(Messages.UnknownFormParameterAccessCheck_Unknown_form_parameter_access, dfaName),
                dfa);
        }
    }

    private boolean isFormParameterAccess(StaticFeatureAccess sfa)
    {
        String name = sfa.getName();
        return name.equalsIgnoreCase(PARAMETERS_KEYWORD) || name.equalsIgnoreCase(PARAMETERS_KEYWORD_RU);
    }

    // TODO replace this method with BslUtil after 2022.2+
    private static boolean isEmptySource(Collection<FeatureEntry> features)
    {
        if (features.isEmpty())
        {
            return true;
        }

        return features.stream().allMatch(e -> {
            if (e.getFeature() instanceof ContainingSourceDerivedProperty)
            {
                return e.getFeature().eGet(CONTAINING_SOURCE_DERIVED_PROPERTY__CONTAINING_SOURCE, false) == null;
            }
            else if (e.getFeature() instanceof DerivedProperty)
            {
                return e.getFeature().eGet(DERIVED_PROPERTY__SOURCE, false) == null;
            }
            return false;
        });
    }

}
