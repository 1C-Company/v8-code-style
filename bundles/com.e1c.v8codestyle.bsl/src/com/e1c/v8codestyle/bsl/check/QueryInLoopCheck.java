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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FEATURE_ACCESS__NAME;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.util.LineAndColumn;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.WhileStatement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
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
 * Checks queries in loops.
 * Skip checks for queries in infinite while loops by default.
 *
 * @author Aleksandr Kapralov
 *
 */
public class QueryInLoopCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "query-in-loop"; //$NON-NLS-1$

    private static final String PARAM_CHECK_QUERIY_IN_INFINITE_LOOP = "checkQueryInInfiniteLoop"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_QUERY_IN_INFINITE_LOOP = Boolean.toString(false);

    private final TypesComputer typesComputer;

    private final IRuntimeVersionSupport versionSupport;

    @Inject
    public QueryInLoopCheck(IRuntimeVersionSupport versionSupport)
    {
        super();

        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        typesComputer = rsp.get(TypesComputer.class);

        this.versionSupport = versionSupport;
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
            .parameter(PARAM_CHECK_QUERIY_IN_INFINITE_LOOP, Boolean.class, DEFAULT_CHECK_QUERY_IN_INFINITE_LOOP,
                Messages.QueryInLoop_check_query_in_infinite_loop);
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

        Map<String, String> methodsWithQuery = getMethodsWithQuery(module, queryExecutionMethods, monitor);
        if (methodsWithQuery.isEmpty())
        {
            return;
        }

        Map<String, String> methodsCalledFromQuery = expandMethodsWithQuery(methodsWithQuery, module, monitor);
        if (monitor.isCanceled())
        {
            return;
        }

        Boolean checkQueriesForInfiniteLoops = parameters.getBoolean(PARAM_CHECK_QUERIY_IN_INFINITE_LOOP);
        Set<FeatureAccess> faWithQueryInLoop = getFaWithQueryInLoop(module, methodsCalledFromQuery,
            queryExecutionMethods, checkQueriesForInfiniteLoops, monitor);

        for (FeatureAccess featureAccess : faWithQueryInLoop)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (featureAccess instanceof DynamicFeatureAccess)
            {
                resultAceptor.addIssue(Messages.QueryInLoop_Loop_has_query, featureAccess, FEATURE_ACCESS__NAME);
            }

            if (featureAccess instanceof StaticFeatureAccess)
            {
                String errorPath = methodsCalledFromQuery.get(featureAccess.getName());
                String errorMessage =
                    MessageFormat.format(Messages.QueryInLoop_Loop_has_method_with_query__0, errorPath);
                resultAceptor.addIssue(errorMessage, featureAccess, FEATURE_ACCESS__NAME);
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

        for (TypeItem sourceType : sourceTypes)
        {
            if (IEObjectTypeNames.QUERY.equals(McoreUtil.getTypeName(sourceType)))
            {
                return true;
            }
        }

        return false;
    }

    private boolean isQueryExecution(DynamicFeatureAccess dfa, Set<String> queryExecutionMethods)
    {
        return queryExecutionMethods.contains(dfa.getName()) && BslUtil.getInvocation(dfa) != null
            && isQueryTypeSource(dfa.getSource());
    }

    private String getSourceName(Expression source)
    {
        String result = ""; //$NON-NLS-1$

        if (source instanceof FeatureAccess)
        {
            result = ((FeatureAccess)source).getName() + "."; //$NON-NLS-1$
        }

        else if (source instanceof Invocation)
        {
            result = getSourceName(((Invocation)source).getMethodAccess());
        }

        return result;
    }

    private String getPositionForEObject(EObject object)
    {
        ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
        LineAndColumn lineColumn = NodeModelUtils.getLineAndColumn(node.getRootNode(), node.getOffset());
        return MessageFormat.format("'{'{0}:{1}'}' ", String.valueOf(lineColumn.getLine()), //$NON-NLS-1$
            String.valueOf(lineColumn.getColumn()));
    }

    private Map<String, String> getMethodsWithQuery(Module module, Set<String> queryExecutionMethods,
        IProgressMonitor monitor)
    {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (DynamicFeatureAccess dfa : EcoreUtil2.eAllOfType(module, DynamicFeatureAccess.class))
        {
            if (monitor.isCanceled())
            {
                return Collections.emptyMap();
            }

            if (isQueryExecution(dfa, queryExecutionMethods))
            {
                Method method = EcoreUtil2.getContainerOfType(dfa, Method.class);
                String sourceName = getSourceName(dfa.getSource());

                String methodPath = String.join("", method.getName(), "() -> ", //$NON-NLS-1$ //$NON-NLS-2$
                    getPositionForEObject(dfa), sourceName, dfa.getName(), "()"); //$NON-NLS-1$
                result.put(method.getName(), methodPath);
            }
        }

        return result;
    }

    private boolean isMethodWithQueryCalled(StaticFeatureAccess sfa, Map<String, String> methodsWithQuery)
    {
        return methodsWithQuery.containsKey(sfa.getName()) && BslUtil.getInvocation(sfa) != null;
    }

    private StaticFeatureAccess isMethodWithQuery(Method method, Map<String, String> methodsWithQuery)
    {
        for (StaticFeatureAccess sfa : EcoreUtil2.eAllOfType(method, StaticFeatureAccess.class))
        {
            if (isMethodWithQueryCalled(sfa, methodsWithQuery))
            {
                return sfa;
            }
        }

        return null;
    }

    private Map<String, String> expandMethodsWithQuery(Map<String, String> methodsWithQuery, Module module,
        IProgressMonitor monitor)
    {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        result.putAll(methodsWithQuery);

        EList<Method> methods = module.allMethods();

        int methodsCount = 0;
        while (result.size() != methodsCount)
        {
            for (Method method : methods)
            {
                if (monitor.isCanceled())
                {
                    return Collections.emptyMap();
                }

                if (result.containsKey(method.getName()))
                {
                    continue;
                }

                StaticFeatureAccess calledMethod = isMethodWithQuery(method, result);
                if (calledMethod == null)
                {
                    continue;
                }

                String calledMethodPath = result.get(calledMethod.getName());
                if (calledMethodPath == null)
                {
                    calledMethodPath = calledMethod.getName() + "()"; //$NON-NLS-1$
                }

                String methodPath = String.join("", method.getName(), "() -> ", //$NON-NLS-1$ //$NON-NLS-2$
                    getPositionForEObject(calledMethod), calledMethodPath);
                result.put(method.getName(), methodPath);
            }

            methodsCount = result.size();
        }

        return result;
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

    private Set<FeatureAccess> getFaWithQueryInLoop(Module module, Map<String, String> methodsWithQuery,
        Set<String> queryExecutionMethods, Boolean checkQueriesForInfiniteLoops, IProgressMonitor monitor)
    {
        Set<FeatureAccess> result = new HashSet<>();

        for (LoopStatement loopStatement : EcoreUtil2.eAllOfType(module, LoopStatement.class))
        {
            if (monitor.isCanceled())
            {
                return Collections.emptySet();
            }

            if (Boolean.FALSE.equals(checkQueriesForInfiniteLoops) && isInfiniteWhileLoop(loopStatement))
            {
                continue;
            }

            for (FeatureAccess featureAccess : EcoreUtil2.eAllOfType(loopStatement, FeatureAccess.class))
            {
                if (featureAccess instanceof StaticFeatureAccess
                    && isMethodWithQueryCalled((StaticFeatureAccess)featureAccess, methodsWithQuery)
                    || featureAccess instanceof DynamicFeatureAccess
                        && isQueryExecution((DynamicFeatureAccess)featureAccess, queryExecutionMethods))
                {
                    result.add(featureAccess);
                }
            }
        }

        return result;
    }

}
