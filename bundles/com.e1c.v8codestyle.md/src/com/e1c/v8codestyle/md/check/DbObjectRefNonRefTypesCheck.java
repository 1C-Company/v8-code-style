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
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_FEATURE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_FEATURE__TYPE;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.mcore.TypeDescription;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.BasicFeature;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * The check that the composite type has reference and non-reference type together.
 *
 * @author Artem Iliukhin
 */
public final class DbObjectRefNonRefTypesCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "db-object-ref-non-ref-type"; //$NON-NLS-1$

  //@formatter:off
    private static final Set<String> EXLUDED_TYPES = Set.of(
        IEObjectTypeNames.NULL,
        IEObjectTypeNames.STRING,
        IEObjectTypeNames.NUMBER,
        IEObjectTypeNames.BOOLEAN,
        IEObjectTypeNames.DATE,
        IEObjectTypeNames.VALUE_STORAGE,
        IEObjectTypeNames.UUID
        );

    private static final Set<String> REF_TYPES = Set.of(
        IEObjectTypeNames.EXCHANGE_PLAN_REF,
        IEObjectTypeNames.DOCUMENT_REF,
        IEObjectTypeNames.BUSINESS_PROCESS_REF,
        IEObjectTypeNames.BUSINESS_PROCESS_ROUTE_POINT_REF,
        IEObjectTypeNames.CHART_OF_CALCULATION_TYPES_REF,
        IEObjectTypeNames.TASK_REF,
        IEObjectTypeNames.CHART_OF_CHARACTERISTIC_TYPES_REF,
        IEObjectTypeNames.CHART_OF_ACCOUNTS_REF,
        IEObjectTypeNames.CATALOG_REF,
        IEObjectTypeNames.ANY_REF
        );
  //@formatter:on


    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DbObjectRefNonRefTypesCheck_Title)
            .description(Messages.DbObjectRefNonRefTypesCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(BASIC_DB_OBJECT)
            .containment(BASIC_FEATURE)
            .features(BASIC_FEATURE__TYPE);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        BasicFeature basicFeature = (BasicFeature)object;
        TypeDescription td = basicFeature.getType();
        boolean hasRef = false;
        boolean hasExl = false;
        for (TypeItem typeItem : td.getTypes())
        {
            String typeItemName = McoreUtil.getTypeName(typeItem);
            String[] partsName = typeItemName.split("\\."); //$NON-NLS-1$
            if (partsName.length > 1 && REF_TYPES.contains(partsName[0]))
            {
                hasRef = true;
            }

            if (EXLUDED_TYPES.contains(typeItemName))
            {
                hasExl = true;
            }

            if (hasRef && hasExl)
            {
                resultAceptor.addIssue(Messages.DbObjectRefNonRefTypesCheck_Ref_and_other, BASIC_FEATURE__TYPE);
                return;
            }
        }
    }
}
