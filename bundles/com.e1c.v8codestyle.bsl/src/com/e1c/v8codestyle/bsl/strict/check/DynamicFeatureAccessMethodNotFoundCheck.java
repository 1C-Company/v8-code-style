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
package com.e1c.v8codestyle.bsl.strict.check;

import java.text.MessageFormat;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.google.inject.Inject;

/**
 * Checks that {@link DynamicFeatureAccess dynamic method} exists in accessed object.
 *
 * @author Dmitriy Marmyshev
 */
public class DynamicFeatureAccessMethodNotFoundCheck
    extends AbstractDynamicFeatureAccessTypeCheck
{

    private static final String CHECK_ID = "dynamic-access-method-not-found"; //$NON-NLS-1$

    /**
     * Instantiates a new dynamic feature access method not found check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     */
    @Inject
    public DynamicFeatureAccessMethodNotFoundCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
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
    protected String getTitle()
    {
        return Messages.DynamicFeatureAccessMethodNotFoundCheck_title;
    }

    @Override
    protected String getDescription()
    {
        return Messages.DynamicFeatureAccessMethodNotFoundCheck_description;
    }

    @Override
    protected boolean isCheckDfaMethod()
    {
        return true;
    }

    @Override
    protected String getErrorMessage(DynamicFeatureAccess fa)
    {
        return MessageFormat.format(Messages.DynamicFeatureAccessTypeCheck_Method_M_not_found_in_accessed_object,
            fa.getName());
    }

}
