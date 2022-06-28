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
 *     Denis Maslennikov - issue #409
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STATIC_FEATURE_ACCESS;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Block;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check if some temporary files are not deleted after opening.
 *
 * @author Denis Maslennikov
 */
public class MissingTemporaryFileDeletionCheck
    extends BasicCheck
{

    private static final String METHOD_NAME = "GetTempFileName"; //$NON-NLS-1$
    private static final String METHOD_NAME_RU = "ПолучитьИмяВременногоФайла"; //$NON-NLS-1$
    private static final String CHECK_ID = "missing-temporary-file-deletion"; //$NON-NLS-1$
    private static final String DOT = "."; //$NON-NLS-1$
    private static final String METHOD_DELIMITER = ","; //$NON-NLS-1$
    private static final String DELETE_FILE_METHODS_PARAM = "deleteFileMethods"; //$NON-NLS-1$
    private static final String DEFAULT_DELETE_FILE_METHODS_PARAM =
        "УдалитьФайлы,DeleteFiles,НачатьУдалениеФайлов,BeginDeletingFiles,ПереместитьФайл,MoveFile"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MissingTemporaryFileDeletionCheck_title)
            .description(Messages.MissingTemporaryFileDeletionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(STATIC_FEATURE_ACCESS)
            .parameter(DELETE_FILE_METHODS_PARAM, String.class, DEFAULT_DELETE_FILE_METHODS_PARAM,
                Messages.MissingTemporaryFileDeletionCheck_Delete_File_Methods);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = BslUtil.getInvocation((StaticFeatureAccess)object);
        if (invocation == null || monitor.isCanceled())
        {
            return;
        }

        StaticFeatureAccess sfa = (StaticFeatureAccess)object;
        String methodName = sfa.getName();

        if (METHOD_NAME.equalsIgnoreCase(methodName) || METHOD_NAME_RU.equalsIgnoreCase(methodName))
        {
            SimpleStatement statement = EcoreUtil2.getContainerOfType(invocation, SimpleStatement.class);
            FeatureAccess tempFile = (FeatureAccess)statement.getLeft();
            String tempFileName = getFullFeatureAccessName(tempFile);
            if (tempFileName == null || monitor.isCanceled())
            {
                return;
            }

            List<String> deleteFileMethods =
                Arrays.asList(parameters.getString(DELETE_FILE_METHODS_PARAM).split(METHOD_DELIMITER));
            deleteFileMethods.replaceAll(String::trim);

            Block block = EcoreUtil2.getContainerOfType(sfa, Block.class);
            boolean isTempFileOpened = false;
            for (FeatureAccess blockFa : EcoreUtil2.eAllOfType(block, FeatureAccess.class))
            {
                String featureName = getFullFeatureAccessName(blockFa);

                if (featureName != null && (METHOD_NAME.equalsIgnoreCase(featureName)
                    || METHOD_NAME_RU.equalsIgnoreCase(featureName) || isTempFileOpened))
                {
                    isTempFileOpened = true;
                    if (deleteFileMethods.contains(featureName) && checkParameterInList(blockFa, tempFileName))
                    {
                        return;
                    }
                }
            }
            resultAcceptor.addIssue(Messages.MissingTemporaryFileDeletionCheck_Missing_Temporary_File_Deletion, sfa);
        }
    }

    private boolean checkParameterInList(FeatureAccess featureAccess, String parameterName)
    {
        Invocation deleteInvocation = BslUtil.getInvocation(featureAccess);
        List<Expression> deleteParameters = deleteInvocation.getParams();
        for (Expression parameter : deleteParameters)
        {
            if (parameter instanceof FeatureAccess)
            {
                String faParameterName = getFullFeatureAccessName((FeatureAccess)parameter);
                return faParameterName != null && faParameterName.equals(parameterName);
            }
        }
        return false;
    }

    private String getFullFeatureAccessName(FeatureAccess featureAccess)
    {
        StringBuilder builder = new StringBuilder();
        Expression expression = featureAccess;

        while (expression instanceof DynamicFeatureAccess)
        {
            DynamicFeatureAccess current = (DynamicFeatureAccess)expression;
            builder.insert(0, current.getName());
            builder.insert(0, DOT);
            expression = current.getSource();
        }
        if (expression instanceof StaticFeatureAccess)
        {
            StaticFeatureAccess staticFeatureAccess = (StaticFeatureAccess)expression;
            builder.insert(0, staticFeatureAccess.getName());
            return builder.toString();
        }
        return null;
    }


}
