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
 *     Sergey Kozynskiy - issue #100
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

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
 * The check on use not recommended methods
 *
 * @author Sergey Kozynskiy
 *
 */
public class UseNotRecommendedMethods
    extends BasicCheck
{

    private static final String CHECK_ID = "use-not-recommended-methods"; //$NON-NLS-1$

    private static final String PARAM_METHODS_LIST = "notRecommendedMethods"; //$NON-NLS-1$

    private static final String DEFAULT_METHODS_LIST = ""; //$NON-NLS-1$

    private static Set<String> NOT_RECOMENDED_METHODS_LIST = Set.of("CurrentDate", //$NON-NLS-1$
        "ТекущаяДата", //$NON-NLS-1$
        "Message", //$NON-NLS-1$
        "Сообщить"); //$NON-NLS-1$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.UseNotRecommendedMethods_title)
            .description(Messages.UseNotRecommendedMethods_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.BLOCKER)
            .issueType(IssueType.ERROR)
            .module()
            .checkedObjectType(INVOCATION);
        builder.parameter(PARAM_METHODS_LIST, String.class, DEFAULT_METHODS_LIST, Messages.UseNotRecommendedMethods_parameter);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = (Invocation)object;
        StaticFeatureAccess staticFeatureAccess = (StaticFeatureAccess)invocation.getMethodAccess();

        if (monitor.isCanceled() || !(staticFeatureAccess instanceof StaticFeatureAccess))
        {
            return;
        }

        EList<FeatureEntry> listFeatureEntrys = staticFeatureAccess.getFeatureEntries();

        if (monitor.isCanceled() || listFeatureEntrys.isEmpty())
        {
            return;
        }

        DuallyNamedElement dd = (DuallyNamedElement)listFeatureEntrys.get(0).getFeature();
        String name = dd.getName().toLowerCase();
        String ruName = dd.getNameRu().toLowerCase();

        Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String paramString = parameters.getString(PARAM_METHODS_LIST);
        Set<String> params = Set.of(paramString.replace(" ", "").split(DELIMITER)); //$NON-NLS-1$ //$NON-NLS-2$
        result.addAll(params);
        result.addAll(NOT_RECOMENDED_METHODS_LIST);

        if (result.contains(name) || result.contains(ruName))
        {
            resultAceptor.addIssue(Messages.UseNotRecommendedMethods_message, invocation);
        }
    }
}