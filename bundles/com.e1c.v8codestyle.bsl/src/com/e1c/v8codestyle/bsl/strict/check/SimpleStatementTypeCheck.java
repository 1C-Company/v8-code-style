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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.ImplicitVariable;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
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
import com.google.inject.Inject;

/**
 * Checks all simple statements that changes type from one to another, that should not do.
 * Implicit variable allowed to reset to {@code Undefined} type by parameter.
 *
 * @author Dmitriy Marmyshev
 */
public class SimpleStatementTypeCheck
    extends AbstractTypeCheck
{
    private static final String CHECK_ID = "statement-type-change"; //$NON-NLS-1$

    private static final String PARAM_ALLOW_IMPLICIT_VAR_RESET_TO_UNDEFINED = "allowImplicitVariableResetToUndefined"; //$NON-NLS-1$

    private static final String DEFAULT_ALLOW_IMPLICIT_VAR_RESET_TO_UNDEFINED = Boolean.TRUE.toString();

    private final IV8ProjectManager v8ProjectManager;

    /**
     * Instantiates a new simple statement change type check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param v8ProjectManager the v8 project manager service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     */
    @Inject
    public SimpleStatementTypeCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IV8ProjectManager v8ProjectManager, IQualifiedNameConverter qualifiedNameConverter,
        INamingService namingService, IBmModelManager bmModelManager)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager);
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.SimpleStatementTypeCheck_title)
            .description(Messages.SimpleStatementTypeCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StrictTypeAnnotationCheckExtension())
            .module()
            .checkedObjectType(SIMPLE_STATEMENT)
            .parameter(PARAM_ALLOW_IMPLICIT_VAR_RESET_TO_UNDEFINED, Boolean.class,
                DEFAULT_ALLOW_IMPLICIT_VAR_RESET_TO_UNDEFINED,
                Messages.SimpleStatementTypeCheck_Allow_local_Variable_reset_to_Undefined_type);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        SimpleStatement statment = (SimpleStatement)object;

        Expression left = statment.getLeft();
        if (left instanceof StaticFeatureAccess && ((StaticFeatureAccess)left).getImplicitVariable() != null)
        {
            // check new variable type exist in VariableTypeCheck
            return;
        }

        if (left == null || statment.getRight() == null)
        {
            return;
        }

        Environmental envs = EcoreUtil2.getContainerOfType(statment, Environmental.class);

        Environments actualEnvs = bslPreferences.getLoadEnvs(statment).intersect(envs.environments());
        if (actualEnvs.isEmpty())
        {
            return;
        }

        List<TypeItem> types = computeTypes(left, actualEnvs);
        if (monitor.isCanceled() || types.isEmpty())
        {
            // empty previous types will be warned in upper code of module
            return;
        }

        boolean allowImplicitVarResetToUndefined = parameters.getBoolean(PARAM_ALLOW_IMPLICIT_VAR_RESET_TO_UNDEFINED);

        boolean canResetToUndefined = allowImplicitVarResetToUndefined && isImplicitVariableSource(left, actualEnvs);

        Expression right = statment.getRight();
        if (right instanceof Invocation && !actualEnvs.containsAny(Environments.SERVER)
            && ((Invocation)right).isIsServerCall())
        {
            actualEnvs = actualEnvs.add(Environments.SERVER);
        }
        List<TypeItem> newTypes = computeTypes(right, actualEnvs);
        if (monitor.isCanceled())
        {
            return;
        }
        if (left instanceof StaticFeatureAccess && !((StaticFeatureAccess)left).getFeatureEntries().isEmpty()
            && ((StaticFeatureAccess)left).getFeatureEntries().get(0).getFeature() instanceof Variable)
        {
            Collection<TypeItem> commentTypes = computeCommentTypes(right, bmTransaction);
            newTypes.addAll(commentTypes);

        }
        if (!monitor.isCanceled() && !hasTypeIntersection(types, newTypes, statment, canResetToUndefined))
        {
            boolean isRussian = com._1c.g5.v8.dt.bsl.util.BslUtil.isRussian(statment, this.v8ProjectManager);

            String message = MessageFormat.format(Messages.SimpleStatementTypeCheck_Value_type_N_changed_to_M,
                getTypesPresentation(types, isRussian), getTypesPresentation(newTypes, isRussian));

            resultAceptor.addIssue(message, BslPackage.Literals.SIMPLE_STATEMENT__LEFT);
        }
    }

    private boolean isImplicitVariableSource(Expression expression, Environments actualEnvs)
    {
        if (expression instanceof StaticFeatureAccess)
        {
            List<FeatureEntry> features =
                dynamicFeatureAccessComputer.resolveObject((FeatureAccess)expression, actualEnvs);

            for (FeatureEntry feature : features)
            {
                if (feature.getFeature() instanceof ImplicitVariable)
                {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean hasTypeIntersection(List<TypeItem> source, List<TypeItem> target, EObject context,
        boolean canResetToUndefined)
    {
        if (target.isEmpty())
        {
            return false;
        }

        if (canResetToUndefined)
        {
            Set<String> targetNames =
                target.stream().map(McoreUtil::getTypeName).filter(Objects::nonNull).collect(Collectors.toSet());
            if (targetNames.contains(IEObjectTypeNames.UNDEFINED) && targetNames.size() == 1)
            {
                return true;
            }
        }

        return intersectTypeItem(source, target, context);
    }

    private String getTypesPresentation(List<TypeItem> types, boolean isRussian)
    {
        StringBuilder sb = new StringBuilder();

        for (TypeItem type : types)
        {
            if (sb.length() > 0)
            {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(isRussian ? McoreUtil.getTypeNameRu(type) : McoreUtil.getTypeName(type));
        }
        return sb.toString();

    }

}
