/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.WEB_SERVICE;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.Operation;
import com._1c.g5.v8.dt.metadata.mdclass.Parameter;
import com._1c.g5.v8.dt.metadata.mdclass.WebService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.google.common.base.CharMatcher;

/**
 *  Checking the naming of web service and operations with parameters
 *  Searching for non-english letters and the substr "service"
 *
 *  @author Artem Samohvalov
 */
public class WebServiceNameCheck
    extends BasicCheck<Object>
{

    @Override
    public String getCheckId()
    {
        return "web-service-name"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.WebServiceNameCheck_title)
            .description(Messages.WebServiceNameCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(550, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(WEB_SERVICE)
            .checkTop();
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        WebService webService = (WebService)object;

        onlyEnglishLettersCheck(webService, resultAcceptor);
        containsCheckedLiteralsCheck(webService, resultAcceptor);
        onlyEnglishLettersInParamCheck(webService, resultAcceptor);
    }

    private void onlyEnglishLettersCheck(WebService webService, ResultAcceptor resultAcceptor)
    {
        if (!onlyEnglishLetters(webService.getName()))
        {
            resultAcceptor.addIssue(Messages.WebServiceNameCheck_only_english_issue, webService);
        }
    }

    private void containsCheckedLiteralsCheck(WebService webService, ResultAcceptor resultAcceptor)
    {
        if (webService.getName().toLowerCase().contains("service")) //$NON-NLS-1$
        {
            resultAcceptor.addIssue(Messages.WebServiceNameCheck_service_substr_issue, webService);
        }
    }

    private void onlyEnglishLettersInParamCheck(WebService webService, ResultAcceptor resultAcceptor)
    {
        for (Operation operation : webService.getOperations())
        {
            if (!onlyEnglishLetters(operation.getName()))
            {
                resultAcceptor.addIssue(Messages.WebServiceNameCheck_only_english_operation_issue, webService);
            }
            for (Parameter parameter : operation.getParameters())
            {
                if (!onlyEnglishLetters(parameter.getName()))
                {
                    resultAcceptor.addIssue(Messages.WebServiceNameCheck_only_english_param_issue, webService);
                }
            }
        }
    }

    private boolean onlyEnglishLetters(String str)
    {
        return str != null && CharMatcher.ascii().matchesAllOf(str); // only ASCII
    }
}
