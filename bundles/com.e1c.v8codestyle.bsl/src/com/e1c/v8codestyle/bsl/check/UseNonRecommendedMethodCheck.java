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
 *     Sergey Kozynskiy - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.mcore.DuallyNamedElement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * The check on use non-recommended method
 *
 * @author Sergey Kozynskiy
 *
 */
public class UseNonRecommendedMethodCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "use-non-recommended-method"; //$NON-NLS-1$

    private static final String PARAM_METHODS_LIST = "nonRecommendedMethods"; //$NON-NLS-1$

    private static final Set<String> NON_RECOMENDED_METHODS_LIST = Set.of("CurrentDate", //$NON-NLS-1$
        "ТекущаяДата", //$NON-NLS-1$
        "Message", //$NON-NLS-1$
        "Сообщить"); //$NON-NLS-1$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_METHODS_LIST = String.join(DELIMITER, NON_RECOMENDED_METHODS_LIST);

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.UseNonRecommendedMethods_title)
            .description(Messages.UseNonRecommendedMethods_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.ERROR)
            .module()
            .checkedObjectType(INVOCATION);
        builder.parameter(PARAM_METHODS_LIST, String.class, DEFAULT_METHODS_LIST,
            Messages.UseNonRecommendedMethods_parameter);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = (Invocation)object;

        FeatureAccess featureAccess = invocation.getMethodAccess();
        if (monitor.isCanceled() || !(featureAccess instanceof StaticFeatureAccess))
        {
            return;
        }

        StaticFeatureAccess staticFeatureAccess = (StaticFeatureAccess)featureAccess;

        EList<FeatureEntry> listFeatureEntries = staticFeatureAccess.getFeatureEntries();

        if (monitor.isCanceled() || listFeatureEntries.isEmpty())
        {
            return;
        }

        EObject feature = listFeatureEntries.get(0).getFeature();

        if (monitor.isCanceled() || !(feature instanceof DuallyNamedElement))
        {
            return;
        }

        DuallyNamedElement duallyName = (DuallyNamedElement)feature;
        String name = duallyName.getName();
        String ruName = duallyName.getNameRu();

        Set<String> methods = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String paramString = parameters.getString(PARAM_METHODS_LIST);
        Set<String> params = Set.of(paramString.replace(" ", "").split(DELIMITER)); //$NON-NLS-1$ //$NON-NLS-2$
        methods.addAll(params);

        if (methods.contains(name) || methods.contains(ruName))
        {
            resultAceptor.addIssue(Messages.UseNonRecommendedMethods_message, invocation);
        }
    }
}