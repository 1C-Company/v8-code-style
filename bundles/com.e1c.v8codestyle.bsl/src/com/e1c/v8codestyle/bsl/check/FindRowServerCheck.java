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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.strict.check.AbstractTypeCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check the FindRows function is called only on the server.
 *
 *  @author Ivan Sergeev
 */
public class FindRowServerCheck
    extends AbstractTypeCheck
{
    private static final String CHECK_ID = "find-rows-server"; //$NON-NLS-1$

    private static final String FIND_ROWS_RU = "НайтиСтроки"; //$NON-NLS-1$

    private static final String FIND_ROWS = "FindRows"; //$NON-NLS-1$

    @Inject
    public FindRowServerCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, INamingService namingService, IBmModelManager bmModelManager,
        IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FindRowServerCheck_Title)
            .description(Messages.FindRowServerCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.PERFORMANCE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(628, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }
        if (object instanceof Invocation invocation)
        {
            FeatureAccess methodAccess = invocation.getMethodAccess();
            if (methodAccess == null)
            {
                return;
            }
            String name = methodAccess.getName();
            if (isFindRow(name) && isFormDataCollectionMethod(methodAccess))
            {
                Method method = EcoreUtil2.getContainerOfType(invocation, Method.class);
                if (method == null)
                {
                    return;
                }
                Environments envi = method.environments();
                if (!envi.contains(Environment.SERVER))
                {
                    resultAcceptor.addIssue(Messages.FindRowServerCheck_Issue,
                        methodAccess);
                }
            }
        }
    }

    private boolean isFindRow(String name)
    {
        return FIND_ROWS_RU.equalsIgnoreCase(name) || FIND_ROWS.equalsIgnoreCase(name);
    }

    private boolean isFormDataCollectionMethod(FeatureAccess methodAccess)
    {
        if (methodAccess instanceof DynamicFeatureAccess)
        {
            EObject source = getSourceMethod(methodAccess);
            if (source == null)
            {
                return false;
            }
            TypeItem owner = EcoreUtil2.getContainerOfType(source, TypeItem.class);
            if (owner == null)
            {
                return false;
            }
            String typeName = McoreUtil.getTypeName(owner);
            return IEObjectTypeNames.FORM_DATA_COLLECTION.equals(typeName);
        }
        return false;
    }
}
