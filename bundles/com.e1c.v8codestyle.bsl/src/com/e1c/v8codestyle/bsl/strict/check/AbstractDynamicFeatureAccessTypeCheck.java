/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FEATURE_ACCESS__NAME;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * The abstract check for {@link DynamicFeatureAccess} if method have source object, means method exist in the object,
 *  or property has return value type that means property exist and has typed value.
 *
 * @author Dmitriy Marmyshev
 */
public abstract class AbstractDynamicFeatureAccessTypeCheck
    extends AbstractTypeCheck
{
    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String PARAMETER_SKIP_SOURCE_TYPES = "skipSourceTypes"; //$NON-NLS-1$

    private static final String DEFAULT_SKIP_SOURCE_TYPES =
        String.join(DELIMITER, Set.of(IEObjectTypeNames.COM_OBJECT));

    /**
     * Instantiates a new dynamic feature access type check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     */
    protected AbstractDynamicFeatureAccessTypeCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, INamingService namingService, IBmModelManager bmModelManager,
        IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(getTitle())
            .description(getDescription())
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StrictTypeAnnotationCheckExtension())
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS)
            .parameter(PARAMETER_SKIP_SOURCE_TYPES, String.class, DEFAULT_SKIP_SOURCE_TYPES,
                Messages.AbstractDynamicFeatureAccessTypeCheck_Skip_source_object_types);

    }

    /**
     * Gets the title of the check.
     *
     * @return the title, cannot return {@code null}.
     */
    protected abstract String getTitle();

    /**
     * Gets the description of the check.
     *
     * @return the description, cannot return {@code null}.
     */
    protected abstract String getDescription();

    /**
     * Checks if the {@link DynamicFeatureAccess} is method.
     *
     * @return true, if the {@link DynamicFeatureAccess} is method
     */
    protected abstract boolean isCheckDfaMethod();

    /**
     * Gets the error message by {@link DynamicFeatureAccess}.
     *
     * @param fa the {@link DynamicFeatureAccess} to generate message
     * @return the error message, cannot return {@code null}.
     */
    protected abstract String getErrorMessage(DynamicFeatureAccess fa);

    @Override
    protected final void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        DynamicFeatureAccess fa = (DynamicFeatureAccess)object;
        if (StringUtils.isBlank(fa.getName()))
        {
            return;
        }

        boolean isMethod = BslUtil.getInvocation(fa) != null;
        if (isMethod == isCheckDfaMethod()
            && (isMethod && isEmptySource(fa) || !isMethod && isEmptyTypes(fa, bmTransaction)) && !monitor.isCanceled()
            && !isSkipSourceType(fa, parameters, monitor))
        {
            String message = getErrorMessage(fa);

            resultAceptor.addIssue(message, FEATURE_ACCESS__NAME);
        }
    }

    private boolean isEmptySource(DynamicFeatureAccess object)
    {
        Environmental envs = EcoreUtil2.getContainerOfType(object, Environmental.class);
        if (envs == null)
        {
            return true;
        }

        Environments actualEnvs = bslPreferences.getLoadEnvs(object).intersect(envs.environments());
        if (actualEnvs.isEmpty())
        {
            return true;
        }
        List<FeatureEntry> objects = dynamicFeatureAccessComputer.getLastObject(object, actualEnvs);
        return objects.isEmpty();
    }

    private boolean isSkipSourceType(DynamicFeatureAccess fa, ICheckParameters parameters, IProgressMonitor monitor)
    {
        String typesString = parameters.getString(PARAMETER_SKIP_SOURCE_TYPES);
        if (StringUtils.isBlank(typesString))
        {
            return false;
        }

        Set<String> typeNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        typeNames.addAll(List.of(typesString.split(",\\s*"))); //$NON-NLS-1$
        if (monitor.isCanceled() || typeNames.isEmpty())
        {
            return false;
        }
        Expression source = fa.getSource();

        Environmental envs = EcoreUtil2.getContainerOfType(source, Environmental.class);
        if (monitor.isCanceled() || envs == null)
        {
            return false;
        }

        Environments actualEnvs = bslPreferences.getLoadEnvs(source).intersect(envs.environments());
        if (monitor.isCanceled() || actualEnvs.isEmpty())
        {
            return false;
        }

        List<TypeItem> types = computeTypes(source, actualEnvs);
        return !monitor.isCanceled() && !types.isEmpty() && types.stream().anyMatch(t -> {
            String typeName = McoreUtil.getTypeName(t);
            return typeName != null && typeNames.contains(typeName);
        });
    }

}
