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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IResourceServiceProvider;

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
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
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
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .module()
            .checkedObjectType(MODULE);
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

        Set<Statement> statementsWithQueryInLoop =
            getStatementsWithQueryInLoop(module, methodsWithQuery, queryExecutionMethods, monitor);
        if (statementsWithQueryInLoop.isEmpty())
        {
            return;
        }

        for (Statement statement : statementsWithQueryInLoop)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            resultAceptor.addIssue(Messages.QueryInLoop_Loop_has_Query, statement, SIMPLE_STATEMENT__LEFT);
        }

    }

    private Set<String> getQueryExecutionMethods(EObject object)
    {
        Set<String> queryExecuteMethods = new HashSet<>();

        IEObjectProvider provider = IEObjectProvider.Registry.INSTANCE.get(McorePackage.Literals.TYPE_ITEM,
            versionSupport.getRuntimeVersionOrDefault(object, Version.LATEST));
        EObject proxyType = provider.getProxy(IEObjectTypeNames.QUERY);
        Type queryType = (Type)EcoreUtil2.cloneWithProxies((TypeItem)EcoreUtil.resolve(proxyType, object));

        EList<com._1c.g5.v8.dt.mcore.Method> queryMethods = queryType.getContextDef().allMethods();
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

    private boolean isQueryExecution(Statement statement, Set<String> queryExecutionMethods)
    {
        if (!(statement instanceof SimpleStatement))
        {
            return false;
        }

        Expression leftStatement = ((SimpleStatement)statement).getLeft();
        if (!(leftStatement instanceof Invocation))
        {
            return false;
        }

        FeatureAccess methodAccess = ((Invocation)leftStatement).getMethodAccess();
        if (!(methodAccess instanceof DynamicFeatureAccess))
        {
            return false;
        }

        Expression source = ((DynamicFeatureAccess)methodAccess).getSource();

        Environmental envs = EcoreUtil2.getContainerOfType(source, Environmental.class);
        List<TypeItem> sourceTypes = typesComputer.computeTypes(source, envs.environments());
        if (sourceTypes.isEmpty())
        {
            return false;
        }

        if (!McoreUtil.getTypeName(sourceTypes.get(0)).equals(IEObjectTypeNames.QUERY))
        {
            return false;
        }

        return queryExecutionMethods.stream().anyMatch(s -> s.equalsIgnoreCase(methodAccess.getName()));
    }

    private Set<String> getMethodsWithQuery(Module module, Set<String> queryExecutionMethods, IProgressMonitor monitor)
    {
        Set<String> result = new HashSet<>();

        for (Statement statement : EcoreUtil2.eAllOfType(module, SimpleStatement.class))
        {
            if (monitor.isCanceled())
            {
                return Collections.emptySet();
            }

            if (isQueryExecution(statement, queryExecutionMethods))
            {
                Method method = EcoreUtil2.getContainerOfType(statement, Method.class);
                result.add(method.getName());
            }
        }

        return result;
    }

    private boolean isMethodCalled(Statement statement, Set<String> methodsWithQuery)
    {
        if (!(statement instanceof SimpleStatement))
        {
            return false;
        }

        Expression leftStatement = ((SimpleStatement)statement).getLeft();
        if (!(leftStatement instanceof Invocation))
        {
            return false;
        }

        FeatureAccess methodAccess = ((Invocation)leftStatement).getMethodAccess();
        if (!(methodAccess instanceof StaticFeatureAccess))
        {
            return false;
        }

        return methodsWithQuery.contains(methodAccess.getName());
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
                for (Statement statement : EcoreUtil2.eAllOfType(method, SimpleStatement.class))
                {
                    if (monitor.isCanceled())
                    {
                        return;
                    }

                    if (isMethodCalled(statement, methodsWithQuery))
                    {
                        methodsWithQuery.add(method.getName());
                        break;
                    }
                }
            }

            methodsCount = methodsWithQuery.size();
        }

    }

    private Set<Statement> getStatementsWithQueryInLoop(Module module, Set<String> methodsWithQuery,
        Set<String> queryExecutionMethods, IProgressMonitor monitor)
    {
        Set<Statement> result = new HashSet<>();

        for (LoopStatement loopStatement : EcoreUtil2.eAllOfType(module, LoopStatement.class))
        {
            for (Statement statement : EcoreUtil2.eAllOfType(loopStatement, SimpleStatement.class))
            {
                if (monitor.isCanceled())
                {
                    return Collections.emptySet();
                }

                if (isMethodCalled(statement, methodsWithQuery) || isQueryExecution(statement, queryExecutionMethods))
                {
                    result.add(statement);
                }
            }
        }

        return result;
    }

}
