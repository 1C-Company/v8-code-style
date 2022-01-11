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
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.common.collect.Lists;

/**
 * Empty module method check.
 *
 * @author Andrey Volkov
 */
public final class ModuleEmptyMethodCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "module-empty-method"; //$NON-NLS-1$

    private static final String EXCLUDE_METHOD_NAME_PATTERN_PARAMETER_NAME = "excludeModuleMethodNamePattern"; //$NON-NLS-1$

    private static final String ALLOW_METHOD_COMMENTS_PARAMETER_NAME = "allowMethodComments"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ModuleEmptyMethodCheck_Title)
            .description(Messages.ModuleEmptyMethodCheck_Description)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .parameter(EXCLUDE_METHOD_NAME_PATTERN_PARAMETER_NAME, String.class, StringUtils.EMPTY,
                Messages.ModuleEmptyMethodCheck_Exclude_method_name_pattern_title)
            .parameter(ALLOW_METHOD_COMMENTS_PARAMETER_NAME, Boolean.class, Boolean.TRUE.toString(),
                Messages.ModuleEmptyMethodCheck_Allow_method_comments_title)
            .issueType(IssueType.WARNING)
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        Method method = (Method)object;
        String methodName = method.getName();

        String excludeNamePattern = parameters.getString(EXCLUDE_METHOD_NAME_PATTERN_PARAMETER_NAME);
        boolean allowMethodComments = parameters.getBoolean(ALLOW_METHOD_COMMENTS_PARAMETER_NAME);
        if (!isExcludeName(methodName, excludeNamePattern) && isEmpty(method, allowMethodComments))
        {
            resultAceptor.addIssue(MessageFormat.format(Messages.ModuleEmptyMethodCheck_Empty_method__0, methodName),
                method, McorePackage.Literals.NAMED_ELEMENT__NAME);
        }
    }

    private boolean isEmpty(Method method, boolean allowMethodComments)
    {
        if (method.allStatements().isEmpty())
        {
            return !allowMethodComments || isMethodHasNoComment(method);
        }
        for (Statement statement : method.allStatements())
        {
            if (!(statement instanceof EmptyStatement))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isMethodHasNoComment(Method method)
    {
        INode node = NodeModelUtils.findActualNodeFor(method);
        if (node != null)
        {
            List<ILeafNode> allLeafNode = Lists.newArrayList(node.getLeafNodes());
            for (int i = allLeafNode.size() - 1; i >= 0; --i)
            {
                ILeafNode leafNode = allLeafNode.get(i);

                if (leafNode.getOffset() < node.getOffset())
                {
                    break;
                }

                if (BslCommentUtils.isCommentNode(leafNode))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isExcludeName(String name, String excludeNamePattern)
    {
        return StringUtils.isNotEmpty(excludeNamePattern) && name.matches(excludeNamePattern);
    }
}
