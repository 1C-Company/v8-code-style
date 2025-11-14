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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.BidiTreeIterator;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com.e1c.g5.v8.dt.check.BslDirectLocationIssue;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.DirectLocation;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.Issue;
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
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        INode node = null;

        Method method = (Method)object;

        List<Statement> allItems = BslUtil.allStatements(method);
        if (allItems.isEmpty())
        {
            return;
        }
        for (Statement statement : allItems)
        {
            if (statement.eContainingFeature().isMany() && statement.eContainer() != null
                && statement.eContainer().eGet(statement.eContainingFeature()) instanceof List<?> statementCollection)
            {
                if (!statementCollection.isEmpty()
                    && statementCollection.get(statementCollection.size() - 1) == statement)
                {
                    if (!(statement instanceof EmptyStatement))
                    {
                        node = NodeModelUtils.findActualNodeFor(statement);
                        if (node == null)
                        {
                            continue;
                        }
                        INode checkNode = node.getNextSibling();
                        if (checkNode == null)
                        {
                            continue;
                        }
                        if (!checkNode.getText().contains(";")) //$NON-NLS-1$
                        {
                            INode checkNextNode = checkNode.getNextSibling();
                            if (checkNextNode == null)
                            {
                                resolveAddIssue(node, statement, resultAceptor);
                                continue;
                            }
                            if (!checkNextNode.getText().contains(";")) //$NON-NLS-1$
                            {
                                resolveAddIssue(node, statement, resultAceptor);
                            }
                        }
                        if (!statement.eContents().isEmpty())
                        {
                            checkSemicolon(statement.eContents(), resultAceptor);
                        }
                    }
                }
            }
        }
    }

    private void checkSemicolon(List<EObject> eObjects, ResultAcceptor resultAceptor)
    {
        if (eObjects.isEmpty())
        {
            return;
        }
        INode node = null;
        INode checkNode = null;
        INode checkNextNode = null;

        for (EObject eObject : eObjects)
        {
            node = NodeModelUtils.findActualNodeFor(eObject);
            if (node == null)
            {
                continue;
            }
            String nodeText = node.getText();
            if (!(eObject instanceof Statement))
            {
                if (eObject instanceof Conditional)
                {
                    BidiTreeIterator<INode> treeIterator = null;
                    treeIterator = node.getAsTreeIterable().reverse().iterator();
                    INode lastNode = treeIterator.next();

                    if (lastNode == null)
                    {
                        continue;
                    }

                    String checkText = lastNode.getText();

                    if (!checkText.contains(";") && !nodeText.isEmpty()) //$NON-NLS-1$
                    {
                        if (checkText.toLowerCase().contains("#EndRegion".toLowerCase()) //$NON-NLS-1$
                            | checkText.toLowerCase().contains("#КонецОбласти".toLowerCase())) //$NON-NLS-1$
                        {
                            checkSemicolon(eObject.eContents(), resultAceptor);
                        }
                        else if (checkText.toLowerCase().contains("#EndIf".toLowerCase()) //$NON-NLS-1$
                            | checkText.toLowerCase().contains("#КонецЕсли".toLowerCase())) //$NON-NLS-1$
                        {
                            checkSemicolon(eObject.eContents(), resultAceptor);
                        }
                        else if (eObject.eContents().isEmpty())
                        {
                            resolveAddIssue(node, eObject, resultAceptor);
                        }
                    }
                }
                checkSemicolon(eObject.eContents(), resultAceptor);
            }
            else if (eObject instanceof SimpleStatement)
            {
                checkNode = node.getNextSibling();
                if (checkNode == null)
                {
                    if (!nodeText.contains(";") && !nodeText.isEmpty()) //$NON-NLS-1$
                    {
                        resolveAddIssue(node, eObject, resultAceptor);
                    }
                    continue;
                }
                if (!checkNode.getText().contains(";")) //$NON-NLS-1$
                {
                    checkNextNode = checkNode.getNextSibling();
                    if (checkNextNode == null)
                    {
                        resolveAddIssue(node, eObject, resultAceptor);
                        continue;
                    }
                    if (!checkNextNode.getText().contains(";")) //$NON-NLS-1$
                    {
                        resolveAddIssue(node, eObject, resultAceptor);
                    }
                }
            }
            else if (eObject instanceof IfStatement || eObject instanceof ForStatement)
            {
                checkNode = node.getNextSibling();
                if (checkNode == null)
                {
                    resolveAddIssue(node, eObject, resultAceptor);
                    continue;
                }
                if (!checkNode.getText().contains(";")) //$NON-NLS-1$
                {
                    resolveAddIssue(node, eObject, resultAceptor);
                }
                checkSemicolon(eObject.eContents(), resultAceptor);
            }
            else if (eObject instanceof Statement & !(eObject instanceof EmptyStatement))
            {
                checkNode = node.getNextSibling();
                if (checkNode == null)
                {
                    if (eObject instanceof ReturnStatement)
                    {
                        resolveAddIssue(node, eObject, resultAceptor);
                    }
                    continue;
                }
                if (!checkNode.getText().contains(";")) //$NON-NLS-1$
                {
                    checkNextNode = checkNode.getNextSibling();
                    if (checkNextNode == null)
                    {
                        resolveAddIssue(node, eObject, resultAceptor);
                        continue;
                    }
                    if (!checkNextNode.getText().contains(";")) //$NON-NLS-1$
                    {
                        resolveAddIssue(node, eObject, resultAceptor);
                    }
                }
                if (!eObject.eContents().isEmpty())
                {
                    checkSemicolon(eObject.eContents(), resultAceptor);
                }
            }
        }
    }

    private void resolveAddIssue(INode node, EObject eObject, ResultAcceptor resultAceptor)
    {
        if (eObject instanceof SimpleStatement || eObject instanceof ReturnStatement)
        {
            resultAceptor.addIssue(Messages.SemicolonMissingCheck_Issue, eObject);
            return;
        }
        LinkedList<ILeafNode> allLeafNodes = new LinkedList<>();
        node.getLeafNodes().forEach(allLeafNodes::add);
        ILeafNode lastNode = allLeafNodes.pollLast();

        if (lastNode == null)
        {
            return;
        }
        DirectLocation directLocation =
            new DirectLocation(lastNode.getOffset(), lastNode.getLength(), lastNode.getStartLine(), eObject);

        Issue issue = new BslDirectLocationIssue(Messages.SemicolonMissingCheck_Issue, directLocation);

        resultAceptor.addIssue(issue);
    }
}
