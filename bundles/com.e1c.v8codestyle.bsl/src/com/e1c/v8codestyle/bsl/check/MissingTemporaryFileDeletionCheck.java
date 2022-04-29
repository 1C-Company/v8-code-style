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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Module;
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

    private static final String NAME = "GetTempFileName"; //$NON-NLS-1$
    private static final String NAME_RU = "ПолучитьИмяВременногоФайла"; //$NON-NLS-1$
    private static final String CHECK_ID = "missing-temporary-file-deletion"; //$NON-NLS-1$
    private static final String METHOD_DELIMITER = "\\|"; //$NON-NLS-1$
    private static final String DELETE_FILE_METHODS_PARAM = "deleteFileMethods"; //$NON-NLS-1$
    private static final String DEFAULT_DELETE_FILE_METHODS_PARAM =
        "УдалитьФайлы|DeleteFiles|НачатьУдалениеФайлов|BeginDeletingFiles|ПереместитьФайл|MoveFile"; //$NON-NLS-1$

    private Map<String, StaticFeatureAccess> undeletedFiles = new HashMap<>();

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
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE)
            .parameter(DELETE_FILE_METHODS_PARAM, String.class, DEFAULT_DELETE_FILE_METHODS_PARAM,
                Messages.MissingTemporaryFileDeletionCheck_Delete_File_Methods);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof Module))
        {
            return;
        }

        Module module = (Module)object;
        if (!addOpenedFiles(module, monitor))
        {
            return;
        }

        List<String> deleteFileMethods =
            Arrays.asList(parameters.getString(DELETE_FILE_METHODS_PARAM).split(METHOD_DELIMITER));
        deleteFileMethods.replaceAll(String::trim);
        if (!removeDeletedFiles(module, monitor, deleteFileMethods))
        {
            return;
        }

        undeletedFiles.values()
            .forEach(feature -> resultAcceptor
                .addIssue(Messages.MissingTemporaryFileDeletionCheck_Missing_Temporary_File_Deletion, feature));
    }

    private boolean addOpenedFiles(Module module, IProgressMonitor monitor)
    {
        for (StaticFeatureAccess sfa : EcoreUtil2.eAllOfType(module, StaticFeatureAccess.class))
        {
            if (monitor.isCanceled())
            {
                return false;
            }

            String name = sfa.getName();
            if (name.equalsIgnoreCase(NAME_RU) || name.equalsIgnoreCase(NAME))
            {
                Invocation invocation = BslUtil.getInvocation(sfa);
                SimpleStatement statement = EcoreUtil2.getContainerOfType(invocation, SimpleStatement.class);
                String temporaryFileName = ((StaticFeatureAccess)statement.getLeft()).getName();
                undeletedFiles.put(temporaryFileName, sfa);
            }
        }
        return true;
    }

    private boolean removeDeletedFiles(Module module, IProgressMonitor monitor, List<String> deleteFileMethods)
    {
        for (StaticFeatureAccess sfa : EcoreUtil2.eAllOfType(module, StaticFeatureAccess.class))
        {
            if (monitor.isCanceled())
            {
                return false;
            }
            String name = sfa.getName();
            if (deleteFileMethods.contains(name))
            {
                Invocation invocation = BslUtil.getInvocation(sfa);
                List<Expression> parameters = invocation.getParams();
                for (Expression param : parameters)
                {
                    if (param instanceof StaticFeatureAccess)
                    {
                        String parameterName =
                            ((StaticFeatureAccess)parameters.get(parameters.indexOf(param))).getName();
                        undeletedFiles.keySet().removeIf(key -> key.equals(parameterName));
                    }
                }
            }
        }
        return true;
    }
}
