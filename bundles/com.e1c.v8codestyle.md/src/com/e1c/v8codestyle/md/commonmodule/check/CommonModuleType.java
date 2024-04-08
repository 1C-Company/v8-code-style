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
 *     Aleksandr Kapralov - issue #15
 *******************************************************************************/
package com.e1c.v8codestyle.md.commonmodule.check;

import static com._1c.g5.v8.dt.common.Functions.featureToLabel;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.metadata.mdclass.util.MdClassUtil;
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
 * Check correct type of common module environment
 *
 * @author Dmitriy Marmyshev
 *
 */
public final class CommonModuleType
    extends BasicCheck
{

    private static final String CHECK_ID = "common-module-type"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    /**
     * Instantiates a new common module type.
     *
     * @param v8ProjectManager the v8 project manager service, cannot be {@code null}.
     */
    @Inject
    public CommonModuleType(IV8ProjectManager v8ProjectManager)
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
        //@formatter:off
        builder.title(Messages.CommonModuleType_title)
            .description(Messages.CommonModuleType_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.BLOCKER)
            .issueType(IssueType.CODE_STYLE)
            .extension(new TopObjectFilterExtension())
            .extension(new StandardCheckExtension(469, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(SkipAdoptedInExtensionMdObjectExtension.instance())
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(CommonModuleTypes.SERVER.getFeatureValues(false).keySet().toArray(new EStructuralFeature[0]));
        //@formatter:on

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        CommonModule commonModule = (CommonModule)object;

        IV8Project project = v8ProjectManager.getProject(commonModule);
        boolean mobileOnly = isMobileApplicationOnly(project);

        Map<EStructuralFeature, Object> values = new HashMap<>();
        for (EStructuralFeature feature : CommonModuleTypes.SERVER.getFeatureValues(mobileOnly).keySet())
        {
            values.put(feature, commonModule.eGet(feature));
        }

        for (CommonModuleTypes type : CommonModuleTypes.values())
        {
            if (values.equals(type.getFeatureValues(mobileOnly)))
            {
                return;
            }
        }

        ScriptVariant scriptVariant = project == null ? ScriptVariant.ENGLISH : project.getScriptVariant();
        CommonModuleTypes type = CommonModuleTypes.findClosestTypeByName(commonModule.getName(), scriptVariant);

        for (Entry<EStructuralFeature, Object> entry : type.getFeatureValues(mobileOnly).entrySet())
        {
            Object value = values.get(entry.getKey());
            if (entry.getValue().equals(value))
            {
                values.remove(entry.getKey());
            }
        }

        //@formatter:off
        String types = String.join(", ",  //$NON-NLS-1$
            values.keySet()
            .stream()
            .map(f -> featureToLabel().apply(f))
            .collect(Collectors.toList()));
        //@formatter:on

        String title = type.getTitle();
        String message = MessageFormat.format(Messages.CommonModuleType_message, title, types);

        //@formatter:off
        EStructuralFeature feature = values.keySet()
            .stream()
            .findFirst()
            .orElse(MD_OBJECT__NAME);
        //@formatter:on

        resultAceptor.addIssue(message, feature);
    }

    private boolean isMobileApplicationOnly(IV8Project project)
    {
        return project != null && MdClassUtil.isMobileApplicationUsePurposes(project.getUsePurposes());
    }
}
