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
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__CLIENT_MANAGED_APPLICATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__EXTERNAL_CONNECTION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__GLOBAL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__PRIVILEGED;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__RETURN_VALUES_REUSE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER_CALL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.check.CheckComplexity;
import com._1c.g5.v8.dt.check.ICheckParameters;
import com._1c.g5.v8.dt.check.components.BasicCheck;
import com._1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com._1c.g5.v8.dt.check.settings.IssueSeverity;
import com._1c.g5.v8.dt.check.settings.IssueType;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;

/**
 * @author Dmitriy Marmyshev
 *
 */
public class CommonModuleNameClientServer
    extends BasicCheck
{

    public static final String CHECK_ID = "common-module-name-client-server"; //$NON-NLS-1$

    public static final String EXCLUDE_NAME_PATTERN_PARAMETER_NAME = "excludeNamePattern"; //$NON-NLS-1$

    public static final String NAME_SUFFIX_PARAMETER_NAME = "nameSuffix"; //$NON-NLS-1$

    private static final String NAME_SUFFIX_DEFAULT = "КлиентСервер,ClientServer"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        //@formatter:off
        builder.title(Messages.CommonModuleNameClientServer_title)
            .description(Messages.CommonModuleNameClientServer_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.CRITICAL)
            .issueType(IssueType.WARNING)
            .extension(new TopObjectFilterExtension(
                EXCLUDE_NAME_PATTERN_PARAMETER_NAME,
                Messages.common_Exclude_name_pattern,
                StringUtils.EMPTY,
                MD_OBJECT__NAME))
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(
                MD_OBJECT__NAME,
                COMMON_MODULE__CLIENT_MANAGED_APPLICATION,
                COMMON_MODULE__SERVER,
                COMMON_MODULE__SERVER_CALL,
                COMMON_MODULE__EXTERNAL_CONNECTION,
                COMMON_MODULE__GLOBAL,
                COMMON_MODULE__PRIVILEGED,
                COMMON_MODULE__RETURN_VALUES_REUSE)
            .parameter(NAME_SUFFIX_PARAMETER_NAME,
                String.class,
                NAME_SUFFIX_DEFAULT,
                Messages.CommonModuleName_Name_suffix_list_title);
        //@formatter:on

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        CommonModule commonModule = (CommonModule)object;
        String name = commonModule.getName();

        String nameSuffix = parameters.getString(NAME_SUFFIX_PARAMETER_NAME);
        if (nameSuffix == null || nameSuffix.isBlank())
        {
            return;
        }

        List<String> nameSuffixs = List.of(nameSuffix.replace(" ", "").split(",")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Map<EStructuralFeature, Boolean> values = new HashMap<>();
        for (EStructuralFeature feature : CommonModuleType.TYPE_CLIENT_SERVER.keySet())
        {
            values.put(feature, (Boolean)commonModule.eGet(feature));
        }

        if (values.equals(CommonModuleType.TYPE_CLIENT_SERVER)
            && commonModule.getReturnValuesReuse().equals(ReturnValuesReuse.DONT_USE)
            && nameSuffixs.stream().noneMatch(name::endsWith))
        {
            String message =
                MessageFormat.format(Messages.CommonModuleNameClientServer_message, String.join(", ", nameSuffixs)); //$NON-NLS-1$
            resultAceptor.addIssue(message, MD_OBJECT__NAME);
        }
    }

}
