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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks an export procedure or function was found in the command module.
 *
 * @author Artem Iliukhin
 */
public final class ExportMethodInCommandFormModuleCheck
    extends BasicCheck
{

    private static final String TYPE_NAME = "NotifyDescription"; //$NON-NLS-1$
    private static final String CHECK_ID = "export-method-in-command-form-module"; //$NON-NLS-1$
    private static final String PARAMETER_NOTIFY_METHODS_EXCLUSION = "notifyDescriptionMethods"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ExportMethodInCommandModule_Do_not_use_export_method_in_commands_module)
            .description(Messages.ExportMethodInCommandModule_Do_not_emded_export_method_in_modules_of_command_des)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE)
            .parameter(PARAMETER_NOTIFY_METHODS_EXCLUSION, String.class, StringUtils.EMPTY,
                Messages.ExportMethodInCommandFormModuleCheck_Notify_description_methods);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;
        ModuleType type = module.getModuleType();
        if (type != ModuleType.COMMAND_MODULE && type != ModuleType.FORM_MODULE)
        {
            return;
        }

        Map<String, Method> exportMethods = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Method method : module.allMethods())
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (method.isExport())
            {
                exportMethods.put(method.getName(), method);
            }
        }

        String parameterMethodNames = parameters.getString(PARAMETER_NOTIFY_METHODS_EXCLUSION);
        if (!StringUtils.isEmpty(parameterMethodNames))
        {
            List<String> list = List.of(parameterMethodNames.split(",\\s*")); //$NON-NLS-1$
            list.forEach(exportMethods::remove);
        }

        if (exportMethods.isEmpty())
        {
            return;
        }

        for (TreeIterator<EObject> iterator = module.eAllContents(); iterator.hasNext();)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            EObject containedObject = iterator.next();
            if (containedObject instanceof OperatorStyleCreator
                && TYPE_NAME.equals(McoreUtil.getTypeName(((OperatorStyleCreator)containedObject).getType())))
            {
                List<Expression> params = ((OperatorStyleCreator)containedObject).getParams();
                if (!params.isEmpty() && params.get(0) instanceof StringLiteral)
                {
                    StringLiteral literal = (StringLiteral)params.get(0);
                    List<String> lines = literal.lines(true);
                    if (!lines.isEmpty())
                    {
                        exportMethods.remove(lines.get(0));
                        if (exportMethods.isEmpty())
                        {
                            return;
                        }
                    }
                }
            }
        }

        for (Method method : exportMethods.values())
        {
            resultAceptor.addIssue(
                Messages.ExportMethodInCommandModule_Do_not_emded_export_method_in_modules_of_command_result, method,
                BslPackage.Literals.METHOD__EXPORT);
        }
    }
}
