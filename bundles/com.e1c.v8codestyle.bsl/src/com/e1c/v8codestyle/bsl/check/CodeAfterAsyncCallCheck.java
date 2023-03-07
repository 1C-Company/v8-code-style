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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItemStatements;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.v8.dt.platform.version.Version;
import com._1c.g5.wiring.ServiceAccess;
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
    private final IResourceLookup resourceLookup;
    private final IAsyncInvocationProvider asyncInvocationProvider;

    @Inject
    public CodeAfterAsyncCallCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, IAsyncInvocationProvider asyncInvocationProvider)
    {
        super();
        this.resourceLookup = resourceLookup;
        this.asyncInvocationProvider = asyncInvocationProvider;
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
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        IProject project = resourceLookup.getProject((EObject)object);

        IRuntimeVersionSupport runtimeVersionSupport = ServiceAccess.get(IRuntimeVersionSupport.class);
        Version version = runtimeVersionSupport.getRuntimeVersion(project);

        Invocation inv = (Invocation)object;
        FeatureAccess featureAccess = inv.getMethodAccess();
        if (featureAccess instanceof StaticFeatureAccess)
        {
            Collection<String> asyncMethodsNames = asyncInvocationProvider.getAsyncInvocationNames(version);
            if (asyncMethodsNames.contains(featureAccess.getName()))
            {
                Statement statement = getStatementFromInvoc(inv);
                if (statement != null)
                {
                    statement = getNextStatement(statement);
                    if (statement != null && !(statement instanceof ReturnStatement)
                        && !(statement instanceof EmptyStatement))
                    {
                        resultAceptor.addIssue(Messages.CodeAfterAsyncCallCheck_Issue, statement);
                    }
                }
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
            List<Statement> st = null;
            if (container instanceof LoopStatement)
            {
                st = ((LoopStatement)container).getStatements();
            }
            else if (container instanceof Conditional)
            {
                st = ((Conditional)container).getStatements();
            }
            else if (container instanceof IfStatement)
            {
                st = ((IfStatement)container).getElseStatements();
            }
            else if (container instanceof TryExceptStatement)
            {
                st = getStatementsFromContainer((TryExceptStatement)container);
            }
            else if (container instanceof PreprocessorItemStatements)
            {
                st = ((PreprocessorItemStatements)container).getStatements();
            }
            else
            {
                st = getStatementsFromContainer(container);
            }
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
