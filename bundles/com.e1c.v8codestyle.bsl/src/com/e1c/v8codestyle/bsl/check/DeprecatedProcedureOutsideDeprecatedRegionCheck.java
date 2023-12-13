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
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check if Deprecated procedure (function) is placed in the Deprecated region of the Public region
 * in a common module area
 *
 * @author Olga Bozhko
 *
 */
public class DeprecatedProcedureOutsideDeprecatedRegionCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "deprecated-procedure-outside-deprecated-region"; //$NON-NLS-1$
    private static final int STANDARD_NUM = 644;
    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public DeprecatedProcedureOutsideDeprecatedRegionCheck(IV8ProjectManager v8ProjectManager)
    {
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
        builder.title(Messages.DeprecatedProcedureOutsideDeprecatedRegionCheck_title)
            .description(Messages.DeprecatedProcedureOutsideDeprecatedRegionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(STANDARD_NUM, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.COMMON_MODULE, ModuleType.MANAGER_MODULE,
                ModuleType.OBJECT_MODULE, ModuleType.RECORDSET_MODULE, ModuleType.VALUE_MANAGER_MODULE))
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

        IV8Project project = v8ProjectManager.getProject(method);
        ScriptVariant scriptVariant = project.getScriptVariant();

        if (method.isExport() && method.getName() != null)
        {
            BslContextDefMethod defMethod = (BslContextDefMethod)bslContextDef.allMethods()
                .stream()
                .filter(m -> m.getName() != null && m.getName().equals(method.getName()))
                .findAny()
                .orElse(null);

            if (defMethod != null && defMethod.isDeprecated()
                && !verifyLocationForDeprecated(
                    getParentRegionByName(method, ModuleStructureSection.DEPRECATED_REGION.getName(scriptVariant)),
                    getTopParentRegion(method), scriptVariant))
            {
                resultAceptor.addIssue(MessageFormat.format(
                    Messages.DeprecatedProcedureOutsideDeprecatedRegionCheck_Deprecated_function_out_of_deprecated_area,
                    method.getName()), McorePackage.Literals.NAMED_ELEMENT__NAME);
            }
        }
    }

    private static boolean verifyLocationForDeprecated(Optional<RegionPreprocessor> regionFirst,
        Optional<RegionPreprocessor> regionTop, ScriptVariant scriptVariant)
    {
        if (regionFirst.isEmpty() || regionTop.isEmpty())
        {
            return false;
        }
        if (regionFirst.get().getName() == null || regionTop.get().getName() == null)
        {
            return false;
        }
        return regionFirst.get().getName().equals(ModuleStructureSection.DEPRECATED_REGION.getName(scriptVariant))
            && (regionTop.get().getName().equals(ModuleStructureSection.PUBLIC.getName(scriptVariant))
                || regionTop.get().getName().equals(ModuleStructureSection.INTERNAL.getName(scriptVariant)));
    }
}
