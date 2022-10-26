/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Invocation;
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
 * Check excessive self references in modules.
 * For form modules only check self reference for methods and existing properties
 * (if PARAMETER_CHECK_ONLY_EXISTING_FORM_PROPERTIES is set, otherwise, check for all cases)
 *
 * @author Maxim Galios
 *
 */
public class SelfReferenceCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "module-self-reference"; //$NON-NLS-1$

    private static final Collection<String> EXCESSIVE_NAMES = Set.of("ЭтотОбъект", "ThisObject"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final String PARAMETER_CHECK_ONLY_EXISTING_FORM_PROPERTIES = "checkOnlyExistingFormProperties"; //$NON-NLS-1$

    private DynamicFeatureAccessComputer dynamicFeatureAccessComputer;

    /**
     * Instantiates a new self reference check.
     *
     * @param dynamicFeatureAccessComputer dynamic feature computer, cannot be {@code null}.
     */
    @Inject
    public SelfReferenceCheck(DynamicFeatureAccessComputer dynamicFeatureAccessComputer)
    {
        super();
        this.dynamicFeatureAccessComputer = dynamicFeatureAccessComputer;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.SelfReferenceCheck_Title)
            .description(Messages.SelfReferenceCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(467, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS)
            .parameter(PARAMETER_CHECK_ONLY_EXISTING_FORM_PROPERTIES, Boolean.class, Boolean.TRUE.toString(),
                Messages.SelfReferenceCheck_check_only_existing_form_properties);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DynamicFeatureAccess dynamicFeatureAccess = (DynamicFeatureAccess)object;

        Expression featureAccessSource = dynamicFeatureAccess.getSource();
        if (monitor.isCanceled() || !(featureAccessSource instanceof StaticFeatureAccess))
        {
            return;
        }

        StaticFeatureAccess source = (StaticFeatureAccess)featureAccessSource;

        if (isReferenceExcessive(dynamicFeatureAccess, source,
            parameters.getBoolean(PARAMETER_CHECK_ONLY_EXISTING_FORM_PROPERTIES)))
        {
            resultAceptor.addIssue(Messages.SelfReferenceCheck_Issue,
                source);
        }
    }

    private boolean isReferenceExcessive(DynamicFeatureAccess dynamicFeatureAccess, StaticFeatureAccess source,
        boolean checkOnlyExistingFormProperties)
    {
        if (!EXCESSIVE_NAMES.contains(source.getName()))
        {
            return false;
        }
        if (!checkOnlyExistingFormProperties || (dynamicFeatureAccess.eContainer() instanceof Invocation))
        {
            return true;
        }

        Module module = EcoreUtil2.getContainerOfType(dynamicFeatureAccess, Module.class);

        return !(module.getModuleType() == ModuleType.FORM_MODULE
            && isEmptySource(dynamicFeatureAccessComputer.resolveObject(dynamicFeatureAccess, module.environments())));
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
