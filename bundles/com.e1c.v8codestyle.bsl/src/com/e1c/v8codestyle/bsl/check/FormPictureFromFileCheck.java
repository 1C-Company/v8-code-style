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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FUNCTION_STYLE_CREATOR;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.OPERATOR_STYLE_CREATOR;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FunctionStyleCreator;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.form.model.Button;
import com._1c.g5.v8.dt.form.model.FormGroup;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.AbstractForm;
import com._1c.g5.v8.dt.metadata.mdclass.BasicForm;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks that a form module does not create pictures from a file, binary data or a stream (standard 517):
 * {@code Новый Картинка(<file name | binary data | stream>, ...)}. Only standard pictures and pictures
 * of the configuration are allowed in forms. A constructor call without parameters creates an empty
 * picture and is not reported.
 *
 * @author Ivan Sergeev
 */
public final class FormPictureFromFileCheck
    extends BasicCheck<Object>
{
    private static final String CHECK_ID = "form-picture-from-file"; //$NON-NLS-1$

    private static final Set<String> PICTURE_TYPES;

    private static final Set<String> TARGET_PROPERTIES;

    private static final Set<String> ITEMS_ROOTS;

    private static final Set<String> COMMANDS_ROOTS;

    static
    {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("Картинка"); //$NON-NLS-1$
        names.add("Picture"); //$NON-NLS-1$
        PICTURE_TYPES = Collections.unmodifiableSet(names);

        Set<String> properties = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        properties.add("Картинка"); //$NON-NLS-1$
        properties.add("Picture"); //$NON-NLS-1$
        TARGET_PROPERTIES = Collections.unmodifiableSet(properties);

        Set<String> items = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        items.add("Элементы"); //$NON-NLS-1$
        items.add("Items"); //$NON-NLS-1$
        ITEMS_ROOTS = Collections.unmodifiableSet(items);

        Set<String> commands = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        commands.add("Команды"); //$NON-NLS-1$
        commands.add("Commands"); //$NON-NLS-1$
        COMMANDS_ROOTS = Collections.unmodifiableSet(commands);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormPictureFromFileCheck_Title)
            .description(Messages.FormPictureFromFileCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(517, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(OPERATOR_STYLE_CREATOR)
            .checkedObjectType(FUNCTION_STYLE_CREATOR);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }

        if (object instanceof OperatorStyleCreator creator)
        {
            Type type = creator.getType();
            if (type != null && !creator.getParams().isEmpty() && isPictureType(McoreUtil.getTypeName(type))
                && isAssignedToInterfaceItem(creator))
            {
                resultAceptor.addIssue(Messages.FormPictureFromFileCheck_Issue, object);
            }
        }
        else if (object instanceof FunctionStyleCreator creator && creator.getParamsExpression() != null
            && creator.getTypeNameExpression() instanceof StringLiteral literal && literal.lines(true).size() == 1
            && isPictureType(literal.lines(true).get(0)) && isAssignedToInterfaceItem(creator))
        {
            resultAceptor.addIssue(Messages.FormPictureFromFileCheck_Issue, object);
        }
    }

    private boolean isPictureType(String typeName)
    {
        return typeName != null && PICTURE_TYPES.contains(typeName);
    }

    private boolean isAssignedToInterfaceItem(EObject creator)
    {
        if (!(creator.eContainer() instanceof SimpleStatement statement) || statement.getRight() != creator
            || !(statement.getLeft() instanceof DynamicFeatureAccess access) || access.getName() == null
            || !TARGET_PROPERTIES.contains(access.getName()))
        {
            return false;
        }

        EObject source = access.getSource();
        String itemName = null;
        while (source instanceof DynamicFeatureAccess owner)
        {
            if (itemName == null)
            {
                itemName = owner.getName();
            }
            source = owner.getSource();
        }

        if (itemName == null || !(source instanceof StaticFeatureAccess root) || root.getName() == null)
        {
            return false;
        }

        if (COMMANDS_ROOTS.contains(root.getName()))
        {
            return true;
        }

        if (ITEMS_ROOTS.contains(root.getName()))
        {
            FormItem item = findFormItem(creator, itemName);
            return item instanceof Button || item instanceof FormGroup;
        }
        return false;
    }

    private FormItem findFormItem(EObject context, String itemName)
    {
        Module module = EcoreUtil2.getContainerOfType(context, Module.class);
        if (module == null || !(module.getOwner() instanceof BasicForm basicForm))
        {
            return null;
        }

        AbstractForm form = basicForm.getForm();
        if (form == null)
        {
            return null;
        }

        TreeIterator<EObject> contents = form.eAllContents();
        while (contents.hasNext())
        {
            if (contents.next() instanceof FormItem item && itemName.equalsIgnoreCase(item.getName()))
            {
                return item;
            }
        }
        return null;
    }
}
