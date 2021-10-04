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
package com.e1c.v8codestyle.bsl.strict.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FEATURE_ACCESS__NAME;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Checks that {@link DynamicFeatureAccess dynamic method} exists in accessed object.
 *
 * @author Dmitriy Marmyshev
 */
public class DynamicFeatureAccessMethodNotFoundCheck
    extends AbstractTypeCheck
{

    private static final String CHECK_ID = "dynamic-access-method-not-found"; //$NON-NLS-1$

    /**
     * Instantiates a new dynamic feature access method not found check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     */
    @Inject
    public DynamicFeatureAccessMethodNotFoundCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DynamicFeatureAccessMethodNotFoundCheck_title)
            .description(Messages.DynamicFeatureAccessMethodNotFoundCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StrictTypeAnnotationCheckExtension())
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof EObject))
        {
            return;
        }

        DynamicFeatureAccess fa = (DynamicFeatureAccess)object;
        if (fa.getName() == null || fa.getName().isBlank())
        {
            return;
        }

        boolean isMethod = BslUtil.getInvocation(fa) != null;
        if (isMethod && isEmptySource(fa))
        {
            String message = MessageFormat
                .format(Messages.DynamicFeatureAccessTypeCheck_Method_M_not_found_in_accessed_object, fa.getName());

            resultAceptor.addIssue(message, FEATURE_ACCESS__NAME);
        }

    }

    private boolean isEmptySource(DynamicFeatureAccess object)
    {
        Environmental envs = EcoreUtil2.getContainerOfType(object, Environmental.class);
        if (envs == null)
        {
            return true;
        }

        Environments actualEnvs = bslPreferences.getLoadEnvs(object).intersect(envs.environments());
        if (actualEnvs.isEmpty())
        {
            return true;
        }
        List<FeatureEntry> objects = dynamicFeatureAccessComputer.getLastObject(object, actualEnvs);
        return objects.isEmpty();
    }
}
