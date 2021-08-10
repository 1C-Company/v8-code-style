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
 *     Aleksandr Kapralov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT__LEFT;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT__RIGHT;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.WhileStatement;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.mcore.ContextDef;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectProvider;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.v8.dt.platform.version.Version;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * @author Aleksandr Kapralov
 *
 */
public class QueryInLoopCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "query-in-loop"; //$NON-NLS-1$

    private static final String PARAM_CHECK_QUERIES_FOR_INFINITE_LOOPS = "checkQueriesForInfiniteLoops"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_QUERIES_FOR_INFINITE_LOOPS = Boolean.toString(false);

    private final TypesComputer typesComputer;

    @Inject
    protected IRuntimeVersionSupport versionSupport;

    public QueryInLoopCheck()
    {
        super();

        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        typesComputer = rsp.get(TypesComputer.class);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        //@formatter:off
        builder.title(Messages.QueryInLoop_title)
            .description(Messages.QueryInLoop_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.CRITICAL)
            .issueType(IssueType.PERFORMANCE)
            .module()
            .checkedObjectType(MODULE)
            .parameter(PARAM_CHECK_QUERIES_FOR_INFINITE_LOOPS, Boolean.class, DEFAULT_CHECK_QUERIES_FOR_INFINITE_LOOPS,
                Messages.QueryInLoop_check_queries_for_infinite_loops);
        //@formatter:on
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof Module))
        {
            return;
        }

        Module module = (Module)object;

        Set<String> queryExecutionMethods = getQueryExecutionMethods(module);
        if (queryExecutionMethods.isEmpty())
        {
            return;
        }

        Set<String> methodsWithQuery = getMethodsWithQuery(module, queryExecutionMethods, monitor);
        if (methodsWithQuery.isEmpty())
        {
            return;
        }

        expandMethodsWithQuery(methodsWithQuery, module, monitor);
        if (monitor.isCanceled())
        {
            return;
        }

        Boolean checkQueriesForInfiniteLoops = parameters.getBoolean(PARAM_CHECK_QUERIES_FOR_INFINITE_LOOPS);
        Map<EReference, Set<Statement>> statementsWithQueryInLoop = getStatementsWithQueryInLoop(module,
            methodsWithQuery, queryExecutionMethods, checkQueriesForInfiniteLoops, monitor);
        if (statementsWithQueryInLoop.isEmpty())
        {
            return;
        }

        for (Entry<EReference, Set<Statement>> entryStatement : statementsWithQueryInLoop.entrySet())
        {
            for (Statement statement : entryStatement.getValue())
            {
                if (monitor.isCanceled())
                {
                    return;
                }

                resultAceptor.addIssue(Messages.QueryInLoop_Loop_has_Query, statement, entryStatement.getKey());
            }
        }

    }

    private Set<String> getQueryExecutionMethods(EObject object)
    {
        Set<String> queryExecuteMethods = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        IEObjectProvider provider = IEObjectProvider.Registry.INSTANCE.get(McorePackage.Literals.TYPE_ITEM,
            versionSupport.getRuntimeVersionOrDefault(object, Version.LATEST));
        EObject proxyType = provider.getProxy(IEObjectTypeNames.QUERY);
        if (!(proxyType instanceof Type))
        {
            return queryExecuteMethods;
        }

        Type queryType = (Type)EcoreUtil.resolve(proxyType, object);

        ContextDef contextDef = queryType.getContextDef();
        if (contextDef == null)
        {
            return queryExecuteMethods;
        }

        EList<com._1c.g5.v8.dt.mcore.Method> queryMethods = contextDef.allMethods();
        for (com._1c.g5.v8.dt.mcore.Method queryMethod : queryMethods)
        {
            if (!queryMethod.getName().startsWith("Execute")) //$NON-NLS-1$
            {
                continue;
            }

            queryExecuteMethods.add(queryMethod.getName());
            queryExecuteMethods.add(queryMethod.getNameRu());
        }

        return queryExecuteMethods;
    }

    private boolean isQueryTypeSource(Expression source)
    {
        Environmental envs = EcoreUtil2.getContainerOfType(source, Environmental.class);
        List<TypeItem> sourceTypes = typesComputer.computeTypes(source, envs.environments());
        if (sourceTypes.isEmpty())
        {
            return false;
        }

        return sourceTypes.stream().anyMatch(t -> IEObjectTypeNames.QUERY.equals(McoreUtil.getTypeName(t)));
    }

    private boolean isQueryExecutionExpression(Expression expr, Set<String> queryExecutionMethods)
    {
        if (!(expr instanceof Invocation))
        {
            return false;
        }

        FeatureAccess methodAccess = ((Invocation)expr).getMethodAccess();
        if (!(methodAccess instanceof DynamicFeatureAccess))
        {
            return false;
        }

        Expression source = ((DynamicFeatureAccess)methodAccess).getSource();

        if (source instanceof Invocation)
        {
            return isQueryExecutionExpression(source, queryExecutionMethods);
        }

        if (!isQueryTypeSource(source))
        {
            return false;
        }

        return queryExecutionMethods.contains(methodAccess.getName());
    }

    private boolean isQueryExecutionLeftStatement(Statement statement, Set<String> queryExecutionMethods)
    {
        if (!(statement instanceof SimpleStatement))
        {
            return false;
        }

        return isQueryExecutionExpression(((SimpleStatement)statement).getLeft(), queryExecutionMethods);
    }

    private boolean isQueryExecutionRightStatement(Statement statement, Set<String> queryExecutionMethods)
    {
        if (!(statement instanceof SimpleStatement))
        {
            return false;
        }

        return isQueryExecutionExpression(((SimpleStatement)statement).getRight(), queryExecutionMethods);
    }

    private Set<String> getMethodsWithQuery(Module module, Set<String> queryExecutionMethods, IProgressMonitor monitor)
    {
        Set<String> result = new HashSet<>();

        for (SimpleStatement statement : EcoreUtil2.eAllOfType(module, SimpleStatement.class))
        {
            if (monitor.isCanceled())
            {
                return Collections.emptySet();
            }

            if (isQueryExecutionLeftStatement(statement, queryExecutionMethods)
                || isQueryExecutionRightStatement(statement, queryExecutionMethods))
            {
                Method method = EcoreUtil2.getContainerOfType(statement, Method.class);
                result.add(method.getName());
            }
        }

        return result;
    }

    private boolean isMethodCalledExpression(Expression expr, Set<String> methodsWithQuery)
    {
        if (!(expr instanceof Invocation))
        {
            return false;
        }

        FeatureAccess methodAccess = ((Invocation)expr).getMethodAccess();
        if (!(methodAccess instanceof StaticFeatureAccess))
        {
            return false;
        }

        return methodsWithQuery.contains(methodAccess.getName());
    }

    private boolean isMethodCalledLeftStatement(Statement statement, Set<String> methodsWithQuery)
    {
        if (!(statement instanceof SimpleStatement))
        {
            return false;
        }

        return isMethodCalledExpression(((SimpleStatement)statement).getLeft(), methodsWithQuery);
    }

    private boolean isMethodCalledRightStatement(Statement statement, Set<String> methodsWithQuery)
    {
        if (!(statement instanceof SimpleStatement))
        {
            return false;
        }

        return isMethodCalledExpression(((SimpleStatement)statement).getRight(), methodsWithQuery);
    }

    private void expandMethodsWithQuery(Set<String> methodsWithQuery, Module module, IProgressMonitor monitor)
    {
        EList<Method> methods = module.allMethods();

        int methodsCount = 0;
        while (methodsWithQuery.size() != methodsCount)
        {
            List<Method> filteredMethods =
                methods.stream().filter(m -> !methodsWithQuery.contains(m.getName())).collect(Collectors.toList());

            for (Method method : filteredMethods)
            {
                for (SimpleStatement statement : EcoreUtil2.eAllOfType(method, SimpleStatement.class))
                {
                    if (monitor.isCanceled())
                    {
                        return;
                    }

                    if (isMethodCalledLeftStatement(statement, methodsWithQuery)
                        || isMethodCalledRightStatement(statement, methodsWithQuery))
                    {
                        methodsWithQuery.add(method.getName());
                        break;
                    }
                }
            }

            methodsCount = methodsWithQuery.size();
        }

    }

    private boolean isInfiniteWhileLoop(LoopStatement loopStatement)
    {
        if (!(loopStatement instanceof WhileStatement))
        {
            return false;
        }

        Expression predicate = ((WhileStatement)loopStatement).getPredicate();
        return predicate instanceof BooleanLiteral;

    }

    private void addStatementWithQueryInLoop(Map<EReference, Set<Statement>> result, SimpleStatement statement,
        Set<String> methodsWithQuery, Set<String> queryExecutionMethods)
    {
        if (isMethodCalledLeftStatement(statement, methodsWithQuery)
            || isQueryExecutionLeftStatement(statement, queryExecutionMethods))
        {
            Set<Statement> leftSet = result.get(SIMPLE_STATEMENT__LEFT);
            leftSet.add(statement);
        }

        if (isMethodCalledRightStatement(statement, methodsWithQuery)
            || isQueryExecutionRightStatement(statement, queryExecutionMethods))
        {
            Set<Statement> rightSet = result.get(SIMPLE_STATEMENT__RIGHT);
            rightSet.add(statement);
        }
    }

    private Map<EReference, Set<Statement>> getStatementsWithQueryInLoop(Module module, Set<String> methodsWithQuery,
        Set<String> queryExecutionMethods, Boolean checkQueriesForInfiniteLoops, IProgressMonitor monitor)
    {
        Map<EReference, Set<Statement>> result = new HashMap<>();
        result.put(SIMPLE_STATEMENT__LEFT, new HashSet<>());
        result.put(SIMPLE_STATEMENT__RIGHT, new HashSet<>());

        for (LoopStatement loopStatement : EcoreUtil2.eAllOfType(module, LoopStatement.class))
        {
            if (Boolean.FALSE.equals(checkQueriesForInfiniteLoops) && isInfiniteWhileLoop(loopStatement))
            {
                continue;
            }

            for (SimpleStatement statement : EcoreUtil2.eAllOfType(loopStatement, SimpleStatement.class))
            {
                if (monitor.isCanceled())
                {
                    result.clear();
                    return result;
                }

                addStatementWithQueryInLoop(result, statement, methodsWithQuery, queryExecutionMethods);
            }
        }

        return result;
    }

}
