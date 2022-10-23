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

package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_COMMAND__ACTION;

import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com._1c.g5.v8.dt.form.model.CommandHandler;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormCommand;
import com._1c.g5.v8.dt.form.model.util.ModelUtils;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;

/**
 * The check for the {@link Form} that each command is assigned to single action handler
 *
 * @author Artem Iliukhin
 */
public class FormCommandsSingleEventHandlerCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-commands-single-action-handler"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormCommandsSingleEventHandlerCheck_Title)
            .description(Messages.FormCommandsSingleEventHandlerCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.WARNING)
            .extension(new CommandHandlerChangeExtension())
            .extension(new StandardCheckExtension(455, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FORM)
            .checkTop();
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (!(object instanceof Form))
        {
            return;
        }

        Form form = (Form)object;

        SubMonitor subMonitor = SubMonitor.convert(monitor, form.getFormCommands().size());

        Map<String, String> handlers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (FormCommand formCommand : form.getFormCommands())
        {
            subMonitor.split(1);
            check(resultAceptor, handlers, formCommand);
        }
    }

    private void check(ResultAcceptor resultAceptor, Map<String, String> handlers, FormCommand formCommand)
    {
        String commandName = formCommand.getName();
        for (CommandHandler commandHandler : ModelUtils.getCommandHandlers(formCommand))
        {
            String nameHandler = commandHandler.getName();
            if (handlers.containsKey(nameHandler))
            {
                resultAceptor.addIssue(
                    MessageFormat.format(
                        Messages.FormCommandsSingleEventHandlerCheck_Handler__0__command__1__assigned_to_command__2,
                        nameHandler, commandName, handlers.get(nameHandler)),
                    formCommand, FORM_COMMAND__ACTION);
            }
            else
            {
                handlers.put(nameHandler, commandName);
            }
        }
    }
}
