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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DECLARE_STATEMENT;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.EXPLICIT_VARIABLE;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.IMPLICIT_VARIABLE;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.ImplicitVariable;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Checks variable of the module has value type.
 *
 * @author Dmitriy Marmyshev
 */
public class VariableTypeCheck
    extends AbstractTypeCheck
{

    private static final String CHECK_ID = "variable-value-type"; //$NON-NLS-1$

    /**
     * Instantiates a new variable type check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     */
    @Inject
    public VariableTypeCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.VariableTypeCheck_title)
            .description(Messages.VariableTypeCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StrictTypeAnnotationCheckExtension())
            .module()
            .checkedObjectType(IMPLICIT_VARIABLE, EXPLICIT_VARIABLE, SIMPLE_STATEMENT, DECLARE_STATEMENT);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof EObject))
        {
            return;
        }

        if (object instanceof ImplicitVariable || object instanceof ExplicitVariable)
        {
            checkVariable((Variable)object, (EObject)object, resultAceptor, monitor);
        }
        else if (object instanceof SimpleStatement && ((SimpleStatement)object).getLeft() instanceof StaticFeatureAccess
            && ((StaticFeatureAccess)((SimpleStatement)object).getLeft()).getImplicitVariable() != null)
        {
            EObject checkType = ((SimpleStatement)object).getLeft();
            Variable variable = ((StaticFeatureAccess)((SimpleStatement)object).getLeft()).getImplicitVariable();
            checkVariable(variable, checkType, resultAceptor, monitor);
        }
        else if (object instanceof DeclareStatement)
        {
            DeclareStatement declare = (DeclareStatement)object;
            for (Variable variable : declare.getVariables())
            {
                if (monitor.isCanceled())
                {
                    return;
                }
                checkVariable(variable, variable, resultAceptor, monitor);
            }
        }
    }

    private void checkVariable(Variable variable, EObject checkObject, ResultAcceptor resultAceptor,
        IProgressMonitor monitor)
    {
        if (checkObject != null && variable != null && isEmptyTypes(checkObject) && !monitor.isCanceled())
        {
            String message =
                MessageFormat.format(Messages.VariableTypeCheck_Variable_M_has_no_value_type, variable.getName());

            resultAceptor.addIssue(message, variable, NAMED_ELEMENT__NAME);
        }
    }

}
