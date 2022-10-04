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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.Statement;
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
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
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

    /**
     * Instantiates a new query in loop check.
     *
     * @param versionSupport - Version support for 1C:Enterprise projects service, cannot be {@code null}
     * @param typesComputer the types computer service, cannot be {@code null}
     */
    @Inject
    public QueryInLoopCheck(IRuntimeVersionSupport versionSupport, TypesComputer typesComputer)
    {
        super();

        this.typesComputer = typesComputer;
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
            .extension(new StandardCheckExtension(436, getCheckId(), BslPlugin.PLUGIN_ID))
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
        if (!(object instanceof Module))
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

        boolean checkQueryInInfiniteLoop = parameters.getBoolean(PARAM_CHECK_QUERIY_IN_INFINITE_LOOP);
        Collection<FeatureAccess> queryInLoopCallers =
            getQueryInLoopCallers(module, methodsWithQuery, queryExecutionMethods, checkQueryInInfiniteLoop, monitor);

        for (FeatureAccess featureAccess : queryInLoopCallers)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (featureAccess instanceof DynamicFeatureAccess)
            {
                resultAceptor.addIssue(Messages.QueryInLoop_Loop_has_query, featureAccess, FEATURE_ACCESS__NAME);
            }

            else if (featureAccess instanceof StaticFeatureAccess
                && methodsWithQuery.containsKey(featureAccess.getName()))
            {

                String errorPath = methodsWithQuery.get(featureAccess.getName());
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
        if (envs == null)
        {
            return false;
        }

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
        String methodName = dfa.getName();

        return methodName != null && queryExecutionMethods.contains(methodName) && BslUtil.getInvocation(dfa) != null
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

    private String getPositionForFeatureObject(EObject object)
    {
        ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
        return MessageFormat.format("'{'{0}'}' ", String.valueOf(node.getStartLine())); //$NON-NLS-1$
    }

    private String getMethodPath(Method method, Map<String, String> result)
    {
        if (result.containsKey(method.getName()))
        {
            return null;
        }

        StaticFeatureAccess calledMethod = getMethodWithQuery(method, result);
        if (calledMethod == null)
        {
            return null;
        }

        String calledMethodPath = result.get(calledMethod.getName());
        if (calledMethodPath == null)
        {
            calledMethodPath = calledMethod.getName() + "()"; //$NON-NLS-1$
        }

        return String.join("", method.getName(), "() -> ", //$NON-NLS-1$ //$NON-NLS-2$
            getPositionForFeatureObject(calledMethod), calledMethodPath);
    }

    private Map<String, String> getQueryExecutionMethodsPath(Module module, Set<String> queryExecutionMethods,
        IProgressMonitor monitor)
    {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (DynamicFeatureAccess dfa : EcoreUtil2.eAllOfType(module, DynamicFeatureAccess.class))
        {
            if (monitor.isCanceled())
            {
                return Collections.emptyMap();
            }

            if (!isQueryExecution(dfa, queryExecutionMethods))
            {
                continue;
            }

            Method method = EcoreUtil2.getContainerOfType(dfa, Method.class);
            if (method != null)
            {
                String sourceName = getSourceName(dfa.getSource());
                String featurePosition = getPositionForFeatureObject(dfa);

                String methodPath = String.join("", method.getName(), "() -> ", //$NON-NLS-1$ //$NON-NLS-2$
                    featurePosition, sourceName, dfa.getName(), "()"); //$NON-NLS-1$
                result.put(method.getName(), methodPath);
            }

        }

        return result;
    }

    private Map<String, String> getMethodsWithQuery(Module module, Set<String> queryExecutionMethods,
        IProgressMonitor monitor)
    {
        Map<String, String> result = getQueryExecutionMethodsPath(module, queryExecutionMethods, monitor);
        if (result.isEmpty())
        {
            return Collections.emptyMap();
        }

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

                String methodPath = getMethodPath(method, result);
                if (methodPath != null)
                {
                    result.put(method.getName(), methodPath);
                }
            }

            methodsCount = result.size();
        }

        return result;
    }

    private boolean isMethodWithQueryCalled(StaticFeatureAccess sfa, Map<String, String> methodsWithQuery)
    {
        return methodsWithQuery.containsKey(sfa.getName()) && BslUtil.getInvocation(sfa) != null;
    }

    private StaticFeatureAccess getMethodWithQuery(Method method, Map<String, String> methodsWithQuery)
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

    private boolean isInfiniteWhileLoop(LoopStatement loopStatement)
    {
        if (!(loopStatement instanceof WhileStatement))
        {
            return false;
        }

        Expression predicate = ((WhileStatement)loopStatement).getPredicate();
        return predicate instanceof BooleanLiteral;
    }

    private Collection<FeatureAccess> getQueryInLoopFeatures(LoopStatement loopStatement,
        Map<String, String> methodsWithQuery, Set<String> queryExecutionMethods)
    {
        Collection<FeatureAccess> result = new ArrayList<>();

        for (Statement statement : loopStatement.getStatements())
        {
            for (FeatureAccess featureAccess : EcoreUtil2.eAllOfType(statement, FeatureAccess.class))
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

    private Collection<FeatureAccess> getQueryInLoopCallers(Module module, Map<String, String> methodsWithQuery,
        Set<String> queryExecutionMethods, boolean checkQueryInInfiniteLoop, IProgressMonitor monitor)
    {
        Collection<FeatureAccess> result = new ArrayList<>();

        for (LoopStatement loopStatement : EcoreUtil2.eAllOfType(module, LoopStatement.class))
        {
            if (monitor.isCanceled())
            {
                return Collections.emptyList();
            }

            if (!checkQueryInInfiniteLoop && isInfiniteWhileLoop(loopStatement))
            {
                continue;
            }

            result.addAll(getQueryInLoopFeatures(loopStatement, methodsWithQuery, queryExecutionMethods));
        }

        return result;
    }

}
