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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;

import java.text.MessageFormat;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.BslContextDef;
import com._1c.g5.v8.dt.bsl.model.BslContextDefMethod;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check if Deprecated procedure (function) is placed in the Deprecated region of the Public region in a common module area
 *
 * @author Olga Bozhko
 *
 */
public class DeprecatedProcedureOutsideDeprecatedRegionCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "deprecated-procedure-outside-deprecated-region"; //$NON-NLS-1$
    private static final String DEPRECATED_REGION = "Deprecated"; //$NON-NLS-1$
    private static final String DEPRECATED_REGION_RU = "УстаревшиеПроцедурыИФункции"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DeprecatedProcedureOutsideDeprecatedRegionCheck_title)
            .description(Messages.DeprecatedProcedureOutsideDeprecatedRegionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        EObject eObject = (EObject)object;
        Module module = EcoreUtil2.getContainerOfType(eObject, Module.class);
        BslContextDef bslContextDef = (BslContextDef)module.getContextDef();
        Method method = (Method)eObject;

        if (method.isExport() && method.getName() != null)
        {
            BslContextDefMethod defMethod = (BslContextDefMethod)bslContextDef.allMethods()
                .stream()
                .filter(m -> (m.getName() != null && m.getName().equals(method.getName())))
                .findAny()
                .orElse(null);

            if (defMethod != null && defMethod.isDeprecated() && (getModuleType(method) != ModuleType.COMMON_MODULE
                || !verifyLocationForDeprecated(getFirstParentRegion(method), getTopParentRegion(method))))
            {
                resultAceptor.addIssue(MessageFormat.format(
                    Messages.DeprecatedProcedureOutsideDeprecatedRegionCheck_Deprecated_procedure_or_function_is_outside_deprecated_region,
                    method.getName()), McorePackage.Literals.NAMED_ELEMENT__NAME);
            }
        }
    }

    private static boolean verifyLocationForDeprecated(Optional<RegionPreprocessor> regionFirst,
        Optional<RegionPreprocessor> regionTop)
    {
        if (regionFirst.isEmpty() || regionTop.isEmpty())
        {
            return false;
        }
        else if (regionFirst.get().getName().equals(DEPRECATED_REGION)
            && regionTop.get().getName().equals(ModuleStructureSection.PUBLIC.getNames()[0]))
        {
            return true;
        }
        else if (regionFirst.get().getName().equals(DEPRECATED_REGION_RU)
            && regionTop.get().getName().equals(ModuleStructureSection.PUBLIC.getNames()[1]))
        {
            return true;
        }
        return false;
    }
}
