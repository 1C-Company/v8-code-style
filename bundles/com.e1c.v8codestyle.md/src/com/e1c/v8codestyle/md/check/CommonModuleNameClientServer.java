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
 *     Aleksandr Kapralov - issue #14
 *******************************************************************************/
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__CLIENT_MANAGED_APPLICATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__CLIENT_ORDINARY_APPLICATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__EXTERNAL_CONNECTION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__GLOBAL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__PRIVILEGED;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__RETURN_VALUES_REUSE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER_CALL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.md.CommonModuleTypes;

/**
 * Check client-server common module name has "ClientServer" suffix
 *
 * @author Dmitriy Marmyshev
 *
 */
public final class CommonModuleNameClientServer
    extends BasicCheck
{

    private static final String CHECK_ID = "common-module-name-client-server"; //$NON-NLS-1$

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
            .extension(new TopObjectFilterExtension())
            .extension(new MdObjectNameWithoutSuffix(NAME_SUFFIX_DEFAULT))
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(MD_OBJECT__NAME,
                COMMON_MODULE__RETURN_VALUES_REUSE,
                COMMON_MODULE__CLIENT_MANAGED_APPLICATION,
                COMMON_MODULE__CLIENT_ORDINARY_APPLICATION,
                COMMON_MODULE__SERVER,
                COMMON_MODULE__SERVER_CALL,
                COMMON_MODULE__EXTERNAL_CONNECTION,
                COMMON_MODULE__GLOBAL,
                COMMON_MODULE__PRIVILEGED);
        //@formatter:on
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        CommonModule commonModule = (CommonModule)object;
        if (commonModule.getReturnValuesReuse() != ReturnValuesReuse.DONT_USE)
        {
            return;
        }

        Map<EStructuralFeature, Object> values = new HashMap<>();
        for (EStructuralFeature feature : CommonModuleTypes.CLIENT_SERVER.getFeatureValues(false).keySet())
        {
            values.put(feature, commonModule.eGet(feature));
        }

        if (values.equals(CommonModuleTypes.CLIENT_SERVER.getFeatureValues(false)))
        {
            String message = MessageFormat.format(Messages.CommonModuleNameClientServer_message,
                parameters.getString(MdObjectNameWithoutSuffix.NAME_SUFFIX_PARAMETER_NAME));
            resultAceptor.addIssue(message, MD_OBJECT__NAME);
        }
    }

}
