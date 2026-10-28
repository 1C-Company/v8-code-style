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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.strict.check.AbstractTypeCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks that form data objects (FormDataStructure, FormDataCollection, FormDataStructureAndCollection, FormDataTree,
 * SpreadsheetDocument) are not passed by value as parameters of a non-context server call from the client. Such objects
 * are always transferred as a whole, so they should be handled on the server via an explicit context server call.
 *
 * @author Ivan Sergeev
 */
public class FormDataByValueToServerCheck
    extends AbstractTypeCheck
{
    private static final String CHECK_ID = "form-data-by-value-to-server"; //$NON-NLS-1$

    private static final Set<String> FORM_DATA_TYPE_NAMES = Set.of(
        IEObjectTypeNames.FORM_DATA_STRUCTURE, IEObjectTypeNames.FORM_DATA_COLLECTION,
        IEObjectTypeNames.FORM_DATA_STRUCTURE_AND_COLLECTION, IEObjectTypeNames.FORM_DATA_TREE,
        IEObjectTypeNames.SPREADSHEET_DOCUMENT);

    private static final Set<String> CONTEXT_SERVER_PRAGMAS;

    static
    {
        Set<String> pragmas = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        pragmas.add(com._1c.g5.v8.dt.bsl.common.Symbols.AT_SERVER_RUS);
        pragmas.add(com._1c.g5.v8.dt.bsl.common.Symbols.AT_SERVER_INTNL);
        CONTEXT_SERVER_PRAGMAS = pragmas;
    }

    /**
     * Instantiates a new form data by value to server check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     * @param namingService service for getting names of EDT object and resources, cannot be {@code null}
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be {@code null}
     * @param v8ProjectManager {@link IV8ProjectManager} for getting project by {@link EObject}, cannot be {@code null}
     */
    @Inject
    public FormDataByValueToServerCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
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
        builder.title(Messages.FormDataByValueToServerCheck_Title)
            .description(Messages.FormDataByValueToServerCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .extension(new StandardCheckExtension(636, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        Invocation invocation = (Invocation)object;
        if (monitor.isCanceled() || !invocation.isIsServerCall() || invocation.getParams().isEmpty())
        {
            return;
        }

        Environments envs = getActualEnvironments(invocation);
        if (!envs.containsAny(Environments.MNG_CLIENTS))
        {
            return;
        }

        EObject callee = getSourceMethod(invocation.getMethodAccess());
        if (callee instanceof Method && isContextServerMethod((Method)callee))
        {
            return;
        }

        for (Expression param : invocation.getParams())
        {
            if (monitor.isCanceled())
            {
                return;
            }

            List<TypeItem> types = computeTypes(param, envs);
            if (isFormDataType(types))
            {
                resultAceptor.addIssue(Messages.FormDataByValueToServerCheck_Issue, param,
                    BslPackage.Literals.EXPRESSION__TYPES);
                return;
            }
        }
    }

    private boolean isContextServerMethod(Method method)
    {
        for (Pragma pragma : method.getPragmas())
        {
            if (CONTEXT_SERVER_PRAGMAS.contains(pragma.getSymbol()))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isFormDataType(List<TypeItem> types)
    {
        for (TypeItem type : types)
        {
            String name = McoreUtil.getTypeName(type);
            if (name != null && FORM_DATA_TYPE_NAMES.contains(name))
            {
                return true;
            }
        }
        return false;
    }
}
