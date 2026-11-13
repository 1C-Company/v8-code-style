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
package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.BUTTON;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_GROUP;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.TITLED__TITLE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EMap;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.form.model.Button;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormGroup;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.FormItemContainer;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.context.OnModelFeatureChangeContextCollector;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;

/**
 * The check finds form items (groups and buttons) with duplicate titles inside one context.
 *
 * @author Artem Samohvalov
 */
public class FormItemsDuplicateTitleCheck
    extends BasicCheck<Object>
{

    @Override
    public String getCheckId()
    {
        return "form-items-duplicate-title"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormItemsDuplicateTitleCheck_Title)
            .description(Messages.FormItemsDuplicateTitleCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new TitleChangeExtension())
            .extension(new StandardCheckExtension(526, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FORM)
            .checkTop();
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Form form = (Form)object;
        parseFormItems(form.getItems(), resultAcceptor, monitor);

        if (form.getAutoCommandBar() != null)
        {
            parseFormItems(form.getAutoCommandBar().getItems(), resultAcceptor, monitor);
        }
    }

    private void parseFormItems(List<FormItem> items, ResultAcceptor resultAcceptor, IProgressMonitor monitor)
    {
        if (items == null || monitor.isCanceled())
        {
            return;
        }

        // key - language, value - titles
        Map<String, Set<String>> buttonTitles = new HashMap<>();
        Map<String, Set<String>> groupTitles = new HashMap<>();

        for (FormItem item : items)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (item instanceof Button button && isDuplicateAndRemember(button.getTitle(), buttonTitles))
            {
                resultAcceptor.addIssue(Messages.FormItemsDuplicateTitleCheck_ButtonIssue, button, TITLED__TITLE);
            }
            else if (item instanceof FormGroup group && isDuplicateAndRemember(group.getTitle(), groupTitles))
            {
                resultAcceptor.addIssue(Messages.FormItemsDuplicateTitleCheck_GroupIssue, group, TITLED__TITLE);
            }

            // if is container -> recursion
            getContainer(item).ifPresent(value -> parseFormItems(value.getItems(), resultAcceptor, monitor));
        }
    }

    /**
     * Checks if is duplicate title and add new value
     *
     * @param title the title
     * @param titles the titles
     * @return true, if is duplicate title
     */
    private boolean isDuplicateAndRemember(EMap<String, String> title, Map<String, Set<String>> titles)
    {
        if (title == null || title.isEmpty())
        {
            return false;
        }

        boolean duplicate = false;
        for (Map.Entry<String, String> entry : title.entrySet())
        {
            String value = entry.getValue();
            if (StringUtils.isBlank(value))
            {
                continue;
            }
            String normalized = value.trim().toLowerCase();
            if (!titles.computeIfAbsent(entry.getKey(), (_) -> new HashSet<>()).add(normalized))
            {
                duplicate = true;
            }
        }
        return duplicate;
    }

    private Optional<FormItemContainer> getContainer(FormItem item)
    {
        if (item instanceof FormItemContainer container)
        {
            return Optional.of(container);
        }
        else if (item instanceof FormField ff && ff.getContextMenu() instanceof FormItemContainer container)
        {

            return Optional.of(container);
        }

        return Optional.empty();
    }

    private static final class TitleChangeExtension
        implements IBasicCheckExtension
    {

        @Override
        public void configureContextCollector(ICheckDefinition definition)
        {
            @SuppressWarnings("unused")
            OnModelFeatureChangeContextCollector collector = (object, feature, bmEvent, contextSession) -> {
                if (TITLED__TITLE.equals(feature))
                {
                    contextSession.addModelCheck(object.bmGetTopObject());
                }
            };
            definition.addGenericModelFeatureChangeContextCollector(collector, FORM_GROUP, FORM);
            definition.addGenericModelFeatureChangeContextCollector(collector, BUTTON, FORM);
        }
    }
}
