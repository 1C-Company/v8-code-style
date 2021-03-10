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

import static com._1c.g5.v8.dt.common.Functions.featureToLabel;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__CLIENT_MANAGED_APPLICATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__CLIENT_ORDINARY_APPLICATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__EXTERNAL_CONNECTION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__GLOBAL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__PRIVILEGED;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER_CALL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

/**
 * Check correct type of common module environment
 *
 * @author Dmitriy Marmyshev
 *
 */
public class CommonModuleType
    extends BasicCheck
{

    public static final String CHECK_ID = "common-module-type"; //$NON-NLS-1$

    public static final String EXCLUDE_NAME_PATTERN_PARAMETER_NAME = "excludeNamePattern"; //$NON-NLS-1$

    //@formatter:off
    public static final Map<EStructuralFeature, Boolean> TYPE_SERVER = Map.of(
        COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
        COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
        COMMON_MODULE__SERVER, true,
        COMMON_MODULE__SERVER_CALL, false,
        COMMON_MODULE__EXTERNAL_CONNECTION, true,
        COMMON_MODULE__GLOBAL, false,
        COMMON_MODULE__PRIVILEGED, false);

    public static final Map<EStructuralFeature, Boolean> TYPE_SERVER_CALL = Map.of(
        COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
        COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, false,
        COMMON_MODULE__SERVER, true,
        COMMON_MODULE__SERVER_CALL, true,
        COMMON_MODULE__EXTERNAL_CONNECTION, false,
        COMMON_MODULE__GLOBAL, false,
        COMMON_MODULE__PRIVILEGED, false);

    public static final Map<EStructuralFeature, Boolean> TYPE_CLIENT = Map.of(
        COMMON_MODULE__CLIENT_MANAGED_APPLICATION, true,
        COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
        COMMON_MODULE__SERVER, false,
        COMMON_MODULE__SERVER_CALL, false,
        COMMON_MODULE__EXTERNAL_CONNECTION, false,
        COMMON_MODULE__GLOBAL, false,
        COMMON_MODULE__PRIVILEGED, false);

    public static final Map<EStructuralFeature, Boolean> TYPE_CLIENT_SERVER = Map.of(
        COMMON_MODULE__CLIENT_MANAGED_APPLICATION, true,
        COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
        COMMON_MODULE__SERVER, true,
        COMMON_MODULE__SERVER_CALL, false,
        COMMON_MODULE__EXTERNAL_CONNECTION, true,
        COMMON_MODULE__GLOBAL, false,
        COMMON_MODULE__PRIVILEGED, false);

    public static final Map<EStructuralFeature, Boolean> TYPE_SERVER_GLOBAL = Map.of(
        COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
        COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
        COMMON_MODULE__SERVER, true,
        COMMON_MODULE__SERVER_CALL, false,
        COMMON_MODULE__EXTERNAL_CONNECTION, true,
        COMMON_MODULE__GLOBAL, true,
        COMMON_MODULE__PRIVILEGED, false);

    public static final Map<EStructuralFeature, Boolean> TYPE_CLIENT_GLOBAL = Map.of(
        COMMON_MODULE__CLIENT_MANAGED_APPLICATION, true,
        COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
        COMMON_MODULE__SERVER, false,
        COMMON_MODULE__SERVER_CALL, false,
        COMMON_MODULE__EXTERNAL_CONNECTION, false,
        COMMON_MODULE__GLOBAL, true,
        COMMON_MODULE__PRIVILEGED, false);

    //@formatter:on

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
            .extension(new TopObjectFilterExtension(
                EXCLUDE_NAME_PATTERN_PARAMETER_NAME,
                Messages.common_Exclude_name_pattern,
                StringUtils.EMPTY,
                MD_OBJECT__NAME))
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(COMMON_MODULE__CLIENT_MANAGED_APPLICATION,
                COMMON_MODULE__SERVER,
                COMMON_MODULE__SERVER_CALL,
                COMMON_MODULE__EXTERNAL_CONNECTION,
                COMMON_MODULE__GLOBAL,
                COMMON_MODULE__PRIVILEGED,
                COMMON_MODULE__CLIENT_ORDINARY_APPLICATION);
        //@formatter:on

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        CommonModule commonModule = (CommonModule)object;

        Map<EStructuralFeature, Boolean> values = new HashMap<>();
        for (EStructuralFeature feature : TYPE_SERVER.keySet())
        {
            values.put(feature, (Boolean)commonModule.eGet(feature));
        }

        if (values.equals(TYPE_SERVER) || values.equals(TYPE_SERVER_CALL) || values.equals(TYPE_CLIENT)
            || values.equals(TYPE_CLIENT_SERVER))
        {
            return;
        }

        //@formatter:off
        String types = String.join(", ",  //$NON-NLS-1$
            values.entrySet()
            .stream()
            .filter(Entry::getValue)
            .map(e -> featureToLabel().apply(e.getKey()))
            .collect(Collectors.toList()));
        //@formatter:on

        String message = MessageFormat.format(Messages.CommonModuleType_message, types);

        //@formatter:off
        EStructuralFeature feature = values.entrySet()
            .stream()
            .filter(Entry::getValue)
            .map(Entry::getKey)
            .findFirst()
            .orElse(MD_OBJECT__NAME);
        //@formatter:on

        resultAceptor.addIssue(message, feature);
    }

}
