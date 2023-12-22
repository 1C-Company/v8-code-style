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
package com.e1c.v8codestyle.bsl.strict.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FEATURE_ACCESS__NAME;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.util.Pair;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.common.IStaticExpressionValueComputer;
import com._1c.g5.v8.dt.bsl.common.IStringLiteralTextProvider;
import com._1c.g5.v8.dt.bsl.model.BslDerivedPropertySource;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.mcore.DerivedProperty;
import com._1c.g5.v8.dt.mcore.Property;
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
 * The check of structure key modification using methods Clear(), Delete() and Insert()
 * for structure created with external constructor function.
 *
 * @author Dmitriy Marmyshev
 */
public class StructureKeyModificationCheck
    extends AbstractTypeCheck
{
    private static final String CHECK_ID = "structure-key-modification"; //$NON-NLS-1$

    private static final String PARAM_CHECK_INSERT = "checkInsert"; //$NON-NLS-1$
    private static final String PARAM_CHECK_DELETE = "checkDelete"; //$NON-NLS-1$
    private static final String PARAM_CHECK_CLEAR = "checkClear"; //$NON-NLS-1$

    private static final String INSERT_RU = "Вставить"; //$NON-NLS-1$
    private static final String INSERT = "Insert"; //$NON-NLS-1$

    private static final String CLEAR_RU = "Очистить"; //$NON-NLS-1$
    private static final String CLEAR = "Clear"; //$NON-NLS-1$

    private static final String DELETE_RU = "Удалить"; //$NON-NLS-1$
    private static final String DELETE = "Delete"; //$NON-NLS-1$

    private static final String MODULE_URI_FRAGMENT = "/0"; //$NON-NLS-1$

    private final IStaticExpressionValueComputer staticExpressionValueComputer;

    @Inject
    public StructureKeyModificationCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, INamingService namingService, IBmModelManager bmModelManager,
        IStaticExpressionValueComputer staticExpressionValueComputer)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager);
        this.staticExpressionValueComputer = staticExpressionValueComputer;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.StructureKeyModificationCheck_title)
            .description(Messages.StructureKeyModificationCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StrictTypeAnnotationCheckExtension())
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS)
            .parameter(PARAM_CHECK_INSERT, Boolean.class, Boolean.TRUE.toString(),
                Messages.StructureKeyModificationCheck_Check_Insert_method)
            .parameter(PARAM_CHECK_DELETE, Boolean.class, Boolean.TRUE.toString(),
                Messages.StructureKeyModificationCheck_Check_Delete_method)
            .parameter(PARAM_CHECK_CLEAR, Boolean.class, Boolean.TRUE.toString(),
                Messages.StructureKeyModificationCheck_Check_Clear_method);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        DynamicFeatureAccess fa = (DynamicFeatureAccess)object;
        String methodName = fa.getName();
        if (StringUtils.isBlank(methodName))
        {
            return;
        }

        Invocation inv = BslUtil.getInvocation(fa);
        if (inv == null)
        {
            return;
        }

        Expression source = fa.getSource();

        boolean isInsert = isInsert(methodName);
        if (parameters.getBoolean(PARAM_CHECK_INSERT) && isInsert
            || parameters.getBoolean(PARAM_CHECK_DELETE) && isDelete(methodName))
        {
            if (inv.getParams().isEmpty())
            {
                return;
            }

            Environments actualEnvs = getActualEnvironments(fa);
            if (actualEnvs.isEmpty())
            {
                return;
            }
            List<TypeItem> sourceTypes = computeTypes(source, actualEnvs).stream()
                .filter(t -> IEObjectTypeNames.STRUCTURE.equals(McoreUtil.getTypeName(t)))
                .collect(Collectors.toList());
            if (sourceTypes.isEmpty())
            {
                return;
            }
            IStringLiteralTextProvider structureKey =
                staticExpressionValueComputer.getStringContent(inv.getParams().get(0));
            if (structureKey != null && StringUtils.isNotBlank(structureKey.getText()))
            {
                String keyName = structureKey.getText();

                if (isExternalStructureKey(sourceTypes, source, keyName, monitor))
                {
                    String message = isInsert ? Messages.StructureKeyModificationCheck_error_message_Insert
                        : Messages.StructureKeyModificationCheck_error_message_Delete;
                    message = MessageFormat.format(message, keyName);
                    resultAcceptor.addIssue(message, FEATURE_ACCESS__NAME);
                }
            }

        }
        else if (parameters.getBoolean(PARAM_CHECK_CLEAR) && inv.getParams().isEmpty() && isClear(methodName))
        {
            Environments actualEnvs = getActualEnvironments(fa);
            if (actualEnvs.isEmpty())
            {
                return;
            }
            List<TypeItem> sourceTypes = computeTypes(source, actualEnvs).stream()
                .filter(t -> IEObjectTypeNames.STRUCTURE.equals(McoreUtil.getTypeName(t)))
                .collect(Collectors.toList());
            if (sourceTypes.isEmpty())
            {
                return;
            }

            Optional<DerivedProperty> firstProperty = getFirstExternalKey(sourceTypes, fa);
            if (!monitor.isCanceled() && firstProperty.isPresent())
            {
                String message = Messages.StructureKeyModificationCheck_error_message_Clear;
                resultAcceptor.addIssue(message, FEATURE_ACCESS__NAME);
            }
        }
    }

    private Optional<DerivedProperty> getFirstExternalKey(List<TypeItem> sourceTypes, Expression context)
    {
        return dynamicFeatureAccessComputer.getAllProperties(sourceTypes, context.eResource())
            .stream()
            .flatMap(e -> e.getFirst().stream())
            .filter(e -> e instanceof DerivedProperty
                && ((DerivedProperty)e).getSource() instanceof BslDerivedPropertySource
                && isExternalStructureKey((DerivedProperty)e, context))
            .map(DerivedProperty.class::cast)
            .findAny();
    }

    private boolean isExternalStructureKey(Collection<TypeItem> structureTypes, Expression currentMethodObject,
        String keyName, IProgressMonitor monitor)
    {
        Collection<Pair<Collection<Property>, TypeItem>> allProperties =
            dynamicFeatureAccessComputer.getAllProperties(structureTypes, currentMethodObject.eResource());
        for (Pair<Collection<Property>, TypeItem> pair : allProperties)
        {
            for (Property property : pair.getFirst())
            {
                if (monitor.isCanceled())
                {
                    return false;
                }

                if (property instanceof DerivedProperty && keyName.equalsIgnoreCase(property.getName())
                    && ((DerivedProperty)property).getSource() instanceof BslDerivedPropertySource)
                {
                    return isExternalStructureKey((DerivedProperty)property, currentMethodObject);
                }
            }
        }

        return false;
    }

    private boolean isExternalStructureKey(DerivedProperty property, Expression currentMethodObject)
    {
        BslDerivedPropertySource source = (BslDerivedPropertySource)property.getSource();

        URI uri = EcoreUtil.getURI(currentMethodObject).trimFragment().appendFragment(MODULE_URI_FRAGMENT);
        if (source.getModuleUri() != null && !source.getModuleUri().equals(uri.toString()))
        {
            return true;
        }

        Method method = EcoreUtil2.getContainerOfType(currentMethodObject, Method.class);

        String currentMethodName = method == null ? StringUtils.EMPTY : method.getName();
        if (!currentMethodName.equalsIgnoreCase(source.getMethodName()))
        {
            return true;
        }
        else if (source.getLocalOffset() > 0 && method != null)
        {
            ICompositeNode node = NodeModelUtils.findActualNodeFor(method);
            return source.getLocalOffset() < node.getOffset() - node.getTotalOffset();
        }
        return false;
    }

    private boolean isDelete(String methodName)
    {
        return DELETE_RU.equalsIgnoreCase(methodName) || DELETE.equalsIgnoreCase(methodName);
    }

    private boolean isInsert(String methodName)
    {
        return INSERT_RU.equalsIgnoreCase(methodName) || INSERT.equalsIgnoreCase(methodName);
    }

    private boolean isClear(String methodName)
    {
        return CLEAR_RU.equalsIgnoreCase(methodName) || CLEAR.equalsIgnoreCase(methodName);
    }

}
