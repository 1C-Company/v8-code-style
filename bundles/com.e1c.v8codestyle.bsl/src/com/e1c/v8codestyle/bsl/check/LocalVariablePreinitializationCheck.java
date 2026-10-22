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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.text.MessageFormat;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check for local variable preinitialization.
 * Reports a read of a local variable that is not initialized in the current or an enclosing scope
 *
 * @author Artem Samohvalov
 */
public class LocalVariablePreinitializationCheck
    extends BasicCheck<Object>
{

    @Override
    public String getCheckId()
    {
        return "local-variable-preinitialization"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.LocalVariablePreinitializationCheck_Title)
            .description(Messages.LocalVariablePreinitializationCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(494, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(SIMPLE_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        SimpleStatement simpleStatement = (SimpleStatement)object;

        if (simpleStatement.getLeft() instanceof StaticFeatureAccess left
            && simpleStatement.getRight() instanceof StaticFeatureAccess right)
        {
            Optional<Variable> optionalLeft = getVariable(left);
            Variable variable = getVariable(right).orElse(null);

            if (optionalLeft.isEmpty() || variable == null)
            {
                return;
            }

            if (isInitialized(simpleStatement, variable))
            {
                return;
            }

            String message = MessageFormat.format(Messages.LocalVariablePreinitializationCheck_Issue, right.getName());
            resultAcceptor.addIssue(message, simpleStatement);
        }
    }

    private boolean isInitialized(SimpleStatement statement, Variable variable)
    {
        INode readNode = NodeModelUtils.findActualNodeFor(statement);
        if (readNode == null)
        {
            return false;
        }

        int readOffset = readNode.getOffset();
        for (EObject container = statement.eContainer(); container != null; container = container.eContainer())
        {
            if (container instanceof ForStatement forStatement)
            {
                StaticFeatureAccess loopStaticAccess = forStatement.getVariableAccess(); // here a forstatement init variable
                Optional<Variable> loopVariable = getVariable(loopStaticAccess);
                if (loopVariable.isPresent() && variable == loopVariable.get())
                {
                    return true;
                }
            }

            for (EObject child : container.eContents())
            {
                if (child instanceof SimpleStatement candidate && candidate != statement
                    && candidate.getLeft() instanceof StaticFeatureAccess candidateLeft
                    && getVariable(candidateLeft).orElse(null) == variable && isBefore(candidate, readOffset))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isBefore(EObject object, int offset)
    {
        INode node = NodeModelUtils.findActualNodeFor(object);
        return node != null && node.getOffset() < offset;
    }

    /**
     * Checks if is variable.
     * But not method parameter and not explicit variable
     *
     * @param staticAccess the static access
     * @return true, if is variable
     */
    private Optional<Variable> getVariable(StaticFeatureAccess staticAccess)
    {
        if (staticAccess == null)
        {
            return Optional.empty();
        }

        return staticAccess.getFeatureEntries()
            .stream()
            .map(entry -> entry.getFeature())
            .filter(feature -> feature instanceof Variable
                && !(feature instanceof FormalParam || feature instanceof ExplicitVariable))
            .map(Variable.class::cast)
            .findFirst();
    }
}
