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
 *     Aleksandr Kapralov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA__SYMBOL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.common.Symbols;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.v8.dt.platform.version.Version;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Checks that pragma &ChangeAndValidate is used when there is no call ProceedWithCall
 *
 * @author Aleksandr Kapralov
 *
 */
public class ChangeAndValidateInsteadOfAroundCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "change-and-validate-instead-of-around"; //$NON-NLS-1$

    private static final String PROCEED_WITH_CALL = "ProceedWithCall"; //$NON-NLS-1$
    private static final String PROCEED_WITH_CALL_RUS = "ПродолжитьВызов"; //$NON-NLS-1$

    private final IRuntimeVersionSupport versionSupport;

    /**
     * Creates new instance which helps to check &Around pragma.
     *
     * @param versionSupport runtime version support manager, cannot be {@code null}.
     */
    @Inject
    public ChangeAndValidateInsteadOfAroundCheck(IRuntimeVersionSupport versionSupport)
    {
        super();

        this.versionSupport = versionSupport;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }

        Pragma pragma = (Pragma)object;

        if (!Symbols.AROUND_ANNOTATION_SYMBOLS.contains(new CaseInsensitiveString(pragma.getSymbol())))
        {
            return;
        }

        Version platformVersion = versionSupport.getRuntimeVersionOrDefault(pragma, Version.LATEST);
        if (platformVersion.isLessThan(Version.V8_3_16))
        {
            return;
        }

        Method method = EcoreUtil2.getContainerOfType(pragma, Method.class);

        if (method == null)
        {
            return;
        }

        boolean hasProceedWithCall = false;
        for (StaticFeatureAccess sfa : EcoreUtil2.eAllOfType(method, StaticFeatureAccess.class))
        {
            String featureName = sfa.getName();
            if (PROCEED_WITH_CALL.equalsIgnoreCase(featureName) || PROCEED_WITH_CALL_RUS.equalsIgnoreCase(featureName))
            {
                hasProceedWithCall = true;
                break;
            }
        }

        if (!hasProceedWithCall)
        {
            resultAceptor.addIssue(
                Messages.ChangeAndValidateInsteadOfAroundCheck_Use_ChangeAndValidate_instead_of_Around, pragma,
                PRAGMA__SYMBOL);
        }

    }

    @Override
    protected void configureCheck(CheckConfigurer configurer)
    {
        configurer.title(Messages.ChangeAndValidateInsteadOfAroundCheck_title)
            .description(Messages.ChangeAndValidateInsteadOfAroundCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.TRIVIAL)
            .issueType(IssueType.CODE_STYLE)
            .module()
            .checkedObjectType(PRAGMA);
    }

}
