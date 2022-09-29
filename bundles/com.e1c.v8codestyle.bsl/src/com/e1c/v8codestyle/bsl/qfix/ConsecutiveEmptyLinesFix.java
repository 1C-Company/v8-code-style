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
package com.e1c.v8codestyle.bsl.qfix;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.services.BslGrammarAccess;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.g5.v8.dt.check.settings.ICheckRepository;
import com.google.inject.Inject;

/**
 * Removes extra empty lines
 *
 * @author Artem Iliukhin
 */
@QuickFix(checkId = "module-consecutive-blank-lines", supplierId = "com.e1c.v8codestyle.bsl")
public class ConsecutiveEmptyLinesFix
    extends SingleVariantXtextBslModuleFix
{
    private static final String PATTERN = "(?<=\\n)"; //$NON-NLS-1$

    private static final String NUMBER_OF_EMPTY_LINES = "numberOfEmptyLines"; //$NON-NLS-1$

    private final BslGrammarAccess grammarAccess;

    private final ICheckRepository checkRepository;

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ConsecutiveEmptyLinesFix(BslGrammarAccess grammarAccess, ICheckRepository checkRepository,
        IV8ProjectManager v8ProjectManager)
    {
        super();
        this.grammarAccess = grammarAccess;
        this.checkRepository = checkRepository;
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.ConsecutiveEmptyLinesFix_Description)
            .details(Messages.ConsecutiveEmptyLinesFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject element = model.getElement();
        INode node = NodeModelUtils.findActualNodeFor(element);
        Iterator<ILeafNode> iterator = node.getLeafNodes().iterator();

        IV8Project project = v8ProjectManager.getProject(element);
        ICheckParameters parameters =
            checkRepository.getCheckParameters(getCheckId(), project.getDtProject().getWorkspaceProject());
        int number = parameters.getInt(NUMBER_OF_EMPTY_LINES);

        MultiTextEdit result = new MultiTextEdit();
        while (iterator.hasNext())
        {
            ILeafNode leafNode = iterator.next();
            EObject grammarElement = leafNode.getGrammarElement();
            int startLine = leafNode.getStartLine();
            int endLine = leafNode.getEndLine();

            if (grammarElement instanceof TerminalRule && grammarAccess.getWSRule().equals(grammarElement)
                && (endLine - startLine - 1) > number)
            {
                String[] lines = leafNode.getText().split(PATTERN);
                int headLength = getAllowedHeadLength(number, lines);

                int tailLength = leafNode.getLength() - getAllowedTailLength(headLength, lines);
                result.addChild(new DeleteEdit(leafNode.getOffset() + headLength, tailLength == 0 ? 1 : tailLength));
            }
        }

        if (result.getChildrenSize() > 0)
        {
            return result;
        }

        return null;
    }

    private int getAllowedHeadLength(int number, String[] lines)
    {
        int sum = 0;
        for (int i = 0; i <= number; i++)
        {
            sum += lines[i].length();
        }
        return sum;
    }

    private int getAllowedTailLength(int sum, String[] lines)
    {
        return sum + lines[lines.length - 1].length();
    }
}
