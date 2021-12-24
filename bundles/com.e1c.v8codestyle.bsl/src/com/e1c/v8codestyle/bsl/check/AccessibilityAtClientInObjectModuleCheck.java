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
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DECLARE_STATEMENT;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DECLARE_STATEMENT__VARIABLES;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Preprocessor;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks that method or declared variables accessible &AtClient in manager or object module.
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 */
public class AccessibilityAtClientInObjectModuleCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "module-accessibility-at-client"; //$NON-NLS-1$

    //@formatter:off
    private static final String MANAGER_EVENT_EXCEPTION_NAMES = String.join(",", Set.of( //$NON-NLS-1$
        "PresentationFieldsGetProcessing",  //$NON-NLS-1$
        "ОбработкаПолученияПолейПредставления", //$NON-NLS-1$
        "PresentationGetProcessing",  //$NON-NLS-1$
        "ОбработкаПолученияПредставления")); //$NON-NLS-1$
    //@formatter:on

    private static final String PARAMETER_ALLOW_MANAGER_EVENTS_AT_CLIENT = "allowManagerEventsAtClient"; //$NON-NLS-1$

    private static final String PARAMETER_METHODS_AT_CLIENT = "methodsAtClient"; //$NON-NLS-1$

    private final IBslPreferences bslPreferences;

    /**
     * Instantiates a new accessibility at client in object module check.
     *
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     */
    @Inject
    public AccessibilityAtClientInObjectModuleCheck(IBslPreferences bslPreferences)
    {
        super();
        this.bslPreferences = bslPreferences;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.AccessibilityAtClientInObjectModuleCheck_title)
            .description(Messages.AccessibilityAtClientInObjectModuleCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.PORTABILITY)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD, DECLARE_STATEMENT)
            .parameter(PARAMETER_ALLOW_MANAGER_EVENTS_AT_CLIENT, String.class, MANAGER_EVENT_EXCEPTION_NAMES,
                Messages.AccessibilityAtClientInObjectModuleCheck_Manager_event_handlers_allows_to_be_AtClient)
            .parameter(PARAMETER_METHODS_AT_CLIENT, String.class, StringUtils.EMPTY,
                Messages.AccessibilityAtClientInObjectModuleCheck_Methods_should_be_AtClient);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        EObject eObject = (EObject)object;

        if (eObject instanceof DeclareStatement
            && (eObject instanceof Preprocessor || EcoreUtil2.getContainerOfType(eObject, Method.class) != null))
        {
            // Skip preprocessor declare statement or declared variables in method
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(eObject, Module.class);

        if (!isValidModule(module) || monitor.isCanceled())
        {
            return;
        }

        Environmental environmental = EcoreUtil2.getContainerOfType(eObject, Environmental.class);
        Environments enivronmetsObject = environmental.environments();
        Environments checkingEnvs = bslPreferences.getLoadEnvs(eObject).intersect(Environments.MNG_CLIENTS);

        boolean isAccessibleAtClient = enivronmetsObject.containsAny(checkingEnvs);

        if (!isAccessibleAtClient && isMethodAtClient(eObject, parameters))
        {
            resultAceptor.addIssue(
                Messages.AccessibilityAtClientInObjectModuleCheck_Event_handler_should_be_accessible_AtClient, eObject,
                NAMED_ELEMENT__NAME);
        }

        if (!isAccessibleAtClient || monitor.isCanceled() || allowManagerEventAtClient(eObject, module, parameters))
        {
            return;
        }

        if (eObject instanceof Method)
        {
            resultAceptor.addIssue(Messages.AccessibilityAtClientInObjectModuleCheck_Method_accessible_AtClient,
                eObject, NAMED_ELEMENT__NAME);
        }
        else if (eObject instanceof DeclareStatement)
        {
            resultAceptor.addIssue(
                Messages.AccessibilityAtClientInObjectModuleCheck_Declared_variable_accessible_AtClient, eObject,
                DECLARE_STATEMENT__VARIABLES);
        }
    }

    private boolean isValidModule(Module module)
    {
        ModuleType type = module.getModuleType();
        return type == ModuleType.MANAGER_MODULE || type == ModuleType.OBJECT_MODULE
            || type == ModuleType.RECORDSET_MODULE;
    }

    private boolean allowManagerEventAtClient(EObject object, Module module, ICheckParameters parameters)
    {
        if (object instanceof Method && module.getModuleType() == ModuleType.MANAGER_MODULE
            && ((Method)object).isEvent())
        {
            String parameterMethodNames = parameters.getString(PARAMETER_ALLOW_MANAGER_EVENTS_AT_CLIENT);
            if (StringUtils.isEmpty(parameterMethodNames))
            {
                return false;
            }

            Method method = (Method)object;
            Set<String> methodNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            methodNames.addAll(List.of(parameterMethodNames.split(",\\s*"))); //$NON-NLS-1$
            return methodNames.contains(method.getName());

        }
        return false;
    }

    private boolean isMethodAtClient(EObject object, ICheckParameters parameters)
    {
        if (!(object instanceof Method))
        {
            return false;
        }
        String parameterMethodNames = parameters.getString(PARAMETER_METHODS_AT_CLIENT);
        if (StringUtils.isEmpty(parameterMethodNames))
        {
            return false;
        }
        Method method = (Method)object;
        Set<String> methodNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        methodNames.addAll(List.of(parameterMethodNames.split(",\\s*"))); //$NON-NLS-1$
        return methodNames.contains(method.getName());
    }
}
