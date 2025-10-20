/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STATEMENT;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessorStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks in method missing semicolon
 *
 *  @author Ivan Sergeev
 */
public class SemicolonMissingCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "semicolon-missing"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.SemicolonMissingCheck_Title)
            .description(Messages.SemicolonMissingCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        INode node = null;

        Statement statement = (Statement)object;

        if (statement instanceof Statement | statement instanceof Conditional
            && !(statement instanceof RegionPreprocessorStatement))
        {
            node = NodeModelUtils.findActualNodeFor(statement);

            if (node == null)
            {
                return;
            }

            String nodeText = node.getText();
            INode checkNode = node.getNextSibling();

            if (checkNode == null)
            {
                return;
            }

            String checkText = checkNode.getText();

            if (checkText.equals(" ")) //$NON-NLS-1$
            {
                INode checkNextNode = checkNode.getNextSibling();

                if (checkNextNode == null)
                {
                    return;
                }
                String checkText2 = checkNextNode.getText();
                if (checkText2.contains(";")) //$NON-NLS-1$
                {
                    return;
                }
            }
            if (!checkText.contains(";") && !nodeText.isEmpty()) //$NON-NLS-1$
            {
                resultAceptor.addIssue(Messages.SemicolonMissingCheck_Issue);
            }

            List<EObject> eObJects = statement.eContents();
            checkSemicolon(eObJects, resultAceptor);
        }
    }

    private void checkSemicolon(List<EObject> eObjects, ResultAcceptor resultAceptor)
    {
        if (eObjects.isEmpty())
        {
            return;
        }

        for (EObject eObject : eObjects)
        {
            List<EObject> ListObjectsInside = eObject.eContents();

            if (eObject instanceof Statement | eObject instanceof Conditional)
            {
                INode statementNode = NodeModelUtils.findActualNodeFor(eObject);
                if (statementNode == null)
                {
                    return;
                }

                String nodeText = statementNode.getText();

                if (eObject instanceof Conditional)
                {
                    LinkedList<ILeafNode> allLeafNodes = new LinkedList<>();
                    statementNode.getLeafNodes().forEach(allLeafNodes::add);
                    ILeafNode lastNode = allLeafNodes.pollLast();

                    if (lastNode == null)
                    {
                        return;
                    }

                    String checkText = lastNode.getText();

                    if (!checkText.contains(";") && !nodeText.isEmpty()) //$NON-NLS-1$
                    {
                        if (checkText.contains("#EndRegion") | checkText.contains("#КонецОбласти")) //$NON-NLS-1$//$NON-NLS-2$
                        {
                            checkSemicolon(ListObjectsInside, resultAceptor);
                        }
                        else
                        {
                            resultAceptor.addIssue(Messages.SemicolonMissingCheck_Issue, eObject);
                        }

                    }
                    checkSemicolon(ListObjectsInside, resultAceptor);
                }
                else if (eObject instanceof RegionPreprocessorStatement)
                {
                    checkSemicolon(ListObjectsInside, resultAceptor);
                }
                else
                {
                    INode checkNode = statementNode.getNextSibling();
                    if (checkNode == null)
                    {
                        if (!nodeText.contains(";") && !nodeText.isEmpty()) //$NON-NLS-1$
                        {
                            resultAceptor.addIssue(Messages.SemicolonMissingCheck_Issue, eObject);
                        }
                        return;
                    }

                    String checkText = checkNode.getText();

                    if (!checkText.contains(";") && !nodeText.isEmpty()) //$NON-NLS-1$
                    {
                            resultAceptor.addIssue(Messages.SemicolonMissingCheck_Issue, eObject);
                        }
                    }
                }
            checkSemicolon(ListObjectsInside, resultAceptor);
        }
    }
}
