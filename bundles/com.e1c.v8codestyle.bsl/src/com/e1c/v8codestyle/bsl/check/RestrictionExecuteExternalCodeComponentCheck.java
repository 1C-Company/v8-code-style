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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.strict.check.AbstractTypeCheck;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Restriction execute external code check.
 *
 *  @author Ivan Sergeev
 */
public class RestrictionExecuteExternalCodeComponentCheck
    extends AbstractTypeCheck
{
    private static final String CHECK_ID = "restriction-execute-external-component-code"; //$NON-NLS-1$

    private static final String COMPONENT_NAME = "Component name"; //$NON-NLS-1$

    private static final Set<String> IMMUTABLE_MAP_COMPONENT = Set.of("подключитьвнешнююкомпоненту", "attachaddin", //$NON-NLS-1$//$NON-NLS-2$
        "начатьустановкувнешнейкомпоненты", "begininstalladdin", "установитьвнешнююкомпоненту", "installaddin", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "начатьподключениевнешнейкомпоненты", "beginattachingaddin", "загрузитьвнешнююкомпоненту", "loadaddin"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_COMPONENT = String.join(DELIMITER, IMMUTABLE_MAP_COMPONENT);

    @Inject
    public RestrictionExecuteExternalCodeComponentCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IV8ProjectManager v8ProjectManager, IQualifiedNameConverter qualifiedNameConverter,
        INamingService namingService, IBmModelManager bmModelManager, IConfigurationProvider configurationProvider)
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
        builder.title(Messages.RestrictionExecuteExternalCodeCheck_Title)
            .description(Messages.RestrictionExecuteExternalCodeCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.SECURITY)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .parameter(COMPONENT_NAME, String.class, DEFAULT_COMPONENT,
                Messages.RestrictionExecuteExternalCodeCheck_Parametr_Title)
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        if (object instanceof Invocation invocation)
        {
            FeatureAccess featureAccess = invocation.getMethodAccess();
            String name = featureAccess.getName();
            String components = parameters.getString(COMPONENT_NAME).toLowerCase();
            List<String> ComponentNames = Arrays.asList(components.split(DELIMITER));
            for (String componentName : ComponentNames)
            {
                if (componentName.equalsIgnoreCase(name))
                {
                    IBmObject bmObject = bmTransaction.getTopObjectByFqn("Subsystem.СтандартныеПодсистемы"); //$NON-NLS-1$
                    IBmObject bmObjectEn = bmTransaction.getTopObjectByFqn("Subsystem.StandardSubsystems");//$NON-NLS-1$
                    if (bmObject != null || bmObjectEn != null)
                    {
                        resultAceptor.addIssue(Messages.RestrictionExecuteExternalCodeCheck_Issue, invocation);
                    }
                }
            }
        }
    }
}
