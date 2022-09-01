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

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Module;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks consecutive empty lines
 *
 * @author Artem Iliukhin
 */
public class ConsecutiveEmptyLinesCheck
    extends BasicCheck
{

    private static final String DEFAULT_NUMBER_OF_EMPTY_LINES = "2"; //$NON-NLS-1$
    private static final String NUMBER_OF_EMPTY_LINES = "numberOfEmptyLines"; //$NON-NLS-1$
    private static final String CHECK_ID = "consecutive-empty-lines"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ConsecutiveEmptyLines_Title)
            .description(Messages.ConsecutiveEmptyLines_Description)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .disable()
            .module()
            .checkedObjectType(MODULE)
            .parameter(NUMBER_OF_EMPTY_LINES, Integer.class, DEFAULT_NUMBER_OF_EMPTY_LINES,
                Messages.ConsecutiveEmptyLines_Parameter_title);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        Module module = (Module)object;
        INode node = NodeModelUtils.findActualNodeFor(module);
        Iterator<ILeafNode> iterator = node.getLeafNodes().iterator();

        int number = parameters.getInt(NUMBER_OF_EMPTY_LINES);

        while (iterator.hasNext())
        {
            ILeafNode leafNode = iterator.next();
            EObject grammarElement = leafNode.getGrammarElement();
            int startLine = leafNode.getStartLine();
            int endLine = leafNode.getEndLine();
            if (grammarElement instanceof TerminalRule && "WS".equals(((TerminalRule)grammarElement).getName()) //$NON-NLS-1$
                && (endLine - startLine) > number)
            {
                resultAceptor.addIssue(
                    MessageFormat.format(
                        Messages.ConsecutiveEmptyLines_Sequence_of_empty_lines_between_0_and_1_is_2_or_greater,
                        startLine, endLine, number),
                    NodeModelUtils.findActualSemanticObjectFor(leafNode));
            }
        }
    }
}
