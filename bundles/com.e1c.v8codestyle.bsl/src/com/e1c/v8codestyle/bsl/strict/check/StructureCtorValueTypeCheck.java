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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.OPERATOR_STYLE_CREATOR;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STRING_LITERAL__LINES;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.EmptyExpression;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
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
 * Checks Structure constructor string literal that each key has typed value.
 *
 * @author Dmitriy Marmyshev
 */
public class StructureCtorValueTypeCheck
    extends AbstractTypeCheck
{

    private static final String CHECK_ID = "structure-consructor-value-type"; //$NON-NLS-1$

    /**
     * Instantiates a new structure constructor value type check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     */
    @Inject
    public StructureCtorValueTypeCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
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
        builder.title(Messages.StructureCtorValueTypeCheck_title)
            .description(Messages.StructureCtorValueTypeCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StrictTypeAnnotationCheckExtension())
            .module()
            .checkedObjectType(OPERATOR_STYLE_CREATOR);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        OperatorStyleCreator osc = (OperatorStyleCreator)object;
        if (monitor.isCanceled() || osc.getParams().isEmpty()
            || !IEObjectTypeNames.STRUCTURE.equals(McoreUtil.getTypeName(osc.getType()))
            || !(osc.getParams().get(0) instanceof StringLiteral))
        {
            return;
        }

        StringLiteral literal = (StringLiteral)osc.getParams().get(0);

        String content = String.join("", literal.lines(true)); //$NON-NLS-1$
        String[] keys = content.replace(" ", "").split(","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        int totalParams = osc.getParams().size() - 1;
        for (int i = 0; i < keys.length; i++)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            String key = keys[i];
            if (totalParams > i && osc.getParams().get(i + 1) != null)
            {
                Expression param = osc.getParams().get(i + 1);
                if (isEmptyTypes(param, bmTransaction))
                {
                    param = param instanceof EmptyExpression ? literal : param;
                    String message = MessageFormat.format(
                        Messages.StructureCtorValueTypeCheck_Structure_key__N__K__value_initialized_with_empty_types,
                        i + 1, key);
                    resultAceptor.addIssue(message, param);
                }
            }
            else
            {
                String message = MessageFormat.format(
                    Messages.StructureCtorValueTypeCheck_Structure_key__N__K__has_no_default_value_initializer, i + 1,
                    key);
                resultAceptor.addIssue(message, literal, STRING_LITERAL__LINES);
            }
        }
    }

}
