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

package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormParameter;
import com._1c.g5.v8.dt.mcore.DuallyNamedElement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check the Parameter.Property() access to exist parameter of form.
 * @author Vadim Goncharov
 */
public class OptionalFormParameterAccessCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "optional-form-parameter-access"; //$NON-NLS-1$

    private static final String PROPERTY_NAME_RU = "Свойство"; //$NON-NLS-1$

    private static final String PROPERTY_NAME = "Property"; //$NON-NLS-1$

    private static final String PARAMETERS_KEYWORD_RU = "Параметры"; //$NON-NLS-1$

    private static final String PARAMETERS_KEYWORD = "Parameters"; //$NON-NLS-1$

    public OptionalFormParameterAccessCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.OptionalFormParameterAccessCheck_title)
            .description(Messages.OptionalFormParameterAccessCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(741, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation inv = (Invocation)object;
        if (monitor.isCanceled() || !isValidInvocation(inv))
        {
            return;
        }

        String paramName = getParamName(inv);
        if (monitor.isCanceled() || StringUtils.isEmpty(paramName))
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(inv, Module.class);
        EObject moduleOwner = module.getOwner();
        if (monitor.isCanceled() || !(moduleOwner instanceof Form))
        {
            return;
        }

        Form form = (Form)moduleOwner;
        if (!monitor.isCanceled() && findParameter(form, paramName))
        {
            String msg = MessageFormat.format(Messages.OptionalFormParameterAccessCheck_Optional_form_parameter_access,
                paramName);
            resultAcceptor.addIssue(msg, inv);
        }
    }

    private boolean isValidInvocation(Invocation inv)
    {

        FeatureAccess access = inv.getMethodAccess();
        if ((access instanceof DynamicFeatureAccess) && !(((DynamicFeatureAccess)access).getFeatureEntries().isEmpty()))
        {

            DynamicFeatureAccess dfa = (DynamicFeatureAccess)access;
            EObject featureEntry = dfa.getFeatureEntries().get(0).getFeature();
            if (featureEntry instanceof DuallyNamedElement)
            {

                DuallyNamedElement namedElement = (DuallyNamedElement)featureEntry;
                Expression source = dfa.getSource();
                if ((namedElement.getName().equalsIgnoreCase(PROPERTY_NAME)
                    || namedElement.getNameRu().equalsIgnoreCase(PROPERTY_NAME_RU))
                    && source instanceof StaticFeatureAccess
                    && !(((StaticFeatureAccess)source).getFeatureEntries().isEmpty()))
                {
                    featureEntry = ((StaticFeatureAccess)source).getFeatureEntries().get(0).getFeature();
                    if (featureEntry instanceof DuallyNamedElement)
                    {
                        namedElement = (DuallyNamedElement)featureEntry;
                        return (namedElement.getName().equalsIgnoreCase(PARAMETERS_KEYWORD)
                            || namedElement.getNameRu().equalsIgnoreCase(PARAMETERS_KEYWORD_RU)) && isValidParam(inv);
                    }
                }
            }
        }

        return false;
    }

    private boolean isValidParam(Invocation inv)
    {
        return !(inv.getParams().isEmpty() || !(inv.getParams().get(0) instanceof StringLiteral));
    }

    private String getParamName(Invocation inv)
    {
        StringLiteral literal = (StringLiteral)inv.getParams().get(0);
        return String.join("", literal.lines(true)); //$NON-NLS-1$
    }

    private boolean findParameter(Form form, String paramName)
    {
        for (FormParameter param : form.getParameters())
        {
            if (param.getName().equalsIgnoreCase(paramName))
            {
                return true;
            }
        }
        return false;
    }
}
