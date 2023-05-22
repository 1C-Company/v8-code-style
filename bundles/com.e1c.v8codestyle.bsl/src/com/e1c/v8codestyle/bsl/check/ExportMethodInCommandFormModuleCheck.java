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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks an export procedure or function was found in the command or form module.
 *
 * @author Artem Iliukhin
 */
public final class ExportMethodInCommandFormModuleCheck
    extends BasicCheck
{

    private static final String PATTERN_EXCLUDE = "^(?U)(Подключаемый|Attachable)_.*$"; //$NON-NLS-1$
    private static final String TYPE_NAME_OLD = "NotifyDescription"; //$NON-NLS-1$
    private static final String TYPE_NAME = "CallbackDescription"; //$NON-NLS-1$
    private static final String CHECK_ID = "export-method-in-command-form-module"; //$NON-NLS-1$
    private static final String PARAMETER_NOTIFY_METHODS_EXCLUSION = "notifyDescriptionMethods"; //$NON-NLS-1$
    private static final String PARAMETER_EXCLUDE_METHOD_NAME_PATTERN = "excludeModuleMethodNamePattern"; //$NON-NLS-1$
    private static final String PARAMETER_CHECK_FORM_SERVER_METHODS = "checkFormServerMethods"; //$NON-NLS-1$
    private static final String PARAMETER_CHECK_FORM_CLIENT_METHODS = "checkFormClientMethods"; //$NON-NLS-1$

    private final IBslPreferences bslPreferences;

    /**
     * Instantiates a new export method in command form module check.
     *
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     */
    @Inject
    public ExportMethodInCommandFormModuleCheck(IBslPreferences bslPreferences)
    {
        super();
        this.bslPreferences = bslPreferences;
    }

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
            .extension(new StandardCheckExtension(544, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE)
            .parameter(PARAMETER_CHECK_FORM_SERVER_METHODS, Boolean.class, Boolean.TRUE.toString(),
                Messages.ExportMethodInCommandFormModuleCheck_CheckServerMethodForm)
            .parameter(PARAMETER_CHECK_FORM_CLIENT_METHODS, Boolean.class, Boolean.FALSE.toString(),
                Messages.ExportMethodInCommandFormModuleCheck_CheckClientMethodForm)
            .parameter(PARAMETER_EXCLUDE_METHOD_NAME_PATTERN, String.class, PATTERN_EXCLUDE,
                Messages.ExportMethodInCommandFormModuleCheck_ExludeMethodNamePattern)
            .parameter(PARAMETER_NOTIFY_METHODS_EXCLUSION, String.class, StringUtils.EMPTY,
                Messages.ExportMethodInCommandFormModuleCheck_Notify_description_methods);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;
        ModuleType type = module.getModuleType();
        boolean serverMethodCheck =
            parameters.getBoolean(PARAMETER_CHECK_FORM_SERVER_METHODS) && type == ModuleType.FORM_MODULE;
        boolean clientMethodCheck =
            parameters.getBoolean(PARAMETER_CHECK_FORM_CLIENT_METHODS) && type == ModuleType.FORM_MODULE;
        boolean commandModule = type == ModuleType.COMMAND_MODULE;
        if (!commandModule && !(serverMethodCheck || clientMethodCheck))
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
                String name = method.getName();
                if (name == null)
                {
                    return;
                }

                if (commandModule)
                {
                    exportMethods.put(name, method);
                }
                else if (serverMethodCheck && clientMethodCheck)
                {
                    exportMethods.put(name, method);
                }
                else if (serverMethodCheck ^ clientMethodCheck)
                {
                    Environmental environmental = EcoreUtil2.getContainerOfType(method, Environmental.class);
                    Environments enivronmetsObject = environmental.environments();
                    Environments checkingEnvs = bslPreferences.getLoadEnvs(method)
                        .intersect(serverMethodCheck ? Environments.ALL_SERVERS : Environments.ALL_CLIENTS);
                    if (enivronmetsObject.containsAny(checkingEnvs))
                    {
                        exportMethods.put(name, method);
                    }
                }
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

        String excludeNamePattern = parameters.getString(PARAMETER_EXCLUDE_METHOD_NAME_PATTERN);
        if (!StringUtils.isEmpty(excludeNamePattern))
        {
            for (Iterator<Entry<String, Method>> it = exportMethods.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry<String, Method> entry = it.next();
                if (isExcludeName(entry.getKey(), excludeNamePattern))
                {
                    it.remove();
                }
            }
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
            if (containedObject instanceof OperatorStyleCreator)
            {
                String typeName = McoreUtil.getTypeName(((OperatorStyleCreator)containedObject).getType());
                if (TYPE_NAME_OLD.equals(typeName) || TYPE_NAME.equals(typeName))
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
        }

        for (Method method : exportMethods.values())
        {
            resultAceptor.addIssue(
                Messages.ExportMethodInCommandModule_Do_not_emded_export_method_in_modules_of_command_result, method,
                BslPackage.Literals.METHOD__EXPORT);
        }
    }

    private boolean isExcludeName(String name, String excludeNamePattern)
    {
        return StringUtils.isNotEmpty(excludeNamePattern) && name.matches(excludeNamePattern);
    }
}
