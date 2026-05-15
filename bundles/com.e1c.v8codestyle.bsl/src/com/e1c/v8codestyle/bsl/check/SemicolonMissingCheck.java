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
import com._1c.g5.v8.dt.bsl.model.IfPreprocessorStatement;
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

    private static String charSemicolon = ";"; //$NON-NLS-1$

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
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;

        List<Statement> allItems = BslUtil.allStatements(method);
        if (allItems.isEmpty())
        {
            return;
        }
        for (Statement statement : allItems)
        {
            if (!(statement instanceof EmptyStatement))
            {
                INode node = NodeModelUtils.findActualNodeFor(statement);
                if (node == null)
                {
                    continue;
                }
                INode checkNode = node.getNextSibling();
                if (checkNode == null)
                {
                    resolveAddIssue(node, statement, resultAcceptor);
                    continue;
                }
                if (!checkNode.getText().contains(charSemicolon))
                {
                    INode checkNextNode = checkNode.getNextSibling();
                    if (checkNextNode == null)
                    {
                        resolveAddIssue(node, statement, resultAcceptor);
                        continue;
                    }
                    if (!checkNextNode.getText().contains(charSemicolon))
                    {
                        resolveAddIssue(node, statement, resultAcceptor);
                    }
                }
                if (!statement.eContents().isEmpty())
                {
                    checkSemicolon(statement.eContents(), resultAcceptor);
                }
            }
        }
    }

    private void checkSemicolon(List<EObject> eObjects, ResultAcceptor resultAcceptor)
    {
        if (eObjects.isEmpty())
        {
            return;
        }

        for (EObject eObject : eObjects)
        {
            if (!(eObject instanceof Statement))
            {
                if (eObject instanceof Conditional)
                {
                    INode node = NodeModelUtils.findActualNodeFor(eObject);
                    if (node == null)
                    {
                        continue;
                    }
                    BidiTreeIterator<INode> treeIterator = node.getAsTreeIterable().reverse().iterator();
                    INode lastNode = treeIterator.next();

                    if (lastNode == null)
                    {
                        continue;
                    }
                    String checkText = lastNode.getText();
                    String nodeText = node.getText();
                    if (!checkText.contains(charSemicolon) && !nodeText.isEmpty())
                    {
                        if (checkText.toLowerCase().contains("#endregion") //$NON-NLS-1$
                            || checkText.toLowerCase().contains("#конецобласти")) //$NON-NLS-1$
                        {
                            checkSemicolon(eObject.eContents(), resultAcceptor);
                        }
                        else if (checkText.toLowerCase().contains("#endif") //$NON-NLS-1$
                            || checkText.toLowerCase().contains("#конецесли")) //$NON-NLS-1$
                        {
                            checkSemicolon(eObject.eContents(), resultAcceptor);
                        }
                        else if (eObject.eContents().isEmpty())
                        {
                            resolveAddIssue(node, eObject, resultAcceptor);
                        }
                    }
                }
                checkSemicolon(eObject.eContents(), resultAcceptor);
            }
            else if (eObject instanceof EmptyStatement)
            {
                continue;
            }
            else if (eObject instanceof IfPreprocessorStatement)
            {
                checkSemicolon(eObject.eContents(), resultAcceptor);
            }
            else if (eObject.eContainingFeature().isMany() && eObject.eContainer() != null
                && eObject.eContainer().eGet(eObject.eContainingFeature()) instanceof List<?> statementCollection)
            {
                if (!statementCollection.isEmpty()
                    && statementCollection.get(statementCollection.size() - 1) == eObject)
                {
                    checkLastStatement(eObject, resultAcceptor);
                }
            }
        }
    }

    private void checkLastStatement(EObject eObject, ResultAcceptor resultAcceptor)
    {
        INode node = NodeModelUtils.findActualNodeFor(eObject);
        if (node == null)
        {
            return;
        }
        if (eObject instanceof SimpleStatement)
        {
            INode checkNode = node.getNextSibling();
            if (checkNode == null)
            {
                String nodeText = node.getText();
                if (!nodeText.contains(charSemicolon) && !nodeText.isEmpty())
                {
                    resolveAddIssue(node, eObject, resultAcceptor);
                }
                return;
            }
            if (!checkNode.getText().contains(charSemicolon))
            {
                INode checkNextNode = checkNode.getNextSibling();
                if (checkNextNode == null)
                {
                    resolveAddIssue(node, eObject, resultAcceptor);
                    return;
                }
                if (!checkNextNode.getText().contains(charSemicolon))
                {
                    resolveAddIssue(node, eObject, resultAcceptor);
                }
            }
        }
        else if (eObject instanceof IfStatement || eObject instanceof ForStatement)
        {
            INode checkNode = node.getNextSibling();
            if (checkNode == null)
            {
                resolveAddIssue(node, eObject, resultAcceptor);
                return;
            }
            if (!checkNode.getText().contains(charSemicolon))
            {
                INode checkNextNode = checkNode.getNextSibling();
                if (checkNextNode == null)
                {
                    resolveAddIssue(node, eObject, resultAcceptor);
                    return;
                }
                if (!checkNextNode.getText().contains(charSemicolon))
                {
                    resolveAddIssue(node, eObject, resultAcceptor);
                }
            }
            checkSemicolon(eObject.eContents(), resultAcceptor);
        }
        else if (eObject instanceof Statement)
        {
            INode checkNode = node.getNextSibling();
            if (checkNode == null)
            {
                if (eObject instanceof ReturnStatement)
                {
                    resolveAddIssue(node, eObject, resultAcceptor);
                }
                return;
            }
            if (!checkNode.getText().contains(charSemicolon))
            {
                INode checkNextNode = checkNode.getNextSibling();
                if (checkNextNode == null)
                {
                    resolveAddIssue(node, eObject, resultAcceptor);
                    return;
                }
                if (!checkNextNode.getText().contains(charSemicolon))
                {
                    resolveAddIssue(node, eObject, resultAcceptor);
                }
            }
            if (!eObject.eContents().isEmpty())
            {
                checkSemicolon(eObject.eContents(), resultAcceptor);
            }
        }
    }

    private static void resolveAddIssue(INode node, EObject eObject, ResultAcceptor resultAcceptor)
    {
        if (eObject instanceof SimpleStatement || eObject instanceof ReturnStatement)
        {
            resultAcceptor.addIssue(Messages.SemicolonMissingCheck_Issue, eObject);
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

        resultAcceptor.addIssue(issue);
    }
}
