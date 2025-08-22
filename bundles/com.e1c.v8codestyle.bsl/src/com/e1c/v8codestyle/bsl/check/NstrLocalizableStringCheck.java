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
/**
 *
 */
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STRING_LITERAL;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STRING_LITERAL__LINES;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * @author Dmitriy Marmyshev
 *
 */
public class NstrLocalizableStringCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "bsl-localizable-string"; //$NON-NLS-1$

    private final TypesComputer typesComputer;

    private final LocalizableRegistry localizableRegistry;

    @Inject
    public NstrLocalizableStringCheck(TypesComputer typesComputer, LocalizableRegistry localizableRegistry)
    {
        this.typesComputer = typesComputer;
        this.localizableRegistry = localizableRegistry;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("String literal should be localizable with NStr")
            .description("String literal should be localizable with NStr")
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(STRING_LITERAL);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        StringLiteral literal = (StringLiteral)object;

        if (literal.getLines().size() == 1 && StringUtils.isBlank(literal.lines(true).get(0)))
        {
            // skip empty lines
            return;
        }

        EObject parent = literal.eContainer();

        if (parent instanceof Invocation && isLocalizableParameter((Invocation)parent, literal, monitor)
            || parent instanceof SimpleStatement && ((SimpleStatement)parent).getLeft() instanceof DynamicFeatureAccess
                && !monitor.isCanceled()
                && isLocalizableProperty((DynamicFeatureAccess)((SimpleStatement)parent).getLeft(), monitor))
        {
            resultAceptor.addIssue("Localizable string should be in NStr()", STRING_LITERAL__LINES);
        }

    }

    private boolean isLocalizableParameter(Invocation inv, EObject parameter, IProgressMonitor monitor)
    {
        FeatureAccess method = inv.getMethodAccess();
        if (StringUtils.isBlank(method.getName()))
        {
            return false;
        }

        if (method instanceof StaticFeatureAccess && !monitor.isCanceled())
        {
            Collection<Integer> params = localizableRegistry.getStaticInvocationParameters(method.getName());
            if (!params.isEmpty())
            {
                int index = inv.getParams().indexOf(parameter);
                return params.contains(index);
            }
        }
        else if (method instanceof DynamicFeatureAccess && !monitor.isCanceled())
        {
            int index = inv.getParams().indexOf(parameter);
            DynamicFeatureAccess dfa = (DynamicFeatureAccess)method;
            Collection<String> typeNames = localizableRegistry.getDynamicTypesForMethod(dfa.getName(), index);
            if (!typeNames.isEmpty() && !monitor.isCanceled())
            {
                Environmental env = EcoreUtil2.getContainerOfType(dfa, Environmental.class);
                List<TypeItem> types = typesComputer.computeTypes(dfa.getSource(), env.environments());
                return !types.isEmpty() && types.stream()
                    .map(McoreUtil::getTypeName)
                    .filter(Objects::nonNull)
                    .anyMatch(typeNames::contains);
            }
        }
        return false;
    }

    private boolean isLocalizableProperty(DynamicFeatureAccess property, IProgressMonitor monitor)
    {
        String name = property.getName();
        if (StringUtils.isBlank(name))
        {
            return false;
        }
        Collection<String> typeNames = localizableRegistry.getDynamicTypesForProperty(name);

        if (!typeNames.isEmpty() && !monitor.isCanceled())
        {
            Environmental env = EcoreUtil2.getContainerOfType(property, Environmental.class);
            List<TypeItem> types = typesComputer.computeTypes(property.getSource(), env.environments());
            return !types.isEmpty()
                && types.stream().map(McoreUtil::getTypeName).filter(Objects::nonNull).anyMatch(typeNames::contains);
        }

        return false;
    }

}
