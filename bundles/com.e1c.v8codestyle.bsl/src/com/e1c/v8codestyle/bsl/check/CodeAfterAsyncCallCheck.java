/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.AwaitStatement;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.PreprocessorConditional;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItemStatements;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.v8.dt.platform.version.Version;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.IAsyncInvocationProvider;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Checks that the asynchronous method is not followed by lines of code,
 * since in this case the specified lines of code are executed immediately,
 * without waiting for the asynchronous method to execute.
 *
 * @author Artem Iliukhin
 */
public final class CodeAfterAsyncCallCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "code-after-async-call"; //$NON-NLS-1$
    private static final String DEFAULT_CHECK = Boolean.toString(Boolean.TRUE);
    private static final String PARAMETER_NAME = "notifyDescriptionIsDefined"; //$NON-NLS-1$
    private static final String TYPE_NAME = "NotifyDescription"; //$NON-NLS-1$
    private final IAsyncInvocationProvider asyncInvocationProvider;
    private final IRuntimeVersionSupport runtimeVersionSupport;
    private final TypesComputer typesComputer;

    @Inject
    public CodeAfterAsyncCallCheck(IRuntimeVersionSupport runtimeVersionSupport,
        IAsyncInvocationProvider asyncInvocationProvider, TypesComputer typesComputer)
    {
        super();
        this.asyncInvocationProvider = asyncInvocationProvider;
        this.runtimeVersionSupport = runtimeVersionSupport;
        this.typesComputer = typesComputer;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CodeAfterAsyncCallCheck_Title)
            .description(Messages.CodeAfterAsyncCallCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.WARNING)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION)
            .parameter(PARAMETER_NAME, Boolean.class, DEFAULT_CHECK,
                Messages.CodeAfterAsyncCallCheck_Parameter);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Version version = runtimeVersionSupport.getRuntimeVersionOrDefault((EObject)object, Version.LATEST);

        Invocation inv = (Invocation)object;
        FeatureAccess featureAccess = inv.getMethodAccess();
        if (featureAccess instanceof StaticFeatureAccess)
        {
            Collection<String> asyncMethodsNames = asyncInvocationProvider.getAsyncInvocationNames(version);
            if (asyncMethodsNames.contains(featureAccess.getName())
                && (isNotifyDescriptionDefined(inv) || !parameters.getBoolean(PARAMETER_NAME)))
            {
                checkNeighboringStatement(resultAceptor, inv);
            }
        }
        else if (featureAccess instanceof DynamicFeatureAccess)
        {
            Map<String, Collection<String>> names = asyncInvocationProvider.getAsyncTypeMethodNames(version);
            if (names.containsKey(featureAccess.getName())
                && (isNotifyDescriptionDefined(inv) || !parameters.getBoolean(PARAMETER_NAME)))
            {
                Expression source = ((DynamicFeatureAccess)featureAccess).getSource();
                List<TypeItem> sourceTypes = computeTypes(source);
                Collection<String> collection = names.get(featureAccess.getName());
                if (collection.retainAll(sourceTypes.stream().map(McoreUtil::getTypeName).collect(Collectors.toSet())))
                {
                    checkNeighboringStatement(resultAceptor, inv);
                }
            }
        }
    }

    private boolean isNotifyDescriptionDefined(Invocation inv)
    {
        for (Expression param : inv.getParams())
        {
            List<TypeItem> sourceTypes = computeTypes(param);
            for (TypeItem typeItem : sourceTypes)
            {
                if (TYPE_NAME.equals(McoreUtil.getTypeName(typeItem)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private List<TypeItem> computeTypes(Expression expression)
    {
        Environmental environmental = EcoreUtil2.getContainerOfType(expression, Environmental.class);
        if (environmental != null)
        {
            return typesComputer.computeTypes(expression, environmental.environments());
        }
        return List.of();
    }

    private void checkNeighboringStatement(ResultAcceptor resultAceptor, Invocation inv)
    {
        Statement statement = getStatementFromInvoc(inv);
        if (statement != null && !(statement instanceof AwaitStatement))
        {
            statement = getNextStatement(statement);
            if (statement != null && !(statement instanceof ReturnStatement)
                && !(statement instanceof EmptyStatement) && !(statement instanceof AwaitStatement))
            {
                resultAceptor.addIssue(Messages.CodeAfterAsyncCallCheck_Issue, statement);
            }
        }
    }

    private Statement getStatementFromInvoc(Invocation invocation)
    {
        EObject container = invocation.eContainer();
        while (!(container instanceof Statement))
        {
            container = container.eContainer();
        }
        return container instanceof Statement ? (Statement)container : null;
    }

    private Statement getNextStatement(Statement statement)
    {
        Iterator<EObject> it = EcoreUtil2.getAllContainers(statement).iterator();
        while (it.hasNext())
        {
            EObject container = it.next();
            if (container instanceof PreprocessorConditional)
            {
                continue;
            }
            List<Statement> st = getContainer(container);
            if (st != null)
            {
                int index = st.indexOf(statement);
                if (index != -1 && index + 1 < st.size())
                {
                    return st.get(index + 1);
                }
            }
        }
        return null;
    }

    private List<Statement> getContainer(EObject container)
    {
        List<Statement> statements = null;
        if (container instanceof LoopStatement)
        {
            statements = ((LoopStatement)container).getStatements();
        }
        else if (container instanceof Conditional)
        {
            statements = ((Conditional)container).getStatements();
        }
        else if (container instanceof IfStatement)
        {
            statements = ((IfStatement)container).getElseStatements();
        }
        else if (container instanceof TryExceptStatement)
        {
            statements = getStatementsFromContainer((TryExceptStatement)container);
        }
        else if (container instanceof PreprocessorItemStatements)
        {
            statements = ((PreprocessorItemStatements)container).getStatements();
        }
        else
        {
            statements = getStatementsFromContainer(container);
        }
        return statements;
    }

    private List<Statement> getStatementsFromContainer(TryExceptStatement container)
    {
        List<Statement> res = Lists.newArrayList();
        res.addAll(container.getTryStatements());
        res.addAll(container.getExceptStatements());
        return res;
    }

    @SuppressWarnings("unchecked")
    private List<Statement> getStatementsFromContainer(EObject container)
    {
        Object obj = container.eGet(BslPackage.Literals.BLOCK__STATEMENTS);
        return obj instanceof List ? (List<Statement>)obj : null;
    }
}
