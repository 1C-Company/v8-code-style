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

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Preprocessor;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Checks that method or declared variables accessible at Client in manager or object module.
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 */
public class AccessibilityAtClientInObjectModuleCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "module-accessibility-at-client"; //$NON-NLS-1$

    //@formatter:off
    private static final Collection<String> MANAGER_EVENT_EXCEPTION_NAMES = Set.of(
        "PresentationFieldsGetProcessin",  //$NON-NLS-1$
        "ОбработкаПолученияПолейПредставления", //$NON-NLS-1$
        "PresentationGetProcessing",  //$NON-NLS-1$
        "ОбработкаПолученияПредставления",  //$NON-NLS-1$
        "FormGetProcessing",  //$NON-NLS-1$
        "ОбработкаПолученияФормы", //$NON-NLS-1$
        "AfterWriteDataHistoryVersionsProcessing",  //$NON-NLS-1$
        "ОбработкаПослеЗаписиВерсийИсторииДанных", //$NON-NLS-1$
        "ChoiceDataGetProcessing",  //$NON-NLS-1$
        "ОбработкаПолученияДанныхВыбора"); //$NON-NLS-1$
    //@formatter:on

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
            .module()
            .checkedObjectType(METHOD, DECLARE_STATEMENT);
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
        Environments chechingEnvs = bslPreferences.getLoadEnvs(eObject).intersect(Environments.MNG_CLIENTS);

        boolean isClietnEvent = isClietnEvent(eObject, module);
        boolean isAccessibleAtClient = enivronmetsObject.containsAny(chechingEnvs);

        if (isClietnEvent && !isAccessibleAtClient)
        {
            resultAceptor.addIssue(
                Messages.AccessibilityAtClientInObjectModuleCheck_Event_handler_should_be_accessible_at_Client, eObject,
                NAMED_ELEMENT__NAME);
        }
        else if (!isAccessibleAtClient || isClietnEvent || monitor.isCanceled())
        {
            return;
        }

        if (eObject instanceof Method)
        {
            resultAceptor.addIssue(Messages.AccessibilityAtClientInObjectModuleCheck_Method_accessible_at_Client,
                eObject, NAMED_ELEMENT__NAME);
        }
        else if (eObject instanceof DeclareStatement)
        {
            resultAceptor.addIssue(
                Messages.AccessibilityAtClientInObjectModuleCheck_Declared_variable_accessible_at_Client, eObject,
                DECLARE_STATEMENT__VARIABLES);
        }
    }

    private boolean isValidModule(Module module)
    {
        ModuleType type = module.getModuleType();
        return type == ModuleType.SESSION_MODULE || type == ModuleType.MANAGER_MODULE
            || type == ModuleType.OBJECT_MODULE || type == ModuleType.RECORDSET_MODULE;
    }

    private boolean isClietnEvent(EObject object, Module module)
    {
        if (object instanceof Method && module.getModuleType() == ModuleType.MANAGER_MODULE)
        {
            Method method = (Method)object;
            return MANAGER_EVENT_EXCEPTION_NAMES.contains(method.getName());
        }
        return false;
    }

}
