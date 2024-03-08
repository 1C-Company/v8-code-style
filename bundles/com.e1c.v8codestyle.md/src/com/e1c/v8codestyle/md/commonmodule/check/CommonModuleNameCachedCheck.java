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
package com.e1c.v8codestyle.md.commonmodule.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__RETURN_VALUES_REUSE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;
import static com.e1c.v8codestyle.md.CommonModuleTypes.SERVER_CACHED;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.e1c.v8codestyle.md.CommonModuleTypes;
import com.e1c.v8codestyle.md.check.SkipAdoptedInExtensionMdObjectExtension;
import com.google.inject.Inject;

/**
 * Check the postfix for a module with the cached attribute.
 *
 * @author Artem Iliukhin
 */
public final class CommonModuleNameCachedCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "common-module-name-cached"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public CommonModuleNameCachedCheck(IV8ProjectManager v8ProjectManager)
    {
        super();
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
        builder.title(Messages.CommonModuleNameServerCallPostfixCheck_Common_module_postfix_title)
            .description(Messages.CommonModuleNameServerCallPostfixCheck_Common_module_name_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(469, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new TopObjectFilterExtension())
            .extension(SkipAdoptedInExtensionMdObjectExtension.instance())
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(MD_OBJECT__NAME, COMMON_MODULE__RETURN_VALUES_REUSE);
    }

    @Override
    protected void check(Object object, ResultAcceptor result, ICheckParameters parameters, IProgressMonitor monitor)
    {
        CommonModule module = (CommonModule)object;
        if (module.getReturnValuesReuse() == ReturnValuesReuse.DONT_USE)
        {
            return;
        }

        IV8Project project = v8ProjectManager.getProject(module);
        ScriptVariant variant = project == null ? ScriptVariant.ENGLISH : project.getScriptVariant();

        addResultAcceptor(SERVER_CACHED, variant, module, result);
    }

    private void addResultAcceptor(CommonModuleTypes types, ScriptVariant variant, CommonModule module,
        ResultAcceptor result)
    {
        Map<EStructuralFeature, Object> featureValues = new HashMap<>(types.getFeatureValues(false));
        featureValues.remove(COMMON_MODULE__RETURN_VALUES_REUSE);

        String suffixe = types.getNameSuffix(variant);

        Map<EStructuralFeature, Object> values = new HashMap<>();
        for (EStructuralFeature feature : featureValues.keySet())
        {
            if (feature != COMMON_MODULE__RETURN_VALUES_REUSE)
            {
                values.put(feature, module.eGet(feature));
            }
        }

        if (values.equals(featureValues))
        {
            String name = module.getName();
            if (!(name.endsWith(suffixe)))
            {
                String message = MessageFormat.format(Messages.CommonModuleNameServerCallPostfixCheck_0, suffixe);
                result.addIssue(message, MD_OBJECT__NAME);
            }
        }
    }
}
